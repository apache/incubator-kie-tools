/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.backend.migration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.security.ProjectAction;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;

/**
 * In previous versions (before the 7 release), the only way to grant access to resources like
 * {@link OrganizationalUnit}, {@link Repository} and {@link Project} was to indicate which groups were able to
 * access a given instance. Those groups were stored as part of the instance persistent status.
 * <p>
 * <p>As of 7 version, the authorization policy is based on permissions. That means is no longer required
 * to keep a list of groups per resource instance. What is required is to define proper permission entries into the
 * active {@link AuthorizationPolicy}</p>
 * <p>
 * <p>This is a utility class which takes care of reading the groups declared for any of the above resource types and
 * creating the necessary permissions so that those resources are protected from user access.</p>
 * <p>
 * <p>The migration procedure is carried out when an {@link AuthorizationPolicyDeployedEvent} is received, which means
 * the application is starting up and deploying the authorization policy for the first time.</p>
 */
@ApplicationScoped
public class ACLMigrationTool {

    private OrganizationalUnitService organizationalUnitService;
    private RepositoryService repositoryService;
    private Instance<ProjectService<?>> projectServices;
    private PermissionManager permissionManager;
    private AuthorizationPolicyStorage authorizationPolicyStorage;
    private Map<String, Group> groupMap = new HashMap<>();

    @Inject
    public ACLMigrationTool(OrganizationalUnitService organizationalUnitService,
                            RepositoryService repositoryService,
                            Instance<ProjectService<?>> projectServices,
                            PermissionManager permissionManager,
                            AuthorizationPolicyStorage authorizationPolicyStorage) {
        this.organizationalUnitService = organizationalUnitService;
        this.repositoryService = repositoryService;
        this.projectServices = projectServices;
        this.permissionManager = permissionManager;
        this.authorizationPolicyStorage = authorizationPolicyStorage;
    }

    public void onDeploy(@Observes AuthorizationPolicyDeployedEvent event) {
        AuthorizationPolicy policy = event.getPolicy();
        migrateOrgUnits(policy);
        migrateRepositories(policy);
        authorizationPolicyStorage.savePolicy(policy);
    }

    private Group getGroup(String groupName) {
        Group group = groupMap.get(groupName);
        if (group == null) {
            group = new GroupImpl(groupName);
            groupMap.put(groupName,
                         group);
        }
        return group;
    }

    public void migrateOrgUnits(AuthorizationPolicy policy) {
        Collection<OrganizationalUnit> itemList = organizationalUnitService.getAllOrganizationalUnits();
        for (OrganizationalUnit item : itemList) {
            Permission p = permissionManager.createPermission(item,
                                                              OrganizationalUnitAction.READ,
                                                              true);
            for (String groupName : item.getGroups()) {
                Group group = getGroup(groupName);
                PermissionCollection pc = policy.getPermissions(group);
                pc.add(p);
            }
        }
    }

    public void migrateRepositories(AuthorizationPolicy policy) {
        Collection<Repository> itemList = repositoryService.getAllRepositories();
        for (Repository item : itemList) {
            Permission p = permissionManager.createPermission(item,
                                                              RepositoryAction.READ,
                                                              true);
            for (String groupName : item.getGroups()) {
                Group group = getGroup(groupName);
                PermissionCollection pc = policy.getPermissions(group);
                pc.add(p);
            }
            migrateProjects(policy,
                            item);
        }
    }

    public void migrateProjects(AuthorizationPolicy policy,
                                Repository repository) {
        ProjectService projectService = getProjectService();
        if (projectService != null) {
            Collection<Project> itemList = projectService.getAllProjects(repository,
                                                                         "master");
            for (Project item : itemList) {
                Permission p = permissionManager.createPermission(item,
                                                                  ProjectAction.READ,
                                                                  true);
                for (String groupName : item.getGroups()) {
                    Group group = getGroup(groupName);
                    PermissionCollection pc = policy.getPermissions(group);
                    pc.add(p);
                }
            }
        }
    }

    public ProjectService getProjectService() {
        return projectServices.get();
    }
}
