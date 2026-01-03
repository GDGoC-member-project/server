package com.gdgoc.member.controller;

import com.gdgoc.member.BaseResponse;
import com.gdgoc.member.dto.ProjectRequest;
import com.gdgoc.member.dto.ProjectResponse;
import com.gdgoc.member.dto.ProjectSummaryResponse;
import com.gdgoc.member.dto.ProjectUpdateRequest;
import com.gdgoc.member.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<ProjectResponse> getProjectById(@PathVariable String projectId) {
        ProjectResponse result = projectService.getProjectById(projectId);
        return BaseResponse.success(result);
    }

    @PostMapping
    public BaseResponse<Void> createProject(@RequestBody ProjectRequest request) {
        String ownerId = currentUserService.requireUser().userId();
        projectService.createProject(ownerId, request);
        return BaseResponse.success();
    }

    @PatchMapping("/{projectId}")
    public BaseResponse<Void> updateProject(@PathVariable String projectId, @RequestBody ProjectUpdateRequest request) {
        String ownerId = currentUserService.requireUser().userId();
        projectService.updateProject(ownerId, projectId, request);
        return BaseResponse.success();
    }

    @DeleteMapping("/{projectId}")
    public BaseResponse<Void> deleteProject(@PathVariable String projectId) {
        String ownerId = currentUserService.requireUser().userId();
        projectService.deleteProject(ownerId, projectId);
        return BaseResponse.success();
    }
}

