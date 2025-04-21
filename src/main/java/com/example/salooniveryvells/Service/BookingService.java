package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.BookingDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

import java.time.LocalDate;

public interface BookingService {
    ResponseDTO createBooking(BookingDTO bookingDTO);
    ResponseDTO getBookingById(int bookingId);
    ResponseDTO getBookingsByCustomer(int customerId);
    ResponseDTO getBookingsByService(int serviceId);
    ResponseDTO getBookingsByServiceProvider(int providerId);
    ResponseDTO updateBookingStatus(int bookingId, String status);
    ResponseDTO updateHoursWorked(int bookingId, double hours,String status);
    ResponseDTO cancelBooking(int bookingId);
    ResponseDTO getActiveBookings();
    ResponseDTO getBookingsForCustomer(int customerId, String status, LocalDate fromDate, LocalDate toDate);
    ResponseDTO getBookingsForProvider(int providerId, String status, LocalDate fromDate, LocalDate toDate);
}