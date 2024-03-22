package org.zatsit.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.zatsit.dto.PostDto;

@ApplicationScoped
public class PostingKafkaAdapter {

    @Inject
    @Channel("posting")
    Emitter<String> emitter;

    public void sendToKafka(String action, PostDto postDto) {
        String message = """
                {"action":%s, %s}"""
                .formatted(action, postDto.toString());
        emitter.send(message);
    }
}
