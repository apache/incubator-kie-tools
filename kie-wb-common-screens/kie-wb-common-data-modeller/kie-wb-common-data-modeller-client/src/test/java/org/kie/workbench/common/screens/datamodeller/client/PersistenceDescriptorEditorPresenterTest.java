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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRow;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ClassRowImpl;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PersistenceUnitPropertyGrid;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassList;
import org.kie.workbench.common.screens.datamodeller.client.type.PersistenceDescriptorType;
import org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector.DataSourceSelector;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PersistenceDescriptorEditorPresenterTest {

    @GwtMock
    private PersistenceDescriptorType persistenceDescriptorType;

    @GwtMock
    private PersistenceDescriptorEditorView view;

    @GwtMock
    private PersistenceUnitPropertyGrid propertyGrid;

    @GwtMock
    private DataSourceSelector dataSourceSelector;

    @GwtMock
    private ProjectClassList projectClassList;

    @GwtMock
    private ObservablePath path;

    @GwtMock
    private VersionRecordManager _versionRecordManager;

    private PersistenceDescriptorEditorPresenter presenter;

    @Mock
    private ValidationPopup validationPopup;

    @Mock
    private PersistenceDescriptorEditorService editorService;

    @Mock
    private PersistenceDescriptorService descriptorService;

    @Mock
    private DataModelerService dataModelerService;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    private CallerMock editorServiceCaller;

    @Before
    public void setup() {

        final PersistenceDescriptorEditorPresenter editorPresenter = makePersistenceDescriptorEditorPresenter();

        presenter = spy(editorPresenter);

        verify(view, times(1)).setPresenter(editorPresenter);
        when(this.editorService.loadContent(any(Path.class), anyBoolean())).thenReturn(createEditorContent());
        when(dataModelerService.findPersistableClasses(any(Path.class))).thenReturn(createPersistableClasses());
    }

    private PersistenceDescriptorEditorPresenter makePersistenceDescriptorEditorPresenter() {

        editorServiceCaller = new CallerMock<>(editorService);

        return new PersistenceDescriptorEditorPresenter(view,
                                                        persistenceDescriptorType,
                                                        dataSourceSelector,
                                                        editorServiceCaller,
                                                        new CallerMock<>(descriptorService),
                                                        new CallerMock<>(dataModelerService),
                                                        validationPopup) {
            {
                kieView = mock(KieEditorWrapperView.class);
                versionRecordManager = _versionRecordManager;
                overviewWidget = mock(OverviewWidgetPresenter.class);
                notification = notificationEvent;
            }

            protected void makeMenuBar() {

            }

            protected void addSourcePage() {

            }
        };
    }

    private void loadContent() {
        when(_versionRecordManager.getCurrentPath()).thenReturn(path);
        when(view.getPersistenceUnitProperties()).thenReturn(propertyGrid);
        when(view.getPersistenceUnitClasses()).thenReturn(projectClassList);

        presenter.onStartup(path, mock(PlaceRequest.class));
    }

    @Test
    public void testOnLoad() {

        loadContent();

        verify(view, times(1)).setReadOnly(false);
        verify(view, times(1)).hideBusyIndicator();

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add(new ClassRowImpl("Class1"));
        classRows.add(new ClassRowImpl("Class2"));
        when(projectClassList.getClasses()).thenReturn(classRows);

        assertEquals("2.0", presenter.getUpdatedContent().getDescriptorModel().getVersion());
        assertEquals("UnitName", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getName());
        assertEquals(TransactionType.JTA, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getTransactionType());
        assertEquals("ProviderClass", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getProvider());
        assertEquals("JTADataSource", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getJtaDataSource());
        assertEquals(2, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().size());
        assertEquals("Class1", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(0));
        assertEquals("Class2", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(1));
    }

    @Test
    public void onJTADataSourceChange() {

        loadContent();

        when(view.getJTADataSource()).thenReturn("NewJTADataSource");
        presenter.onJTADataSourceChange();

        verify(view, times(1)).getJTADataSource();
        assertEquals("NewJTADataSource", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getJtaDataSource());
    }

    @Test
    public void testOnJTATransactionsChange() {

        loadContent();

        when(view.getJTATransactions()).thenReturn(true);
        presenter.onJTATransactionsChange();

        verify(view, times(1)).getJTATransactions();
        assertEquals(TransactionType.JTA, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getTransactionType());
    }

    @Test
    public void testOnResourceLocalTransactionsChange() {

        loadContent();

        when(view.getResourceLocalTransactions()).thenReturn(true);
        presenter.onResourceLocalTransactionsChange();

        verify(view, times(2)).getResourceLocalTransactions();
        assertEquals(TransactionType.RESOURCE_LOCAL, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getTransactionType());
    }

    @Test
    public void testOnPersistenceUnitNameChange() {

        loadContent();

        when(view.getPersistenceUnitName()).thenReturn("NewUnitName");
        presenter.onPersistenceUnitNameChange();

        verify(view, times(1)).getPersistenceUnitName();
        assertEquals("NewUnitName", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getName());
    }

    @Test
    public void testOnPersistenceProviderChange() {

        loadContent();

        when(view.getPersistenceProvider()).thenReturn("NewPersistenceProvider");
        presenter.onPersistenceProviderChange();

        verify(view, times(1)).getPersistenceProvider();
        assertEquals("NewPersistenceProvider", presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getProvider());
    }

    @Test
    public void testOnLoadClasses() {

        loadContent();

        when(view.getPersistenceUnitClasses()).thenReturn(projectClassList);

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add(new ClassRowImpl("Class3"));
        classRows.add(new ClassRowImpl("Class4"));
        when(projectClassList.getClasses()).thenReturn(classRows);

        presenter.onLoadClasses();

        verify(view, times(1)).showBusyIndicator(anyString());

        verify(view, times(2)).hideBusyIndicator();
        verify(view, times(5)).getPersistenceUnitClasses();

        assertEquals(2, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().size());
        assertEquals(classRows.get(0).getClassName(), presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(0));
        assertEquals(classRows.get(1).getClassName(), presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(1));
    }

    @Test
    public void testOnLoadClass() {

        loadContent();

        when(view.getPersistenceUnitClasses()).thenReturn(projectClassList);

        List<ClassRow> classRows = new ArrayList<ClassRow>();
        classRows.add(new ClassRowImpl("Class3"));
        classRows.add(new ClassRowImpl("Class4"));
        projectClassList.setClasses(classRows);
        when(projectClassList.getClasses()).thenReturn(classRows);
        when(dataModelerService.isPersistableClass(eq("NewClass"), any(Path.class))).thenReturn(true);

        presenter.onLoadClass("NewClass");
        classRows.add(new ClassRowImpl("NewClass"));

        verify(view, times(1)).showBusyIndicator(anyString());

        verify(view, times(2)).hideBusyIndicator();
        verify(view, times(5)).getPersistenceUnitClasses();
        verify(projectClassList, times(1)).setNewClassName(null);
        verify(projectClassList, times(1)).setNewClassHelpMessage(null);

        assertEquals(3, presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().size());
        assertEquals(classRows.get(0).getClassName(), presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(0));
        assertEquals(classRows.get(1).getClassName(), presenter.getUpdatedContent().getDescriptorModel().getPersistenceUnit().getClasses().get(1));
        assertEquals(classRows.get(2).getClassName(), "NewClass");
    }

    @Test
    public void testOnValidateWithNoMessages() {
        loadContent();
        List<ValidationMessage> messages = new ArrayList<>();
        when(descriptorService.validate(path, presenter.getUpdatedContent().getDescriptorModel())).thenReturn(messages);
        presenter.onValidate().execute();
        verify(validationPopup, never()).showTranslatedMessages(anyList());
        verify(notificationEvent, times(1)).fire(new NotificationEvent(
                CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                NotificationEvent.NotificationType.SUCCESS));
    }

    @Test
    public void testOnValidateWithMessages() {
        loadContent();
        List<ValidationMessage> messages = new ArrayList<>();
        messages.add(mock(ValidationMessage.class));
        messages.add(mock(ValidationMessage.class));

        when(descriptorService.validate(path, presenter.getUpdatedContent().getDescriptorModel())).thenReturn(messages);
        presenter.onValidate().execute();
        verify(validationPopup, times(1)).showTranslatedMessages(messages);
    }

    @Test
    public void testGetUpdatedContent() {

        final PersistenceDescriptorEditorContent expectedContent = mock(PersistenceDescriptorEditorContent.class);

        doNothing().when(presenter).updateContent();
        doReturn(expectedContent).when(presenter).getContent();

        final PersistenceDescriptorEditorContent actualContent = presenter.getUpdatedContent();

        verify(presenter).updateContent();

        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void testGetContentSupplier() {

        final PersistenceDescriptorEditorContent expectedContent = mock(PersistenceDescriptorEditorContent.class);

        doReturn(expectedContent).when(presenter).getUpdatedContent();

        final PersistenceDescriptorEditorContent actualContent = presenter.getContentSupplier().get();

        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertEquals(editorServiceCaller, presenter.getSaveAndRenameServiceCaller());
    }

    private PersistenceDescriptorEditorContent createEditorContent() {
        PersistenceDescriptorEditorContent content = new PersistenceDescriptorEditorContent();
        PersistenceDescriptorModel model = new PersistenceDescriptorModel();
        model.setVersion("2.0");
        PersistenceUnitModel unitModel = new PersistenceUnitModel();
        model.setPersistenceUnit(unitModel);

        unitModel.setName("UnitName");
        unitModel.setTransactionType(TransactionType.JTA);
        unitModel.setProvider("ProviderClass");
        unitModel.setJtaDataSource("JTADataSource");
        List<String> classes = new ArrayList<String>();
        classes.add("Class1");
        classes.add("Class2");
        unitModel.setClasses(classes);

        content.setDescriptorModel(model);
        content.setOverview(new Overview());
        return content;
    }

    private List<String> createPersistableClasses() {
        List<String> classes = new ArrayList<String>();
        classes.add("Class3");
        classes.add("Class4");
        return classes;
    }
}
