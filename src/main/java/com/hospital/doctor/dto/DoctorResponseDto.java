package com.hospital.doctor.dto;




import com.hospital.doctor.entity.ScheduleDateEntity;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponseDto {
    private Long id;
    private String name;
    private String qualifications;
    private String registrationNumber;
    private String specialization;
    private String languages;
    private Integer experienceYears;
    private String location;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private List<ScheduleResponseDto> schedules=new ArrayList<>();
    @Builder.Default
    private List<BookedSlotResponse> BookedSlotResponse=new ArrayList<>();
}
