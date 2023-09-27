package org.kie.kogito;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PingResponse {

    private final String version;
    private final ProxyConfig proxy_config;
    private final String kie_sandbox_url;
    private final boolean started;

    @Inject
    public PingResponse(@ConfigProperty(name = "quarkus.application.version") String version,
                        ProxyConfig proxyConfig,
                        @ConfigProperty(name = "kie.sandbox.url") String kie_sandbox_url) {
        this.version = version;
        this.proxy_config = proxyConfig;
        this.kie_sandbox_url = kie_sandbox_url;
        this.started = true;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("proxy")
    public ProxyConfig getProxyConfig() {
        return proxy_config;
    }

    @JsonProperty("kieSandboxUrl")
    public String getKIESandboxURL() {
        return kie_sandbox_url;
    }

    @JsonProperty("started")
    public boolean getStarted() {
        return started;
    }
}
