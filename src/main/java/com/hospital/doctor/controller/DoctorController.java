package com.hospital.doctor.controller;

import com.hospital.doctor.dto.*;
import com.hospital.doctor.service.IDocterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final IDocterService doctorService;


    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorRequestDto request) {
       DoctorResponseDto response = doctorService.createDoctor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getalldoctors")
    public ResponseEntity<?> getAllDoctors() {
        List<DoctorResponseDto> response = doctorService.getAllDoctors();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("getbyid/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {

        DoctorResponseDto response = doctorService.getDoctorById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDto request) {

        DoctorResponseDto response = doctorService.updateDoctor(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/by-specialization/{specialization}")
    public ResponseEntity<?> getBySpecialization(
            @PathVariable String specialization,
            Pageable pageable) {


        List<DoctorResponseDto> response = doctorService.getDoctorsBySpecialization(specialization);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getByName(
            @PathVariable String name) {
        return new ResponseEntity<>(doctorService.getDoctorsByName(name),
                HttpStatus.OK);
    }

    @GetMapping("/getbyregno/{regNo}")
    public ResponseEntity<DoctorResponseDto> getDoctorbaseonRegNo(String regNo) {
       DoctorResponseDto response= doctorService.getDoctorByRegistrationNumber(regNo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/doctor/{id}/{date}/available-slots")
    public ResponseEntity<Map<LocalTime, Boolean>> getAvailableSlots(
            @PathVariable Long id,
            @PathVariable LocalDate date) {
        Map<LocalTime, Boolean> availableSlots = doctorService.getAvailableSlots(id, date);
        return ResponseEntity.ok(availableSlots);
    }

    @PutMapping("/doctor/{id}/set-availability")
    public ResponseEntity<String> setAvailability(
            @PathVariable Long id,
            @RequestBody DoctorAvailabilityRequest request) {
        doctorService.setDoctorAvailability(id, request);
        return ResponseEntity.ok("Availability updated successfully.");
    }

    @GetMapping("/doctor/{id}/{date}/booked-slots")
    public ResponseEntity<List<BookedSlotResponse>> getBookedSlots(
            @PathVariable Long id,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IllegalAccessException {
        List<BookedSlotResponse> bookedSlots = doctorService.getBookedSlotTimes(id, date);
        return ResponseEntity.ok(bookedSlots);
    }


}