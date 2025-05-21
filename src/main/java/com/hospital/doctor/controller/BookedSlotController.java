package com.hospital.doctor.controller;

import com.hospital.doctor.dto.BookedSlotDTO;

import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTime;
import com.hospital.doctor.dto.SlotTimeDto;
import com.hospital.doctor.service.BookedSlotService;

import com.hospital.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("/api/doctors")
@RestController
@RequiredArgsConstructor
public class BookedSlotController {


    private final BookedSlotService bookedSlotService;

    private final DoctorService doctorService;

    @GetMapping("/booked-slots")
    public ResponseEntity<List<BookedSlotDTO>> getBookedSlots1(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Implement logic to fetch booked slots
        return new ResponseEntity<>(bookedSlotService.fetchBookedSlots(doctorId,date), HttpStatus.OK);
    }// after

    @PostMapping("/book-slot")
    public ResponseEntity<String> bookSlot(@RequestBody SlotBookedDTO request) {
        bookedSlotService.bookSlot(request);
        return ResponseEntity.ok("Slot booked successfully");
    }

    @GetMapping("/available/slots/timings/{doctorId}")
    public ResponseEntity<List<SlotTimeDto>> getAvailableSlotsTimings(@PathVariable Long doctorId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<SlotTimeDto> sloTimes =bookedSlotService.fetchAvailableSlotTimings(doctorId, date);

        if(date.isEqual(LocalDate.now())){
            LocalTime now = LocalTime.now();

            sloTimes = sloTimes.stream().filter(slot->{
                LocalTime fromTime = LocalTime.parse(slot.getSlot());
                return fromTime.isAfter(now);
            }).toList();
        }
        return new ResponseEntity<>(sloTimes,HttpStatus.OK);

    }
    @PutMapping("/reschedule/{doctorId}/{patientId}")
    public ResponseEntity<String> rescheduleSlots(@PathVariable Long doctorId,@PathVariable Long patientId,@RequestParam LocalDate date,@RequestParam LocalTime time) {
        bookedSlotService.rescheduleAppointment(doctorId,patientId,date,time);
        return ResponseEntity.ok("Slots rescheduled, emails sent, and schedule cleaned up if necessary.");
    }


    @GetMapping("/data/{doctorId}")
    public  ResponseEntity<BookedSlotDTO> bookedSlot(@PathVariable Long doctorId, @RequestParam LocalDate date, @RequestParam LocalTime time){
        return new ResponseEntity<>(bookedSlotService.getBookedData(doctorId,date,time), HttpStatus.OK);
    }

    @DeleteMapping("/delete-slots/{doctorId}/{date}")
    public ResponseEntity<String> deleteSlot(@PathVariable Long doctorId,@PathVariable LocalDate date){
        bookedSlotService.deleteSlot(doctorId,date);
        return ResponseEntity.ok("Deleted successfully");
    }
    @GetMapping("/slots-status/{doctorId}")
    public ResponseEntity<List<SlotTime>> getSlotStatus(
            @PathVariable Long doctorId,
            @RequestParam("date") LocalDate date) {
        System.out.println("Received doctorId: " + doctorId + ", date: " + date);
        List<SlotTime> slots = bookedSlotService.getSlotStatusWithRescheduleOption(doctorId, date);
        return ResponseEntity.ok(slots);
    }

}
