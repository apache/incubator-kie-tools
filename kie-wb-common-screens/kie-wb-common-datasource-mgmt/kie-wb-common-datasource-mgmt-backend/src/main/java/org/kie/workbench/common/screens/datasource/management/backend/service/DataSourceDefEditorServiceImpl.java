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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.net.URI;
import java.sql.Connection;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.screens.datasource.management.util.URLConnectionFactory;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.ext.editor.commons.service.PathNamingService;
import org.uberfire.io.IOService;

import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.isEmpty;

@Service
@ApplicationScoped
public class DataSourceDefEditorServiceImpl
        extends AbstractDefEditorService<DataSourceDefEditorContent, DataSourceDef, DataSourceDeploymentInfo>
        implements DataSourceDefEditorService {

    private DataSourceDefQueryService dataSourceDefQueryService;

    private DriverDefEditorService driverDefService;

    private Event<NewDataSourceEvent> newDataSourceEvent;

    private Event<UpdateDataSourceEvent> updateDataSourceEvent;

    private Event<DeleteDataSourceEvent> deleteDataSourceEvent;

    public DataSourceDefEditorServiceImpl() {
    }

    @Inject
    public DataSourceDefEditorServiceImpl(DataSourceRuntimeManager runtimeManager,
                                          DataSourceServicesHelper serviceHelper,
                                          @Named("ioStrategy") IOService ioService,
                                          KieModuleService moduleService,
                                          CommentedOptionFactory optionsFactory,
                                          PathNamingService pathNamingService,
                                          MavenArtifactResolver artifactResolver,
                                          DataSourceDefQueryService dataSourceDefQueryService,
                                          DriverDefEditorService driverDefService,
                                          Event<NewDataSourceEvent> newDataSourceEvent,
                                          Event<UpdateDataSourceEvent> updateDataSourceEvent,
                                          Event<DeleteDataSourceEvent> deleteDataSourceEvent) {
        super(runtimeManager,
              serviceHelper,
              ioService,
              moduleService,
              optionsFactory,
              pathNamingService,
              artifactResolver);
        this.dataSourceDefQueryService = dataSourceDefQueryService;
        this.driverDefService = driverDefService;
        this.newDataSourceEvent = newDataSourceEvent;
        this.updateDataSourceEvent = updateDataSourceEvent;
        this.deleteDataSourceEvent = deleteDataSourceEvent;
    }

    @Override
    protected DataSourceDefEditorContent newContent() {
        return new DataSourceDefEditorContent();
    }

    @Override
    protected String serializeDef(DataSourceDef def) {
        return DataSourceDefSerializer.serialize(def);
    }

    @Override
    protected DataSourceDef deserializeDef(String source) {
        return DataSourceDefSerializer.deserialize(source);
    }

    @Override
    protected DataSourceDeploymentInfo readDeploymentInfo(String uuid) throws Exception {
        return runtimeManager.getDataSourceDeploymentInfo(uuid);
    }

    @Override
    protected void deploy(DataSourceDef def,
                          DeploymentOptions options) throws Exception {
        runtimeManager.deployDataSource(def,
                                        options);
    }

    @Override
    protected void unDeploy(DataSourceDeploymentInfo deploymentInfo,
                            UnDeploymentOptions options) throws Exception {
        runtimeManager.unDeployDataSource(deploymentInfo,
                                          options);
    }

    @Override
    protected void fireCreateEvent(final DataSourceDef def,
                                   final Module module) {
        newDataSourceEvent.fire(new NewDataSourceEvent(def,
                                                       module,
                                                       optionsFactory.getSafeSessionId(),
                                                       optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected void fireCreateEvent(DataSourceDef def) {
        newDataSourceEvent.fire(new NewDataSourceEvent(def,
                                                       optionsFactory.getSafeSessionId(),
                                                       optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected void fireUpdateEvent(DataSourceDef def,
                                   Module module,
                                   DataSourceDef originalDef) {
        updateDataSourceEvent.fire(new UpdateDataSourceEvent(def,
                                                             module,
                                                             optionsFactory.getSafeSessionId(),
                                                             optionsFactory.getSafeIdentityName(),
                                                             originalDef));
    }

    @Override
    protected void fireDeleteEvent(DataSourceDef def,
                                   Module module) {
        deleteDataSourceEvent.fire(new DeleteDataSourceEvent(def,
                                                             module,
                                                             optionsFactory.getSafeSessionId(),
                                                             optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected String buildFileName(DataSourceDef def) {
        return def.getName() + ".datasource";
    }

    @Override
    public TestResult testConnection(DataSourceDef dataSourceDef) {
        return testConnection(dataSourceDef,
                              null);
    }

    @Override
    public TestResult testConnection(DataSourceDef dataSourceDef,
                                     Module module) {

        TestResult result = new TestResult(false);

        if (isEmpty(dataSourceDef.getConnectionURL())) {
            result.setMessage("A valid connection url is required");
            return result;
        }

        if (isEmpty(dataSourceDef.getUser()) || isEmpty(dataSourceDef.getPassword())) {
            result.setMessage("A valid user and password are required");
            return result;
        }

        DriverDefInfo driverDefInfo = null;
        if (isEmpty(dataSourceDef.getDriverUuid())) {
            result.setMessage("A valid driver is required");
            return result;
        }
        if (module != null) {
            driverDefInfo = dataSourceDefQueryService.findModuleDriver(dataSourceDef.getDriverUuid(),
                                                                       module.getRootPath());
        } else {
            driverDefInfo = dataSourceDefQueryService.findGlobalDriver(dataSourceDef.getDriverUuid());
        }

        if (driverDefInfo == null) {
            result.setMessage("Data source driver: " + dataSourceDef.getUuid() + " was not found");
            return result;
        }

        DriverDefEditorContent driverDefEditorContent = driverDefService.loadContent(driverDefInfo.getPath());
        DriverDef driverDef = driverDefEditorContent.getDef();
        URI uri;

        try {
            uri = artifactResolver.resolve(driverDef.getGroupId(),
                                           driverDef.getArtifactId(),
                                           driverDef.getVersion());
        } catch (Exception e) {
            result.setMessage("Connection could not be tested due to the following error: " + e.getMessage());
            return result;
        }

        if (uri == null) {
            result.setMessage("Driver artifact: " + driverDef.getGroupId() + ":"
                                      + driverDef.getArtifactId() + ":" + driverDef.getVersion() + " was not found");
            return result;
        }

        try {
            Properties properties = new Properties();
            properties.put("user",
                           dataSourceDef.getUser());
            properties.put("password",
                           dataSourceDef.getPassword());

            URLConnectionFactory connectionFactory = new URLConnectionFactory(uri.toURL(),
                                                                              driverDef.getDriverClass(),
                                                                              dataSourceDef.getConnectionURL(),
                                                                              properties);

            Connection conn = connectionFactory.createConnection();

            if (conn == null) {
                result.setMessage("It was not possible to open connection");
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Connection was successfully obtained: " + conn);
                stringBuilder.append("\n");
                stringBuilder.append("*** DatabaseProductName: " + conn.getMetaData().getDatabaseProductName());
                stringBuilder.append("\n");
                stringBuilder.append("*** DatabaseProductVersion: " + conn.getMetaData().getDatabaseProductVersion());
                stringBuilder.append("\n");
                stringBuilder.append("*** DriverName: " + conn.getMetaData().getDriverName());
                stringBuilder.append("\n");
                stringBuilder.append("*** DriverVersion: " + conn.getMetaData().getDriverVersion());
                stringBuilder.append("\n");
                conn.close();
                stringBuilder.append("Connection was successfully released.");
                stringBuilder.append("\n");

                result.setTestPassed(true);
                result.setMessage(stringBuilder.toString());
            }

            return result;
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            return result;
        }
    }
}