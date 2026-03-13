package com.alice.syncly.auth.web.dto;

public class MemberCreateRequest {

    private String email;
    private String password;
    private String name;
    private String slackUserId;

    public MemberCreateRequest() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlackUserId() { return slackUserId; }
    public void setSlackUserId(String slackUserId) { this.slackUserId = slackUserId; }
}
