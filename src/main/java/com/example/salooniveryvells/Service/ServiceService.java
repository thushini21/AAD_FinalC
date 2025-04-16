package com.example.salooniveryvells.Service;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ServiceDTO;

public interface ServiceService {

    int addService(ServiceDTO serviceDTO);

    ResponseDTO getServicesByCategoryId(int categoryId);

    ResponseDTO getAllServices();

    ResponseDTO getServiceById(int serviceId);

    boolean hasAssociatedBookings(int serviceId);

    int updateService(ServiceDTO serviceDTO);

    ResponseDTO deleteService(int serviceId);

}
