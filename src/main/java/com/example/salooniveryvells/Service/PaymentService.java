package com.example.salooniveryvells.Service;



import com.example.salooniveryvells.Dto.PaymentDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

import java.time.LocalDateTime;

public interface PaymentService {
    ResponseDTO createPayment(PaymentDTO paymentDTO);
    ResponseDTO getAllPayments();
    ResponseDTO getPaymentById(int paymentId);
    ResponseDTO getPaymentByBookingId(int bookingId);
    ResponseDTO updatePayment(int paymentId, PaymentDTO paymentDTO);
    ResponseDTO deletePayment(int paymentId);
    ResponseDTO updatePaymentDetails(int paymentId, Double finalAmount, String  status, LocalDateTime paymentDate);
}