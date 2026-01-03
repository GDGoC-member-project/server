package com.gdgoc.member.repository;
import com.gdgoc.member.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
