package com.hospital.doctor.service;

import com.hospital.doctor.dto.BookedSlotResponse;
import com.hospital.doctor.dto.DoctorAvailabilityRequest;
import com.hospital.doctor.dto.DoctorRequestDto;
import com.hospital.doctor.dto.DoctorResponseDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface DoctorService {

    DoctorResponseDto createDoctor(DoctorRequestDto request);

    List<DoctorResponseDto> getAllDoctors();

    DoctorResponseDto getDoctorById(Long id);

    DoctorResponseDto updateDoctor(Long id, DoctorRequestDto request);

    void deleteDoctor(Long id);

    List<DoctorResponseDto> getDoctorsBySpecialization(String specialization);

    List<DoctorResponseDto> getDoctorsByName(String name);

    DoctorResponseDto getDoctorByRegistrationNumber(String registrationNumber);

    void setDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request);

    List<BookedSlotResponse> getBookedSlotTimes(Long id, LocalDate date) throws IllegalAccessException;

    Map<LocalTime, String> getAvailableSlots(Long doctorId, LocalDate date);
}
