package org.zatsit.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.zatsit.dto.PostDto;
import org.zatsit.exception.InvalidDtoException;

import java.util.UUID;

@ApplicationScoped
public class PostingKafkaAdapter {

    @Inject
    @Channel("posting")
    Emitter<String> emitter;

    ObjectMapper objectMapper = new ObjectMapper();

    public void sendToKafka(String action, PostDto postDto) {
        if (postDto.getUuid() == null) {
            postDto.setUuid(UUID.randomUUID());
        }

        try {
            String message = """
                    {"action":"%s","payload":%s}"""
                    .formatted(action, objectMapper.writeValueAsString(postDto));
            emitter.send(message);
        } catch (JsonProcessingException _) {
            throw new InvalidDtoException("Error while processing Post");
        }

    }
}
