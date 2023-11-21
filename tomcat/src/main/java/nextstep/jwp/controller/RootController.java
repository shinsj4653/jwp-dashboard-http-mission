package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.request.startline.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;

import static org.apache.coyote.response.StatusCode.OK;

public class RootController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String requestPath = request.getRequestPath();

        response.setResponse(OK, ContentType.from(requestPath), requestPath);
        response.print();
    }
}
