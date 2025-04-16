package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.ServiceDTO;
import com.example.salooniveryvells.Service.ServiceService;
import com.example.salooniveryvells.Util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@RestController
@RequestMapping(name = "api/v1/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> addService(@RequestPart("serviceDTO") ServiceDTO serviceDTO,
                                                  @RequestPart("file") MultipartFile file) {


        System.out.println(serviceDTO);
        try {
            String imagePath = null;
            if (!file.isEmpty()) {
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                String uploadDir = "FrontEnd/view/uploads/";

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Path path = Paths.get(uploadDir + filename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                imagePath = (filename);
            }
            // Set image path in DTO
            serviceDTO.setImage(imagePath);

            // Save to database
            int result = serviceService.addService(serviceDTO);

            if (result == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Service created", serviceDTO));
            } else {
                return ResponseEntity.status(HttpStatus.valueOf(result))
                        .body(new ResponseDTO(result, "Failed to create service", null));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ResponseDTO> getServicesByCategoryId(
            @PathVariable int categoryId) {
        try {
            ResponseDTO responseDTO = serviceService.getServicesByCategoryId(categoryId);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }

    }


    @GetMapping
    public ResponseEntity<ResponseDTO> getAllServices() {
        ResponseDTO response = serviceService.getAllServices();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ResponseDTO> getServiceById(@PathVariable int serviceId) {
        try {
            ResponseDTO responseDTO = serviceService.getServiceById(serviceId);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{serviceId}/has-bookings")
    public ResponseEntity<Boolean> hasAssociatedBookings(@PathVariable int serviceId) {
        boolean hasBookings = serviceService.hasAssociatedBookings(serviceId);
        return ResponseEntity.ok(hasBookings);
    }
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateService(
            @RequestPart("serviceDTO") ServiceDTO serviceDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            String uploadDir = "FrontEnd/view/uploads/";

            // Handle image update
            if (file != null && !file.isEmpty()) {
                // Generate new filename
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

                // Ensure directory exists
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Save new file
                Path path = Paths.get(uploadDir + filename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Delete old image if exists
                if (serviceDTO.getImage() != null) {
                    Path oldImagePath = Paths.get(uploadDir + serviceDTO.getImage());
                    Files.deleteIfExists(oldImagePath);
                }

                serviceDTO.setImage(filename);
            }

            // Update service
            int result = serviceService.updateService(serviceDTO);

            if (result == VarList.Updated) {
                return ResponseEntity.ok()
                        .body(new ResponseDTO(VarList.Updated, "Service updated", serviceDTO));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Service not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(result, "Update failed", null));
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "File error: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }


    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ResponseDTO> deleteService(@PathVariable int serviceId) {
        ResponseDTO response = serviceService.deleteService(serviceId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }






}
