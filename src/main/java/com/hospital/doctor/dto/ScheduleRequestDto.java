package com.hospital.doctor.dto;

import com.hospital.doctor.entity.ScheduleDateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Create or update a schedule with multiple available dates
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequestDto {
    private LocalTime availableFrom;          // e.g. 09:00
    private LocalTime availableTo;
    @Builder.Default// e.g. 12:00
    private List<ScheduleDateDto> availableDates=new ArrayList<>();   // e.g. ["2025-04-21", "2025-04-23"]
}
