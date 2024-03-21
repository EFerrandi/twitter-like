package org.zatsit.controller;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import org.zatsit.domain.PostingKafkaAdaptor;
import org.zatsit.dto.PostDto;

import java.util.UUID;

@Path("/posting")
public class PostingController {

    @Inject
    PostingKafkaAdaptor postingKafkaAdaptor;

    @GET
    @Path("/allPosts")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> getPosts() {
        throw new NotImplementedYet();
    }

    @PUT
    @Path("/createPost")
    @Consumes({MediaType.APPLICATION_JSON, "application/json;charset=UTF-8"})
    public void createPost(PostDto postDto) {
        postingKafkaAdaptor.sendToKafka("CREATE", postDto);
    }

    @POST
    @Path("/updatePost")
    public void updatePost(PostDto postDto) {
        postingKafkaAdaptor.sendToKafka("UPDATE", postDto);
    }

    @DELETE
    @Path("/deletePost")
    public void createPost(UUID postUuid) {
        postingKafkaAdaptor.sendToKafka("DELETE",
                PostDto.builder().uuid(postUuid).build());
    }

}
