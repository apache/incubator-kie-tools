package org.kie.kogito.tooling;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/login/oauth")
@RegisterRestClient(baseUri = "https://github.com")
@Singleton
public interface GitHubOAuthApi {

    @POST
    @Path("/access_token")
    @Produces("application/json")
    @Consumes("application/json")
    GitHubOAuthResponse postAccessToken(
            final @QueryParam("client_secret") String clientSecret,
            final @QueryParam("client_id") String clientId,
            final @QueryParam("code") String code,
            final @QueryParam("redirect_uri") String redirectUri);
}
