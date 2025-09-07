package com.example.salooniveryvells.Controller;

import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.ChangePasswordRequestDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.UserDTO;
import com.example.salooniveryvells.Service.UserService;
import com.example.salooniveryvells.Utill.JwtUtil;
import com.example.salooniveryvells.Utill.VarList;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@CrossOrigin
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> addUser(@Valid @RequestBody UserDTO userDTO) {
        System.out.println("register");
        System.out.println(userDTO.getEmail());
        System.out.println(userDTO.getName());
        System.out.println(userDTO.getRole());
        try {
            int res = userService.addUser(userDTO);
            switch (res) {
                case VarList.Created -> {
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", userDTO));
                }
                case VarList.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Email Already Used", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @GetMapping("/allProviderIds")
    @PreAuthorize("hasAuthority('SERVICE_PROVIDER')")
    public ResponseEntity<ResponseDTO> getAllCategoryIds() {
        try {
            ResponseDTO responseDTO = userService.getAllServiceProviderIds();
            System.out.println(responseDTO);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/getidbyemail")
    public ResponseEntity<ResponseDTO> getIdByEmail(@Valid
            @RequestParam String email) {
        try {
            ResponseDTO responseDTO = userService.getUserIdByEmail(email);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }
    @PatchMapping("/{userId}/verification")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateVerificationStatus(@Valid
            @PathVariable int userId,
            @RequestParam String status) {

        try {
            ResponseDTO responseDTO = userService.updateVerificationStatus(
                    userId,
                    status
            );
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Verification status updated", responseDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Invalid status value", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> updateUser(@Valid
            @RequestPart("userDTO") UserDTO userDTO,
            @RequestPart(value = "idProof", required = false) MultipartFile idProof,
            @RequestPart(value = "addressProof", required = false) MultipartFile addressProof) {

        try {
            String uploadDir = "FrontEnd/view/uploads/";


            if (idProof != null && !idProof.isEmpty()) {
                String filename = UUID.randomUUID().toString() + "_" + idProof.getOriginalFilename();
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                Path path = Paths.get(uploadDir + filename);
                Files.copy(idProof.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                if (userDTO.getIdProofPath() != null) {
                    Path oldFilePath = Paths.get(uploadDir + userDTO.getIdProofPath());
                    Files.deleteIfExists(oldFilePath);
                }
                userDTO.setIdProofPath(filename);
            }


            if (addressProof != null && !addressProof.isEmpty()) {
                String filename = UUID.randomUUID().toString() + "_" + addressProof.getOriginalFilename();
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                Path path = Paths.get(uploadDir + filename);
                Files.copy(addressProof.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                if (userDTO.getAddressProofPath() != null) {
                    Path oldFilePath = Paths.get(uploadDir + userDTO.getAddressProofPath());
                    Files.deleteIfExists(oldFilePath);
                }
                userDTO.setAddressProofPath(filename);
            }


            int result = userService.updateUserPartial(userDTO);

            if (result == VarList.Updated) {
                return ResponseEntity.ok()
                        .body(new ResponseDTO(VarList.Updated, "User updated", userDTO));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
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
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@Valid @PathVariable int userId) {
        ResponseDTO response = userService.deleteUser(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        try {
        ResponseDTO responseDTO = userService.getAllUsers();
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseDTO> changePassword(@Valid
            @RequestBody ChangePasswordRequestDTO request,
            @RequestHeader("Authorization") String token) {

        try {

            String jwtToken = token.substring(7);

            int result = userService.changePassword(jwtToken, request.getCurrentPassword(), request.getNewPassword());

            if (result == VarList.Updated) {
                return ResponseEntity.ok()
                        .body(new ResponseDTO(VarList.Updated, "Password changed successfully", null));
            } else if (result == VarList.Unauthorized) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO(VarList.Unauthorized, "Current password is incorrect", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(result, "Failed to change password", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/documents")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getUserDocuments(@PathVariable int userId) {
        try {
            ResponseDTO responseDTO = userService.getUserDocuments(userId);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> toggleUserStatus(@PathVariable int userId) {
        ResponseDTO responseDTO = userService.toggleUserStatus(userId);


        HttpStatus httpStatus;
        switch (responseDTO.getCode()) {
            case VarList.OK:
                httpStatus = HttpStatus.OK;
                break;
            case VarList.Not_Found:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(httpStatus).body(responseDTO);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable int userId) {
        try {
            ResponseDTO responseDTO = userService.getUserById(userId);
            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "Success", responseDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }
    @GetMapping(value = "/get")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<ResponseDTO> get(){
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}