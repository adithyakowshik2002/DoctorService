package com.hospital.doctor.dto;

/**
 * Payload to create or update a doctor (with schedules)
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequestDto {
    private String name;
    private String qualifications;
    private String registrationNumber;
    private String specialization;
    private String languages;
    private Integer experienceYears;
    private String location;
    private MultipartFile profileImage;
    @Builder.Default // Base64 or URL
    private List<AvailableScheduleDto> schedules=new ArrayList<>();

    @Builder.Default
    private List<BookedSlotDTO> bookedSlotDto=new ArrayList<>();


    @Builder.Default
    private List<AvailableDateDto> availableDateDto =new ArrayList<>();
}
