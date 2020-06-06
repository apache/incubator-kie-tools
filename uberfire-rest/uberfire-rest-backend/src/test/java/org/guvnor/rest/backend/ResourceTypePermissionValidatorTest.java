/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.guvnor.rest.backend;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Lists;
import org.kie.soup.commons.util.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.workbench.model.AppFormerActivities;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.guvnor.structure.security.RepositoryAction.BUILD;
import static org.guvnor.structure.security.RepositoryAction.CREATE;
import static org.guvnor.structure.security.RepositoryAction.UPDATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.uberfire.security.ResourceAction.READ;

@RunWith(MockitoJUnitRunner.class)
public class ResourceTypePermissionValidatorTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private AppFormerActivities appFormerActivities;

    @Mock
    private PermissionManager permissionManager;

    @InjectMocks
    private ResourceTypePermissionValidator permissionValidator;

    @Test
    public void testIsPermissionAllowed() {
        assertTrue(permissionValidator.isPermissionAllowed(OrganizationalUnit.RESOURCE_TYPE, READ));
        assertTrue(permissionValidator.isPermissionAllowed(ActivityResourceType.PERSPECTIVE, READ));

        assertFalse(permissionValidator.isPermissionAllowed(ActivityResourceType.EDITOR, CREATE));
        assertTrue(permissionValidator.isPermissionAllowed(Repository.RESOURCE_TYPE, BUILD));
    }
    @Test
    public void testResourceDependancy() {
        assertEquals( permissionValidator.resourceDependancy(OrganizationalUnit.RESOURCE_TYPE, UPDATE).get().getDependantAction(), READ);
        assertNull(permissionValidator.resourceDependancy(ActivityResourceType.PERSPECTIVE, READ).get().getDependantAction());

        assertEquals( permissionValidator.resourceDependancy(Repository.RESOURCE_TYPE, UPDATE).get().getDependantAction(), READ);
        assertFalse(permissionValidator.resourceDependancy(ActivityResourceType.EDITOR, UPDATE).isPresent());
    }

    @Test
    public void testSatisfyDependancy() {
        PermissionCollection permissionCollection = mock(PermissionCollection.class);
        Permission p = mock(Permission.class);
        when(permissionManager.createPermission(OrganizationalUnit.RESOURCE_TYPE, READ, true)).thenReturn(p);
        when(permissionCollection.implies(p)).thenReturn(true);

        assertTrue(permissionValidator.satisfyDependancies(permissionCollection, OrganizationalUnit.RESOURCE_TYPE, UPDATE));
        assertTrue(permissionValidator.satisfyDependancies(permissionCollection, OrganizationalUnit.RESOURCE_TYPE, CREATE));
        assertTrue(permissionValidator.satisfyDependancies(permissionCollection, OrganizationalUnit.RESOURCE_TYPE, BUILD));
    }

    @Test
    public void testIsValidSpaceType() {
        OrganizationalUnit ou1 = new OrganizationalUnitImpl("ou1",
                                                            "defaultGroupID");
        OrganizationalUnit ou2 = new OrganizationalUnitImpl("ou2",
                                                            "defaultGroupID");
        final List<OrganizationalUnit> allOUs = new Lists.Builder<OrganizationalUnit>()
                .add(ou2).add(ou1).build();
        doReturn(allOUs).when(organizationalUnitService).getOrganizationalUnits();
        assertTrue(permissionValidator.isValidResourceType(OrganizationalUnit.RESOURCE_TYPE, "ou1"));
    }

    @Test
    public void testIsValidPerpectiveType() {
        doReturn(new Lists.Builder<String>().add("ExperimentalFeaturesPerspective").build()).when(appFormerActivities).getAllPerpectivesIds();
        assertTrue(permissionValidator.isValidResourceType(ActivityResourceType.PERSPECTIVE, "ExperimentalFeaturesPerspective"));
        assertFalse(permissionValidator.isValidResourceType(ActivityResourceType.PERSPECTIVE, "TestPerspective"));
    }

    @Test
    public void testIsValidEditorType() {
        doReturn(new Lists.Builder<String>().add("BPMNDiagramEditor").build()).when(appFormerActivities).getAllEditorIds();
        assertTrue(permissionValidator.isValidResourceType(ActivityResourceType.EDITOR, "BPMNDiagramEditor"));
        assertFalse(permissionValidator.isValidResourceType(ActivityResourceType.EDITOR, "TestEditor"));
    }

    @Test
    public void testIsValidRepositoryType() {
        WorkspaceProject itemB = mock(WorkspaceProject.class);
        when(itemB.getName()).thenReturn("Item B");
        when(projectService.getAllWorkspaceProjects()).thenReturn(new Sets.Builder<WorkspaceProject>().add(itemB).build());
        assertTrue(permissionValidator.isValidResourceType(Repository.RESOURCE_TYPE, "Item B"));
        assertFalse(permissionValidator.isValidResourceType(Repository.RESOURCE_TYPE, "Item A"));
    }
}
