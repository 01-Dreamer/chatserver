package com.zxylearn.chat_auth_service.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    @NotBlank
    @Size(min = 2, max = 25)
    private String nickname;

    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String username;
    
    @NotBlank
    @Size(min = 10, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String password;
}
