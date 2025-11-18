package com.zxylearn.chat_auth_service.pojo.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
}
