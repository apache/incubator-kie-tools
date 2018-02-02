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
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.PathNamingService;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class DriverDefEditorServiceImpl
        extends AbstractDefEditorService<DriverDefEditorContent, DriverDef, DriverDeploymentInfo>
        implements DriverDefEditorService {

    private Event<NewDriverEvent> newDriverEvent;

    private Event<UpdateDriverEvent> updateDriverEvent;

    private Event<DeleteDriverEvent> deleteDriverEvent;

    public DriverDefEditorServiceImpl() {
    }

    @Inject
    public DriverDefEditorServiceImpl(DataSourceRuntimeManager runtimeManager,
                                      DataSourceServicesHelper serviceHelper,
                                      @Named("ioStrategy") IOService ioService,
                                      KieModuleService moduleService,
                                      CommentedOptionFactory optionsFactory,
                                      PathNamingService pathNamingService,
                                      MavenArtifactResolver artifactResolver,
                                      Event<NewDriverEvent> newDriverEvent,
                                      Event<UpdateDriverEvent> updateDriverEvent,
                                      Event<DeleteDriverEvent> deleteDriverEvent) {
        super(runtimeManager,
              serviceHelper,
              ioService,
              moduleService,
              optionsFactory,
              pathNamingService,
              artifactResolver);
        this.newDriverEvent = newDriverEvent;
        this.updateDriverEvent = updateDriverEvent;
        this.deleteDriverEvent = deleteDriverEvent;
    }

    @Override
    protected DriverDefEditorContent newContent() {
        return new DriverDefEditorContent();
    }

    @Override
    protected DriverDef deserializeDef(String source) {
        return DriverDefSerializer.deserialize(source);
    }

    @Override
    protected DriverDeploymentInfo readDeploymentInfo(String uuid) throws Exception {
        return runtimeManager.getDriverDeploymentInfo(uuid);
    }

    @Override
    protected String serializeDef(DriverDef def) {
        return DriverDefSerializer.serialize(def);
    }

    @Override
    protected void unDeploy(DriverDeploymentInfo deploymentInfo,
                            UnDeploymentOptions options) throws Exception {
        runtimeManager.unDeployDriver(deploymentInfo,
                                      options);
    }

    @Override
    protected void deploy(DriverDef def,
                          DeploymentOptions options) throws Exception {
        runtimeManager.deployDriver(def,
                                    options);
    }

    @Override
    protected void fireCreateEvent(final DriverDef def,
                                   final Module module) {
        newDriverEvent.fire(new NewDriverEvent(def,
                                               module,
                                               optionsFactory.getSafeSessionId(),
                                               optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected void fireCreateEvent(DriverDef def) {
        newDriverEvent.fire(new NewDriverEvent(def,
                                               optionsFactory.getSafeSessionId(),
                                               optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected void fireUpdateEvent(DriverDef def,
                                   Module module,
                                   DriverDef originalDef) {
        updateDriverEvent.fire(new UpdateDriverEvent(def,
                                                     module,
                                                     optionsFactory.getSafeSessionId(),
                                                     optionsFactory.getSafeIdentityName(),
                                                     originalDef));
    }

    @Override
    protected void fireDeleteEvent(DriverDef def,
                                   Module module) {
        deleteDriverEvent.fire(new DeleteDriverEvent(def,
                                                     module,
                                                     optionsFactory.getSafeSessionId(),
                                                     optionsFactory.getSafeIdentityName()));
    }

    @Override
    protected String buildFileName(DriverDef def) {
        return def.getName() + ".driver";
    }

    @Override
    protected Path create(final DriverDef driverDef,
                          final Path context) {

        try {
            validateDriver(driverDef);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }

        return super.create(driverDef,
                            context);
    }

    @Override
    public List<ValidationMessage> validate(DriverDef driverDef) {

        List<ValidationMessage> messages = new ArrayList<>();
        ValidationMessage message;
        try {
            validateDriver(driverDef);
        } catch (Exception e) {
            message = new ValidationMessage();
            message.setLevel(Level.ERROR);
            message.setText(e.getMessage());
            messages.add(message);
        }
        return messages;
    }

    private void validateDriver(DriverDef driverDef) throws Exception {

        final URI uri = artifactResolver.resolve(driverDef.getGroupId(),
                                                 driverDef.getArtifactId(),
                                                 driverDef.getVersion());

        if (uri == null) {
            throw new Exception("maven artifact was not found: " + driverDef.getGroupId() + ":"
                                        + driverDef.getArtifactId() + ":" + driverDef.getVersion());
        }

        final URL[] urls = {uri.toURL()};
        final URLClassLoader classLoader = new URLClassLoader(urls);

        try {
            Class driverClass = classLoader.loadClass(driverDef.getDriverClass());

            if (!Driver.class.isAssignableFrom(driverClass)) {
                throw new Exception("class: " + driverDef.getDriverClass() + " do not extend from: " + Driver.class.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new Exception("driver class: " + driverDef.getDriverClass() + " was not found in current gav");
        }
    }
}