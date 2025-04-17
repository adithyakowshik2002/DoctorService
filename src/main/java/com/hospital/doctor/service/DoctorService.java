package com.hospital.doctor.service;

import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.DoctorMapper.DoctorMapper;
import com.hospital.doctor.DoctorMapper.ScheduleMapper;
import com.hospital.doctor.dto.*;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;
import com.hospital.doctor.entity.ScheduleEntity;
import com.hospital.doctor.exceptions.DoctorNameNotFound;
import com.hospital.doctor.exceptions.DoctorNotFoundException;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.repository.DoctorRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorService implements IDocterService {


    private final DoctorMapper doctorMapper;
    private final ScheduleMapper scheduleMapper;

    private final BookedSlotMapper bookedSlotMapper;


    private final DoctorRepository doctorRepository;
    private final BookedSlotRepository bookedSlotRepository;
    @Transactional
    @Override
    public DoctorResponseDto createDoctor(DoctorRequestDto request) {


        DoctorEntity doctorEntity = doctorMapper.toEntity(request);
        /* saving entity */

        if(doctorEntity.getSchedules() != null)
        {
            for(ScheduleEntity schedule :doctorEntity.getSchedules()){
                schedule.setDoctorEntitySchedule(doctorEntity);
            }
        }

        if (doctorEntity.getBookedSlotEntities() != null) {
            for (BookedSlotEntity slot : doctorEntity.getBookedSlotEntities()) {
                slot.setDoctorEntity(doctorEntity);  // Make sure the field name matches your entity
            }
        }

        DoctorEntity savedDoctorEntity = doctorRepository.save(doctorEntity);

        return doctorMapper.toResponse(savedDoctorEntity);
    }

    @Transactional
    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        List<DoctorResponseDto> result=  doctorRepository.findAll().stream().map(doctorMapper::toResponse).toList();
      if(result.isEmpty()){
          throw new DoctorNotFoundException("Doctors not found ask the admin to add the doctors");
      }
      return result;

    }


    //GET http://localhost:8080/getalldoctors?page=0&size=5&sort=name,asc


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
        if(doctorRepository.findById(id).isPresent()){
            doctorRepository.deleteById(id);
        }
       else{
           throw new DoctorNotFoundException("Doctor with ID " + id + " not found");
        }
    }

    @Transactional
    @Override
    public List<DoctorResponseDto> getDoctorsBySpecialization(String specialization) {

        if(specialization==null || specialization.isEmpty()){
            throw new DoctorNameNotFound("Specialization is null or empty--enter the valid Sepcialization");
        }
        List<DoctorEntity> doctors = doctorRepository.findBySpecialization(specialization);

        if(doctors.isEmpty()){
            throw new DoctorNotFoundException("Doctor with specialization  "+specialization +" not found");
        }

      return doctors.stream().map(doctorMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<DoctorResponseDto> getDoctorsByName(String name) {
        try {
            List<DoctorResponseDto> response = doctorRepository
                    .findByName(name).stream()
                    .map(doctorMapper::toResponse).collect(Collectors.toList());


            if (response.isEmpty()) {
                throw new DoctorNameNotFound("Doctor with name '" + name + "' not found");
            }

            return response;
        } catch (Exception e) {
            throw new DoctorNameNotFound("Doctor with name '" + name + "' not found");
        }
    }

@Transactional
    @Override
    public DoctorResponseDto getDoctorByRegistrationNumber(String registrationNumber) {
        if(registrationNumber==null || registrationNumber.isEmpty()){
            throw new DoctorNotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }
        if(doctorRepository.findByRegistrationNumber(registrationNumber)==null){
            throw new DoctorNotFoundException("Doctor with registration number " + registrationNumber + " not found");
        }

        DoctorEntity entity = doctorRepository.findByRegistrationNumber(registrationNumber);

        return  doctorMapper.toResponse(entity);
    }
    @Transactional
    @Override
    public Map<LocalTime, Boolean> getAvailableSlots(Long doctorId, LocalDate date) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalTime from = doctor.getAvailableFrom();
        LocalTime to = doctor.getAvailableTo();

        List<BookedSlotEntity> bookedSlotEntities = bookedSlotRepository
                .findByDoctorEntityAndSlotDate(doctor, date);

        Set<LocalTime> bookedTimes = bookedSlotEntities.stream()
                .map(BookedSlotEntity::getSlotStartTime)
                .collect(Collectors.toSet());

        Map<LocalTime, Boolean> slots = new LinkedHashMap<>();
        LocalTime current = from;

        while (current.isBefore(to)) {
            slots.put(current, !bookedTimes.contains(current));
            current = current.plusMinutes(15);
        }

        return slots;
    }

    @Transactional
    @Override
    public void setDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setAvailableFrom(request.getAvailableFrom());
        doctor.setAvailableTo(request.getAvailableTo());
        doctor.setAvailableDate(request.getAvailableDate());

        doctorRepository.save(doctor);
    }

    @Transactional
    @Override
    public List<BookedSlotResponse> getBookedSlotTimes(Long doctorId, LocalDate date) throws IllegalAccessException {
        if(date==null)
        {
            throw new IllegalAccessException("Date must not be null");
        }


        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<BookedSlotEntity> slots = bookedSlotRepository.findByDoctorEntityAndSlotDate(doctor, date);

        return slots.stream().map(bookedSlotMapper::toResponse).collect(Collectors.toList());
    }


@Transactional
@Override
    public DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request) {


        DoctorEntity existing  = doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException("Doctor with ID " + id + " not found"));
        existing.setName(request.getName());
        existing.setQualifications(request.getQualifications());
        existing.setRegistrationNumber(request.getRegistrationNumber());
        existing.setSpecialization(request.getSpecialization());
        existing.setLanguages(request.getLanguages());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setLocation(request.getLocation());
        existing.setAvailableFrom(request.getAvailableFrom());
        existing.setAvailableDate(request.getAvailableDate());
        existing.setAvailableTo(request.getAvailableTo());
        existing.setDay(request.getDay());

        // Only decode and update image if it's provided
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                existing.setProfileImage(Base64.getDecoder().decode(request.getProfileImage()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 image data", e);
            }
        }


        // Update schedules (optional: replace all or merge)
        if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
            existing.getSchedules().clear();  // Clear existing if replacing
            existing.getSchedules().addAll(
                    request.getSchedules().stream().map(s -> ScheduleEntity.builder()
                            .day(s.getDay())
                            .time(s.getTime())
                            .patientType(s.getPatientType())
                            .build()).toList()
            );
        }
        Set<BookedSlotEntity> slots = request.getBookedSlotEntities().stream()
                .map(slot -> BookedSlotEntity.builder()
                        .slotDate(slot.getSlotDate())
                        .slotStartTime(slot.getSlotStartTime())
                        .slotEndTime(slot.getSlotEndTime())
                        .build()
                ).collect(Collectors.toSet());

        slots.forEach(slot -> slot.setDoctorEntity(existing));
        existing.getBookedSlotEntities().clear();
        existing.getBookedSlotEntities().addAll(slots);


        return doctorMapper.toResponse(existing);
    }

}
