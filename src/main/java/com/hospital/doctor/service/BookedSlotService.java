package com.hospital.doctor.service;

import com.hospital.doctor.dto.BookedSlotDTO;
import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTimeDto;

import java.time.LocalDate;

import java.util.List;


public interface BookedSlotService {
     // List<BookedSlotDTO> fetchAvailableSlots(Long doctorId, LocalDate date);
      List<SlotTimeDto> fetchAvailableSlotTimings(Long doctorId, LocalDate date);
     List<BookedSlotDTO> fetchBookedSlots(Long doctorId,LocalDate date);
    void bookSlot(SlotBookedDTO request);


}
