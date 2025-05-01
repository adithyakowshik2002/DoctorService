package com.hospital.doctor.service.impl;


import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.dto.BookedSlotDTO;
import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTimeDto;
import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.repository.AvailableScheduleRepository;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.service.BookedSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
class BookedSlotServiceImpl implements BookedSlotService {

    private final BookedSlotMapper bookedSlotMapper;

private final AvailableScheduleRepository availableScheduleRepository;
    private final BookedSlotRepository bookedSlotRepository;

    @Override
    public List<BookedSlotDTO> fetchBookedSlots(Long doctorId, LocalDate date) {
        List<BookedSlotEntity> bookedSlots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        return bookedSlotMapper.toResponse(bookedSlots).stream()
                .map(slot -> new BookedSlotDTO(
                        slot.getSlotDate(),
                        slot.getSlotStartTime(),
                        slot.getSlotEndTime(),
                        slot.getStatus()
                ))
                .collect(Collectors.toList());

    }



    @Override
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

}