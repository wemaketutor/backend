package com.tutoras.tutoras.model;

import lombok.Data;

@Data
public class UserResponse {
    private UserData user;

    @Data
    public static class UserData {
        private Long id;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String password;
        private String phone;
        private String role;
    }
    
}
