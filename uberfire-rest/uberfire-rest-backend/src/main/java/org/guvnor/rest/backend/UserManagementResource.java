/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.rest.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.guvnor.rest.client.NewGroup;
import org.guvnor.rest.client.NewUser;
import org.guvnor.rest.client.PermissionResponse;
import org.guvnor.rest.client.UberfireRestResponse;
import org.guvnor.rest.client.UpdateSettingRequest;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.Customizable;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.workbench.model.AppFormerActivities;

import static org.guvnor.rest.backend.PermissionConstants.ADMIN_ROLE;

/**
 * REST services for user management operations
 */
@Path("/")
@Named
@ApplicationScoped
public class UserManagementResource {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementResource.class);

    @Inject
    private GroupManagerService groupManagerService;

    @Inject
    private RoleManagerService roleManagerService;

    @Inject
    private UserManagerService userManagerService;

    @Inject
    private UserManagementResourceHelper resourceHelper;

    @Inject
    @Customizable
    private AppFormerActivities appFormerActivities;

    private Variant defaultVariant = getDefaultVariant();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups")
    @RolesAllowed({ADMIN_ROLE})
    public Response createGroup(NewGroup group) {
        logger.debug("-----createGroup--- , Group name: {}, User assigned : {}",
                     group.getName(),
                     group.getUsers());
        UberfireRestResponse response = resourceHelper.createGroup(group.getName(), group.getUsers());
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    @RolesAllowed({ADMIN_ROLE})
    public Response createUser(NewUser newUser) {
        logger.debug("-----createUsers--- , User name: {}",
                     newUser.getName());
        UberfireRestResponse response = resourceHelper.createUser(newUser);
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/changePassword")
    @RolesAllowed({ADMIN_ROLE})
    public Response changePassword(@PathParam("userName") String userName, String password) {
        logger.debug("-----changePassword--- , User name: {}", userName);

        assertObjectExists(userManagerService.get(userName),
                           "user",
                           userName);

        UberfireRestResponse response = resourceHelper.changePassword(userName, password);
        return createResponse(response);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups/{groupName}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteGroup(@PathParam("groupName") String groupName) {
        logger.debug("-----deleteGroup--- , Group Name: {}",
                     groupName);

        assertObjectExists(groupManagerService.get(groupName),
                           "group",
                           groupName);
        UberfireRestResponse response = resourceHelper.removeGroup(groupName);
        return createResponse(response);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteUser(@PathParam("userName") String userName) {
        logger.debug("-----deleteUser--- , User Name: {}",
                     userName);

        assertObjectExists(userManagerService.get(userName),
                           "user",
                           userName);
        UberfireRestResponse response = resourceHelper.removeUser(userName);
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups/{groupName}/permissions")
    @RolesAllowed({ADMIN_ROLE})
    public Response updateGroupPermissions(@PathParam("groupName") String groupName, UpdateSettingRequest permissionRequest) {
        logger.debug("-----updateGroupPermissions--- , Group name: {}",
                     groupName);

        assertObjectExists(groupManagerService.get(groupName),
                           "group",
                           groupName);
        UberfireRestResponse response = resourceHelper.updateGroupPermissions(groupName, permissionRequest);
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/{rolesName}/permissions")
    @RolesAllowed({ADMIN_ROLE})
    public Response updateRolePermissions(@PathParam("rolesName") String rolesName, UpdateSettingRequest permissionRequest) {
        logger.debug("-----updateRolePermissions--- , Role name: {}",
                     rolesName);

        assertObjectExists(roleManagerService.get(rolesName),
                           "role",
                           rolesName);
        UberfireRestResponse response = resourceHelper.updateRolePermissions(rolesName, permissionRequest);
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/groups")
    @RolesAllowed({ADMIN_ROLE})
    public Response assignGroupsToUser(@PathParam("userName") String userName, List<String> groups) {
        logger.debug("-----assignGroupsToUser--- , User name: {}",
                     userName);

        assertObjectExists(userManagerService.get(userName),
                           "user",
                           userName);
        UberfireRestResponse response = resourceHelper.assignGroupsToUser(userName, groups);
        return createResponse(response);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/roles")
    @RolesAllowed({ADMIN_ROLE})
    public Response assignRolesToUser(@PathParam("userName") String userName, List<String> roles) {
        logger.debug("-----assignRolesToUser--- , User name: {}",
                     userName);

        assertObjectExists(userManagerService.get(userName),
                           "user",
                           userName);
        UberfireRestResponse response = resourceHelper.assignRolesToUser(userName, roles);
        return createResponse(response);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/permissions")
    @RolesAllowed({ADMIN_ROLE})
    public PermissionResponse getUserPermissions(@PathParam("userName") String userName) {
        logger.debug("-----getUserPermissions--- ");

        final User user = userManagerService.getUser(userName);
        assertObjectExists(user,
                           "user",
                           userName);
        return resourceHelper.getUserPermissions(userName);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups/{groupName}/permissions")
    @RolesAllowed({ADMIN_ROLE})
    public PermissionResponse getGroupPermissions(@PathParam("groupName") String groupName) {
        logger.debug("-----getGroupPermissions--- ");

        final Group group = groupManagerService.get(groupName);
        assertObjectExists(group,
                           "group",
                           groupName);

        return resourceHelper.getGroupPermissions(groupName);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/{roleName}/permissions")
    @RolesAllowed({ADMIN_ROLE})
    public PermissionResponse getRolePermissions(@PathParam("roleName") String roleName) {
        logger.debug("-----getUserPermissions--- ");

        final Role role = roleManagerService.get(roleName);
        assertObjectExists(role,
                           "role",
                           roleName);
        return resourceHelper.getRolePermissions(roleName);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<Role> getRoles() {
        logger.debug("-----getRoles--- ");

        return roleManagerService.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<String> getUsers() {
        logger.debug("-----getUsers--- ");
        List<String> results = new ArrayList<>();
        final List<User> users = userManagerService.getAll();
        if (users != null) {
            for (User user : users) {
                results.add(user.getIdentifier());
            }
        }
        return results;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/groups")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<Group> getUserGroups(@PathParam("userName") String userName) {
        logger.debug("-----getUserGroups--- ");

        final User user = userManagerService.getUser(userName);
        assertObjectExists(user,
                           "user",
                           userName);
        return user.getGroups();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users/{userName}/roles")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<Role> getUserRoles(@PathParam("userName") String userName) {
        logger.debug("-----getUserRoles--- ");

        final User user = userManagerService.getUser(userName);
        assertObjectExists(user,
                           "user",
                           userName);
        return user.getRoles();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<Group> getGroups() {
        logger.debug("-----getGroups--- ");
        return groupManagerService.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/editors")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<String> getEditors() {
        logger.debug("-----getEditors--- ");

        return appFormerActivities.getAllEditorIds();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/perspectives")
    @RolesAllowed({ADMIN_ROLE})
    public Collection<String> getPerpectives() {
        logger.debug("-----getPerpectives--- ");

        return appFormerActivities.getAllPerpectivesIds();
    }

    protected void assertObjectExists(final Object o,
                                      final String objectInfo,
                                      final String objectName) {
        if (o == null) {
            throw new WebApplicationException(String.format("Could not find %s with name %s.", objectInfo, objectName),
                                              Response.status(Response.Status.NOT_FOUND).build());
        }
    }

    protected Response createResponse(final UberfireRestResponse restResponse) {
        return Response.status(restResponse.getStatus()).entity(restResponse).variant(defaultVariant).build();
    }

    protected Variant getDefaultVariant() {
        return Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build().get(0);
    }
}
