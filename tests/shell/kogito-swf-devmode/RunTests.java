///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.slf4j:slf4j-simple:2.0.6

// Junit console to start the test engine:
//DEPS org.junit.platform:junit-platform-console:1.8.2

// engine to run the tests (tests are written with Junit5):
//DEPS org.junit.jupiter:junit-jupiter-engine:5.8.2

// testcontainers
//DEPS org.testcontainers:testcontainers:1.17.6
//DEPS org.testcontainers:junit-jupiter:1.17.6

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.platform.console.options.CommandLineOptions;
import org.junit.platform.console.tasks.ConsoleTestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RunTests {

    private static Logger LOGGER = LoggerFactory.getLogger(RunTests.class);

    private Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);

    @Container
    private GenericContainer devModeImage = new GenericContainer(getTestImage())
            .withFileSystemBind(getScriptDirPath() + "/resources", "/home/kogito/serverless-workflow-project/src/main/resources", BindMode.READ_ONLY)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/jsongreet"))
            .withLogConsumer(logConsumer);

    @Test
    public void testBuiltContainerAnswerCorrectly() throws URISyntaxException, IOException, InterruptedException {
        devModeImage.start();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + devModeImage.getHost() + ":" + devModeImage.getFirstMappedPort() + "/jsongreet"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers
                        .ofString("{\"workflowdata\" : {\"name\": \"John\", \"language\": \"English\"}}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    public static void main(String... args) throws Exception {
        CommandLineOptions options = new CommandLineOptions();
        options.setSelectedClasses(Collections.singletonList(RunTests.class.getName()));
        options.setReportsDir(Paths.get(getOutputDir()));
        new ConsoleTestExecutor(options).execute(new PrintWriter(System.out));
    }

    static String getTestImage() {
        return System.getenv("TEST_IMAGE");
    }

    static String getOutputDir() {
        return System.getenv("OUTPUT_DIR");
    }

    static String getScriptDirPath() {
        return System.getenv("TESTS_SCRIPT_DIR_PATH");
    }
}