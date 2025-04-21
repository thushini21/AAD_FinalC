package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ServiceDTO;
import com.example.salooniveryvells.Entity.Category;
import com.example.salooniveryvells.Entity.Service;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.BookingRepository;
import com.example.salooniveryvells.Repo.CategoryRepository;
import com.example.salooniveryvells.Repo.ServiceRepository;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.ServiceService;
import com.example.salooniveryvells.Utill.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private final BookingRepository bookingRepository;

    public ServiceServiceImpl(BookingRepository bookingRepository,
                              ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public boolean hasAssociatedBookings(int serviceId) {
        return bookingRepository.existsByServiceServiceId(serviceId);
    }

    @Override
    public int addService(ServiceDTO serviceDTO) {
        try {
            // Validate required fields
            if (serviceDTO.getServiceName() == null || serviceDTO.getServiceName().trim().isEmpty()) {
                return VarList.Bad_Request; // 400 - Missing name
            }

            // Fetch related entities
            Category category = categoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            User serviceProvider = userRepository.findById(serviceDTO.getServiceProviderId())
                    .orElseThrow(() -> new RuntimeException("Service provider not found"));

            // Create and populate service entity
            Service service = new Service();
            service.setServiceName(serviceDTO.getServiceName());
            service.setDescription(serviceDTO.getDescription()); // Fixed: was using getName() twice
            service.setFixedPrice(serviceDTO.getFixedPrice());
            service.setHourlyRate(serviceDTO.getHourlyRate());
            service.setImage(serviceDTO.getImage());

            // Set relationships with actual entities
            service.setCategory(category);
            service.setServiceProvider(serviceProvider);

            // Save and verify
            Service savedService = serviceRepository.save(service);

            if (savedService.getServiceId() != 0) { // Changed to null check for Long
                return VarList.Created; // 201 - Success
            } else {
                return VarList.Internal_Server_Error; // 500 - Save failed
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return VarList.Internal_Server_Error; // 500
        }
    }

    @Override
    public ResponseDTO getServicesByCategoryId(int categoryId) {
        List<Service> services = serviceRepository.findByCategoryId(categoryId);
        List<ServiceDTO> dtos = new ArrayList<>();

        for (Service service : services) {
            ServiceDTO dto = new ServiceDTO();
            dto.setServiceId(service.getServiceId());
            dto.setServiceName(service.getServiceName());
            dto.setDescription(service.getDescription());
            dto.setFixedPrice(service.getFixedPrice());
            dto.setHourlyRate(service.getHourlyRate());
            dto.setCategoryId(service.getCategory().getCategoryId());
            dto.setServiceProviderId(service.getServiceProvider().getUserId());

            // Fix image URL construction
            if (service.getImage() != null) {
                dto.setImage(service.getImage());
            } else {
                dto.setImage("");
            }

            dtos.add(dto);
        }

        return new ResponseDTO(200, "Success", dtos);
    }

    @Override
    public ResponseDTO getAllServices() {
        List<ServiceDTO> serviceList = modelMapper.map(serviceRepository.findAll(),
                new TypeToken<List<ServiceDTO>>() {}.getType());
        return new ResponseDTO(200, "Services retrieved successfully", serviceList);
    }

    @Override
    public ResponseDTO getServiceById(int serviceId) {
        Service service = serviceRepository.findByServiceId(serviceId);

        ServiceDTO dto = modelMapper.map(service, ServiceDTO.class);

        // Handle the image field separately if needed
        if (dto.getImage() == null) {
            dto.setImage("");
        }
        return new ResponseDTO(200, "Success", dto);
    }

    @Override
    public int updateService(ServiceDTO serviceDTO) {
        try {
            // Find existing service
            Service existingService = serviceRepository.findById(serviceDTO.getServiceId())
                    .orElse(null);

            if (existingService == null) {
                return VarList.Not_Found;
            }

            // Update fields
            existingService.setServiceName(serviceDTO.getServiceName());
            existingService.setDescription(serviceDTO.getDescription());
            existingService.setFixedPrice(serviceDTO.getFixedPrice());
            existingService.setHourlyRate(serviceDTO.getHourlyRate());

            // Update image path if provided
            if (serviceDTO.getImage() != null) {
                existingService.setImage(serviceDTO.getImage());
            }

            // Update category if changed
            if (existingService.getCategory().getCategoryId() != serviceDTO.getCategoryId()) {
                Category category = categoryRepository.findById(serviceDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                existingService.setCategory(category);
            }

            serviceRepository.save(existingService);
            return VarList.Updated;

        } catch (Exception e) {
            System.err.println("Update error: " + e.getMessage());
            return VarList.Internal_Server_Error;
        }
    }
    @Override
    public ResponseDTO deleteService(int serviceId) {
        if (!serviceRepository.existsById(serviceId)) {
            return new ResponseDTO(404, "Service not found with id: " + serviceId, null);
        }
        serviceRepository.deleteById(serviceId);
        return new ResponseDTO(200, "Service deleted successfully", null);
    }
}