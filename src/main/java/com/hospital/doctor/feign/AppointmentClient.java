package com.hospital.doctor.feign;

import com.hospital.doctor.dto.other.PatientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

@FeignClient(name = "PATIENT-SERVICE")
public interface AppointmentClient {

    @GetMapping("/api/patients/{id}")
    PatientResponse getPatientById(@PathVariable  Long id);

    @PutMapping("/api/appointments/update-status")
    public ResponseEntity<String> updateStatus(@RequestParam Long patientId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)LocalTime time);


}
