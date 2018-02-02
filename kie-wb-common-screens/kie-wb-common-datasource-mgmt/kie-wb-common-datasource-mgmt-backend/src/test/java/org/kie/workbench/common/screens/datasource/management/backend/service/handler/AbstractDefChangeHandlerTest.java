/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.service.handler;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.service.DataSourceServicesHelper;
import org.kie.workbench.common.screens.datasource.management.backend.service.DefRegistry;
import org.kie.workbench.common.screens.datasource.management.backend.service.TestDriver;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceEventHelper;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractDefChangeHandlerTest {

    protected static final String FILE_URI = "default://master@datasources/MockUri.file";

    protected static final String SESSION_ID = "SESSION_ID";

    protected static final String IDENTIFIER = "IDENTIFIER";

    @Mock
    protected DataSourceRuntimeManager runtimeManager;

    @Mock
    protected DataSourceServicesHelper serviceHelper;

    @Mock
    protected IOService ioService;

    @Mock
    protected KieModuleService moduleService;

    @Mock
    protected DataSourceEventHelper eventHelper;

    @Mock
    protected DefRegistry defRegistry;

    @Mock
    protected Path path;

    @Mock
    protected Path originalPath;

    @Mock
    protected SessionInfo sessionInfo;

    @Mock
    protected User identity;

    @Mock
    protected KieModule module;

    protected DataSourceDef dataSourceDef;

    @Mock
    protected DataSourceDef registeredDataSourceDef;

    @Mock
    protected DataSourceDef originalDataSourceDef;

    protected DriverDef driverDef;

    @Mock
    protected DriverDef registeredDriverDef;

    @Mock
    protected DriverDef originalDriverDef;

    @Mock
    protected DataSourceDeploymentInfo registeredDataSourceDeploymentInfo;

    @Mock
    protected DataSourceDeploymentInfo originalDatasourceDeploymentInfo;

    @Mock
    protected DriverDeploymentInfo registeredDriverDeploymentInfo;

    @Mock
    protected DriverDeploymentInfo originalDriverDeploymentInfo;

    protected AbstractDefChangeHandler changeHandler;

    @Before
    public void setup() {
        setupChangeHandler();
        when(serviceHelper.getDefRegistry()).thenReturn(defRegistry);
        when(moduleService.resolveModule(path)).thenReturn(module);

        when(sessionInfo.getId()).thenReturn(SESSION_ID);
        when(sessionInfo.getIdentity()).thenReturn(identity);
        when(identity.getIdentifier()).thenReturn(IDENTIFIER);

        dataSourceDef = new DataSourceDef();
        dataSourceDef.setUuid("uuid");
        dataSourceDef.setName("dataSourceName");
        dataSourceDef.setConnectionURL("connectionURL");
        dataSourceDef.setUser("user");
        dataSourceDef.setPassword("password");

        driverDef = new DriverDef();
        driverDef.setUuid("uuid");
        driverDef.setName("driverName");
        driverDef.setDriverClass(TestDriver.class.getName());
        driverDef.setGroupId("groupId");
        driverDef.setArtifactId("artifactId");
        driverDef.setVersion("version");

        when(registeredDataSourceDef.getUuid()).thenReturn("registeredDataSourceUuid");
        when(registeredDataSourceDeploymentInfo.getUuid()).thenReturn("registeredDataSourceUuid");
        when(originalDataSourceDef.getUuid()).thenReturn("originalDataSourceUuid");
        when(originalDatasourceDeploymentInfo.getUuid()).thenReturn("originalDataSourceUuid");

        when(registeredDriverDef.getUuid()).thenReturn("registeredDriverDefUuid");
        when(registeredDriverDeploymentInfo.getUuid()).thenReturn("registeredDriverDefUuid");
        when(originalDriverDef.getUuid()).thenReturn("originalDriverDefUuid");
        when(originalDriverDeploymentInfo.getUuid()).thenReturn("originalDriverDefUuid");
    }

    protected abstract void setupChangeHandler();

    /**
     * Tests the case when the file added is a datasource that wasn't previously registered.
     */
    @Test
    public void testAddDataSourceNotRegistered() throws Exception {
        testAddResourceNotRegistered(dataSourceDef);
    }

    /**
     * Tests the case when the file added is a driver that wasn't previously registered.
     */
    @Test
    public void testAddDriverNotRegistered() throws Exception {
        testAddResourceNotRegistered(driverDef);
    }

    protected void testAddResourceNotRegistered(Def def) throws Exception {
        prepareDef(def);
        changeHandler.processResourceAdd(path,
                                         sessionInfo);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyAddEvent(path,
                       def);
    }

    /**
     * Tests the case when the file added is a datasource that was already registered and the definition didn't change.
     */
    @Test
    public void testAddDataSourceRegisteredNotChanged() throws Exception {
        testAddResourceRegisteredNotChanged(dataSourceDef);
    }

    /**
     * Tests the case when the file added is a driver that was already registered and the definition didn't change.
     */
    @Test
    public void testAddDriverRegisteredNotChanged() throws Exception {
        testAddResourceRegisteredNotChanged(driverDef);
    }

    protected void testAddResourceRegisteredNotChanged(Def def) throws Exception {
        prepareDef(def);
        //emulates that the definition was already registered.
        prepareRegisteredResource(path,
                                  def,
                                  null);
        changeHandler.processResourceAdd(path,
                                         sessionInfo);
        verifyNoActions();
    }

    /**
     * Tests the case when the file added is a datasource that was already registered, the definition has changed,
     * and the previous definition was not deployed.
     */
    @Test
    public void testAddDataSourceRegisteredChangedNotDeployed() throws Exception {
        testAddResourceRegisteredChangedNotDeployed(dataSourceDef,
                                                    registeredDataSourceDef);
    }

    /**
     * Tests the case when the file added is a driver that was already registered, the definition has changed,
     * and the previous definition was not deployed.
     */
    @Test
    public void testAddDriverRegisteredChangedNotDeployed() throws Exception {
        testAddResourceRegisteredChangedNotDeployed(driverDef,
                                                    registeredDriverDef);
    }

    protected void testAddResourceRegisteredChangedNotDeployed(Def def,
                                                               Def registeredDef) throws Exception {
        prepareDef(def);
        //emulates that a different definition is registered.
        prepareRegisteredResource(path,
                                  registeredDef,
                                  null);
        changeHandler.processResourceAdd(path,
                                         sessionInfo);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyAddEvent(path,
                       def);
    }

    /**
     * Tests the case when the file added is a datasource that was already registered, the definition has changed,
     * and the previous definition was deployed.
     */
    @Test
    public void testAddDataSourceRegisteredChangedDeployed() throws Exception {
        testAddResourceRegisteredChangedDeployed(dataSourceDef,
                                                 registeredDataSourceDef,
                                                 registeredDataSourceDeploymentInfo);
    }

    /**
     * Tests the case when the file added is a driver that was already registered, the definition has changed,
     * and the previous definition was deployed.
     */
    @Test
    public void testAddDriverRegisteredChangedDeployed() throws Exception {
        testAddResourceRegisteredChangedDeployed(driverDef,
                                                 registeredDriverDef,
                                                 registeredDriverDeploymentInfo);
    }

    protected void testAddResourceRegisteredChangedDeployed(Def def,
                                                            Def registeredDef,
                                                            DeploymentInfo registeredDefDeploymentInfo) throws Exception {
        prepareDef(def);
        //emulates that a different definition is registered and also was deployed.
        prepareRegisteredResource(path,
                                  registeredDef,
                                  registeredDefDeploymentInfo);
        changeHandler.processResourceAdd(path,
                                         sessionInfo);
        verifyUnDeployed(registeredDef);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyAddEvent(path,
                       def);
    }

    /**
     * Tests the case when the file updated is a datasource that wasn't previously registered.
     */
    @Test
    public void testUpdateDataSourceNotRegistered() throws Exception {
        testUpdateResourceNotRegistered(dataSourceDef);
    }

    /**
     * Tests the case when the file updated is a driver that wasn't previously registered.
     */
    @Test
    public void testUpdateDriverNotRegistered() throws Exception {
        testUpdateResourceNotRegistered(driverDef);
    }

    protected void testUpdateResourceNotRegistered(Def def) throws Exception {
        prepareDef(def);
        changeHandler.processResourceUpdate(path,
                                            sessionInfo);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          null);
    }

    /**
     * Tests the case when the file updated is a datasource that was already registered and the definition didn't change.
     */
    @Test
    public void testUpdateDataSourceRegisteredNotChanged() throws Exception {
        testUpdateResourceRegisteredNotChanged(dataSourceDef);
    }

    /**
     * Tests the case when the file updated is a driver that was already registered and the definition didn't change.
     */
    @Test
    public void testUpdateDriverRegisteredNotChanged() throws Exception {
        testUpdateResourceRegisteredNotChanged(driverDef);
    }

    protected void testUpdateResourceRegisteredNotChanged(Def def) throws Exception {
        prepareDef(def);
        //emulates that the definition was already registered.
        prepareRegisteredResource(path,
                                  def,
                                  null);
        changeHandler.processResourceUpdate(path,
                                            sessionInfo);
        verifyNoActions();
    }

    /**
     * Tests the case when the file updated is a datasource that was already registered, the definition has changed,
     * and the previous definition was not deployed.
     */
    @Test
    public void testUpdateDataSourceRegisteredChangedNotDeployed() throws Exception {
        testUpdateResourceRegisteredChangedNotDeployed(dataSourceDef,
                                                       registeredDataSourceDef);
    }

    /**
     * Tests the case when the file updated is a driver that was already registered, the definition has changed,
     * and the previous definition was not deployed.
     */
    @Test
    public void testUpdateDriverRegisteredChangedNotDeployed() throws Exception {
        testUpdateResourceRegisteredChangedNotDeployed(driverDef,
                                                       registeredDriverDef);
    }

    protected void testUpdateResourceRegisteredChangedNotDeployed(Def def,
                                                                  Def registeredDef) throws Exception {
        prepareDef(def);
        //emulates that a different definition is registered, but not deployed.
        prepareRegisteredResource(path,
                                  registeredDef,
                                  null);
        changeHandler.processResourceUpdate(path,
                                            sessionInfo);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          registeredDef);
    }

    /**
     * Tests the case when the file updated is a datasource that was already registered, the definition has changed,
     * and the previous definition was deployed.
     */
    @Test
    public void testUpdateDataSourceRegisteredChangedDeployed() throws Exception {
        testUpdateResourceRegisteredChangedDeployed(dataSourceDef,
                                                    registeredDataSourceDef,
                                                    registeredDataSourceDeploymentInfo);
    }

    /**
     * Tests the case when the file updated is a driver that was already registered, the definition has changed,
     * and the previous definition was deployed.
     */
    @Test
    public void testUpdateDriverRegisteredChangedDeployed() throws Exception {
        testUpdateResourceRegisteredChangedDeployed(driverDef,
                                                    registeredDriverDef,
                                                    registeredDriverDeploymentInfo);
    }

    protected void testUpdateResourceRegisteredChangedDeployed(Def def,
                                                               Def registeredDef,
                                                               DeploymentInfo registeredDefDeploymentInfo) throws Exception {
        prepareDef(def);
        //emulates that a different definition is registered and also was deployed.
        prepareRegisteredResource(path,
                                  registeredDef,
                                  registeredDefDeploymentInfo);
        changeHandler.processResourceUpdate(path,
                                            sessionInfo);
        verifyUnDeployed(registeredDef);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          registeredDef);
    }

    /**
     * Tests the case where the file in the original path wasn't registered, and the target path is not registered.
     */
    @Test
    public void testRenameDataSourceOriginalPathNotRegisteredTargetPathNotRegistered() throws Exception {
        testRenameResourceOriginalPathNotRegisteredTargetPathNotRegistered(dataSourceDef);
    }

    /**
     * Tests the case where the file in the original path wasn't registered, and the target path is not registered.
     */
    @Test
    public void testRenameDriverOriginalPathNotRegisteredTargetPathNotRegistered() throws Exception {
        testRenameResourceOriginalPathNotRegisteredTargetPathNotRegistered(driverDef);
    }

    protected void testRenameResourceOriginalPathNotRegisteredTargetPathNotRegistered(Def def) throws Exception {
        prepareDef(def);
        changeHandler.processResourceRename(originalPath,
                                            path,
                                            sessionInfo);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          null);
    }

    /**
     * Tests the case where the file in the original path was registered, and the target path is not registered.
     */
    @Test
    public void testRenameDataSourceOriginalPathRegisteredTargetPathNotRegistered() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathNotRegistered(dataSourceDef,
                                                                        originalDataSourceDef,
                                                                        originalDatasourceDeploymentInfo);
    }

    /**
     * Tests the case where the file in the original path was registered, and the target path is not registered.
     */
    @Test
    public void testRenameDriverOriginalPathRegisteredTargetPathNotRegistered() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathNotRegistered(driverDef,
                                                                        originalDriverDef,
                                                                        originalDriverDeploymentInfo);
    }

    protected void testRenameResourceOriginalPathRegisteredTargetPathNotRegistered(Def def,
                                                                                   Def originalDef,
                                                                                   DeploymentInfo originalDeploymentInfo) throws Exception {
        prepareDef(def);
        prepareRegisteredResource(originalPath,
                                  originalDef,
                                  originalDeploymentInfo);
        changeHandler.processResourceRename(originalPath,
                                            path,
                                            sessionInfo);
        verifyUnDeployed(originalDef);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          null);
    }

    /**
     * Tests the case where the file in the original path was registered, and the target path is also registered but
     * the definition didn't change
     */
    @Test
    public void testRenameDataSourceOriginalPathRegisteredTargetPathRegisteredNotChanged() throws Exception {
        testRenameResourceSourceOriginalPathRegisteredTargetPathRegisteredNotChanged(dataSourceDef,
                                                                                     originalDataSourceDef,
                                                                                     originalDatasourceDeploymentInfo);
    }

    /**
     * Tests the case where the file in the original path was registered, and the target path is also registered but
     * the definition didn't change
     */
    @Test
    public void testRenameDriverOriginalPathRegisteredTargetPathRegisteredNotChanged() throws Exception {
        testRenameResourceSourceOriginalPathRegisteredTargetPathRegisteredNotChanged(driverDef,
                                                                                     originalDriverDef,
                                                                                     originalDriverDeploymentInfo);
    }

    protected void testRenameResourceSourceOriginalPathRegisteredTargetPathRegisteredNotChanged(Def def,
                                                                                                Def originalDef,
                                                                                                DeploymentInfo originalDeploymentInfo) throws Exception {
        prepareDef(def);
        prepareRegisteredResource(originalPath,
                                  originalDef,
                                  originalDeploymentInfo);
        prepareRegisteredResource(path,
                                  def,
                                  null);
        changeHandler.processResourceRename(originalPath,
                                            path,
                                            sessionInfo);
        verifyUnDeployed(originalDef);
        verify(defRegistry,
               never()).setEntry(path,
                                 def);
        verifyNoEvents();
        verifyNoDeployments();
    }

    /**
     * Tests the case where the file in the original path was registered, the target path is also registered and the
     * definition has changed, and the original definition was not deployed.
     */
    @Test
    public void testRenameDataSourceOriginalPathRegisteredTargetPathRegisteredChangedNotDeployed() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedNotDeployed(dataSourceDef,
                                                                                       originalDataSourceDef,
                                                                                       originalDatasourceDeploymentInfo,
                                                                                       registeredDataSourceDef);
    }

    /**
     * Tests the case where the file in the original path was registered, the target path is also registered and the
     * definition has changed, and the original definition was not deployed.
     */
    @Test
    public void testRenameDriverOriginalPathRegisteredTargetPathRegisteredChangedNotDeployed() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedNotDeployed(driverDef,
                                                                                       originalDriverDef,
                                                                                       originalDriverDeploymentInfo,
                                                                                       registeredDriverDef);
    }

    protected void testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedNotDeployed(Def def,
                                                                                                  Def originalDef,
                                                                                                  DeploymentInfo originalDeploymentInfo,
                                                                                                  Def registeredDef) throws Exception {
        prepareDef(def);
        prepareRegisteredResource(originalPath,
                                  originalDef,
                                  originalDeploymentInfo);
        prepareRegisteredResource(path,
                                  registeredDef,
                                  null);
        changeHandler.processResourceRename(originalPath,
                                            path,
                                            sessionInfo);
        verifyUnDeployed(originalDef);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          registeredDef);
    }

    /**
     * Tests the case where the file in the original path was registered, the target path is also registered and the
     * definition has changed, and the original definition was deployed.
     */
    @Test
    public void testRenameDataSourceOriginalPathRegisteredTargetPathRegisteredChangedDeployed() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedDeployed(dataSourceDef,
                                                                                    originalDataSourceDef,
                                                                                    originalDatasourceDeploymentInfo,
                                                                                    registeredDataSourceDef,
                                                                                    registeredDataSourceDeploymentInfo);
    }

    /**
     * Tests the case where the file in the original path was registered, the target path is also registered and the
     * definition has changed, and the original definition was deployed.
     */
    @Test
    public void testRenameDriverOriginalPathRegisteredTargetPathRegisteredChangedDeployed() throws Exception {
        testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedDeployed(driverDef,
                                                                                    originalDriverDef,
                                                                                    originalDriverDeploymentInfo,
                                                                                    registeredDriverDef,
                                                                                    registeredDriverDeploymentInfo);
    }

    protected void testRenameResourceOriginalPathRegisteredTargetPathRegisteredChangedDeployed(Def def,
                                                                                               Def originalDef,
                                                                                               DeploymentInfo originalDeploymentInfo,
                                                                                               Def registeredDef,
                                                                                               DeploymentInfo registeredDeploymentInfo) throws Exception {
        prepareDef(def);
        prepareRegisteredResource(originalPath,
                                  originalDef,
                                  originalDeploymentInfo);
        prepareRegisteredResource(path,
                                  registeredDef,
                                  registeredDeploymentInfo);
        changeHandler.processResourceRename(originalPath,
                                            path,
                                            sessionInfo);
        verifyUnDeployed(originalDef);
        verifyUnDeployed(registeredDef);
        verifyRegisteredAndDeployed(path,
                                    def);
        verifyUpdateEvent(path,
                          def,
                          registeredDef);
    }

    /**
     * Tests the case where the deleted file is not registered.
     */
    @Test
    public void testDeleteDataSourceNotRegistered() throws Exception {
        testDeleteResourceNotRegistered();
    }

    /**
     * Tests the case where the deleted file is not registered.
     */
    @Test
    public void testDeleteDriverNotRegistered() throws Exception {
        testDeleteResourceNotRegistered();
    }

    protected void testDeleteResourceNotRegistered() throws Exception {
        changeHandler.processResourceDelete(path,
                                            sessionInfo);
        verify(defRegistry,
               times(1)).getEntry(path);
        verifyNoActions();
    }

    /**
     * Tests the case where the deleted file is registered but not deployed.
     */
    @Test
    public void testDeleteDataSourceRegisteredNotDeployed() throws Exception {
        testDeleteResourceRegisteredNotDeployed(registeredDataSourceDef);
    }

    /**
     * Tests the case where the deleted file is registered but not deployed.
     */
    @Test
    public void testDeleteDriverRegisteredNotDeployed() throws Exception {
        testDeleteResourceRegisteredNotDeployed(registeredDriverDef);
    }

    protected void testDeleteResourceRegisteredNotDeployed(Def registeredDef) throws Exception {
        prepareRegisteredResource(path,
                                  registeredDef,
                                  null);
        changeHandler.processResourceDelete(path,
                                            sessionInfo);
        verify(defRegistry,
               times(1)).getEntry(path);
        verify(defRegistry,
               times(1)).invalidateCache(path);
        verifyDeleteEvent(path,
                          registeredDef);
    }

    /**
     * Tests the case where the deleted file is registered and deployed deployed.
     */
    @Test
    public void testDeleteDataSourceRegisteredDeployed() throws Exception {
        testDeleteResourceRegisteredDeployed(registeredDataSourceDef,
                                             registeredDataSourceDeploymentInfo);
    }

    /**
     * Tests the case where the deleted file is registered and deployed deployed.
     */
    @Test
    public void testDeleteDriverRegisteredDeployed() throws Exception {
        testDeleteResourceRegisteredDeployed(registeredDriverDef,
                                             registeredDriverDeploymentInfo);
    }

    protected void testDeleteResourceRegisteredDeployed(Def registeredDef,
                                                        DeploymentInfo registeredDeploymentInfo) throws Exception {
        prepareRegisteredResource(path,
                                  registeredDef,
                                  registeredDeploymentInfo);
        changeHandler.processResourceDelete(path,
                                            sessionInfo);
        verify(defRegistry,
               times(1)).getEntry(path);
        verify(defRegistry,
               times(1)).invalidateCache(path);
        verifyUnDeployed(registeredDef);
        verifyDeleteEvent(path,
                          registeredDef);
    }

    /**
     * Verifies that the given definition has been properly registered and deployed.
     */
    protected void verifyRegisteredAndDeployed(Path path,
                                               Def def) throws Exception {
        // the definition should have been registered and deployed
        verify(defRegistry,
               times(1)).setEntry(path,
                                  def);
        if (def instanceof DataSourceDef) {
            verify(runtimeManager,
                   times(1)).deployDataSource((DataSourceDef) def,
                                              DeploymentOptions.create());
        } else {
            verify(runtimeManager,
                   times(1)).deployDriver((DriverDef) def,
                                          DeploymentOptions.create());
        }
    }

    /**
     * Verifies that the given definition has been un-deployed.
     */
    protected void verifyUnDeployed(Def def) throws Exception {
        // the definition should have been un-deployed.
        if (def instanceof DataSourceDef) {
            DataSourceDeploymentInfo deploymentInfo = runtimeManager.getDataSourceDeploymentInfo(def.getUuid());
            // is deployed by construction
            assertNotNull(deploymentInfo);
            verify(runtimeManager,
                   times(1)).unDeployDataSource(deploymentInfo,
                                                UnDeploymentOptions.forcedUnDeployment());
        } else {
            DriverDeploymentInfo deploymentInfo = runtimeManager.getDriverDeploymentInfo(def.getUuid());
            // is deployed by construction
            assertNotNull(deploymentInfo);
            verify(runtimeManager,
                   times(1)).unDeployDriver(deploymentInfo,
                                            UnDeploymentOptions.forcedUnDeployment());
        }
    }

    /**
     * verifies that no actions has been invoked on the main components.
     */
    protected void verifyNoActions() throws Exception {
        verify(defRegistry,
               never()).setEntry(any(Path.class),
                                 any(Def.class));
        verify(defRegistry,
               never()).invalidateCache(any(Path.class));

        verify(runtimeManager,
               never()).unDeployDataSource(any(DataSourceDeploymentInfo.class),
                                           any(UnDeploymentOptions.class));
        verify(runtimeManager,
               never()).deployDataSource(any(DataSourceDef.class),
                                         any(DeploymentOptions.class));
        verify(runtimeManager,
               never()).unDeployDriver(any(DriverDeploymentInfo.class),
                                       any(UnDeploymentOptions.class));
        verify(runtimeManager,
               never()).deployDriver(any(DriverDef.class),
                                     any(DeploymentOptions.class));

        verifyNoEvents();
    }

    protected void verifyNoDeployments() throws Exception {
        verify(runtimeManager,
               never()).deployDataSource(any(DataSourceDef.class),
                                         any(DeploymentOptions.class));
        verify(runtimeManager,
               never()).deployDriver(any(DriverDef.class),
                                     any(DeploymentOptions.class));
    }

    protected void verifyNoEvents() {
        verify(eventHelper,
               never()).fireCreateEvent(any(NewDriverEvent.class));
        verify(eventHelper,
               never()).fireUpdateEvent(any(UpdateDriverEvent.class));
        verify(eventHelper,
               never()).fireDeleteEvent(any(DeleteDriverEvent.class));
        verify(eventHelper,
               never()).fireCreateEvent(any(NewDataSourceEvent.class));
        verify(eventHelper,
               never()).fireUpdateEvent(any(UpdateDataSourceEvent.class));
        verify(eventHelper,
               never()).fireDeleteEvent(any(DeleteDataSourceEvent.class));
    }

    protected void verifyAddEvent(Path path,
                                  Def addedDef) {
        if (addedDef instanceof DataSourceDef) {
            verify(eventHelper,
                   times(1)).fireCreateEvent(new NewDataSourceEvent((DataSourceDef) addedDef,
                                                                    moduleService.resolveModule(path),
                                                                    SESSION_ID,
                                                                    IDENTIFIER));
        } else {
            verify(eventHelper,
                   times(1)).fireCreateEvent(new NewDriverEvent((DriverDef) addedDef,
                                                                moduleService.resolveModule(path),
                                                                SESSION_ID,
                                                                IDENTIFIER));
        }
    }

    protected void verifyUpdateEvent(Path path,
                                     Def def,
                                     Def originalDef) {
        if (def instanceof DataSourceDef) {
            verify(eventHelper,
                   times(1)).fireUpdateEvent(new UpdateDataSourceEvent((DataSourceDef) def,
                                                                       moduleService.resolveModule(path),
                                                                       SESSION_ID,
                                                                       IDENTIFIER,
                                                                       (DataSourceDef) originalDef));
        } else {
            verify(eventHelper,
                   times(1)).fireUpdateEvent(new UpdateDriverEvent((DriverDef) def,
                                                                   moduleService.resolveModule(path),
                                                                   SESSION_ID,
                                                                   IDENTIFIER,
                                                                   (DriverDef) originalDef));
        }
    }

    protected void verifyDeleteEvent(Path path,
                                     Def def) {
        if (def instanceof DataSourceDef) {
            verify(eventHelper,
                   times(1)).fireDeleteEvent(new DeleteDataSourceEvent((DataSourceDef) def,
                                                                       moduleService.resolveModule(path),
                                                                       SESSION_ID,
                                                                       IDENTIFIER));
        } else {
            verify(eventHelper,
                   times(1)).fireDeleteEvent(new DeleteDriverEvent((DriverDef) def,
                                                                   moduleService.resolveModule(path),
                                                                   SESSION_ID,
                                                                   IDENTIFIER));
        }
    }

    protected void prepareRegisteredResource(Path path,
                                             Def registeredDef,
                                             DeploymentInfo deploymentInfo) throws Exception {
        when(defRegistry.getEntry(path)).thenReturn(registeredDef);
        if (registeredDef != null && deploymentInfo != null) {
            if (registeredDef instanceof DataSourceDef) {
                when(runtimeManager.getDataSourceDeploymentInfo(registeredDef.getUuid())).thenReturn((DataSourceDeploymentInfo) deploymentInfo);
            } else {
                when(runtimeManager.getDriverDeploymentInfo(registeredDef.getUuid())).thenReturn((DriverDeploymentInfo) deploymentInfo);
            }
        }
    }

    protected void prepareDef(Def def) {
        when(path.toURI()).thenReturn(FILE_URI);
        String content;
        if (def instanceof DataSourceDef) {
            when(path.getFileName()).thenReturn("File.datasource");
            when(serviceHelper.isDataSourceFile(path)).thenReturn(true);
            content = DataSourceDefSerializer.serialize((DataSourceDef) def);
        } else {
            when(path.getFileName()).thenReturn("File.driver");
            when(serviceHelper.isDriverFile(path)).thenReturn(true);
            content = DriverDefSerializer.serialize((DriverDef) def);
        }
        when(ioService.readAllString(Paths.convert(path))).thenReturn(content);
    }
}