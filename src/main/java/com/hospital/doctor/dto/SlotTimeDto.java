package com.hospital.doctor.dto;

import lombok.Data;

@Data

public class SlotTimeDto {
    private String slot;
    public SlotTimeDto(String slotStart){

        this.slot=slotStart;
    }

}
