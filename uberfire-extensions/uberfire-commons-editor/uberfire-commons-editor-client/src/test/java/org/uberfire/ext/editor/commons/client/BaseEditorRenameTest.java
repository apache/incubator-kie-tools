/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButton;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BaseEditorRenameTest {

    private static VersionMenuDropDownButton versionMenuDropDownButton;

    private CallerMock<VersionService> versionServiceCaller;

    private static BaseEditor baseEditor;

    @Mock
    private static VersionRecordManager versionRecordManager;
    @Mock
    private RestoreUtil restoreUtil;
    @Mock
    private PlaceRequest placeRequest;
    @Mock
    private ClientResourceType clientResourceType;
    @Mock
    private BaseEditorView baseView;
    @Mock
    private VersionService versionService;
    @Mock
    private BasicFileMenuBuilder menuBuilder;
    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification = new EventSourceMock<>();
    @Mock
    private ObservablePath observablePath;
    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        versionMenuDropDownButton = new VersionMenuDropDownButton();

        versionServiceCaller = new CallerMock<>(versionService);

        baseEditor = new BaseEditor(versionRecordManager,
                                    baseView,
                                    menuBuilder,
                                    changeTitleNotification) {
            {
                promises = BaseEditorRenameTest.this.promises;
            }

            @Override
            protected void loadContent() {
            }

            @Override
            protected SaveAndRenameCommandBuilder getSaveAndRenameCommandBuilder() {
                return new SaveAndRenameCommandBuilder<>(null, null, null, null);
            }

            @Override
            public Validator getRenameValidator() {
                return mock(Validator.class);
            }

            @Override
            protected Caller<? extends SupportsSaveAndRename> getSaveAndRenameServiceCaller() {
                return mock(Caller.class);
            }
        };
        when(restoreUtil.createObservablePath(any(),
                                              any())).thenReturn(observablePath);
        baseEditor.init(observablePath,
                        placeRequest,
                        clientResourceType,
                        true,
                        true,
                        MenuItems.SAVE,
                        MenuItems.DELETE,
                        MenuItems.RENAME,
                        MenuItems.COPY,
                        MenuItems.HISTORY);
    }

    //a reproducer for AF-497
    @Test
    public void testVersionRecordManagerIsInitializedOnRename() {
        //clear the interaction on versionRecordManager from the baseEditor init method
        Mockito.reset(versionRecordManager);

        when(versionRecordManager.getCurrentPath()).thenReturn(observablePath);
        baseEditor.onRename();

        verify(versionRecordManager).init(any(),
                                          eq(observablePath),
                                          any());
    }
}
