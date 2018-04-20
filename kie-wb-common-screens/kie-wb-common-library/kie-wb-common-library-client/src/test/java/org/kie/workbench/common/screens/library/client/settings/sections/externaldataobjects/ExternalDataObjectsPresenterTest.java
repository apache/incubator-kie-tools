package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.ProjectImports;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.AddImportPopup;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalDataObjectsPresenterTest {

    private ExternalDataObjectsPresenter externalDataObjectsPresenter;

    @Mock
    private ExternalDataObjectsPresenter.View view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @GwtMock
    private AddImportPopup addImportPopup;

    @Mock
    private ExternalDataObjectsPresenter.ImportsListPresenter itemPresenters;

    @Mock
    private EventSourceMock<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {
        externalDataObjectsPresenter = spy(new ExternalDataObjectsPresenter(view,
                                                                            promises,
                                                                            menuItem,
                                                                            addImportPopup,
                                                                            itemPresenters,
                                                                            settingsSectionChangeEvent));
    }

    @Test
    public void testSetup() {
        final ProjectScreenModel model = mock(ProjectScreenModel.class);
        doReturn(new ProjectImports()).when(model).getProjectImports();

        externalDataObjectsPresenter.setup(model).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).init(eq(externalDataObjectsPresenter));
        verify(itemPresenters).setup(any(), any(), any());
    }

    @Test
    public void testOpenAddPopup() {
        externalDataObjectsPresenter.openAddPopup();
        verify(addImportPopup).show();
        verify(addImportPopup).setCommand(any());
    }

    @Test
    public void testAddImport() {
        externalDataObjectsPresenter.addImport("Test");
        verify(itemPresenters).add(any());
        verify(externalDataObjectsPresenter).fireChangeEvent();
    }
}