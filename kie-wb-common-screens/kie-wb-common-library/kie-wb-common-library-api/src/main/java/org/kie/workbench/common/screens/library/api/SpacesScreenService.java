package org.kie.workbench.common.screens.library.api;

import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.annotations.Portable;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Remote
@Path("/spacesScreen")
@Produces(APPLICATION_JSON)
public interface SpacesScreenService {

    @GET
    @Path("/spaces")
    Collection<OrganizationalUnit> getSpaces();

    @GET
    @Path("/spaces/{name}")
    OrganizationalUnit getSpace(final @PathParam("name") String name);

    @GET
    @Path("/spaces/validGroupId")
    boolean isValidGroupId(final @QueryParam("groupId") String groupId);

    @POST
    @Path("/spaces")
    @Consumes(APPLICATION_JSON)
    Response postSpace(final NewSpace newSpace);

    @Portable
    class NewSpace {

        public NewSpace() {
        }

        public String name;
        public String groupId;
    }
}
