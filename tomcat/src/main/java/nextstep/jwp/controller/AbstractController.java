package nextstep.jwp.controller;

import org.apache.coyote.request.startline.HttpRequest;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.response.HttpResponse;

import static org.apache.coyote.request.startline.HttpMethod.*;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.getRequestMethod();

        if (method.equals(POST)) {
            doPost(request, response);
        }
        if (method.equals(GET)) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
    protected void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
}
