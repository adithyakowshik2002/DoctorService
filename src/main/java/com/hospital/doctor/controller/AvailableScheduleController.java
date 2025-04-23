package com.hospital.doctor.controller;


import com.hospital.doctor.dto.SlotAvailableDto;
import com.hospital.doctor.service.AvailableDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class AvailableScheduleController {

    @Autowired
    private AvailableDateService availableDateService;

    @GetMapping("/schedule-dates/doctor/{doctorId}")
    public List<SlotAvailableDto> getAvailableDatesByDoctor(@PathVariable Long doctorId) {
        return availableDateService.getAvailableDatesByDoctorId(doctorId);
    }

//    @GetMapping("/schedule-dates/doctor/{doctorId}/date")
//    public List<AvailableDateDto> getAvailableDatesByDoctorAndDate(
//            @PathVariable Long doctorId,
//            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//        return availableDateService.getAvailableDatesByDoctorIdAndDate(doctorId, date);
//    }
}

