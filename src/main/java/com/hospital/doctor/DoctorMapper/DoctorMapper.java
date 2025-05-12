package com.hospital.doctor.DoctorMapper;

import com.hospital.doctor.dto.DoctorDto;
import com.hospital.doctor.dto.DoctorRequestDto;
import com.hospital.doctor.dto.DoctorResponseDto;
import com.hospital.doctor.entity.DoctorEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class DoctorMapper {

    @Autowired
    private ModelMapper modelMapper;

    public DoctorEntity toEntity(DoctorRequestDto request){
        return modelMapper.map(request,DoctorEntity.class);
    }

    public DoctorResponseDto toResponse(DoctorEntity entity) {
        return DoctorResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .qualifications(entity.getQualifications())
                .registrationNumber(entity.getRegistrationNumber())
                .specialization(entity.getSpecialization())
                .languages(entity.getLanguages())
                .experienceYears(entity.getExperienceYears())
                .email(entity.getEmail())
                .userId(entity.getUserId())
                .location(entity.getLocation())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .profileImageBase64(entity.getProfileImage() != null
                        ? Base64.getEncoder().encodeToString(entity.getProfileImage())
                        : null)
                .build();
    }

    public DoctorDto Response(DoctorEntity entity){
        return modelMapper.map(entity,DoctorDto.class);
    }


}
