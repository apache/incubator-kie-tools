package org.kie.kogito;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class PingResourceTest {

    @Inject
    @ConfigProperty(name = "quarkus.application.version")
    String expected_version;
    @Inject
    @ConfigProperty(name = "quarkus.http.host")
    String expected_ip;
    @Inject
    @ConfigProperty(name = "quarkus.http.port")
    String expected_port;
    boolean expected_insecure_skip_verify = false;
    @Inject
    @ConfigProperty(name = "kie.sandbox.url")
    String expected_kie_sandbox_url;
    boolean expected_started = true;

    @Test
    public void testPingEndpoint() {
        given()
          .when().get("/ping")
          .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("version", equalTo(expected_version))
                .body("proxy.ip", equalTo(expected_ip))
                .body("proxy.port", equalTo(expected_port))
                .body("proxy.insecureSkipVerify", equalTo(expected_insecure_skip_verify))
                .body("kieSandboxUrl", equalTo(expected_kie_sandbox_url))
                .body("started", equalTo(expected_started));
    }

}