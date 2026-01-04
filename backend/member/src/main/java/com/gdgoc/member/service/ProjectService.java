package com.gdgoc.member.service;

import com.gdgoc.member.domain.Project;
import com.gdgoc.member.domain.Recruitment;
import com.gdgoc.member.projectdto.*;
import com.gdgoc.member.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;


    @Transactional
    public UUID createProject(UUID ownerId, ProjectRequest requestDto) {

        List<Recruitment> recruitments = requestDto.recruitments() == null
                ? new ArrayList<>()
                : requestDto.recruitments().stream()
                .map(r -> new Recruitment(
                        r.position(),
                        r.description(),
                        r.filled(),
                        r.max()
                ))
                .toList();

        Project project = new Project(
                ownerId,
                requestDto.title(),
                requestDto.description(),
                requestDto.externalUrl(),
                recruitments,
                requestDto.content(),
                requestDto.deadline()
        );
        Project saved = projectRepository.save(project);
        return saved.getProjectId();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트를 찾을 수 없습니다: " + projectId));

        return new ProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectSummaryResponse::new)
                .toList();
    }

    @Transactional
    public void updateProject(UUID ownerId, UUID projectId, ProjectUpdateRequest requestDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 프로젝트를 찾을 수 없습니다: " + projectId));

        // 본인 글만 수정
        if (!ownerId.equals(project.getOwnerId())) {
            throw new IllegalArgumentException("본인 프로젝트만 수정할 수 있습니다.");
        }

        // recruitments 변환 (dto -> embeddable)
        List<Recruitment> recruitments = null;
        if (requestDto.recruitments() != null) {
            recruitments = requestDto.recruitments().stream()
                    .map(r -> new Recruitment(
                            r.position(),
                            r.description(),
                            r.filled(),
                            r.max()
                    ))
                    .toList();
        }

        project.update(
                requestDto.title(),
                requestDto.description(),
                requestDto.externalUrl(),
                recruitments,
                requestDto.content(),
                requestDto.deadline()
        );
    }


    @Transactional
    public void deleteProject(UUID ownerId, UUID projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 게시글을 찾을 수 없습니다: "+ projectId));

        if (!ownerId.equals(project.getOwnerId())) {
            throw new IllegalArgumentException("본인 프로젝트만 삭제할 수 있습니다.");
        }

        projectRepository.delete(project);
    }
}
