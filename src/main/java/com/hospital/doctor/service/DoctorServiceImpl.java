package com.hospital.doctor.service;

import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.DoctorMapper.DoctorMapper;
import com.hospital.doctor.DoctorMapper.ScheduleDateMapper;
import com.hospital.doctor.DoctorMapper.ScheduleMapper;
import com.hospital.doctor.dto.*;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;

import com.hospital.doctor.entity.ScheduleDateEntity;
import com.hospital.doctor.entity.ScheduleEntity;
import com.hospital.doctor.exceptions.*;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper doctorMapper;
    private final BookedSlotMapper bookedSlotMapper;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookedSlotRepository bookedSlotRepository;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleDateMapper scheduleDateMapper;

    @Transactional
    @Override
    public DoctorResponseDto createDoctor(DoctorRequestDto request) {
        DoctorEntity doctor = doctorMapper.toEntity(request);

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                doctor.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
            } catch (Exception e) {
                throw new ImageInvalidException("Invalid Base64 image data");
            }
        }

        // Handle Schedules
        if (request.getSchedules() != null) {
            List<ScheduleEntity> schedules = new ArrayList<>();

            for (ScheduleRequestDto dto : request.getSchedules()) {

                // Map schedule dates
                List<ScheduleDateEntity> scheduleDates = new ArrayList<>();
                for (ScheduleDateDto dateDto : dto.getAvailableDates()) {
                    if (dateDto.getAvailableDate() == null) {
                        throw new IllegalArgumentException("ScheduleDate cannot be null.");
                    }

                    ScheduleDateEntity dateEntity = ScheduleDateEntity.builder()
                            .availableDate(LocalDate.parse(dateDto.getAvailableDate()))
                            .build();

                    scheduleDates.add(dateEntity);
                }

                ScheduleEntity schedule = ScheduleEntity.builder()
                        .doctor(doctor)
                        .availableFrom(dto.getAvailableFrom())
                        .availableTo(dto.getAvailableTo())
                        .availableDates(scheduleDates)
                        .build();

                // Assign back-reference
                for (ScheduleDateEntity d : scheduleDates) {
                    d.setSchedule(schedule);
                }

                schedules.add(schedule);
            }

            doctor.setSchedules(schedules);
        }

        // Handle Booked Slots
        if (request.getBookedSlotDto() != null) {
            List<BookedSlotEntity> bookedSlots = new ArrayList<>();

            for (BookedSlotRequest dto : request.getBookedSlotDto()) {
                if (dto.getSlotDate() == null) {
                    throw new IllegalArgumentException("Slot date cannot be null for booked slots.");
                }

                BookedSlotEntity bookedSlot = BookedSlotEntity.builder()
                        .doctorEntity(doctor)
                        .slotDate(LocalDate.parse(dto.getSlotDate()))
                        .slotStartTime(dto.getSlotStartTime())
                        .slotEndTime(dto.getSlotEndTime())
                        .isBooked(dto.getIsBooked() != null ? dto.getIsBooked() : false)
                        .build();

                bookedSlots.add(bookedSlot);
            }
            doctor.setBookedSlotEntities(bookedSlots);
        }

        DoctorEntity saved = doctorRepository.save(doctor);
        return doctorMapper.toResponse(saved);
    }


    @Transactional
    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        List<DoctorResponseDto> result = doctorRepository.findAll().stream()
                .map(doctorMapper::toResponse).toList();
        if (result.isEmpty()) {
            throw new DoctorNotFoundException("Doctors not found ask the admin to add the doctors");
        }
        return result;
    }

    @Transactional
    @Override
    public DoctorResponseDto getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + id + " not found"));
    }

    @Transactional
    @Override
    public void deleteDoctor(Long id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
        } else {
            throw new DoctorNotFoundException("Doctor with ID " + id + " not found");
        }
    }


    @Transactional
    @Override
    public List<DoctorResponseDto> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.isEmpty()) {
            throw new DoctorNameNotFound("Specialization is null or empty--enter the valid Sepcialization");
        }
        List<DoctorEntity> doctors = doctorRepository.findBySpecialization(specialization);

        if (doctors.isEmpty()) {
            throw new DoctorNotFoundException("Doctor with specialization  " + specialization + " not found");
        }

        return doctors.stream().map(doctorMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<DoctorResponseDto> getDoctorsByName(String name) {
        List<DoctorResponseDto> response = doctorRepository.findByName(name).stream()
                .map(doctorMapper::toResponse).collect(Collectors.toList());

        if (response.isEmpty()) {
            throw new DoctorNameNotFound("Doctor with name '" + name + "' not found");
        }

        return response;
    }

    @Transactional
    @Override
    public DoctorResponseDto getDoctorByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isEmpty()) {
            throw new DoctorNotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }

        DoctorEntity entity = doctorRepository.findByRegistrationNumber(registrationNumber);
        if (entity == null) {
            throw new DoctorNotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }

        return doctorMapper.toResponse(entity);
    }

    @Transactional
    @Override
    public Map<LocalTime, String> getAvailableSlots(Long doctorId, LocalDate date) {

        DoctorEntity doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException("doctor not with selected id"));

        List<ScheduleEntity> schedules = scheduleRepository.findByDoctorAndAvailableDates_AvailableDate(doctor, date);

        if (schedules.isEmpty()) {
            throw new SlotNotAvailableException("No schedules found for doctor on date: " + date);
        }


        List<BookedSlotEntity> bookedSlotEntities = bookedSlotRepository
                .findByDoctorEntityAndSlotDate(doctor, date);

        Set<LocalTime> bookedTimes = bookedSlotEntities.stream()
                .map(BookedSlotEntity::getSlotStartTime)
                .collect(Collectors.toSet());


        Map<LocalTime, String> slots = new LinkedHashMap<>();

        for (ScheduleEntity schedule : schedules) {
            LocalTime current = schedule.getAvailableFrom();
            LocalTime to = schedule.getAvailableTo();

            while (current.isBefore(to)) {
                LocalTime slotStart = current;
                String status = bookedTimes.contains(slotStart) ? "Not Available" : "Available";
                slots.put(slotStart, status);
                current = current.plusMinutes(15);
            }
        }

        return slots;


    }




    @Transactional
    @Override
    public void setDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        // 🔹 Fetch doctor first
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + doctorId + " not found"));

        String date = request.getAvailableDate();
        LocalTime from = request.getAvailableFrom();
        LocalTime to = request.getAvailableTo();

// Delete existing slots for the doctor on this date
        bookedSlotRepository.deleteByDoctorEntityAndSlotDate(doctor, LocalDate.parse(date));

// Create new 15-minute slots
        List<BookedSlotEntity> slotList = new ArrayList<>();
        LocalTime current = from;

        while (current.isBefore(to)) {
            LocalTime end = current.plusMinutes(15);

            BookedSlotEntity slot = BookedSlotEntity.builder()
                    .doctorEntity(doctor)
                    .slotDate(LocalDate.parse(date))
                    .slotStartTime(current)
                    .slotEndTime(end)
                    .isBooked(false)
                    .build();

            slotList.add(slot);
            current = end;
        }

        bookedSlotRepository.saveAll(slotList);


    }



    @Transactional
    @Override

    public DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request) {
        DoctorEntity existing = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + id + " not found"));

        existing.setName(request.getName());
        existing.setQualifications(request.getQualifications());
        existing.setRegistrationNumber(request.getRegistrationNumber());
        existing.setSpecialization(request.getSpecialization());
        existing.setLanguages(request.getLanguages());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setLocation(request.getLocation());

        // Handle profile image update
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                existing.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
            } catch (Exception e) {
                throw new ImageInvalidException("Invalid Base64 image data");
            }
        }

        // Handle Schedules update
        if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
            existing.getSchedules().clear(); // Clear existing schedules

            for (ScheduleRequestDto dto : request.getSchedules()) {
                List<ScheduleDateEntity> scheduleDates = new ArrayList<>();

                for (ScheduleDateDto dateDto : dto.getAvailableDates()) {
                    if (dateDto.getAvailableDate() == null) {
                        throw new IllegalArgumentException("ScheduleDate cannot be null.");
                    }

                    ScheduleDateEntity dateEntity = ScheduleDateEntity.builder()
                            .availableDate(LocalDate.parse(dateDto.getAvailableDate()))
                            .build();

                    scheduleDates.add(dateEntity);
                }

                ScheduleEntity schedule = ScheduleEntity.builder()
                        .doctor(existing)
                        .availableFrom(dto.getAvailableFrom())
                        .availableTo(dto.getAvailableTo())
                        .availableDates(scheduleDates)
                        .build();

                for (ScheduleDateEntity d : scheduleDates) {
                    d.setSchedule(schedule); // Back-reference
                }

                existing.getSchedules().add(schedule); // ADD instead of SET
            }
        }

        // Handle Booked Slots update
        if (request.getBookedSlotDto() != null && !request.getBookedSlotDto().isEmpty()) {
            existing.getBookedSlotEntities().clear(); // Clear existing booked slots

            for (BookedSlotRequest dto : request.getBookedSlotDto()) {
                if (dto.getSlotDate() == null) {
                    throw new SlotNotAvailableException("Slot date cannot be null for booked slots.");
                }

                BookedSlotEntity bookedSlot = BookedSlotEntity.builder()
                        .doctorEntity(existing)
                        .slotDate(LocalDate.parse(dto.getSlotDate()))
                        .slotStartTime(dto.getSlotStartTime())
                        .slotEndTime(dto.getSlotEndTime())
                        .isBooked(dto.getIsBooked() != null ? dto.getIsBooked() : false)
                        .build();

                existing.getBookedSlotEntities().add(bookedSlot); // ADD instead of SET
            }
        }

        DoctorEntity updated = doctorRepository.save(existing);
        return doctorMapper.toResponse(updated);
    }
}
