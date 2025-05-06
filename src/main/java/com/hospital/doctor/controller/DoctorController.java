package com.hospital.doctor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.doctor.dto.*;
import com.hospital.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

   // private final AuthClient authClient;
    private final DoctorService doctorService;
    private final ObjectMapper mapper;
    @PostMapping("/creating")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<DoctorResponseDto> createDoctor(@RequestPart("profileImage") MultipartFile profileImage, @RequestPart("doctorJson") String doctorJson)
    {
        try
       {

        DoctorRequestDto requestDto =mapper.readValue(doctorJson,DoctorRequestDto.class);
        requestDto.setProfileImage(profileImage);
        DoctorResponseDto responseDto = doctorService.createDoctor(requestDto);
        return new ResponseEntity<>(responseDto,HttpStatus.CREATED);
    }catch (Exception e)
      {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }

    @GetMapping("/getalldoctors")

    public ResponseEntity<?> getAllDoctors() {
        List<DoctorResponseDto> response = doctorService.getAllDoctors();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("getbyid/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {

        DoctorDto response = doctorService.getDoctorById(id);
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

    @GetMapping("/doctor/{id}/available-slots")
    public ResponseEntity<List<AvailableDateDto>> getAvailableDates(@PathVariable Long id) {
        List<AvailableDateDto> availableSlots = doctorService.getAvailableDates(id);
        return ResponseEntity.ok(availableSlots);
    }//used

    @PostMapping("/doctor/{id}/set-availability")
    public ResponseEntity<AvailableDateDto> setAvailability(
            @PathVariable Long id,
            @RequestBody AvailableDateDto request) {
        AvailableDateDto availableDateDto=doctorService.setDoctorAvailability(id, request);
        return ResponseEntity.ok(availableDateDto);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<AvailableDateDto>> getAvailableSchedules(
            @RequestParam Long doctorId) {
        List<AvailableDateDto> availableScheduleDtoList=doctorService.getAvailableDates(doctorId);
        return new ResponseEntity<>(availableScheduleDtoList,
                HttpStatus.OK);
    }//no use
    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorDto> getDoctorByEmail(@PathVariable("email") String email) throws Exception {
        DoctorDto responseDto = doctorService.findByEmail(email);
        if(responseDto==null)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/user/{id}")
    public DoctorDto getByUserId(@PathVariable Long id){
        return doctorService.getByUserId(id);
    }


    @PutMapping("/update-user-id")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<String> updateDoctorUserId(
            @RequestParam Long doctorId,
            @RequestParam Long userId
            ) {
        try {
            String message = doctorService.updateDoctorUserId(doctorId, userId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }



}