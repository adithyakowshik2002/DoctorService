package com.hospital.doctor.service.impl;

import com.hospital.doctor.repository.AvailableScheduleRepository;
import com.hospital.doctor.service.AvailableScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AvailableScheduleServiceImpl implements AvailableScheduleService {

    private final AvailableScheduleRepository availableScheduleRepository;

    @Override
    public Long findScheduleIdByDoctorDateTime(Long doctorId, LocalDate date, LocalTime slotTime) {
        return availableScheduleRepository.findScheduleIdByDoctorDateAndSlotTime(doctorId, date, slotTime);
    }
}

