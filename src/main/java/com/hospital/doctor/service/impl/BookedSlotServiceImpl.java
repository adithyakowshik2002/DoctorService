package com.hospital.doctor.service.impl;


import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.dto.BookedSlotDTO;
import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTime;
import com.hospital.doctor.dto.SlotTimeDto;
import com.hospital.doctor.dto.other.MailDtoClass;
import com.hospital.doctor.dto.other.PatientResponse;
import com.hospital.doctor.entity.AvailableDateEntity;
import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;
import com.hospital.doctor.exceptions.NotFoundException;
import com.hospital.doctor.feign.AppointmentClient;
import com.hospital.doctor.feign.MailServiceClient;
import com.hospital.doctor.repository.AvailableDateRepository;
import com.hospital.doctor.repository.AvailableScheduleRepository;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.service.BookedSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
class BookedSlotServiceImpl implements BookedSlotService {

    private final MailServiceClient mailServiceClient;
    private final BookedSlotMapper bookedSlotMapper;

    private final DoctorRepository doctorRepository;

    private final AppointmentClient appointmentClient;

    private final AvailableDateRepository availableDateRepository;
private final AvailableScheduleRepository availableScheduleRepository;
    private final BookedSlotRepository bookedSlotRepository;

    @Override
    @Transactional
    public List<BookedSlotDTO> fetchBookedSlots(Long doctorId, LocalDate date) {
        List<BookedSlotEntity> bookedSlots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        return bookedSlotMapper.toResponse(bookedSlots).stream()
                .map(slot -> new BookedSlotDTO(
                        slot.getBookedSlotId(),
                        slot.getPatientId(),
                        slot.getSlotDate(),
                        slot.getSlotStartTime(),
                        slot.getSlotEndTime(),
                        slot.getStatus()
                ))
                .collect(Collectors.toList());

    }



    @Override
    @Transactional
    public List<SlotTimeDto> fetchAvailableSlotTimings(Long doctorId, LocalDate date) {

        //  Fetch all booked slots
        List<BookedSlotEntity> bookedSlots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        // Extract booked start times into a Set<LocalTime> for accurate comparison
        Set<LocalTime> bookedStartTimes = bookedSlots.stream()
                .map(BookedSlotEntity::getSlotStartTime)
                .collect(Collectors.toSet());

        // Fetch all schedules for the given date
        List<AvailableScheduleEntity> schedules = availableScheduleRepository
                .findByDoctorIdAndAvailableDate(doctorId, date);

        List<SlotTimeDto> availableSlots = new ArrayList<>();

        for (AvailableScheduleEntity schedule : schedules) {
            LocalTime current = schedule.getAvailableFrom();
            LocalTime end = schedule.getAvailableTo();

            // Create slots in 15-minute intervals
            while (current.isBefore(end)) {
                // Only add slot if it's NOT already booked
                if (!bookedStartTimes.contains(current)) {
                    availableSlots.add(new SlotTimeDto(current.toString()));
                }
                current = current.plusMinutes(15);
            }
        }

        return availableSlots;
    }


    @Override
    @Transactional
    public void bookSlot(SlotBookedDTO request) {
        AvailableScheduleEntity schedule = availableScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for ID: " + request.getScheduleId()));

        LocalDate slotDate = LocalDate.parse(request.getSlotDate());
        LocalTime requestedStartTime = LocalTime.parse(request.getSlotStartTime());
        LocalTime requestedEndTime = requestedStartTime.plusMinutes(15).minusSeconds(1);  // assuming 15 min slots


        if (requestedStartTime.isBefore(schedule.getAvailableFrom()) || requestedEndTime.isAfter(schedule.getAvailableTo())) {
            throw new IllegalStateException("Requested slot time is outside the doctor's available hours.");
        }


        boolean overlapExists = bookedSlotRepository.existsByAvailableScheduleEntityAndSlotDateAndSlotStartTimeLessThanAndSlotEndTimeGreaterThan(
                schedule,
                slotDate,
                requestedEndTime,     // existing.startTime < requestedEnd
                requestedStartTime    // existing.endTime > requestedStart
        );

        if (overlapExists) {
            throw new IllegalStateException("This slot is already booked. Please choose another time.");
        }


        BookedSlotEntity bookedSlot = BookedSlotEntity.builder()
                .availableScheduleEntity(schedule)
                .slotDate(slotDate)
                .slotStartTime(requestedStartTime)
                .slotEndTime(requestedEndTime)
                .patientId(request.getPatientId())
                .status(request.getStatus())
                .build();

        bookedSlotRepository.save(bookedSlot);
    }
    @Transactional
    public List<SlotTime> getSlotStatusWithRescheduleOption(Long doctorId, LocalDate date) {
        List<AvailableDateEntity> dates = availableDateRepository.findByDoctorId(doctorId);

        Long dateId = null;
        for (AvailableDateEntity entry : dates) {
            if (entry.getAvailableDate().equals(date)) {
                dateId = entry.getId();
                break;
            }
        }

        if (dateId == null) {
            throw new RuntimeException("No available date found for doctor on given date");
        }

        List<AvailableScheduleEntity> scheduleList = availableScheduleRepository.findByAvailableDateId(dateId);

        if (scheduleList.isEmpty()) {
            throw new RuntimeException("No available schedule found for given date ID");
        }

        List<SlotTime> result = new ArrayList<>();

        for (AvailableScheduleEntity schedule : scheduleList) {
            List<LocalTime> allSlots = new ArrayList<>();
            LocalTime current = schedule.getAvailableFrom();
            LocalTime end = schedule.getAvailableTo();

            while (current.isBefore(end)) {
                allSlots.add(current);
                current = current.plusMinutes(15);
            }

            List<BookedSlotEntity> booked = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

            Set<LocalTime> bookedTimes = booked.stream()
                    .map(BookedSlotEntity::getSlotStartTime)
                    .collect(Collectors.toSet());

            Map<LocalTime, BookedSlotEntity> bookedMap = booked.stream()
                    .collect(Collectors.toMap(BookedSlotEntity::getSlotStartTime, Function.identity(), (e1, e2) -> e1));

            for (LocalTime slot : allSlots) {
                SlotTime dto = new SlotTime();
                dto.setSlot(slot);
                if (bookedTimes.contains(slot)) {
                    BookedSlotEntity bookedSlot = bookedMap.get(slot);
                    dto.setBooked(true);
                    dto.setRescheduled("RESCHEDULE".equals(bookedSlot.getStatus()));
                } else {
                    dto.setBooked(false);
                    dto.setRescheduled(false);
                }
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteSlot(Long doctorId, LocalDate date) {

        AvailableDateEntity dateEntity = availableDateRepository
                .findByDoctorIdAndAvailableDate(doctorId, date)
                .orElseThrow(() -> new NotFoundException("Doctor not found for given date"));

        Long dateId = dateEntity.getId();

        List<AvailableScheduleEntity> scheduleEntities = availableScheduleRepository.findByAvailableDateId(dateId);

        List<BookedSlotEntity> slots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        boolean hasBookedSlot = slots.stream()
                .anyMatch(slot -> "BOOKED".equalsIgnoreCase(slot.getStatus()));

        if (!hasBookedSlot) {
            for (AvailableScheduleEntity schedule : scheduleEntities) {
                bookedSlotRepository.deleteByScheduleId(schedule.getScheduleId());
                availableScheduleRepository.deleteById(schedule.getScheduleId());
            }
            availableDateRepository.deleteById(dateId);
        } else {
            throw new RuntimeException("Slot date not deleted because booked slots still exist.");
        }
    }


    @Override
    @Transactional
    public BookedSlotDTO getBookedData(Long doctorId, LocalDate date, LocalTime time) {
        BookedSlotEntity entity =  bookedSlotRepository.findByAvailableScheduleEntity_AvailableDate_Doctor_IdAndSlotDateAndSlotStartTime(doctorId,date,time);
        return bookedSlotMapper.toResponse(entity);
    }
    @Override
    @Transactional
    public void rescheduleAppointment(Long doctorId, Long patientId,LocalDate date,LocalTime time) {
        DoctorEntity doctor = doctorRepository.findById(doctorId).orElseThrow(()-> new NotFoundException("Doctor not found with ID "+doctorId));

        BookedSlotEntity bookedSlot = bookedSlotRepository.findByPatientIdAndSlotDateAndSlotStartTime(patientId,date,time);

        PatientResponse patient = appointmentClient.getPatientById(patientId);

        MailDtoClass mail = MailDtoClass.builder()
                .fullName(patient.getFirstName() + " " + patient.getLastName())
                .doctorName(doctor.getName())
                .bookedDate(bookedSlot.getSlotDate())
                .slotTime(bookedSlot.getSlotStartTime())
                .mailId(patient.getEmail())
                .build();

        mailServiceClient.sendRescheduleEmail(mail);

        bookedSlot.setStatus("RESCHEDULE");

        bookedSlotRepository.save(bookedSlot);

        appointmentClient.updateStatus(patientId,date,time);
    }



}