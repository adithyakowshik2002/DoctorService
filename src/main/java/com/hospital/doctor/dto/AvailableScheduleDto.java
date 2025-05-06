package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableScheduleDto {
    private LocalTime availableFrom;
    private LocalTime availableTo;
    @Builder.Default// e.g. 12:00
    private List<AvailableDateDto> availableDates=new ArrayList<>();
}
