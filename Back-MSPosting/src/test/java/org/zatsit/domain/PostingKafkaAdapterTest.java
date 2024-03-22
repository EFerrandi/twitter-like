package org.zatsit.domain;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.zatsit.KafkaTestResourceLifecycleManager;
import org.zatsit.dto.PostDto;
import org.zatsit.dto.UserDto;

import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
class PostingKafkaAdapterTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    PostingKafkaAdapter postingKafkaAdapter;

    @BeforeAll
    public static void switchMyChannels() {
        InMemoryConnector.switchIncomingChannelsToInMemory("posting");
        InMemoryConnector.switchOutgoingChannelsToInMemory("posting");
    }

    @AfterAll
    public static void revertMyChannels() {
        InMemoryConnector.clear();
    }

    @Test
    void sendToKafka() {
        InMemorySink<String> results = connector.sink("posting");

        PostDto postDto = PostDto.builder()
                .uuid(UUID.fromString("018e6572-1479-7ad2-889d-98a38c3bddd0"))
                .message("Test Message")
                .imageUrl("http://img.url")
                .user(UserDto.builder()
                        .uuid(UUID.fromString("018e6573-b0e4-7373-ab17-602e02dde1e5"))
                        .username("Test Username")
                        .profilPictureUrl("http://profile.pic")
                        .build())
                .build();

        String expectedResult = """
                {"action":ACTION, %s}""".formatted(postDto.toString());

        postingKafkaAdapter.sendToKafka("ACTION", postDto);

        Assertions.assertEquals(1, results.received().size());
        Assertions.assertEquals(expectedResult, results.received().getFirst().getPayload());
    }
}