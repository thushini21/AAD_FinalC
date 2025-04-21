package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ServiceDTO;

public interface ServiceService {
    int addService(ServiceDTO serviceDTO);
    ResponseDTO getAllServices();
    ResponseDTO getServiceById(int serviceId);
    int updateService(ServiceDTO serviceDTO);
    boolean hasAssociatedBookings(int serviceId);
    ResponseDTO deleteService(int serviceId);
    ResponseDTO getServicesByCategoryId(int categoryId);
}