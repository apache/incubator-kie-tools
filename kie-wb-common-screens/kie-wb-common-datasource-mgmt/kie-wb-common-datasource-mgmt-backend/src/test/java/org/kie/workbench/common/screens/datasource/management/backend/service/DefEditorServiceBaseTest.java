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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.Def;
import org.kie.workbench.common.screens.datasource.management.model.DefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.PathNamingService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class DefEditorServiceBaseTest {

    protected static final String SESSION_ID = "sessionId";

    protected static final String IDENTITY = "userId";

    protected static final String GLOBAL_URI = "default://master@datasources/";

    protected static final String PROJECT_URI = "default://master@TestRepo/module/src/resources/META-INF";

    protected static final String FILE_URI = "default://master@TestRepo/module/src/resources/META-INF/someFile.txt";

    protected static final String COMMENT = "Some comment";

    @Mock
    protected DataSourceRuntimeManager runtimeManager;

    @Mock
    protected DataSourceServicesHelper serviceHelper;

    @Mock
    protected DefRegistry defRegistry;

    @Mock
    protected IOService ioService;

    @Mock
    protected KieModuleService moduleService;

    @Mock
    protected CommentedOptionFactory optionsFactory;

    @Mock
    protected PathNamingService pathNamingService;

    @Mock
    protected MavenArtifactResolver artifactResolver;

    @Mock
    protected Path path;

    @Mock
    protected Path renamedPath;

    @Mock
    protected KieModule module;

    protected AbstractDefEditorService editorService;

    @Mock
    protected DataSourceDeploymentInfo dataSourceDeploymentInfo;

    @Mock
    protected DriverDeploymentInfo driverDeploymentInfo;

    @Before
    public void setup() {
        when(optionsFactory.getSafeSessionId()).thenReturn(SESSION_ID);
        when(optionsFactory.getSafeIdentityName()).thenReturn(IDENTITY);
        when(serviceHelper.getDefRegistry()).thenReturn(defRegistry);
    }

    protected abstract DefEditorContent getExpectedContent();

    protected abstract String getExpectedDefString();

    protected abstract String getExpectedFileName();

    protected abstract Def getExpectedDef();

    protected abstract Def getOriginalDef();

    protected abstract String getOriginalDefString();

    @Test
    public void testLoadContent() {

        when(path.toURI()).thenReturn(FILE_URI);
        org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        String source = getExpectedDefString();
        when(ioService.readAllString(nioPath)).thenReturn(source);
        when(moduleService.resolveModule(path)).thenReturn(module);

        DefEditorContent result = editorService.loadContent(path);

        //The returned content should be the expected.
        assertEquals(getExpectedContent(),
                     result);
    }

    @Test
    public void testCreateInModule() {
        testCreate(false);
    }

    @Test
    public void testCreateGlobal() {
        testCreate(true);
    }

    private void testCreate(boolean global) {

        if (global) {
            when(path.toURI()).thenReturn(GLOBAL_URI);
        } else {
            when(path.toURI()).thenReturn(PROJECT_URI);
            when(serviceHelper.getModuleDataSourcesContext(module)).thenReturn(path);
        }

        //expected target path
        org.uberfire.java.nio.file.Path targetNioPath = Paths.convert(path).resolve(getExpectedFileName());
        //expected source
        String source = getExpectedDefString();

        when(serviceHelper.getGlobalDataSourcesContext()).thenReturn(path);
        when(ioService.exists(targetNioPath)).thenReturn(false);

        if (global) {
            editorService.createGlobal(getExpectedDef());
        } else {
            editorService.create(getExpectedDef(),
                                 module);
        }

        //we wants that:
        // 1) the expected file was saved.
        verify(ioService,
               times(1)).write(eq(targetNioPath),
                               eq(source),
                               any(CommentedOption.class));
        // 2) the definition was registered
        verify(defRegistry,
               times(1)).setEntry(Paths.convert(targetNioPath),
                                  getExpectedDef());
        // 3) the definition was deployed, and 4) the notification was fired.
        verifyCreateConditions(global);
    }

    protected abstract void verifyCreateConditions(boolean global);

    @Test
    public void testSave() {
        //The name was not chanted.
        getExpectedContent().getDef().setName(getOriginalDef().getName());
        String originalSource = getOriginalDefString();

        //expected path
        when(path.toURI()).thenReturn(FILE_URI);
        org.uberfire.java.nio.file.Path targetNioPath = Paths.convert(path);

        when(ioService.readAllString(targetNioPath)).thenReturn(originalSource);
        when(moduleService.resolveModule(path)).thenReturn(module);

        editorService.save(path,
                           getExpectedContent(),
                           COMMENT);

        //we wants that:
        // 1) previous definition was un-registered and the expected file was saved
        verify(defRegistry,
               times(1)).invalidateCache(path);
        verify(ioService,
               times(1)).write(eq(targetNioPath),
                               eq(getExpectedDefString()),
                               any(CommentedOption.class));
        verify(optionsFactory,
               times(1)).makeCommentedOption(COMMENT);
        // 2) the new definition was registered.
        verify(defRegistry,
               times(1)).setEntry(path,
                                  getExpectedDef());

        // 3) the definition was deployed and 4) the notification was fired.
        verifySaveConditions();
    }

    @Test
    public void testSaveWithNameModified() {
        String originalSource = getOriginalDefString();
        //expected path
        when(path.toURI()).thenReturn(FILE_URI);
        org.uberfire.java.nio.file.Path targetNioPath = Paths.convert(path);

        //rename path
        when(renamedPath.toURI()).thenReturn(FILE_URI);
        when(pathNamingService.buildTargetPath(path,
                                               getExpectedDef().getName())).thenReturn(renamedPath);
        org.uberfire.java.nio.file.Path renamedNioPath = Paths.convert(renamedPath);

        when(ioService.readAllString(targetNioPath)).thenReturn(originalSource);
        when(moduleService.resolveModule(path)).thenReturn(module);

        editorService.save(path,
                           getExpectedContent(),
                           COMMENT);

        //we wants that:
        //1) previous definition was un-registered and the expected file was saved
        verify(defRegistry,
               times(1)).invalidateCache(path);
        verify(ioService,
               times(1)).write(eq(targetNioPath),
                               eq(getExpectedDefString()),
                               any(CommentedOption.class));
        //2) the expected file was renamed and the new definition was registered.
        verify(ioService,
               timeout(1)).move(eq(targetNioPath),
                                eq(renamedNioPath),
                                any(CommentedOption.class));
        verify(optionsFactory,
               times(2)).makeCommentedOption(COMMENT);
        verify(defRegistry,
               times(1)).setEntry(Paths.normalizePath(renamedPath),
                                  getExpectedDef());

        //3) the definition was deployed and 4) the notification was fired.
        verifySaveConditions();
    }

    protected abstract void verifySaveConditions();

    @Test
    public void testDelete() throws Exception {
        //current file
        String content = getExpectedDefString();
        when(path.toURI()).thenReturn(FILE_URI);
        org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        when(ioService.readAllString(nioPath)).thenReturn(content);
        when(ioService.exists(nioPath)).thenReturn(true);

        when(moduleService.resolveModule(path)).thenReturn(module);

        when(runtimeManager.getDataSourceDeploymentInfo(
                getExpectedDef().getUuid())).thenReturn(dataSourceDeploymentInfo);
        when(runtimeManager.getDriverDeploymentInfo(
                getExpectedDef().getUuid())).thenReturn(driverDeploymentInfo);

        editorService.delete(path,
                             COMMENT);

        //we wants that:
        //1) the file was deleted, and the definition was un-registered
        verify(ioService,
               times(1)).delete(eq(Paths.convert(path)),
                                any(CommentedOption.class));
        verify(optionsFactory,
               times(1)).makeCommentedOption(COMMENT);
        verify(defRegistry,
               times(1)).invalidateCache(path);
        //2) the definition was un-deployed, and 3) the delete notification was fired.
        verifyDeleteConditions();
    }

    protected abstract void verifyDeleteConditions();
}