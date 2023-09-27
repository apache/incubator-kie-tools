package org.kie.kogito;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class PingResponseTest {

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

    @Inject
    PingResponse pingResponse;

    @Test
    public void testGetVersion() {
        assertEquals(expected_version, pingResponse.getVersion());
    }

    @Test
    public void testGetProxyConfig() {
        ProxyConfig proxyConfig = pingResponse.getProxyConfig();
        assertEquals(expected_ip, proxyConfig.getIP());
        assertEquals(expected_port, proxyConfig.getPort());
        assertEquals(expected_insecure_skip_verify, proxyConfig.getInsecureSkipVerify());
    }

    @Test
    public void testGetKIESandboxURL() {
        assertEquals(expected_kie_sandbox_url, pingResponse.getKIESandboxURL());
    }

    @Test
    public void testGetStarted() {
        assertEquals(expected_started, pingResponse.getStarted());
    }
}
