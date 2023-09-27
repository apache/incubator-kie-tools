package org.kie.kogito;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProxyConfig {

    private final String ip;
    private final String port;
    private final boolean insecure_skip_verify;

    public ProxyConfig(@ConfigProperty(name = "quarkus.http.host") String ip,
                       @ConfigProperty(name = "quarkus.http.port") String port) {
        this.ip = ip;
        this.port = port;
        this.insecure_skip_verify = false;
    }

    @JsonProperty("ip")
    public String getIP() {
        return ip;
    }

    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    @JsonProperty("insecureSkipVerify")
    public boolean getInsecureSkipVerify() {
        return insecure_skip_verify;
    }
}
