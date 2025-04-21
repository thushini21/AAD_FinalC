package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.*;
import com.example.salooniveryvells.Service.BookingService;
import com.example.salooniveryvells.Service.Impl.EmailService;
import com.example.salooniveryvells.Service.ServiceService;
import com.example.salooniveryvells.Service.UserService;
import com.example.salooniveryvells.Utill.VarList;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO) {

        try {
            // 1. Create booking
            ResponseDTO response = bookingService.createBooking(bookingDTO);

            // 2. Only send email if booking was successful
            if (response.getCode() == VarList.Created) {
                        try {
                            // Get the created booking from response
                            BookingDTO booking = (BookingDTO) response.getData();
                            UserDTO customer = null;
                            // Fetch customer details by customerId
                            ResponseDTO customerResponse = userService.getUserById(booking.getCustomerId());
                            if (customerResponse.getCode() == 200) {
                               customer  = (UserDTO) customerResponse.getData();

                            }
                            ServiceDTO service = null;
                            ResponseDTO serviceResponse = serviceService.getServiceById(booking.getServiceId());
                            if (serviceResponse.getCode() == 200) {
                               service  = (ServiceDTO) serviceResponse.getData();

                            }


                            System.out.println(service);
                                sendBookingConfirmationEmail(booking, customer,service);
                } catch (MessagingException e) {
                    // Log email failure but don't fail the request
                    System.err.println("Email sending failed: " + e.getMessage());
                    // You could add this to the response message if needed
                    response.setMessage(response.getMessage() + " (Note: Confirmation email failed to send)");
                }
            }

            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    private void sendBookingConfirmationEmail(BookingDTO bookingDTO ,UserDTO user,ServiceDTO service) throws MessagingException {
        String emailContent = buildEmailContent(bookingDTO,user,service);

        EmailDTO emailDto = new EmailDTO();
        emailDto.setTo(user.getEmail());
        emailDto.setSubject("Booking Confirmation #" + bookingDTO.getBookingId());
        emailDto.setContent(emailContent);
        emailService.sendEmail(emailDto);
    }

    private String buildEmailContent(BookingDTO dto,UserDTO user,ServiceDTO service) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fffaf5; color: #4e342e; line-height: 1.6; }" +
                "h2 { color: #a67c52; }" +
                "table { border-collapse: collapse; width: 100%; max-width: 500px; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }" +
                "td { padding: 12px; border-bottom: 1px solid #e0d6cf; }" +
                "td:first-child { font-weight: bold; width: 35%; background-color: #f8f1eb; }" +
                "p { margin: 1rem 0; }" +
                "</style></head>" +
                "<body>" +
                "<h2>Your Appointment is Confirmed!</h2>" +
                "<p>Dear " + user.getName() + ",</p>" +
                "<p>Thank you for booking with <strong>Ivory Veils Salon</strong>. Here are your appointment details:</p>" +
                "<table>" +
                "<tr><td>Appointment ID:</td><td>" + dto.getBookingId() + "</td></tr>" +
                "<tr><td>Service:</td><td>" + service.getServiceName() + "</td></tr>" +
                "<tr><td>Date & Time:</td><td>" + dto.getBookingDateTime() + "</td></tr>" +
                "<tr><td>Status:</td><td>" + dto.getStatus() + "</td></tr>" +
                "</table>" +
                "<p>You can manage your appointments anytime by logging into your Ivory Veils account.</p>" +
                "<p>We look forward to pampering you!</p>" +
                "<p>Warm regards,<br><strong>Ivory Veils Salon Team</strong></p>" +
                "</body></html>";

    }


    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getBookingsForCustomer(@Valid
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
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<?> getBookingsForProvider(@Valid
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
    public ResponseEntity<ResponseDTO> getBookingById(@Valid @PathVariable int bookingId) {
        try {
            ResponseDTO response = bookingService.getBookingById(bookingId);
            return ResponseEntity.status(HttpStatus.valueOf(response.getCode()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ResponseDTO> getBookingsByService(@Valid @PathVariable int serviceId) {
        System.out.println("fgdhfhhdh" + serviceId);
        try {
            ResponseDTO responseDTO = bookingService.getBookingsByService(serviceId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }


    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<ResponseDTO> updateBookingStatus(@Valid
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
    public ResponseEntity<ResponseDTO> updateHoursWorked(@Valid
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
    public ResponseEntity<ResponseDTO> cancelBooking(@Valid @PathVariable int bookingId) {
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