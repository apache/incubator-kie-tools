package org.kie.kogito.tooling;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
public class GitHubOAuthResource {

    @Inject
    @RestClient
    GitHubOAuthApi githubOAuthApi;

    @Inject
    @ConfigProperty(name = "org.kie.kogito.tooling.github.app.clientSecret")
    String clientSecret;

    @GET
    @Path("/github_oauth")
    @Produces("application/json")
    public GitHubOAuthResponse getAccessToken(
            final @QueryParam("code") String code,
            final @QueryParam("client_id") String clientId,
            final @QueryParam("redirect_uri") String redirectUri) {
        return githubOAuthApi.postAccessToken(clientSecret, clientId, code, redirectUri);
    }
}