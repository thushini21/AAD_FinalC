package com.example.salooniveryvells.Repo;

import com.example.salooniveryvells.Enum.UserRole;
import com.example.salooniveryvells.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by email
    User findByEmail(String email);

    // Check if user exists by email
    boolean existsByEmail(String email);

    // Find users by role (e.g., CUSTOMER, MANAGER, ADMIN)
    List<User> findByRole(UserRole role);

    // Find users by verification status
    List<User> findByVerificationStatus(String verificationStatus);

    // Find user by userId
    User findByUserId(int userId);

    // Find all user emails (if needed, return List<String>)
    @Query(value = "SELECT email FROM user", nativeQuery = true)
    List<String> getAllEmails();

    // Get all manager user IDs
    @Query("SELECT u.userId FROM User u WHERE u.role = 'MANAGER'")
    List<Integer> findAllManagerIds();

    // Get userId by email
    @Query("SELECT u.userId FROM User u WHERE u.email = :email")
    int findUserIdByEmailAddress(@Param("email") String email);
}
