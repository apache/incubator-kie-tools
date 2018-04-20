package org.kie.workbench.common.screens.library.client.settings.sections.validation;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.ModuleRepositories;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidationPresenterTest {

    private ValidationPresenter validationPresenter;

    @Mock
    private ValidationView view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private ValidationPresenter.ValidationListPresenter validationItemPresenters;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {

        validationPresenter = spy(new ValidationPresenter(view,
                                                          promises,
                                                          menuItem,
                                                          settingsSectionChangeEvent,
                                                          validationItemPresenters));
    }

    @Test
    public void testSetup() {

        final ProjectScreenModel model = mock(ProjectScreenModel.class);
        doReturn(new ModuleRepositories()).when(model).getRepositories();

        validationPresenter.setup(model).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).init(eq(validationPresenter));
        verify(validationItemPresenters).setup(any(), any(), any());
    }

    @Test
    public void testCurrentHashCode() {

        final ProjectScreenModel model = mock(ProjectScreenModel.class);
        final ModuleRepositories repositories = new ModuleRepositories();
        doReturn(repositories).when(model).getRepositories();

        validationPresenter.setup(model);

        int currentHashCode = validationPresenter.currentHashCode();
        Assert.assertEquals(repositories.getRepositories().hashCode(), currentHashCode);

        repositories.getRepositories().add(mock(ModuleRepositories.ModuleRepository.class));
        int updatedHashCode = validationPresenter.currentHashCode();
        Assert.assertEquals(repositories.getRepositories().hashCode(), updatedHashCode);
    }
}