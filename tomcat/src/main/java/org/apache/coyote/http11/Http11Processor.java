package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = HttpRequest.readRequest(bufferedReader);
            final HttpResponse httpResponse = HttpResponse.from(outputStream);

            handleRequest(httpRequest, httpResponse);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void handleRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws URISyntaxException {
        final Optional<Controller> controller = RequestMapping.getController(httpRequest);
        controller.ifPresent(
                (it) -> {
                    try {
                        it.service(httpRequest, httpResponse);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        if (controller.isEmpty()) {
            new ResourceController().service(httpRequest, httpResponse);
        }
    }
}
