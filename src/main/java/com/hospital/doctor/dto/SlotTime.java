package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotTime {

    private LocalTime slot;
    private boolean isBooked;
    private boolean isRescheduled;
}
