package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Dependency;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencySelectorPopup;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.NewDependencyPopup;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesPresenterTest {

    private DependenciesPresenter dependenciesPresenter;

    @Mock
    private DependenciesPresenter.View view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private DependencySelectorPopup dependencySelectorPopup;

    @Mock
    private Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private NewDependencyPopup newDependencyPopup;

    @Mock
    private EnhancedDependenciesManager enhancedDependenciesManager;

    @Mock
    private ManagedInstance<DependenciesItemPresenter> presenters;

    private Promises promises = new SyncPromises();

    @Before
    public void before() {

        dependenciesPresenter = spy(new DependenciesPresenter(view,
                                                              promises,
                                                              menuItem,
                                                              dependencySelectorPopup,
                                                              settingsSectionChangeEvent,
                                                              newDependencyPopup,
                                                              enhancedDependenciesManager,
                                                              presenters));
    }

    @Test
    public void testSetup() {

        dependenciesPresenter.setup(mock(ProjectScreenModel.class));

        verify(view).init(eq(dependenciesPresenter));
        verify(dependencySelectorPopup).addSelectionHandler(any());
        verify(enhancedDependenciesManager).init(any(), any());
    }

    @Test
    public void testAdd() {
        dependenciesPresenter.add();
        verify(newDependencyPopup).show(any());
    }

    @Test
    public void testAddDependency() {
        final Dependency dependency = mock(Dependency.class);
        dependenciesPresenter.add(dependency);
        verify(enhancedDependenciesManager).addNew(eq(dependency));
    }

    @Test
    public void testAddAllToWhiteList() {
        dependenciesPresenter.model = mock(ProjectScreenModel.class);
        doReturn(new WhiteList()).when(dependenciesPresenter.model).getWhiteList();
        assertEquals(0, dependenciesPresenter.model.getWhiteList().size());

        dependenciesPresenter.addAllToWhiteList(new HashSet<>(Arrays.asList("foo", "bar")));

        assertEquals(2, dependenciesPresenter.model.getWhiteList().size());
        verify(enhancedDependenciesManager).update();
    }

    @Test
    public void testRemoveAllFromWhiteList() {
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));

        dependenciesPresenter.model = mock(ProjectScreenModel.class);
        doReturn(new WhiteList(packages)).when(dependenciesPresenter.model).getWhiteList();
        assertEquals(2, dependenciesPresenter.model.getWhiteList().size());

        dependenciesPresenter.removeAllFromWhiteList(packages);

        assertEquals(0, dependenciesPresenter.model.getWhiteList().size());
        verify(enhancedDependenciesManager).update();
    }

    @Test
    public void testAddFromRepository() {
        dependenciesPresenter.addFromRepository();
        verify(dependencySelectorPopup).show();
    }

    @Test
    public void testRemove() {
        final EnhancedDependency enhancedDependency = mock(EnhancedDependency.class);

        dependenciesPresenter.remove(enhancedDependency);

        verify(enhancedDependenciesManager).delete(eq(enhancedDependency));
    }
}