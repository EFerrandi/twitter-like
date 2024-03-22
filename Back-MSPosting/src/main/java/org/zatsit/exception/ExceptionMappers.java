package org.zatsit.exception;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    @ServerExceptionMapper
    public RestResponse<String> mapException(InvalidDtoException invalidDtoException) {
        return RestResponse.status(RestResponse.Status.BAD_REQUEST, STR."Invalid post: \{invalidDtoException.getMessage()}");
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(UnavailableResourceException unavailableResourceException) {
        return RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, "Kafka resource is unavailable");
    }
}
