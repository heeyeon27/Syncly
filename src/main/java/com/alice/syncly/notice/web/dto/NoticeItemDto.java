package com.alice.syncly.notice.web.dto;

import com.alice.syncly.notice.domain.Notice;

import java.time.format.DateTimeFormatter;

public class NoticeItemDto {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Long id;
    private final String title;
    private final String content;
    private final String authorName;
    private final Long authorMemberId;
    private final String createdAtLabel;
    private final String projectName;
    private final Long projectId;

    public NoticeItemDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.authorName = notice.getAuthor().getName();
        this.authorMemberId = notice.getAuthor().getId();
        this.createdAtLabel = notice.getCreatedAt() != null
                ? notice.getCreatedAt().format(FMT) : "-";
        this.projectName = notice.getProject() != null ? notice.getProject().getName() : "";
        this.projectId   = notice.getProject() != null ? notice.getProject().getId() : null;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public Long getAuthorMemberId() { return authorMemberId; }
    public String getCreatedAtLabel() { return createdAtLabel; }
    public String getProjectName() { return projectName; }
    public Long getProjectId() { return projectId; }
}
