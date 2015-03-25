package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;

import static org.kie.remote.common.rest.RestEasy960Util.*;
import static org.kie.workbench.common.screens.server.management.model.ConnectionType.REMOTE;
import static org.kie.workbench.common.screens.server.management.model.ContainerStatus.STARTED;

@Path("/controller")
@ApplicationScoped
public class ServerControllerRestImpl {

    @Inject
    private ServerReferenceStorageImpl storage;

    @Inject
    private Event<ServerConnected> serverConnectedEvent;

    @Inject
    private Event<ServerOnError> serverOnErrorEvent;

    @GET
    @Path("server/{serverId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response syncOnConnect(@Context HttpHeaders headers, @PathParam("serverId")String serverId) {
        List<KieContainerResource> containerResources = new ArrayList<KieContainerResource>();
        ServerRef serverRef = storage.loadServerRef(serverId);
        if (serverRef == null) {
            return createCorrectVariant("Server " + serverId + " not found", headers, Response.Status.NOT_FOUND);
        }

        Collection<ContainerRef> containerRefList = serverRef.getContainersRef();

        for (ContainerRef containerRef : containerRefList) {
            ReleaseId releaseId = new ReleaseId(containerRef.getReleasedId().getGroupId(), containerRef.getReleasedId().getArtifactId(), containerRef.getReleasedId().getVersion());

            containerResources.add(new KieContainerResource(containerRef.getId(), releaseId,
                    containerRef.getStatus() == ContainerStatus.STARTED? KieContainerStatus.STARTED:KieContainerStatus.FAILED));
        }

        KieContainerResourceList containerResourceList = new KieContainerResourceList();
        containerResourceList.setContainers(containerResources);

        Server server = new ServerImpl( serverRef.getId(), serverRef.getUrl(), serverRef.getName(), serverRef.getUsername(), serverRef.getPassword(), STARTED, REMOTE, new ArrayList<Container>(), serverRef.getProperties(), serverRef.getContainersRef() );
        serverConnectedEvent.fire(new ServerConnected(server));

        return createCorrectVariant(containerResourceList, headers, Response.Status.OK);
    }

    @POST
    @Path("server/{serverId}")
    public Response disconnect(@Context HttpHeaders headers, @PathParam("serverId")String serverId) {
        ServerRef serverRef = storage.loadServerRef(serverId);
        if (serverRef == null) {
            return createCorrectVariant("Server " + serverId + " not found", headers, Response.Status.NOT_FOUND);
        }
        serverOnErrorEvent.fire(new ServerOnError(serverRef, "Server disconnected"));

        return null;
    }

    public static Response createCorrectVariant(Object responseObj, HttpHeaders headers, javax.ws.rs.core.Response.Status status) {
        Response.ResponseBuilder responseBuilder = null;
        Variant v = getVariant(headers);
        if( v == null ) {
            v = defaultVariant;
        }
        if( status != null ) {
            responseBuilder = Response.status(status).entity(responseObj).variant(v);
        } else {
            responseBuilder = Response.ok(responseObj, v);
        }
        return responseBuilder.build();
    }
}
