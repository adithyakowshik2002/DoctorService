package com.hospital.doctor.controller;


import com.hospital.doctor.dto.SlotAvailableDto;
import com.hospital.doctor.service.AvailableDateService;
import com.hospital.doctor.service.AvailableScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:5173")

public class AvailableScheduleController {

    @Autowired
    private AvailableDateService availableDateService;

    @Autowired
    private AvailableScheduleService availableScheduleService;

    @GetMapping("/schedule-dates/{doctorId}")
    public List<SlotAvailableDto> getAvailableDatesByDoctor(@PathVariable Long doctorId) {
        List<SlotAvailableDto> availableDates = availableDateService.getAvailableDatesByDoctorId(doctorId);

        LocalDate currentDate = LocalDate.now();

        return availableDates.stream().filter(slot-> !slot.getAvailableDate().isBefore(currentDate)).collect(Collectors.toList());
    }//no use

     @GetMapping("/find-schedule-id")
    public ResponseEntity<Long> findScheduleId(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime slotTime) {

        Long scheduleId = availableScheduleService.findScheduleIdByDoctorDateTime(doctorId, date, slotTime);

        if (scheduleId != null) {
            return ResponseEntity.ok(scheduleId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

