package nextstep.jwp.controller;

import javassist.NotFoundException;
import nextstep.jwp.exception.badrequest.ExistUserException;
import nextstep.jwp.exception.notfound.NotFoundUserException;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.apache.coyote.response.StatusCode;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URISyntaxException;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.*;


public final class ControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

    private ControllerAdvice() {
    }

    public static void handle(final HttpResponse response, final Exception exception) throws Exception {
        if (exception != null) {
            log.error("Internal Server Error : {}", exception.getMessage());
            response.setResponse(INTERNAL_SERVER_ERROR, HTML, Location.from("/500.html"));
        }
        if (exception instanceof NotFoundUserException) {
            log.error("Unauthorized : {}", exception.getMessage());
            response.setResponse(UNAUTHORIZED, HTML, Location.from("/401.html"));
        }
        if (exception instanceof IllegalArgumentException || exception instanceof ExistUserException) {
            log.error("Bad Request : {}", exception.getMessage());
            response.setResponse(BAD_REQUEST, HTML, Location.from("/400.html"));
        }
        if (exception instanceof NotFoundException) {
            log.error("Not Found : {}", exception.getMessage());
            response.setResponse(NOT_FOUND, HTML, Location.from("/404.html"));
        }
        if (exception instanceof URISyntaxException) {
            log.error("URI Syntax Error : {}", exception.getMessage());
        }

        response.print();
    }
}
