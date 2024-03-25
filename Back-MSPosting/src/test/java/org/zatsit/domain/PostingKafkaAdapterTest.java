package org.zatsit.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.providers.extension.EmitterImpl;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.zatsit.KafkaTestResourceLifecycleManager;
import org.zatsit.dto.PostDto;
import org.zatsit.dto.UserDto;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
class PostingKafkaAdapterTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @InjectSpy
    PostingKafkaAdapter postingKafkaAdapter;

    ObjectMapper objectMapper = new ObjectMapper();

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
    @Order(1)
    void sendToKafka_NoUUID() throws JsonProcessingException {
        InMemorySink<String> results = connector.sink("posting");

        PostDto postDto = PostDto.builder()
                .message("Test Message")
                .imageUrl("http://img.url")
                .user(UserDto.builder()
                        .uuid(UUID.fromString("018e6573-b0e4-7373-ab17-602e02dde1e5"))
                        .username("Test Username")
                        .profilePictureUrl("http://profile.pic")
                        .build())
                .build();

        UUID postUuid = postingKafkaAdapter.sendToKafka("ACTION", postDto);

        Assertions.assertEquals(1, results.received().size());

        String payload = results.received().getFirst().getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        Assertions.assertNotNull(jsonNode.get("payload").get("uuid"));

        postDto.setUuid(UUID.fromString(jsonNode.get("payload").get("uuid").textValue()));
        Assertions.assertEquals(postUuid, postDto.getUuid());

        String expectedResult = STR."{\"action\":\"ACTION\",\"payload\":\{objectMapper.writeValueAsString(postDto)}}";
        Assertions.assertEquals(expectedResult, results.received().getFirst().getPayload());
    }

    @Test
    @Order(2)
    void sendToKafka_WithUUID() throws JsonProcessingException {
        InMemorySink<String> results = connector.sink("posting");

        UUID entryUuid = UUID.fromString("018e6572-1479-7ad2-889d-98a38c3bddd0");
        PostDto postDto = PostDto.builder()
                .uuid(entryUuid)
                .message("Test Message")
                .imageUrl("http://img.url")
                .user(UserDto.builder()
                        .uuid(UUID.fromString("018e6573-b0e4-7373-ab17-602e02dde1e5"))
                        .username("Test Username")
                        .profilePictureUrl("http://profile.pic")
                        .build())
                .build();

        String expectedResult = STR."{\"action\":\"ACTION\",\"payload\":\{objectMapper.writeValueAsString(postDto)}}";

        UUID postUuid = postingKafkaAdapter.sendToKafka("ACTION", postDto);

        Assertions.assertEquals(postUuid, entryUuid);
        Assertions.assertEquals(2, results.received().size());
        Assertions.assertEquals(expectedResult, results.received().get(1).getPayload());
    }

    @Test
    @Order(3)
    void sendToKafka_NoKafka() {
        postingKafkaAdapter.emitter = Mockito.mock(EmitterImpl.class);

        doThrow(IllegalStateException.class)
                .when(postingKafkaAdapter.emitter).send(Mockito.anyString());

        PostDto postDto = PostDto.builder()
                .uuid(UUID.fromString("4506d538-65af-4c2f-8ba1-bbd74430ecc6"))
                .message("Test Message")
                .imageUrl("http://img.url")
                .user(UserDto.builder()
                        .uuid(UUID.fromString("4f62b055-84a0-411f-8327-d20a635029e6"))
                        .username("Test Username")
                        .profilePictureUrl("http://profile.pic")
                        .build())
                .build();

        Assertions.assertThrows(IllegalStateException.class,
                () -> postingKafkaAdapter.sendToKafka("Action", postDto));
    }
}