package com.hospital.doctor.service;


import com.hospital.doctor.dto.AvailableDateDto;
import com.hospital.doctor.dto.SlotAvailableDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

public interface AvailableDateService {
    List<SlotAvailableDto> getAvailableDatesByDoctorId(Long doctorId);
    //List<AvailableDateDto> getAvailableDatesByDoctorIdAndDate(Long doctorId, LocalDate date);
}
