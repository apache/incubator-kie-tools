package org.kie.kogito;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    @Inject
    PingResponse pingResponse;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPingResponse() {
        return Response.ok(pingResponse).build();
    }
}