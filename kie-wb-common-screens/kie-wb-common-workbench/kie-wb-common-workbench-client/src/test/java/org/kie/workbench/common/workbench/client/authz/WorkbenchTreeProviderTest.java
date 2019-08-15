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
package org.kie.workbench.common.workbench.client.authz;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.impl.DefaultLoadOptions;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.guvnor.m2repo.security.MavenRepositoryPagedJarTableFeatures.JAR_DOWNLOAD;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.EDIT_SOURCES;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.GUIDED_DECISION_TABLE_EDIT_COLUMNS;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.PLANNER_AVAILABLE;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.EDIT_PROFILE_PREFERENCES;
import static org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures.ACCESS_DATA_TRANSFER;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchTreeProviderTest {

    private static final String[] FEATURES_NAMES = {
        EDIT_SOURCES,
        PLANNER_AVAILABLE,
        JAR_DOWNLOAD,
        EDIT_GLOBAL_PREFERENCES,
        GUIDED_DECISION_TABLE_EDIT_COLUMNS,
        EDIT_PROFILE_PREFERENCES,
        ACCESS_DATA_TRANSFER
    };

    private DefaultPermissionManager permissionManager;

    private PermissionNode permissionNode;

    @Before
    public void setup() {
        permissionNode = mock(PermissionNode.class);
        permissionManager = new DefaultPermissionManager();

        when(permissionNode.propertyEquals(anyString(),
                                           anyObject())).thenReturn(true);
    }

    @Test
    public void testWorkbenchPermissionsNames() {
        WorkbenchTreeProvider workbenchTreeProvider = new WorkbenchTreeProvider(permissionManager);
        Callback callback = new Callback();
        workbenchTreeProvider.loadChildren(permissionNode,
                                           new DefaultLoadOptions(),
                                           callback);
        List<PermissionNode> permissionNodeList = callback.getList();

        Assert.assertEquals(FEATURES_NAMES.length,
                            permissionNodeList.size());

        for (int i = 0; i < permissionNodeList.size(); i++) {
            Assert.assertEquals(permissionNodeList.get(i).getPermissionList().get(0).getName(),
                                FEATURES_NAMES[i]);
        }
    }

    private class Callback implements LoadCallback {

        private List<PermissionNode> list;

        @Override
        public void afterLoad(List<PermissionNode> list) {
            this.list = list;
        }

        public List<PermissionNode> getList() {
            return list;
        }
    }
}
