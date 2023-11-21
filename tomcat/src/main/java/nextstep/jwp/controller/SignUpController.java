package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.badrequest.ExistUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.query.QueryParams;
import org.apache.coyote.request.startline.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Optional;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;
import static org.apache.coyote.response.StatusCode.OK;

public class SignUpController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws URISyntaxException {
        final QueryParams queryParams = request.getQueryParams();
        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");
        final String email = queryParams.getValueFromKey("email");

        checkAlreadyExistUser(account);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! : {}", user);

        response.setResponse(FOUND, HTML, Location.from("/index.html"));
        response.print();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws URISyntaxException {
        final String requestPath = request.getRequestPath();

        response.setResponse(OK, ContentType.from(requestPath), requestPath);
        response.print();
    }

    private static void checkAlreadyExistUser(String account) {
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            throw new ExistUserException();
        }
    }

}
