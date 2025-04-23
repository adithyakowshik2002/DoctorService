package com.hospital.doctor.service.impl;

import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.DoctorMapper.DoctorMapper;
import com.hospital.doctor.DoctorMapper.AvailableDateMapper;
import com.hospital.doctor.DoctorMapper.AvailableScheduleMapper;
import com.hospital.doctor.dto.*;
import com.hospital.doctor.entity.DoctorEntity;

import com.hospital.doctor.entity.AvailableDateEntity;
import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.exceptions.*;
import com.hospital.doctor.repository.AvailableDateRepository;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.repository.DoctorRepository;
import com.hospital.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private final AvailableDateRepository availableDateRepository;
    private final BookedSlotRepository bookedSlotRepository;
    private final AvailableScheduleMapper availableScheduleMapper;
    private final AvailableDateMapper availableDateMapper;
    //private final AvailableScheduleRepository availableScheduleRepository;

    public DoctorResponseDto createDoctor(DoctorRequestDto request) {
        DoctorEntity doctor = doctorMapper.toEntity(request);
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                doctor.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
            } catch (Exception e) {
                throw new ImageInvalidException("Invalid Base64 image data");
            }
        }
        doctor.setRegistrationNumber(UUID.randomUUID());
        DoctorEntity saved = doctorRepository.save(doctor);
        return doctorMapper.toResponse(saved);
    }


    @Transactional
    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        List<DoctorResponseDto> result = doctorRepository.findAll().stream()
                .map(doctorMapper::toResponse).toList();
        if (result.isEmpty()) {
            throw new NotFoundException("Doctors not found ask the admin to add the doctors");
        }
        return result;
    }

    @Transactional
    @Override
    public DoctorResponseDto getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Doctor with ID " + id + " not found"));
    }

    @Transactional
    @Override
    public void deleteDoctor(Long id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
        } else {
            throw new NotFoundException("Doctor with ID " + id + " not found");
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
            throw new NotFoundException("Doctor with specialization  " + specialization + " not found");
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
            throw new NotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }

        UUID regNumber = UUID.fromString(registrationNumber);
        DoctorEntity entity = doctorRepository.findByRegistrationNumber(regNumber);
        if (entity == null) {
            throw new NotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }

        return doctorMapper.toResponse(entity);
    }

    @Transactional
    @Override
    public List<AvailableDateDto> getAvailableDates(Long doctorId) {

        List<AvailableDateEntity> availableDateEntities = availableDateRepository.findByDoctorId(doctorId);

        if (availableDateEntities.isEmpty()) {
            throw new SlotNotAvailableException("No available dates found for doctor id: " + doctorId);
        }

        return availableDateMapper.toResponse(availableDateEntities);


    }





    @Override
    @Transactional
    public AvailableDateDto setDoctorAvailability(Long doctorId, AvailableDateDto availableDateDto) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNameNotFound("Doctor not found with ID: " + doctorId));

        LocalDate date = LocalDate.parse(availableDateDto.getAvailableDate());

        // Step 1: Create the date entity
        AvailableDateEntity newDateEntity = new AvailableDateEntity();
        newDateEntity.setAvailableDate(date);
        newDateEntity.setDoctor(doctor);

        // Step 2: Map the schedule DTOs to entities
        if (!CollectionUtils.isEmpty(availableDateDto.getSchedule())) {
            List<AvailableScheduleEntity> scheduleEntities = availableDateDto.getSchedule().stream()
                    .map(availableScheduleDto -> {
                        LocalTime availableFrom = LocalTime.parse(availableScheduleDto.getAvailableFrom());
                        LocalTime availableTo = LocalTime.parse(availableScheduleDto.getAvailableTo());

                        //  Setting the availableDate reference properly
                        AvailableScheduleEntity schedule = new AvailableScheduleEntity();
                        schedule.setAvailableFrom(availableFrom);
                        schedule.setAvailableTo(availableTo);
                        schedule.setAvailableDate(newDateEntity);
                        return schedule;
                    })
                    .toList(); // Java 16+ or use .collect(Collectors.toList()) for older versions

            // Step 3: Set the list into the date entity
            newDateEntity.setAvailableScheduleEntity(scheduleEntities);
        }

        // Step 4: Save and return DTO
        AvailableDateEntity savedEntity = availableDateRepository.save(newDateEntity);
        return availableDateMapper.toResponse(savedEntity);
    }


    @Transactional
    @Override
    public DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request) {
        DoctorEntity existing = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor with ID " + id + " not found"));

        // Update only the specified fields
        existing.setName(request.getName());
        existing.setQualifications(request.getQualifications());
        //existing.setRegistrationNumber(request.getRegistrationNumber());
        existing.setSpecialization(request.getSpecialization());
        existing.setLanguages(request.getLanguages());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setLocation(request.getLocation());

        // Update profile image if present
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                existing.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
            } catch (Exception e) {
                throw new ImageInvalidException("Invalid Base64 image data");
            }
        }

        DoctorEntity updated = doctorRepository.save(existing);
        return doctorMapper.toResponse(updated);
    }

}
