package com.hospital.doctor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private Long id;
    private String name;
    private String qualifications;
    private String email;
    private Long userId;

    public DoctorDto(Long userId, String name, String email, Long id) {
    }
}
