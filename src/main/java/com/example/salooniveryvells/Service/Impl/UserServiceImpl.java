package com.example.salooniveryvells.Service.Impl;

import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Dto.UserDTO;
import com.example.salooniveryvells.Enum.UserRole;
import com.example.salooniveryvells.Service.UserService;
import com.example.salooniveryvells.Util.JwtUtil;
import com.example.salooniveryvells.Util.VarList;
import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.UserRepository;
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
public class UserServiceImpl implements UserDetailsService , UserService {

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
                return new ResponseDTO(VarList.Bad_Request, "No Manager found", null);
            }
            return new ResponseDTO(VarList.OK, "Success", idByEmail);
        }catch (Exception e) {
            return new ResponseDTO(VarList.Internal_Server_Error, "Internal server error", null);
        }
    }

    @Override
    public ResponseDTO getAllManagerIds() {
        try {
            List<Integer> managerIds = userRepository.findAllManagerIds();

            if (managerIds.isEmpty()) {
                return new ResponseDTO(VarList.Bad_Request, "No managers found", null);
            }

            return new ResponseDTO(VarList.OK, "Success", managerIds);

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
                user.getEmail(),
                user.getPassword(),
                getAuthority(user)
        );
    }

    @Override
    public ResponseDTO getUserDocuments(int userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getIdProofPath() == null && user.getAddressProofPath() == null) {
            return new ResponseDTO(VarList.Not_Found, "No documents found for user", null);
        }

        Map<String, String> documents = new HashMap<>();
        documents.put("idProofPath", user.getIdProofPath());
        documents.put("addressProofPath", user.getAddressProofPath());
        documents.put("verificationStatus", user.getVerificationStatus().toString());

        return new ResponseDTO(VarList.OK, "Documents retrieved successfully", documents);
    }

    @Override
    public ResponseDTO updateVerificationStatus(int userId, String status) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setVerificationStatus(status);
        userRepository.save(user);
        return new ResponseDTO(VarList.OK, "Verification status updated", null);
    }

    @Override
    public int addUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        } else {
            User user = modelMapper.map(userDTO, User.class);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            if (user.getRole() == null) {
                user.setRole(UserRole.CUSTOMER);
            }
            if (user.getVerificationStatus() == null) {
                user.setVerificationStatus("Pending");
            }
            if (user.getStatus() == null) {
                user.setStatus("Active");
            }
            userRepository.save(user);
            return VarList.Created;
        }
    }

    public int updateUserPartial(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findById(userDTO.getUserId());

        if (!existingUser.isPresent()) {
            return VarList.Not_Found;
        }

        User user = existingUser.get();

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

        userRepository.save(user);

        return VarList.Updated;
    }

    @Override
    public ResponseDTO toggleUserStatus(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseDTO(VarList.Not_Found, "User not found with id: " + userId, null);
        }

        User user = optionalUser.get();

        String newStatus = user.getStatus().equals("Active") ? "Inactive" : "Active";

        user.setStatus(newStatus);
        userRepository.save(user);

        return new ResponseDTO(VarList.OK, "User status updated successfully", newStatus);
    }

    @Override
    public ResponseDTO deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseDTO(VarList.Not_Found, "User not found with id: " + userId, null);
        }

        userRepository.deleteById(userId);

        return new ResponseDTO(VarList.OK, "User deleted successfully", null);
    }

    @Override
    public int changePassword(String token, String currentPassword, String newPassword) {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        User user = userRepository.findByEmail(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return VarList.Unauthorized;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return VarList.Bad_Request;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return VarList.Updated;
    }

    @Override
    public ResponseDTO getAllUsers() {
        List<User> user = userRepository.findAll();
        List<UserDTO> dtos = new ArrayList<>();

        for (User users : user) {
            UserDTO dto = modelMapper.map(users, UserDTO.class);
            dtos.add(dto);
        }
        return new ResponseDTO(200, "Success", dtos);
    }

    @Override
    public ResponseDTO getUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        return new ResponseDTO(200, "Success", dto);
    }
}
