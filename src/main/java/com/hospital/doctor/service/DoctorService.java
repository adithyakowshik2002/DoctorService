package com.hospital.doctor.service;

import com.hospital.doctor.dto.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    DoctorResponseDto createDoctor(DoctorRequestDto request);

    List<DoctorResponseDto> getAllDoctors();

    DoctorResponseDto getDoctorById(Long id);

    DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request);

    void deleteDoctor(Long id);

    List<DoctorResponseDto> getDoctorsBySpecialization(String specialization);

    List<DoctorResponseDto> getDoctorsByName(String name);

    DoctorResponseDto getDoctorByRegistrationNumber(String registrationNumber);

    AvailableDateDto setDoctorAvailability(Long doctorId, AvailableDateDto request);

    DoctorDto findByEmail(String email) throws Exception;

    List<AvailableDateDto> getAvailableDates(Long doctorId);

    DoctorDto getByUserId(Long id);
    String updateDoctorUserId(Long doctorId, Long userId);
}
