package nextstep.org.apache.coyote.http11;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.LoggerFactory;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.logging.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

//    @Test
//    void process() {
//        // given
//        final var socket = new StubSocket();
//        final var processor = new Http11Processor(socket);
//
//        // when
//        processor.process(socket);
//
//        // then
//        var expected = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "",
//                "Hello world!");
//
//        assertThat(socket.output()).isEqualTo(expected);
//    }

    @DisplayName("루트 주소로 접속시, index.html 파일을 불러온다.")
    @Test
    void rootPage() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = ClassLoader.getSystemResource("static/index.html");
        final Path path = Path.of(resource.toURI());

        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }


    @DisplayName("/index.html 페이지 요청시 응답으로 반환된다.")
    @Test
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = ClassLoader.getSystemResource("static/index.html");
        final Path path = Path.of(resource.toURI());

        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("CSS 파일 로드가 정상적으로 수행된다.")
    @Test
    void loadCss() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        // when
        processor.process(socket);
        // then
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n";
        assertThat(socket.output()).startsWith(expected);
    }

    @DisplayName("JS 파일 로드가 정상적으로 수행된다.")
    @Test
    void loadJs() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */* ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        // when
        processor.process(socket);
        // then
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: application/javascript;charset=utf-8 \r\n";

        assertThat(socket.output()).startsWith(expected);
    }

    @DisplayName("파일 확장자가 없는 경우 기본 확장자인 .html로 응답한다.")
    @Test
    void defaultExtension() throws IOException, URISyntaxException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = ClassLoader.getSystemResource("static/index.html");
        final Path path = Path.of(resource.toURI());

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " +  Files.readAllBytes(path).length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("Query String과 함께 '/login' 요청시, Query String 파싱하여 정상 응답 보낸다.")
    @Test
    void loginWithQueryString() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n";
        assertThat(socket.output()).startsWith(expected);
    }

    @DisplayName("Query String과 함께 '/login' 요청시, User 정보를 로그로 남긴다.")
    @Test
    void loginWithLog() {
        // given
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        final Logger logger = (Logger) LoggerFactory.getLogger(LoginHandler.class);
        logger.addAppender(appender);
        appender.start();

        // when
        final String httpRequest= String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        final List<ILoggingEvent> logs = appender.list;
        final String message = logs.get(0).getFormattedMessage();
        final Level level = logs.get(0).getLevel();

        // then
        assertThat(message).isEqualTo("User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}");
        assertThat(level).isEqualTo(INFO);
    }
}
