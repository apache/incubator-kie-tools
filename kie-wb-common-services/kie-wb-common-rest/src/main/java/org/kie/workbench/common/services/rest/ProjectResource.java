/*
 * Copyright 2011 JBoss Inc
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


package org.kie.workbench.common.services.rest;

import java.util.Iterator;

import org.jboss.resteasy.annotations.GZIP;
import org.kie.workbench.common.services.rest.domain.BuildConfig;
import org.kie.workbench.common.services.rest.domain.Entity;
import org.kie.workbench.common.services.rest.domain.Result;
import org.kie.workbench.common.services.shared.builder.BuildService;


import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.uberfire.backend.server.util.Paths;
//import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.Path;


@Path("/")
@RequestScoped
@Named
@GZIP
//@ApplicationScoped
public class ProjectResource {
    private HttpHeaders headers;

    @Context
    protected UriInfo uriInfo;

    @Inject
    protected ProjectService projectService;

    @Inject
    protected BuildService buildService;

/*    @Inject
    protected ScenarioTestEditorService scenarioTestEditorService;
*/
    @Inject
    private Paths paths;

    @Inject
    @Named("ioSystemStrategy")
    private IOService ioSystemService;


    @Context
    public void setHttpHeaders(HttpHeaders theHeaders) {
        headers = theHeaders;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories")
    public Entity createRepository(Entity repository) {
        System.out.println("-----createRepository--- , repository name:" + repository.getName());

        return repository;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}")
    public Result deleteRepository(
            @PathParam("repositoryName") String repositoryName) {
        System.out.println("-----deleteRepository--- , repositoryName:" + repositoryName);

        Result result = new Result();
        result.setStatus("SUCCESS");
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects")
    public Entity createProject(
            @PathParam("repositoryName") String repositoryName, Entity project) {
        System.out.println("-----createProject--- , repositoryName:"
                + repositoryName + ", project name:" + project.getName());

        org.kie.commons.java.nio.file.Path repositoryPath = getRepositoryRootPath(repositoryName);

        if (repositoryPath == null) {
            //TODO: Return proper error message
            return null;
        } else {
            POM pom = new POM();
            org.uberfire.backend.vfs.Path vfsPath = projectService.newProject(paths.convert(repositoryPath.resolve("project"), false), project.getName(), pom, "/");

            //TODO: handle errors, exceptions.

            //project.setPath(vfsPath.getFileName());
            return project;
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}")
    public Result deleteProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName) {
        System.out.println("-----deleteProject--- , repositoryName:"
                + repositoryName + ", project name:" + projectName);

        //TODO: Delete project


        Result result = new Result();
        result.setStatus("SUCCESS");
        return result;
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/compile")
    public Result compileProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName, BuildConfig mavenConfig) {
        System.out.println("-----compileProject--- , repositoryName:" + repositoryName + ", project name:" + projectName);

        org.kie.commons.java.nio.file.Path repositoryPath = getRepositoryRootPath(repositoryName);

        if (repositoryPath == null) {
            Result result = new Result();
            result.setStatus("FAILURE");
            result.setDetailedMessage("The repository " + repositoryName + " does not exist");
            return result;
        } else {
            org.uberfire.backend.vfs.Path pathToPomXML = paths.convert(
                    repositoryPath.resolve(projectName), false);

            if (pathToPomXML == null) {
			    Result result = new Result();
			    result.setStatus("FAILURE");
			    result.setDetailedMessage("The project " + projectName + " does not exist");
                return result;
		    }

            buildService.build(pathToPomXML);

            // TODO: get BuildResults

            Result result = new Result();
            result.setStatus("SUCCESS");
            return result;
        }
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/install")
    public Result installProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName, BuildConfig mavenConfig) {
        System.out.println("-----installProject--- , repositoryName:"
                + repositoryName + ", project name:" + projectName);

        org.kie.commons.java.nio.file.Path repositoryPath = getRepositoryRootPath(repositoryName);

        if (repositoryPath == null) {
            Result result = new Result();
            result.setStatus("FAILURE");
            result.setDetailedMessage("The repository " + repositoryName + " does not exist");
            return result;
        } else {
            org.uberfire.backend.vfs.Path pathToPomXML = paths.convert(repositoryPath.resolve( projectName ), false);
            if (pathToPomXML == null) {
			    Result result = new Result();
			    result.setStatus("FAILURE");
			    result.setDetailedMessage("The project " + projectName + " does not exist");
                return result;
		    }

            buildService.buildAndDeploy(pathToPomXML);

            //TODO: get BuildResults

            Result result = new Result();
            result.setStatus("SUCCESS");
            return result;
        }
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/test")
    public Result testProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName, BuildConfig config) {
        System.out.println("-----testProject--- , repositoryName:"
                + repositoryName + ", project name:" + projectName);

        org.kie.commons.java.nio.file.Path repositoryPath = getRepositoryRootPath(repositoryName);

        if (repositoryPath == null) {
            Result result = new Result();
            result.setStatus("FAILURE");
            result.setDetailedMessage("The repository " + repositoryName + " does not exist");
            return result;
        } else {
            org.uberfire.backend.vfs.Path pathToPomXML = paths.convert(repositoryPath.resolve( projectName ), false);

            if (pathToPomXML == null) {
			    Result result = new Result();
			    result.setStatus("FAILURE");
			    result.setDetailedMessage("The project " + projectName + " does not exist");
                return result;
		    }

            //TODO: Get session from BuildConfig or create a default session for testing if no session is provided.
            //scenarioTestEditorService.runAllScenarios(pathToPomXML, "someSession");

            //TODO: Get test result. We need an sync version of runAllScenarios (instead of listening for test result from event listeners).

            Result result = new Result();
            result.setStatus("SUCCESS");
            return result;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/deploy")
    public Result deployProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName, BuildConfig config) {
        System.out.println("-----deployProject--- , repositoryName:"
                + repositoryName + ", project name:" + projectName);

        org.kie.commons.java.nio.file.Path repositoryPath = getRepositoryRootPath(repositoryName);

        if (repositoryPath == null) {
            Result result = new Result();
            result.setStatus("FAILURE");
            result.setDetailedMessage("The repository " + repositoryName + " does not exist");
            return result;
        } else {
            org.uberfire.backend.vfs.Path pathToPomXML = paths.convert(repositoryPath.resolve( projectName ), false);

            if (pathToPomXML == null) {
			    Result result = new Result();
			    result.setStatus("FAILURE");
			    result.setDetailedMessage("The project " + projectName + " does not exist");
                return result;
		    }

            buildService.buildAndDeploy(pathToPomXML);

            //TODO: get BuildResults

            Result result = new Result();
            result.setStatus("SUCCESS");
            return result;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups")
    public Entity createGroup(Entity group) {
        System.out.println("-----createGroup--- , Group name:" + group.getName());

/*        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        return group;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups/{groupName}")
    public Result deleteGroup(@PathParam("groupName") String groupName) {
        System.out.println("-----deleteGroup--- , Group name:" + groupName);

        Result result = new Result();
        result.setStatus("SUCCESS");
        return result;
    }

    public org.kie.commons.java.nio.file.Path getRepositoryRootPath(String repositoryName) {
        org.kie.commons.java.nio.file.Path repositoryPath = null;

        final Iterator<FileSystem> fsIterator = ioSystemService.getFileSystems().iterator();
        if ( fsIterator.hasNext() ) {
            final FileSystem fileSystem = fsIterator.next();
            System.out.println("-----FileSystem--- :" + fileSystem);

            final Iterator<org.kie.commons.java.nio.file.Path> rootIterator = fileSystem.getRootDirectories().iterator();
            if ( rootIterator.hasNext() ) {
                org.kie.commons.java.nio.file.Path rootPath = rootIterator.next();
                if (rootPath != null && rootPath.startsWith(repositoryName)) {
                    repositoryPath = rootPath;
                }
            }
        }

        return repositoryPath;
    }
}





