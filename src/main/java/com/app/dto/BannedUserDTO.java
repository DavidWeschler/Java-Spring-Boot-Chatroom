package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BannedUserDTO {
    private Long id;
    private String username;
    private LocalDateTime bannedUntil;
}
