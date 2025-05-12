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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        DoctorEntity doctorEntity = doctorMapper.toEntity(request);
        MultipartFile imageFile = request.getProfileImage();if(imageFile != null && !imageFile.isEmpty()){    try{
            byte[] imageBytes = imageFile.getBytes();
            doctorEntity.setProfileImage(imageBytes);
        }catch (IOException e){
            throw  new ImageInvalidException("Faild to read image file"+e.getMessage());
        }
        }
        doctorEntity.setRegistrationNumber(request.getRegistrationNumber());
        DoctorEntity saved = doctorRepository.save(doctorEntity);
        return convertToDto(saved);

    }

    public DoctorResponseDto convertToDto(DoctorEntity doctor){
       return DoctorResponseDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .qualifications(doctor.getQualifications())
                .registrationNumber(doctor.getRegistrationNumber().toString())
                .specialization(doctor.getSpecialization())
                .languages(doctor.getLanguages())
                .experienceYears(doctor.getExperienceYears())
                .location(doctor.getLocation())
                .profileImageBase64(doctor.getProfileImage() != null ? Base64.getEncoder().encodeToString(doctor.getProfileImage()) : null ).createdAt(doctor.getCreatedAt()).updatedAt(doctor.getUpdatedAt()).build();
    }




    @Transactional
    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        List<DoctorResponseDto> result = doctorRepository.findAll().stream()
                .map(this::convertToDto).toList();
        if (result.isEmpty()) {
            throw new NotFoundException("Doctors not found ask the admin to add the doctors");
        }
        return result;
    }

    @Transactional
    @Override
    public DoctorDto getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::Response)
                .orElseThrow(() -> new NotFoundException("Doctor with ID " + id + " not found"));
    }


    @Transactional
    @Override
    public DoctorResponseDto fetchDoctorById(Long id){
        return doctorRepository.findById(id).map(doctorMapper::toResponse).orElseThrow(() -> new NotFoundException("Doctor with ID " + id + " not found"));
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


        DoctorEntity entity = doctorRepository.findByRegistrationNumber(registrationNumber);
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

        LocalDate today = LocalDate.now();

        // Filter out past dates (i.e., only include today or future)
        List<AvailableDateEntity> futureDates = availableDateEntities.stream()
                .filter(entity -> !entity.getAvailableDate().isBefore(today))
                .collect(Collectors.toList());

        if (futureDates.isEmpty()) {
            throw new SlotNotAvailableException("No future dates found for doctor id: " + doctorId);
        }

        return availableDateMapper.toResponse(futureDates);
    }






    @Override
    @Transactional
    public AvailableDateDto setDoctorAvailability(Long doctorId, AvailableDateDto availableDateDto) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNameNotFound("Doctor not found with ID: " + doctorId));

        LocalDate date = availableDateDto.getAvailableDate();


        AvailableDateEntity dateEntity = availableDateRepository.findByDoctorIdAndAvailableDate(doctorId,date)
                .orElseGet(()->{
                    AvailableDateEntity newDate = new AvailableDateEntity();
                    newDate.setAvailableDate(date);
                    newDate.setDoctor(doctor);
                    newDate.setAvailableScheduleEntity(new ArrayList<>());
                    return newDate;
                });
        // process and validate new time slots
        List<AvailableScheduleEntity> existingSchedules = dateEntity.getAvailableScheduleEntity();

        availableDateDto.getSchedule().forEach(dto ->{
            LocalTime from = dto.getAvailableFrom();
            LocalTime to  = dto.getAvailableTo();

            boolean isOverlap = existingSchedules.stream().anyMatch(existing ->isOverlapping(from,to,existing.getAvailableFrom(),existing.getAvailableTo()));

            if(isOverlap){
                try {
                    throw new IllegalAccessException("TIme slot "+from+" overlaps with another slot.");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            AvailableScheduleEntity schedule = new AvailableScheduleEntity();
            schedule.setAvailableFrom(from);
            schedule.setAvailableTo(to);
            schedule.setAvailableDate(dateEntity);
            existingSchedules.add(schedule);
        });
        AvailableDateEntity newDateEntity = new AvailableDateEntity();
        newDateEntity.setAvailableDate(date);
        newDateEntity.setDoctor(doctor);



        //  Save and return DTO
        AvailableDateEntity savedEntity = availableDateRepository.save(dateEntity);
        return availableDateMapper.toResponse(savedEntity);
    }
    private boolean isOverlapping(LocalTime from, LocalTime to, LocalTime availableFrom, LocalTime availableTo) {
        return !from.isAfter(availableFrom) && ! to.isBefore(availableFrom);
    }


    @Override
    @Transactional
    public DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request) {
        DoctorEntity existing = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor with ID " + id + " not found"));

        // Update basic fields
        existing.setName(request.getName());
        existing.setQualifications(request.getQualifications());
        existing.setSpecialization(request.getSpecialization());
        existing.setLanguages(request.getLanguages());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setLocation(request.getLocation());

        // Conditionally update profile image only if provided
        MultipartFile imageFile = request.getProfileImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                existing.setProfileImage(imageBytes);
            } catch (IOException e) {
                throw new ImageInvalidException("Failed to read image file: " + e.getMessage());
            }
        }
        // Else: retain existing image — no action needed

        DoctorEntity updated = doctorRepository.save(existing);
        return doctorMapper.toResponse(updated);
    }


    @Override
    @Transactional
    public DoctorDto findByEmail(String email) throws Exception {

        DoctorEntity entity =doctorRepository.findByEmail(email);
        if(entity.getEmail().isEmpty())
        {
            throw new Exception("the email you entered is not found"+email);
        }

        DoctorDto response = doctorMapper.Response(entity);
        return response;
    }
    @Override
    public DoctorDto getByUserId(Long id){
        DoctorEntity entity = doctorRepository.findByUserId(id);
        return new DoctorDto(entity.getUserId(),entity.getName(),entity.getEmail(),entity.getId());
    }

    @Override
    public String updateDoctorUserId(Long doctorId, Long userId) {
        Optional<DoctorEntity> optionalDoctor = doctorRepository.findById(doctorId);

        if (optionalDoctor.isPresent()) {
            DoctorEntity doctor = optionalDoctor.get();
            doctor.setUserId(userId);
            doctorRepository.save(doctor);
            return "UserId updated successfully";
        } else {
            throw new NotFoundException("Doctor not found with ID: " + doctorId);
        }
    }
}
