package com.hospital.doctor.DoctorMapper;

import com.hospital.doctor.dto.BookedSlotDTO;

import com.hospital.doctor.entity.BookedSlotEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookedSlotMapper {

    @Autowired
    private ModelMapper modelMapper;

    public BookedSlotEntity toEntity(BookedSlotDTO request){
        return modelMapper.map(request, BookedSlotEntity.class);
    }

    public BookedSlotDTO toResponse(BookedSlotEntity entity){
        return modelMapper.map(entity,BookedSlotDTO.class);
    }
    public List<BookedSlotDTO> toResponse(List<BookedSlotEntity> entity){
        return entity.stream().map(this::toResponse).toList();
    }
}

