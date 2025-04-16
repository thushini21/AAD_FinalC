package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.PaymentDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.PaymentService;
import com.example.salooniveryvells.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        System.out.println("///////////////////////////////////////" + paymentDTO);
        ResponseDTO responseDTO = paymentService.createPayment(paymentDTO);
        return ResponseEntity.ok()
                .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllPayments() {
        ResponseDTO response = paymentService.getAllPayments();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> getPaymentById(@PathVariable int paymentId) {
        ResponseDTO response = paymentService.getPaymentById(paymentId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
    @GetMapping("get-by-booking/{bookingId}")
    public ResponseEntity<ResponseDTO> getPaymentByBookingId(@PathVariable int bookingId) {
        System.out.println(bookingId);
        System.out.println("-----------------------------------------");
        try {
        ResponseDTO responseDTO = paymentService.getPaymentByBookingId(bookingId);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> updatePayment(@PathVariable int paymentId, @RequestBody PaymentDTO paymentDTO) {
        ResponseDTO responseDTO = paymentService.updatePayment(paymentId, paymentDTO);
        return ResponseEntity.ok()
                .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> updatePayment(
            @PathVariable int paymentId,
            @RequestBody Map<String, Object> updates) {

        try {
            // Manually parse the fields from the map
            Double finalAmount = updates.containsKey("finalAmount") ?
                    Double.valueOf(updates.get("finalAmount").toString()) : null;
            String status = updates.containsKey("status") ?
                    updates.get("status").toString() : null;

            // Handle date parsing manually
            LocalDateTime paymentDate = null;
            if (updates.containsKey("paymentDate")) {
                String dateStr = updates.get("paymentDate").toString();
                paymentDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
            }

            // Create a PaymentDTO with the parsed values
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setFinalAmount(finalAmount);
            paymentDTO.setStatus(status);
            paymentDTO.setPaymentDate(paymentDate);

            ResponseDTO responseDTO = paymentService.updatePaymentDetails(
                    paymentId,
                    paymentDTO.getFinalAmount(),
                    paymentDTO.getStatus(),
                    paymentDTO.getPaymentDate());

            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Payment updated successfully", responseDTO.getData()));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Invalid date format. Use ISO-8601 format (e.g., 2023-05-15T12:34:56.789Z)", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> deletePayment(@PathVariable int paymentId) {
        ResponseDTO response = paymentService.deletePayment(paymentId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}