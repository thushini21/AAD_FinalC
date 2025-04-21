package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.UserDTO;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.UserService;
import com.example.salooniveryvells.Utill.JwtUtil;
import com.example.salooniveryvells.Utill.VarList;
import com.example.salooniveryvells.enums.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtTokenUtil;


    public UserDTO loadUserDetailsByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        return modelMapper.map(user,UserDTO.class);
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return authorities;
    }

    @Override
    public UserDTO searchUser(String username) {
        if (userRepository.existsByEmail(username)) {
            User user=userRepository.findByEmail(username);
            return modelMapper.map(user,UserDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public ResponseDTO getUserIdByEmail(String email) {
        try {
            int idByEmail = userRepository.findUserIdByEmailAddress(email);
            if (idByEmail == 0) {
                return new ResponseDTO(VarList.Bad_Request, "No Service Provider found", null);
            }
            return new ResponseDTO(VarList.OK, "Success", idByEmail);
        }catch (Exception e) {
            return new ResponseDTO(VarList.Internal_Server_Error, "Internal server error", null);
        }
    }

    @Override
    public ResponseDTO getAllServiceProviderIds() {
        try {
            List<Integer> providerIds = userRepository.findAllServiceProviderIds();

            if (providerIds.isEmpty()) {
                return new ResponseDTO(VarList.Bad_Request, "No service providers found", null);
            }

            return new ResponseDTO(VarList.OK, "Success", providerIds);

        } catch (Exception e) {
            return new ResponseDTO(VarList.Internal_Server_Error, "Internal server error: " + e.getMessage(), null);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Use email as the username
                user.getPassword(), // Password
                getAuthority(user) // Convert role to GrantedAuthority
        );
    }

    @Override
    public ResponseDTO getUserDocuments(int userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if documents exist
        if (user.getIdProofPath() == null && user.getAddressProofPath() == null) {
            return new ResponseDTO(VarList.Not_Found, "No documents found for user", null);
        }

        // Create response data
        Map<String, String> documents = new HashMap<>();
        documents.put("idProofPath", user.getIdProofPath());
        documents.put("addressProofPath", user.getAddressProofPath());
        documents.put("verificationStatus", user.getVerificationStatus().toString());

        return new ResponseDTO(VarList.OK, "Documents retrieved successfully", documents);
    }

    @Override
    public ResponseDTO updateVerificationStatus(int userId, String status)
            throws ResourceNotFoundException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update verification status
        user.setVerificationStatus(status);
        userRepository.save(user);
        // Return minimal response
        return new ResponseDTO(VarList.OK, "Verification status updated", null);
    }

    @Override
    public int addUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        } else {
            // Map UserDTO to User entity
            User user = modelMapper.map(userDTO, User.class);

            // Encrypt the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole(UserRole.CUSTOMER); // Default role
            }

            // Set default verification status if not provided
            if (user.getVerificationStatus() == null) {
                user.setVerificationStatus("Pending"); // Example default value
            }
            if (user.getStatus() == null) {
                user.setStatus("Active"); // Example default value
            }
            userRepository.save(user);
            // Return success response
            return VarList.Created;
        }
    }


    public int updateUserPartial(UserDTO userDTO) {
        // 1. Fetch the existing user
        Optional<User> existingUser = userRepository.findById(userDTO.getUserId());

        if (!existingUser.isPresent()) {
            return VarList.Not_Found;
        }

        User user = existingUser.get();

        // 2. Update only the fields that are present in the DTO
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getServiceArea() != null) {
            user.setServiceArea(userDTO.getServiceArea());
        }
        if (userDTO.getAdminLevel() != null) {
            user.setAdminLevel(userDTO.getAdminLevel());
        }
        if (userDTO.getIdProofPath() != null) {
            user.setIdProofPath(userDTO.getIdProofPath());
        }
        if (userDTO.getAddressProofPath() != null) {
            user.setAddressProofPath(userDTO.getAddressProofPath());
        }

        // 3. Save the updated user
        userRepository.save(user);

        return VarList.Updated;
    }
    @Override
    public ResponseDTO toggleUserStatus(int userId) {
        // Check if user exists
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseDTO(VarList.Not_Found, "User not found with id: " + userId, null);
        }

        User user = optionalUser.get();

        // Toggle the status
        String newStatus = user.getStatus().equals("Active")
                ? "Inactive"
                : "Active";

        user.setStatus(newStatus);
        userRepository.save(user);

        // Return success response
        return new ResponseDTO(VarList.OK, "User status updated successfully", newStatus);
    }

    @Override
    public ResponseDTO deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseDTO(VarList.Not_Found, "User not found with id: " + userId, null);
        }

        // Delete the user
        userRepository.deleteById(userId);

        // Return success response
        return new ResponseDTO(VarList.OK, "User deleted successfully", null);
    }

    @Override
    public int changePassword(String token, String currentPassword, String newPassword) {
        // Extract username from token
        String username = jwtTokenUtil.getUsernameFromToken(token);

        // Find user by username
        User user = userRepository.findByEmail(username);


        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return VarList.Unauthorized;
        }

        // Validate new password (add your own validation rules)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return VarList.Bad_Request;
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return VarList.Updated;
    }

    @Override
    public ResponseDTO getAllUsers() {
        System.out.println("**********************************************************");
        List<User> user = userRepository.findAll();
        List<UserDTO> dtos = new ArrayList<>();

        for (User users : user) {
            UserDTO dto = modelMapper.map(users, UserDTO.class);

            dtos.add(dto);
        }
        System.out.println(dtos);
        return new ResponseDTO(200, "Success", dtos);
    }

    @Override
    public ResponseDTO getUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);

        UserDTO dto = modelMapper.map(user, UserDTO.class);

        return new ResponseDTO(200, "Success", dto);
    }
    

}