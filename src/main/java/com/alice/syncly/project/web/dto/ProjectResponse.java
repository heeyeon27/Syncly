package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.Project;

public class ProjectResponse {

    private Long id;
    private String name;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
