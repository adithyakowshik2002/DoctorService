package com.hospital.doctor.DoctorMapper;

import com.hospital.doctor.dto.AvailableScheduleDto;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailableScheduleMapper {

    @Autowired
    private ModelMapper modelMapper;


    public AvailableScheduleEntity toEntity(AvailableScheduleDto request){
        return modelMapper.map(request, AvailableScheduleEntity.class);
    }



    public AvailableScheduleDto toResponse(AvailableScheduleEntity entity){
        return modelMapper.map(entity, AvailableScheduleDto.class);
    }

    public List<AvailableScheduleDto> toResponse(List<AvailableScheduleEntity> entities){
        return entities.stream().map(this::toResponse).toList();
    }

}
