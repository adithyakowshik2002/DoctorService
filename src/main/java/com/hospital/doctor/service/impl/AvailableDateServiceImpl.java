package com.hospital.doctor.service.impl;



import com.hospital.doctor.dto.AvailableDateDto;
import com.hospital.doctor.dto.SlotAvailableDto;
import com.hospital.doctor.entity.AvailableDateEntity;
import com.hospital.doctor.repository.AvailableDateRepository;
import com.hospital.doctor.service.AvailableDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailableDateServiceImpl implements AvailableDateService {

    @Autowired
    private AvailableDateRepository availableDateRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public List<SlotAvailableDto> getAvailableDatesByDoctorId(Long doctorId) {
        List<AvailableDateEntity> entities = availableDateRepository.findByDoctorId(doctorId);
        return entities.stream()
                .map(e -> new SlotAvailableDto(e.getAvailableDate()))
                .collect(Collectors.toList());
    }
}

//
