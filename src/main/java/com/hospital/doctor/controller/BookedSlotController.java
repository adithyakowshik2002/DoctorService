package com.hospital.doctor.controller;

import com.hospital.doctor.dto.BookedSlotDTO;

import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTimeDto;
import com.hospital.doctor.service.BookedSlotService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;



@RequestMapping("/api/doctors")
@RestController
@RequiredArgsConstructor
public class BookedSlotController {


    private final BookedSlotService bookedSlotService;


    @GetMapping("/booked-slots")
    public ResponseEntity<List<BookedSlotDTO>> getBookedSlots1(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Implement logic to fetch booked slots
        return new ResponseEntity<>(bookedSlotService.fetchBookedSlots(doctorId,date), HttpStatus.OK);
    }

    @PostMapping("/book-slot")
    public ResponseEntity<String> bookSlot(@RequestBody SlotBookedDTO request) {
        bookedSlotService.bookSlot(request);
        return ResponseEntity.ok("Slot booked successfully");
    }
@GetMapping("/available/slots/timings/{doctorId}")
    public ResponseEntity<List<SlotTimeDto>> getAvailableSlotsTimings(@PathVariable Long doctorId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<SlotTimeDto> bookedSlotDTOS =bookedSlotService.fetchAvailableSlotTimings(doctorId, date);
        return new ResponseEntity<>(bookedSlotDTOS,HttpStatus.OK);

    }

}
