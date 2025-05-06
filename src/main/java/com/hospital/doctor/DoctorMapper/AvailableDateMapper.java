package com.hospital.doctor.DoctorMapper;

import com.hospital.doctor.dto.AvailableDateDto;
import com.hospital.doctor.dto.SlotAvailableDto;
import com.hospital.doctor.entity.AvailableDateEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailableDateMapper {

    @Autowired
    private ModelMapper modelMapper;

    public AvailableDateEntity toEntity(AvailableDateDto request){
        return modelMapper.map(request, AvailableDateEntity.class);
    }

    public AvailableDateDto toResponse(AvailableDateEntity entity){
        return modelMapper.map(entity, AvailableDateDto.class);
    }

    public List<AvailableDateDto> toResponse(List<AvailableDateEntity> entity){
        return entity.stream().map(this::toResponse).toList();
    }



}
