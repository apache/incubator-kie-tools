package org.kie.kogito.tooling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubOAuthResponse {

    public String access_token;
    public String token_type;
    public String scope;
}
