package org.zatsit.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostDto(
        UUID uuid,
        UserDto user,
        String message,
        String imageUrl) {
}
