package com.gdgoc.member.domain.profile.repository;

import com.gdgoc.member.domain.profile.entity.Profile;
import com.gdgoc.member.domain.profile.type.Part;
import com.gdgoc.member.domain.profile.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(String userId);
    boolean existsByUserId(String userId);

    List<Profile> findByPartAndRole(Part part, Role role);
    List<Profile> findByNameContainingIgnoreCase(String keyword);
}
