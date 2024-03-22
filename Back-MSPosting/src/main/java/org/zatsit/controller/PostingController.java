package org.zatsit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.common.NotImplementedYet;
import org.zatsit.domain.PostingKafkaAdapter;
import org.zatsit.dto.PostDto;
import org.zatsit.exception.InvalidDtoException;
import org.zatsit.exception.UnavailableResourceException;

import java.util.UUID;

@Path("/posting")
public class PostingController {

    @Inject
    PostingKafkaAdapter postingKafkaAdapter;

    @Inject
    Validator validator;
    
    @GET
    @Path("/allPosts")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<String> getPosts() {
        throw new NotImplementedYet();
    }

    @PUT
    @Path("/createPost")
    @Consumes({MediaType.APPLICATION_JSON})
    public RestResponse<Void> createPost(@Valid PostDto postDto) {
        if (postDto == null) {
            throw new InvalidDtoException("No post :(");
        }

        try {
            postingKafkaAdapter.sendToKafka("CREATE", postDto);
            return RestResponse.status(RestResponse.Status.CREATED);
        } catch (IllegalStateException _) {
            throw new UnavailableResourceException("Cannot create post");
        }
    }

    @POST
    @Path("/updatePost")
    public void updatePost(PostDto postDto) throws JsonProcessingException {
        postingKafkaAdapter.sendToKafka("UPDATE", postDto);
    }

    @DELETE
    @Path("/deletePost")
    public void deletePost(UUID postUuid) throws JsonProcessingException {
        postingKafkaAdapter.sendToKafka("DELETE",
                PostDto.builder().uuid(postUuid).build());
    }

}
