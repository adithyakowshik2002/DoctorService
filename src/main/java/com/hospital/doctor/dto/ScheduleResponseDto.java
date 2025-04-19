package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Returned when fetching a doctor's schedules
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponseDto {
    private Long scheduleId;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private List<String> availableDates;
    @Builder.Default
    private List<BookedSlotResponse> bookedSlots=new ArrayList<>();
}
