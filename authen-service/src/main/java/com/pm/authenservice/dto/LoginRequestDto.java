package com.pm.authenservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

public class LoginRequestDto {
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String secondName;
    @Getter
    @Setter
    private LocalDate registeredDate;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    public @NotBlank(message = "Email is required.") @Email(message = "Email should be valid email address.") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required.") @Email(message = "Email should be valid email address.") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required.") @Size(min = 8, message = "Password must be at least 8 characters long.") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required.") @Size(min = 8, message = "Password must be at least 8 characters long.") String password) {
        this.password = password;
    }
}
