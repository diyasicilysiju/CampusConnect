package com.campusconnect.dto;

import com.campusconnect.entity.Role;

public class AuthResponse {

    private Long id;
    private String fullName;
    private String email;
    private String studentId;
    private Role role;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String fullName, String email, String studentId, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.studentId = studentId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
