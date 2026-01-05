package com.gdgoc.member.domain.project;

import com.gdgoc.member.domain.project.Recruitment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "project")
public class Project {
    @Id
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 100)
    private String description;

    @Column(name = "link_url")
    private String externalUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "project_recruitments", joinColumns = @JoinColumn(name = "project_id"))
    private List<Recruitment> recruitments = new ArrayList<>();

    @Column
    private LocalDate deadline;

    @Column(name = "post_date", nullable = false)
    private LocalDateTime postDate;


    // 서비스 계층에서 사용할 생성자
    public Project(UUID ownerId, String title, String description, String externalUrl, List<Recruitment> recruitments, String content, LocalDate deadline) {
        this.projectId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.externalUrl = externalUrl;
        this.content = content;
        if (recruitments != null) {
            this.recruitments.addAll(recruitments);
        }
        this.deadline = deadline;

    }

    @PrePersist
    public void prePersist() {
        this.postDate = LocalDateTime.now();
    }

    public void update(String title, String description, String externalUrl, List<Recruitment> recruitments, String content, LocalDate deadline) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (externalUrl != null) this.externalUrl = externalUrl;
        if (content != null) this.content = content;
        if (recruitments != null) {
            this.recruitments.clear();
            this.recruitments.addAll(recruitments);
        }
        if (deadline != null) this.deadline = deadline;
    }
}
