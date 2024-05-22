///usr/bin/env jbang "$0" "$@" ; exit $?

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

//DEPS org.slf4j:slf4j-simple:2.0.9

// Junit console to start the test engine:
//DEPS org.junit.platform:junit-platform-console:1.10.1

// engine to run the tests (tests are written with Junit5):
//DEPS org.junit.jupiter:junit-jupiter-engine:5.10.1

// testcontainers
//DEPS org.testcontainers:testcontainers:1.19.3
//DEPS org.testcontainers:junit-jupiter:1.19.3

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.platform.console.ConsoleLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RunTests {

    private static Logger LOGGER = LoggerFactory.getLogger(RunTests.class);

    private Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);

    @Container
    private GenericContainer devModeImage = new GenericContainer(getTestImage())
            .withEnv("MAVEN_ARGS_APPEND", "-Ddebug=false -Dquarkus.devservices.enabled=false")
            .withFileSystemBind(getScriptDirPath() + "/resources",
                    "/home/kogito/serverless-workflow-project/src/main/resources", BindMode.READ_ONLY)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/jsongreet"))
            .withStartupTimeout(Duration.ofMinutes(2))
            .withLogConsumer(logConsumer);

    @Test
    public void testBuiltContainerAnswerCorrectly() throws URISyntaxException, IOException, InterruptedException {
        devModeImage.start();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(
                        "http://" + devModeImage.getHost() + ":" + devModeImage.getFirstMappedPort() + "/jsongreet"))
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
        ConsoleLauncher.main("--select-class=" + RunTests.class.getName(),
                "--reports-dir=" + Paths.get(getOutputDir()).toString());
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