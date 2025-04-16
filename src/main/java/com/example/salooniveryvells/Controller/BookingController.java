package com.example.salooniveryvells.Controller;

import com.example.salooniveryvells.Dto.BookingDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.BookingService;
import com.example.salooniveryvells.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(name = "api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            ResponseDTO response = bookingService.createBooking(bookingDTO);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }


    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')") // Only customers can access this
    public ResponseEntity<?> getBookingsForCustomer(
            @PathVariable int id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            ResponseDTO responseDTO = bookingService.getBookingsForCustomer(id, status, fromDate, toDate);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/provider/{id}")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')") // Only providers can access this
    public ResponseEntity<?> getBookingsForProvider(
            @PathVariable int id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            ResponseDTO responseDTO = bookingService.getBookingsForProvider(id, status, fromDate, toDate);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }



    @GetMapping("/{bookingId}")
    public ResponseEntity<ResponseDTO> getBookingById(@PathVariable int bookingId) {
        try {
            ResponseDTO response = bookingService.getBookingById(bookingId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

   /* @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ResponseDTO> getBookingsByCustomer(@PathVariable int customerId) {
        try {
            ResponseDTO response = bookingService.getBookingsByCustomer(customerId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }*/

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ResponseDTO> getBookingsByService(@PathVariable int serviceId) {
        try {
            ResponseDTO response = bookingService.getBookingsByService(serviceId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

  /*  @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<ResponseDTO> getBookingsByProvider(@PathVariable int providerId) {
        try {
            ResponseDTO response = bookingService.getBookingsByServiceProvider(providerId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }*/

    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<ResponseDTO> updateBookingStatus(
            @PathVariable int bookingId,
            @RequestParam String status) {
        try {
            ResponseDTO response = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PatchMapping("/{bookingId}/status/duration")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<ResponseDTO> updateHoursWorked(
            @PathVariable int bookingId,
            @RequestParam String status,
            @RequestParam double duration) {
        try {
            ResponseDTO response = bookingService.updateHoursWorked(bookingId, duration,status);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ResponseDTO> cancelBooking(@PathVariable int bookingId) {
        try {
            ResponseDTO response = bookingService.cancelBooking(bookingId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<ResponseDTO> getActiveBookings() {
        try {
            ResponseDTO response = bookingService.getActiveBookings();
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}
