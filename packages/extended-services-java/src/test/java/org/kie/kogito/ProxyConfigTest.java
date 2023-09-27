package org.kie.kogito;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ProxyConfigTest {

    @Inject
    @ConfigProperty(name = "quarkus.http.host")
    String expected_ip;
    @Inject
    @ConfigProperty(name = "quarkus.http.port")
    String expected_port;
    boolean expected_insecure_skip_verify = false;

    @Inject
    ProxyConfig proxyConfig;

    @Test
    public void testProxyConfig() {
        assertEquals(expected_ip, proxyConfig.getIP());
        assertEquals(expected_port, proxyConfig.getPort());
        assertEquals(expected_insecure_skip_verify, proxyConfig.getInsecureSkipVerify());
    }
}
