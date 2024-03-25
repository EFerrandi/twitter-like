package org.zatsit.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID uuid;

        @Valid
        UserDto user;

        String message;

        @URL(message = "Invalid image")
        String imageUrl;

        @AssertTrue(message = "No content in the post")
        private boolean isContentValid() {
                return (message != null && !message.isBlank())
                        || (imageUrl != null && !imageUrl.isBlank());
        }
}
