package com.hospital.doctor.dto;

/**
 * Payload to create or update a doctor (with schedules)
 */

import com.hospital.doctor.entity.BookedSlotEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String profileImage;
    @Builder.Default // Base64 or URL
    private List<ScheduleRequestDto> schedules=new ArrayList<>();

    @Builder.Default
    private List<BookedSlotRequest> bookedSlotDto=new ArrayList<>();
}
