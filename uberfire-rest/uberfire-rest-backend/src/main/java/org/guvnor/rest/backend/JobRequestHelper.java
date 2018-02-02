/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestService;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

/**
 * Utility class to perform various functions for the REST service involving backend services
 */
@ApplicationScoped
public class JobRequestHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobRequestHelper.class);

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ModuleService<? extends Module> moduleService;

    @Inject
    private WorkspaceProjectService workspaceProjectService;

    @Inject
    private RepositoryService repoService;

    @Inject
    private BuildService buildService;

    @Inject
    private SpacesAPI spacesAPI;

    @Inject
    @Named("ioStrategy")
    private IOService ioSystemService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private TestService testService;

    public JobResult cloneProject(final String jobId,
                                  final String spaceName,
                                  final CloneProjectRequest repository) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (!("clone".equals(repository.getRequestType()))) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("Repository request type can only be clone.");
            return result;
        }

        final String scheme = "git";

        OrganizationalUnit orgUnit = organizationalUnitService.getOrganizationalUnit(spaceName);
        if (orgUnit == null) {
            // double check, this is also checked at input
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("Space '" + spaceName + "' does not exist!");
            return result;
        }

        if ("clone".equals(repository.getRequestType())) {
            if (repository.getName() == null || "".equals(repository.getName()) || repository.getGitURL() == null
                    || "".equals(repository.getGitURL())) {
                result.setStatus(JobStatus.BAD_REQUEST);
                result.setResult("Repository name and GitURL must be provided");
            }

            // username and password are optional
            final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
            if (repository.getUserName() != null && !"".equals(repository.getUserName())) {
                configuration.setUserName(repository.getUserName());
            }
            if (repository.getPassword() != null && !"".equals(repository.getPassword())) {
                configuration.setPassword(repository.getPassword());
            }
            configuration.setOrigin(repository.getGitURL());

            org.guvnor.structure.repositories.Repository newlyCreatedRepo = repositoryService.createRepository(
                    orgUnit,
                    scheme,
                    repository.getName(),
                    configuration);
            if (newlyCreatedRepo != null) {
                result.setStatus(JobStatus.SUCCESS);
                result.setResult("Alias: " + newlyCreatedRepo.getAlias() + ", Scheme: " + newlyCreatedRepo.getScheme() + ", Uri: " + newlyCreatedRepo.getUri());
            } else {
                result.setStatus(JobStatus.FAIL);
            }
        }

        return result;
    }

    public JobResult createProject(final String jobId,
                                   final String organizationalUnitName,
                                   final String projectName,
                                   String projectGroupId,
                                   String projectVersion,
                                   String projectDescription) {

        final JobResult result = new JobResult();
        result.setJobId(jobId);

        final OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit(organizationalUnitName);
        if (organizationalUnit == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("OrganizationalUnit [" + organizationalUnitName + "] does not exist");
        } else {

            if (projectGroupId == null || projectGroupId.trim().isEmpty()) {
                projectGroupId = projectName;
            }
            if (projectVersion == null || projectVersion.trim().isEmpty()) {
                projectVersion = "1.0";
            }

            final POM pom = new POM();
            pom.getGav().setArtifactId(projectName);
            pom.getGav().setGroupId(projectGroupId);
            pom.getGav().setVersion(projectVersion);
            pom.setDescription(projectDescription);
            pom.setName(projectName);

            try {
                workspaceProjectService.newProject(organizationalUnit,
                                                   pom);
            } catch (GAVAlreadyExistsException gae) {
                result.setStatus(JobStatus.DUPLICATE_RESOURCE);
                result.setResult("Project's GAV [" + gae.getGAV().toString() + "] already exists at [" + toString(gae.getRepositories()) + "]");
                return result;
            } catch (org.uberfire.java.nio.file.FileAlreadyExistsException e) {
                result.setStatus(JobStatus.DUPLICATE_RESOURCE);
                result.setResult("Project [" + projectName + "] already exists");
                return result;
            }

            result.setStatus(JobStatus.SUCCESS);
        }
        return result;
    }

    private String toString(final Set<MavenRepositoryMetadata> repositories) {
        final StringBuilder sb = new StringBuilder();
        for (MavenRepositoryMetadata md : repositories) {
            sb.append(md.getId()).append(" : ").append(md.getUrl()).append(" : ").append(md.getSource()).append(", ");
        }
        sb.delete(sb.length() - 2,
                  sb.length() - 1);
        return sb.toString();
    }

    public JobResult deleteProject(final String jobId,
                                   final String spaceName,
                                   final String projectName) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        Space space = spacesAPI.getSpace(spaceName);
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, projectName);

        if (workspaceProject == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Project [" + projectName + "] does not exist");
            return result;
        } else {
            try {
                repositoryService.removeRepository(space, workspaceProject.getRepository().getAlias());
            } catch (Exception e) {
                result.setStatus(JobStatus.FAIL);
                result.setResult("Project [" + projectName + "] could not be deleted: " + e.getMessage());
                logger.error("Unable to delete project '" + projectName + "': " + e.getMessage(),
                             e);
                return result;
            }

            result.setStatus(JobStatus.SUCCESS);
            return result;
        }
    }

    public JobResult compileProject(final String jobId,
                                    final String spaceName,
                                    final String projectName) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        final Space space = spacesAPI.getSpace(spaceName);
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, projectName);

        if (workspaceProject == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Project [" + projectName + "] does not exist");
            return result;
        } else {
            Module module = workspaceProject.getMainModule();

            if (module == null) {
                result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                result.setResult("Project [" + projectName + "] has no main module.");
                return result;
            }

            BuildResults buildResults = buildService.build(module);

            result.setDetailedResult(buildResultsToDetailedStringMessages(buildResults.getMessages()));
            result.setStatus(buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL);
            return result;
        }
    }

    private List<String> buildResultsToDetailedStringMessages(List<BuildMessage> messages) {
        List<String> result = new ArrayList<>();
        for (BuildMessage message : messages) {
            String detailedStringMessage = "level:" + message.getLevel() +
                    ", path:" + message.getPath() +
                    ", text:" + message.getText();
            result.add(detailedStringMessage);
        }
        return result;
    }

    public JobResult installProject(final String jobId,
                                    final String spaceName,
                                    final String projectName) {

        PortablePreconditions.checkNotNull("jobId", jobId);
        PortablePreconditions.checkNotNull("spaceName", spaceName);
        PortablePreconditions.checkNotNull("projectName", projectName);

        JobResult result = new JobResult();
        result.setJobId(jobId);

        final Space space = spacesAPI.getSpace(spaceName);

        if (space == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Space [" + spaceName + "] does not exist");
            return result;
        } else {
            final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space,
                                                                                             projectName);
            if (workspaceProject == null) {
                result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                result.setResult("Project [" + projectName + "] does not exist");
                return result;
            }
            
            final Module module = workspaceProject.getMainModule();

            if (module == null) {
                result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                result.setResult("Project [" + projectName + "] has no main module.");
                return result;
            }

            BuildResults buildResults = null;
            try {
                buildResults = buildService.buildAndDeploy(module);

                result.setDetailedResult(buildResults == null ? null : deployResultToDetailedStringMessages(buildResults));
                result.setStatus(buildResults != null && buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL);
            } catch (Throwable t) {
                Optional<GAVAlreadyExistsException> gaeOpt = findCause(t,
                                                                       GAVAlreadyExistsException.class);
                if (gaeOpt.isPresent()) {
                    GAVAlreadyExistsException gae = gaeOpt.get();
                    result.setStatus(JobStatus.DUPLICATE_RESOURCE);
                    result.setResult("Project's GAV [" + gae.getGAV() + "] already exists at [" + toString(gae.getRepositories()) + "]");
                } else {
                    List<String> errorResult = new ArrayList<>();
                    errorResult.add(t.getMessage());
                    result.setDetailedResult(errorResult);
                    result.setStatus(JobStatus.FAIL);
                }
            }
            return result;
        }
    }

    private List<String> deployResultToDetailedStringMessages(final BuildResults deployResult) {
        GAV gav = deployResult.getGAV();
        List<String> result = buildResultsToDetailedStringMessages(deployResult.getErrorMessages());
        String detailedStringMessage = "artifactID:" + gav.getArtifactId() +
                ", groupId:" + gav.getGroupId() +
                ", version:" + gav.getVersion();
        result.add(detailedStringMessage);
        return result;
    }

    public JobResult testProject(final String jobId,
                                 final String spaceName,
                                 final String projectName) {
        final JobResult result = new JobResult();
        result.setJobId(jobId);

        Space space = spacesAPI.getSpace(spaceName);
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, projectName);

        if (workspaceProject == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Project [" + projectName + "] does not exist.");
            return result;
        } else {
            final Module module = workspaceProject.getMainModule();

            if (module == null) {
                result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                result.setResult("Project [" + projectName + "] has no main module.");
                return result;
            } else {

                testService.runAllTests(
                        "JobRequestHelper",
                        module.getPomXMLPath(),
                        getCustomTestResultEvent(result));
                return result;
            }
        }
    }

    private Event<TestResultMessage> getCustomTestResultEvent(final JobResult result) {
        return new Event<TestResultMessage>() {
            @Override
            public void fire(TestResultMessage event) {
                result.setDetailedResult(event.getResultStrings());
                result.setStatus(event.wasSuccessful() ? JobStatus.SUCCESS : JobStatus.FAIL);
            }

            @Override
            public Event<TestResultMessage> select(Annotation... qualifiers) {
                // not used
                return null;
            }

            @Override
            public <U extends TestResultMessage> Event<U> select(Class<U> subtype,
                                                                 Annotation... qualifiers) {
                // not used
                return null;
            }
        };
    }

    public JobResult deployProject(final String jobId,
                                   final String spaceName,
                                   final String projectName) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        Space space = spacesAPI.getSpace(spaceName);
        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(space, projectName);

        if (workspaceProject == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Project [" + projectName + "] does not exist");
            return result;
        } else {
            Module module = workspaceProject.getMainModule();

            if (module == null) {
                result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                result.setResult("Project [" + projectName + "] has no main module.");
                return result;
            }

            BuildResults buildResults = null;
            try {
                buildResults = buildService.buildAndDeploy(module);

                result.setDetailedResult(buildResults == null ? null : deployResultToDetailedStringMessages(buildResults));
                result.setStatus(buildResults != null && buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL);
            } catch (RuntimeException ex) {
                GAVAlreadyExistsException gae = findCause(ex,
                                                          GAVAlreadyExistsException.class).orElseThrow(() -> ex);
                result.setStatus(JobStatus.DUPLICATE_RESOURCE);
                result.setResult("Project's GAV [" + gae.getGAV() + "] already exists at [" + toString(gae.getRepositories()) + "]");
                return result;
            }
            return result;
        }
    }

    public JobResult removeOrganizationalUnit(final String jobId,
                                              final String organizationalUnitName) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (organizationalUnitName == null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit name must be provided");
            return result;
        }

        try {
            organizationalUnitService.removeOrganizationalUnit(organizationalUnitName);
            result.setStatus(JobStatus.SUCCESS);
        } catch (Exception e) {
            result.setStatus(JobStatus.FAIL);
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to remove '" + organizationalUnitName + "': " + e.getMessage();
            result.setResult(errMsg);
            logger.error(errMsg,
                         e);
        }

        return result;
    }

    public JobResult createOrganizationalUnit(final String jobId,
                                              final String organizationalUnitName,
                                              final String organizationalUnitOwner,
                                              final String defaultGroupId,
                                              final List<String> repositoryNameList) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (organizationalUnitName == null || organizationalUnitOwner == null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit name and owner must be provided");
            return result;
        }

        String _defaultGroupId = null;
        if (defaultGroupId == null || defaultGroupId.trim().isEmpty()) {
            _defaultGroupId = organizationalUnitService.getSanitizedDefaultGroupId(organizationalUnitName);
            logger.warn("No default group id was provided, reverting to the space unit name");
        } else {
            if (!organizationalUnitService.isValidGroupId(defaultGroupId)) {
                result.setStatus(JobStatus.BAD_REQUEST);
                result.setResult("Invalid default group id, only alphanumerical characters are admitted, " +
                                         "as well as '\"_\"', '\"-\"' or '\".\"'.");
                return result;
            } else {
                _defaultGroupId = defaultGroupId;
            }
        }

        OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit(organizationalUnitName);
        if (organizationalUnit != null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit with name " + organizationalUnitName + " already exists");
            return result;
        }
        Space space = spacesAPI.getSpace(organizationalUnitName);

        List<org.guvnor.structure.repositories.Repository> repositories = new ArrayList<>();
        if (repositoryNameList != null && repositoryNameList.size() > 0) {
            for (String repositoryAlias : repositoryNameList) {
                org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath(space, repositoryAlias);

                if (repositoryPath == null) {
                    result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
                    result.setResult("Repository [" + repositoryAlias + "] does not exist");
                    return result;
                }
                GitRepository repo = new GitRepository(repositoryAlias,
                                                       spacesAPI.getSpace(organizationalUnitName));
                repositories.add(repo);
            }
            organizationalUnit = organizationalUnitService.createOrganizationalUnit(organizationalUnitName,
                                                                                    organizationalUnitOwner,
                                                                                    _defaultGroupId,
                                                                                    repositories);
        } else {
            organizationalUnit = organizationalUnitService.createOrganizationalUnit(organizationalUnitName,
                                                                                    organizationalUnitOwner,
                                                                                    _defaultGroupId);
        }

        if (organizationalUnit != null) {
            result.setResult("OrganizationalUnit " + organizationalUnit.getName() + " is created successfully.");
            result.setStatus(JobStatus.SUCCESS);
        } else {
            result.setStatus(JobStatus.FAIL);
        }
        return result;
    }

    public JobResult updateOrganizationalUnit(final String jobId,
                                              final String organizationalUnitName,
                                              final String organizationalUnitOwner,
                                              final String defaultGroupId) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (organizationalUnitName == null || organizationalUnitOwner == null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit name and owner must be provided");
            return result;
        }

        String _defaultGroupId = null;
        if (defaultGroupId == null || defaultGroupId.trim().isEmpty()) {
            _defaultGroupId = organizationalUnitService.getSanitizedDefaultGroupId(organizationalUnitName);
            logger.warn("No default group id was provided, reverting to the space unit name");
        } else {
            if (!organizationalUnitService.isValidGroupId(defaultGroupId)) {
                result.setStatus(JobStatus.BAD_REQUEST);
                result.setResult("Invalid default group id, only alphanumerical characters are admitted, " +
                                         "as well as '\"_\"', '\"-\"' or '\".\"'.");
                return result;
            } else {
                _defaultGroupId = defaultGroupId;
            }
        }

        OrganizationalUnit organizationalUnit = organizationalUnitService.updateOrganizationalUnit(organizationalUnitName,
                                                                                                   organizationalUnitOwner,
                                                                                                   _defaultGroupId);

        if (organizationalUnit != null) {
            result.setResult("OrganizationalUnit " + organizationalUnit.getName() + " was successfully updated.");
            result.setStatus(JobStatus.SUCCESS);
        } else {
            result.setStatus(JobStatus.FAIL);
        }
        return result;
    }

    public JobResult addRepositoryToOrganizationalUnit(final String jobId,
                                                       final String organizationalUnitName,
                                                       final String repositoryAlias) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (organizationalUnitName == null || repositoryAlias == null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit name and Repository name must be provided");
            return result;
        }
        Space space = spacesAPI.getSpace(organizationalUnitName);

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath(space, repositoryAlias);
        if (repositoryPath == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Repository [" + repositoryAlias + "] does not exist");
            return result;
        }

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(organizationalUnitName,
                                                                           null,
                                                                           null);

        GitRepository repo = new GitRepository(repositoryAlias,
                                               spacesAPI.getSpace(organizationalUnit.getName()));
        try {
            organizationalUnitService.addRepository(organizationalUnit,
                                                    repo);
        } catch (IllegalArgumentException e) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit " + organizationalUnit.getName() + " not found");
            return result;
        }

        result.setStatus(JobStatus.SUCCESS);
        return result;
    }

    public JobResult removeRepositoryFromOrganizationalUnit(final String jobId,
                                                            final String organizationalUnitName,
                                                            final String repositoryAlias) {
        JobResult result = new JobResult();
        result.setJobId(jobId);

        if (organizationalUnitName == null || repositoryAlias == null) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit name and Repository name must be provided");

            return result;
        }

        Space space = spacesAPI.getSpace(organizationalUnitName);

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath(space, repositoryAlias);
        if (repositoryPath == null) {
            result.setStatus(JobStatus.RESOURCE_NOT_EXIST);
            result.setResult("Repository [" + repositoryAlias + "] does not exist");
            return result;
        }

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl(organizationalUnitName,
                                                                           null,
                                                                           null);
        GitRepository repo = new GitRepository(repositoryAlias,
                                               spacesAPI.getSpace(organizationalUnit.getName()));
        try {
            organizationalUnitService.removeRepository(organizationalUnit,
                                                       repo);
        } catch (IllegalArgumentException e) {
            result.setStatus(JobStatus.BAD_REQUEST);
            result.setResult("OrganizationalUnit " + organizationalUnit.getName() + " not found");
            return result;
        }

        result.setStatus(JobStatus.SUCCESS);
        return result;
    }

    private org.uberfire.java.nio.file.Path getRepositoryRootPath(final Space space, final String repositoryAlias) {

        final org.guvnor.structure.repositories.Repository repository = repositoryService.getRepositoryFromSpace(space, repositoryAlias);
        if (repository == null || !repository.getDefaultBranch().isPresent()) {
            return null;
        } else {
            return Paths.convert(repository.getDefaultBranch().get().getPath());
        }
    }

    private <T> Optional<T> findCause(Throwable t,
                                      Class<T> causeClass) {
        if (t == null) {
            return Optional.empty();
        } else if (t.getClass().equals(causeClass)) {
            return Optional.of((T) t);
        } else {
            return findCause(t.getCause(),
                             causeClass);
        }
    }
}