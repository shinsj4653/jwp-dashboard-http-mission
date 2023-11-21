package nextstep.jwp.controller;

import org.apache.coyote.request.startline.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
