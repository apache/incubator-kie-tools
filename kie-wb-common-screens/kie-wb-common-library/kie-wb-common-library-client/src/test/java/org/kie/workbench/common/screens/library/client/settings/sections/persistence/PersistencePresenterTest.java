package org.kie.workbench.common.screens.library.client.settings.sections.persistence;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersistencePresenterTest {

    private PersistencePresenter persistencePresenter;

    @Mock
    private PersistencePresenter.View view;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private PersistenceDescriptorEditorService editorService;

    @Mock
    private DataModelerService dataModelerService;

    @Mock
    private PersistencePresenter.PropertiesListPresenter propertiesListPresenter;

    @Mock
    private PersistencePresenter.PersistableDataObjectsListPresenter persistableDataObjectsListPresenter;

    @Mock
    private Module module;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {

        final ObservablePath observablePath = mock(ObservablePath.class);
        doReturn(observablePath).when(observablePath).wrap(any());
        doReturn(observablePath).when(observablePaths).get();

        Path path = mock(Path.class);
        when(path.toURI()).thenReturn("root");
        when(module.getRootPath()).thenReturn(path);
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(mock(WorkspaceProject.class)));
        when(projectContext.getActiveModule()).thenReturn(Optional.of(module));

        persistencePresenter = spy(new PersistencePresenter(view,
                                                            projectContext,
                                                            promises,
                                                            menuItem,
                                                            notificationEvent,
                                                            settingsSectionChangeEvent,
                                                            observablePaths,
                                                            new CallerMock<>(editorService),
                                                            new CallerMock<>(dataModelerService),
                                                            propertiesListPresenter,
                                                            persistableDataObjectsListPresenter));

    }

    @Test
    public void testSetup() {

        final PersistenceDescriptorEditorContent model = spy(newPersistenceDescriptorEditorContent());
        doReturn(model).when(editorService).loadContent(any(),
                                                        anyBoolean());

        persistencePresenter.setup(mock(ProjectScreenModel.class)).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).init(eq(persistencePresenter));
        verify(editorService).loadContent(any(),
                                          anyBoolean());

        verify(propertiesListPresenter).setup(any(),
                                              any(),
                                              any());
        verify(persistableDataObjectsListPresenter).setup(any(),
                                                          any(),
                                                          any());
        verify(view).setPersistenceUnit(model.getDescriptorModel().getPersistenceUnit().getName());
        verify(view).setPersistenceProvider(model.getDescriptorModel().getPersistenceUnit().getProvider());
        verify(view).setDataSource(eq(model.getDescriptorModel().getPersistenceUnit().getJtaDataSource()));
    }

    @Test
    public void testSetupFail() {

        final RuntimeException testException = new RuntimeException("Test exception");
        doThrow(testException).when(editorService).loadContent(any(),
                                                               anyBoolean());

        persistencePresenter.setup(mock(ProjectScreenModel.class)).then(i -> {
            Assert.fail("Promise should've not been resolved!");
            return promises.resolve();
        }).catch_(o -> promises.catchOrExecute(o, e -> promises.resolve(), e -> {
            Assert.fail("RPC failed so default RPC error handler was called.");
            return promises.resolve();
        }));
    }

    @Test
    public void testAddProperty() {
        final Property property = new Property("Name",
                                               "Value");

        persistencePresenter.add(property);

        verify(propertiesListPresenter).add(eq(property));
    }

    @Test
    public void testAddPersistableDataObject() {
        final String className = "ClassName";

        persistencePresenter.add(className);

        verify(persistableDataObjectsListPresenter).add(eq(new PersistableDataObject(className)));
    }

    @Test
    public void testAddAllProjectsPersistableDataObjects() {
        persistencePresenter.persistenceDescriptorEditorContent = newPersistenceDescriptorEditorContent();
        doReturn(Arrays.asList("NewClass1",
                               "NewClass2")).when(dataModelerService).findPersistableClasses(any());

        persistencePresenter.addAllProjectsPersistableDataObjects();

        verify(dataModelerService).findPersistableClasses(any());
        verify(persistencePresenter,
               never()).add(eq("Class1"));
        verify(persistencePresenter).add(eq("NewClass1"));
        verify(persistencePresenter).add(eq("NewClass1"));
    }

    @Test
    public void testAddNewProperty() {
        persistencePresenter.addNewProperty();
        verify(propertiesListPresenter).add(eq(new Property("","")));
    }

    @Test
    public void testAddNewPersistableDataObject() {
        persistencePresenter.addNewPersistableDataObject();
        verify(persistableDataObjectsListPresenter).add(eq(new PersistableDataObject("")));
    }

    @Test
    public void testSetPersistenceUnit() {
        final PersistenceDescriptorEditorContent model = newPersistenceDescriptorEditorContent();
        persistencePresenter.persistenceDescriptorEditorContent = model;
        persistencePresenter.setPersistenceUnit("PersistenceUnit");

        Assert.assertEquals("PersistenceUnit",
                            model.getDescriptorModel().getPersistenceUnit().getName());
        verify(persistencePresenter).fireChangeEvent();
    }

    @Test
    public void testSetPersistenceProvider() {
        final PersistenceDescriptorEditorContent model = newPersistenceDescriptorEditorContent();
        persistencePresenter.persistenceDescriptorEditorContent = model;
        persistencePresenter.setPersistenceProvider("PersistenceProvider");

        Assert.assertEquals("PersistenceProvider",
                            model.getDescriptorModel().getPersistenceUnit().getProvider());
        verify(persistencePresenter).fireChangeEvent();
    }

    @Test
    public void testSetDataSource() {
        final PersistenceDescriptorEditorContent model = newPersistenceDescriptorEditorContent();
        persistencePresenter.persistenceDescriptorEditorContent = model;
        persistencePresenter.setDataSource("DataSource");

        Assert.assertEquals("DataSource",
                            model.getDescriptorModel().getPersistenceUnit().getJtaDataSource());
        verify(persistencePresenter).fireChangeEvent();
    }

    @Test
    public void testSave() {
        doReturn(promises.resolve()).when(persistencePresenter).save(eq("Test comment"));

        persistencePresenter.save("Test comment",
                                  null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(persistencePresenter).save(eq("Test comment"));
        verify(persistencePresenter,
               never()).setup();
        verify(notificationEvent,
               never()).fire(any());
    }

    @Test
    public void testSaveWithConcurrentUpdate() {
        persistencePresenter.concurrentPersistenceXmlUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doReturn(promises.resolve()).when(persistencePresenter).setup();

        persistencePresenter.save("Test comment",
                                  null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(persistencePresenter,
               never()).save(any());
        verify(persistencePresenter).setup();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSaveModel() {
        persistencePresenter.persistenceDescriptorEditorContent = newPersistenceDescriptorEditorContent();
        doReturn(mock(Path.class)).when(editorService).save(any(),
                                                            any(),
                                                            any(),
                                                            eq("Test comment"));

        persistencePresenter.save("Test comment").catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(editorService).save(any(),
                                   any(),
                                   any(),
                                   eq("Test comment"));
    }

    private PersistenceDescriptorEditorContent newPersistenceDescriptorEditorContent() {
        final PersistenceDescriptorEditorContent content = new PersistenceDescriptorEditorContent();
        final PersistenceDescriptorModel model = new PersistenceDescriptorModel();
        model.setVersion("2.0");

        final PersistenceUnitModel unitModel = new PersistenceUnitModel();
        model.setPersistenceUnit(unitModel);

        unitModel.setName("UnitName");
        unitModel.setTransactionType(TransactionType.JTA);
        unitModel.setProvider("ProviderClass");
        unitModel.setJtaDataSource("JTADataSource");
        unitModel.setClasses(Arrays.asList(new PersistableDataObject("Class1"),
                                           new PersistableDataObject("Class2")));

        content.setDescriptorModel(model);
        content.setOverview(new Overview());

        return content;
    }
}