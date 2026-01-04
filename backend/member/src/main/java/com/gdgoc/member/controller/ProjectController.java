package com.gdgoc.member.controller;

import com.gdgoc.member.BaseResponse;
import com.gdgoc.member.projectdto.ProjectRequest;
import com.gdgoc.member.projectdto.ProjectResponse;
import com.gdgoc.member.projectdto.ProjectSummaryResponse;
import com.gdgoc.member.projectdto.ProjectUpdateRequest;
import com.gdgoc.member.service.ProjectService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.gdgoc.member.security.CurrentUserService; //feature/auth에 있음


import java.util.List;
import java.util.UUID;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")

public class ProjectController {

    private final ProjectService projectService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public BaseResponse<List<ProjectSummaryResponse>> getAllProjects() {
        List<ProjectSummaryResponse> result = projectService.getAllProjects();
        return BaseResponse.success(result);
    }

    @GetMapping("/{projectId}")
    public BaseResponse<ProjectResponse> getProjectById(@PathVariable UUID projectId) {
        ProjectResponse result = projectService.getProjectById(projectId);
        return BaseResponse.success(result);
    }

    @PostMapping
    public BaseResponse<UUID> createProject(@Valid @RequestBody ProjectRequest request) {
        UUID ownerId = currentUserService.requireUser().userId();
        UUID projectId = projectService.createProject(ownerId, request);
        return BaseResponse.success(projectId);
    }

    @PatchMapping("/{projectId}")
    public BaseResponse<Void> updateProject(@PathVariable UUID projectId, @Valid @RequestBody ProjectUpdateRequest request) {
        UUID ownerId = currentUserService.requireUser().userId();
        projectService.updateProject(ownerId, projectId, request);
        return BaseResponse.success();
    }

    @DeleteMapping("/{projectId}")
    public BaseResponse<Void> deleteProject(@PathVariable UUID projectId) {
        UUID ownerId = currentUserService.requireUser().userId();
        projectService.deleteProject(ownerId, projectId);
        return BaseResponse.success();
    }
}

