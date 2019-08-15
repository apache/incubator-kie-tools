package org.dashbuilder.client.navigation.widget.editor;

import java.util.Collection;
import java.util.HashSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavItemEditCancelledEvent;
import org.dashbuilder.client.navigation.event.NavItemEditStartedEvent;
import org.dashbuilder.client.navigation.event.NavItemGotoEvent;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.client.navigation.event.NavTreeLoadedEvent;
import org.dashbuilder.client.navigation.impl.NavigationManagerImpl;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.service.NavigationServices;
import org.dashbuilder.navigation.workbench.NavSecurityController;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NavTreeEditorTest {

    @Mock
    NavTreeEditorView view;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PerspectiveTreeProvider perspectiveTreeProvider;

    @Mock
    EventSourceMock<NavItemEditStartedEvent> navItemEditStartedEvent;

    @Mock
    EventSourceMock<NavItemEditCancelledEvent> navItemEditCancelledEvent;

    @Mock
    LoadingBox loadingBox;

    @Mock
    EventSourceMock<NavItemGotoEvent> navItemGotoEvent;

    @Mock
    EventSourceMock<NavTreeLoadedEvent> navTreeLoadedEvent;

    @Mock
    EventSourceMock<NavTreeChangedEvent> navTreeChangedEvent;

    @Mock
    NavigationServices navServices;

    @Mock
    NavSecurityController navController;

    @Mock
    PlaceManager placeManager;

    @Mock
    TargetPerspectiveEditor targetPerspectiveEditor;

    @Mock
    PerspectivePluginManager perspectivePluginManager;

    @Mock
    NavItemDefaultEditorView navItemEditorView;

    @Mock
    NavRootNodeEditorView navRootNodeEditorView;

    @Mock
    SyncBeanDef<NavItemDefaultEditor> navItemEditorBeanDef;

    @Mock
    SyncBeanDef<NavRootNodeEditor> navRootNodeEditorBeanDef;

    @Mock
    Command updateCommand;

    @Mock
    Plugin perspectivePlugin;

    Collection<Plugin> perspectivePlugins = new HashSet<>();
    NavigationManager navigationManager;
    NavTreeEditor navTreeEditor;
    NavItemDefaultEditor navItemEditor;
    NavRootNodeEditor navRootNodeEditor;

    NavTree NAV_TREE = new NavTreeBuilder()
            .group("level1a", "level1a", "level1a", true)
            .group("level2a", "level2a", "level2a", true)
            .endGroup()
            .endGroup()
            .group("level1b", "level1b", "level1b", true)
            .group("level2b", "level2b", "level2b", true)
            .endGroup()
            .endGroup()
            .build();

    @Before
    public void setUp() {
        navigationManager = spy(new NavigationManagerImpl(new CallerMock<>(navServices),
                                                          navController,
                                                          navTreeLoadedEvent,
                                                          navTreeChangedEvent,
                                                          navItemGotoEvent));

        navTreeEditor = spy(new NavTreeEditor(view,
                                              navigationManager,
                                              beanManager,
                                              placeManager,
                                              perspectiveTreeProvider,
                                              targetPerspectiveEditor,
                                              perspectivePluginManager,
                                              navItemEditStartedEvent,
                                              navItemEditCancelledEvent,
                                              loadingBox));

        navTreeEditor.setChildEditorClass(NavRootNodeEditor.class);

        navItemEditor = spy(new NavItemDefaultEditor(navItemEditorView, beanManager, placeManager,
                                                     perspectiveTreeProvider, targetPerspectiveEditor, perspectivePluginManager,
                                                     navItemEditStartedEvent, navItemEditCancelledEvent));

        navRootNodeEditor = spy(new NavRootNodeEditor(navRootNodeEditorView, beanManager, placeManager,
                                                      perspectiveTreeProvider, targetPerspectiveEditor, perspectivePluginManager,
                                                      navItemEditStartedEvent, navItemEditCancelledEvent));

        when(beanManager.lookupBean(NavItemDefaultEditor.class)).thenReturn(navItemEditorBeanDef);
        when(beanManager.lookupBean(NavRootNodeEditor.class)).thenReturn(navRootNodeEditorBeanDef);
        when(navItemEditorBeanDef.newInstance()).thenReturn(navItemEditor);
        when(navRootNodeEditorBeanDef.newInstance()).thenReturn(navRootNodeEditor);

        when(navItemEditorView.getItemName()).thenReturn("editor1");
        when(navRootNodeEditorView.getItemName()).thenReturn("editor2");

        perspectivePlugins.add(perspectivePlugin);
        doAnswer(invocationOnMock -> {
            ParameterizedCommand callback = (ParameterizedCommand) invocationOnMock.getArguments()[0];
            callback.execute(perspectivePlugins);
            return null;
        }).when(perspectivePluginManager).getPerspectivePlugins(any());
    }

    @Test
    public void testNewPerspectiveEnabled() {
        navTreeEditor.getSettings().setNewPerspectiveEnabled(true);
        assertTrue(navTreeEditor.getSettings().isNewPerspectiveEnabled(NAV_TREE.getItemById("level1b")));

        navTreeEditor.setNewPerspectiveEnabled("level1b", false);
        assertFalse(navTreeEditor.getSettings().isNewPerspectiveEnabled(NAV_TREE.getItemById("level1b")));
        assertTrue(navTreeEditor.getSettings().isNewPerspectiveEnabled(NAV_TREE.getItemById("level2b")));

        navTreeEditor.setNewPerspectiveEnabled("level1b", false).applyToAllChildren();
        assertFalse(navTreeEditor.getSettings().isNewPerspectiveEnabled(NAV_TREE.getItemById("level1b")));
        assertFalse(navTreeEditor.getSettings().isNewPerspectiveEnabled(NAV_TREE.getItemById("level2b")));
    }

    @Test
    public void testNewDividerEnabled() {
        navTreeEditor.getSettings().setNewDividerEnabled(true);
        assertTrue(navTreeEditor.getSettings().isNewDividerEnabled(NAV_TREE.getItemById("level1b")));

        navTreeEditor.setNewDividerEnabled("level1b", false);
        assertFalse(navTreeEditor.getSettings().isNewDividerEnabled(NAV_TREE.getItemById("level1b")));
        assertTrue(navTreeEditor.getSettings().isNewDividerEnabled(NAV_TREE.getItemById("level2b")));

        navTreeEditor.setNewDividerEnabled("level1b", false).applyToAllChildren();
        assertFalse(navTreeEditor.getSettings().isNewDividerEnabled(NAV_TREE.getItemById("level1b")));
        assertFalse(navTreeEditor.getSettings().isNewDividerEnabled(NAV_TREE.getItemById("level2b")));
    }

    @Test
    public void testAllSubgroupsAllowed() {
        navTreeEditor.getSettings().setMaxLevels(-1);
        navTreeEditor.edit(NAV_TREE);

        verify(navTreeEditor, times(2)).createChildEditor(any());
        verify(navRootNodeEditor, times(2)).createChildEditor(any());
        verify(navItemEditor, never()).createChildEditor(any());
    }

    @Test
    public void testNoSubgroupsAllowed() {
        navTreeEditor.getSettings().setMaxLevels(1);
        navTreeEditor.edit(NAV_TREE);

        NavItem level1a = NAV_TREE.getItemById("level1a");
        NavItem level2a = NAV_TREE.getItemById("level2a");
        NavItem level1b = NAV_TREE.getItemById("level1b");
        NavItem level2b = NAV_TREE.getItemById("level2b");

        verify(navTreeEditor, never()).createChildEditor(eq(level1a));
        verify(navTreeEditor, never()).createChildEditor(eq(level1b));
        verify(navRootNodeEditor, never()).createChildEditor(eq(level2b));
        verify(navRootNodeEditor, never()).createChildEditor(eq(level2a));
    }

    @Test
    public void testSubgroupNotAllowed() {
        NavItem level1a = NAV_TREE.getItemById("level1a");
        NavItem level2a = NAV_TREE.getItemById("level2a");
        NavItem level1b = NAV_TREE.getItemById("level1b");
        NavItem level2b = NAV_TREE.getItemById("level2b");

        navTreeEditor.getSettings().setMaxLevels("level1a", 1);
        navTreeEditor.edit(NAV_TREE);

        verify(navTreeEditor).createChildEditor(eq(level1a));
        verify(navTreeEditor).createChildEditor(eq(level1b));
        verify(navRootNodeEditor).createChildEditor(eq(level2b));
        verify(navRootNodeEditor, never()).createChildEditor(eq(level2a));
    }

    @Test
    public void testOnlyThreeLevelsAllowed() {
        NavItem level1a = NAV_TREE.getItemById("level1a");
        NavItem level2a = NAV_TREE.getItemById("level2a");
        NavItem level1b = NAV_TREE.getItemById("level1b");
        NavItem level2b = NAV_TREE.getItemById("level2b");

        navTreeEditor.getSettings().setMaxLevels("root", 3);
        navTreeEditor.edit(NAV_TREE);

        verify(navTreeEditor).createChildEditor(eq(level1a));
        verify(navTreeEditor).createChildEditor(eq(level1b));
        verify(navTreeEditor, never()).createChildEditor(eq(level2a));
        verify(navTreeEditor, never()).createChildEditor(eq(level2b));
    }

    @Test
    public void testFinishEdition() {
        navTreeEditor.edit(NAV_TREE);

        navRootNodeEditor.newGroup();
        navRootNodeEditor.finishEdition();

        assertNull(navTreeEditor.getCurrentlyEditedItem());
    }

    @Test
    public void itShouldBeImpossibleToOpenMultipleNavItemEditorInputs() { // DASHBUILDE-217
        NavTree tree = NavFactory.get().createNavTree();
        navTreeEditor.edit(tree);

        NavItemEditor first = mock(NavItemEditor.class);
        NavItemEditor second = mock(NavItemEditor.class);
        NavItem firstItem = mock(NavItem.class);
        when(first.getNavItem()).thenReturn(firstItem);

        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(first));
        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(second));

        verify(first).cancelEdition();
    }

    @Test
    public void whenItemEditFinishedNavTreeEditorCleared() {
        navTreeEditor.edit(NAV_TREE);

        // When item editing starts the item is remembered in the tree
        NavItemEditor navItemEditor = navTreeEditor.newGroup();
        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(navItemEditor));
        assertEquals(navItemEditor, navTreeEditor.currentlyEditedItem.get());

        // When item editing finishes, it is cleaned from the navTreeEditor and view resets to "non-editing" state
        navItemEditor.onItemUpdated();
        assertFalse(navTreeEditor.currentlyEditedItem.isPresent());
    }

    @Test
    public void testNewGroup() {
        navTreeEditor.setOnUpdateCommand(updateCommand);
        navTreeEditor.edit(NAV_TREE);
        navTreeEditor.collapse();
        assertFalse(navTreeEditor.isExpanded());

        reset(view);
        NavItemEditor navItemEditor = navTreeEditor.newGroup();
        assertEquals(((NavGroup) navTreeEditor.getNavItem()).getChildren().size(), 2);
        assertTrue(navTreeEditor.isExpanded());
        verify(navItemEditor).startEdition();
        verify(view, times(3)).addChild(any());
        verify(updateCommand, never()).execute();

        when(navRootNodeEditorView.getItemName()).thenReturn("A");
        navItemEditor.onChangesOk();
        verify(updateCommand).execute();
    }

    @Test
    public void testNewPerspective() {
        navTreeEditor.setOnUpdateCommand(updateCommand);
        navTreeEditor.edit(NAV_TREE);
        navTreeEditor.collapse();
        assertFalse(navTreeEditor.isExpanded());

        reset(view);
        NavItemEditor navItemEditor = navTreeEditor.newPerspective();
        assertEquals(((NavGroup) navTreeEditor.getNavItem()).getChildren().size(), 2);
        assertTrue(navTreeEditor.isExpanded());
        verify(navItemEditor).startEdition();
        verify(view, times(3)).addChild(any());
        verify(updateCommand, never()).execute();
    }

    @Test
    public void testNewDivider() {
        navTreeEditor.setOnUpdateCommand(updateCommand);
        navTreeEditor.edit(NAV_TREE);
        navTreeEditor.collapse();
        assertFalse(navTreeEditor.isExpanded());

        reset(view);
        navTreeEditor.newDivider();
        assertTrue(navTreeEditor.isExpanded());
        assertEquals(((NavGroup) navTreeEditor.getNavItem()).getChildren().size(), 3);
        verify(view, times(3)).addChild(any());
        verify(updateCommand).execute();
    }

    @Test
    public void testSaveAndCancel() {
        navTreeEditor.edit(NAV_TREE);
        NavItemEditor newEditor = navTreeEditor.newGroup();
        newEditor.onChangesOk();
        navTreeEditor.onSaveClicked();

        NavTree navTree = navTreeEditor.getNavTree();
        assertNotNull(navTree.getItemById(newEditor.getNavItem().getId()));
    }

    @Test
    public void testNewPerspectiveActionAvailable() {
        NavItemEditor navItemEditor = navTreeEditor.newGroup();
        assertTrue(navItemEditor.isNewPerspectiveEnabled());

        perspectivePlugins.clear();
        assertFalse(navItemEditor.isNewPerspectiveEnabled());
    }

    @Test
    public void testCancelLastEditedItem() {
        NavItemEditor navItemEditorA = mock(NavItemEditor.class);
        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(navItemEditorA));
        assertEquals(navTreeEditor.getCurrentlyEditedItem(), navItemEditorA);

        // No need for cancel if the same item is edited again
        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(navItemEditorA));
        verify(navItemEditorA, never()).cancelEdition();

        // Last edited item must be cancelled
        NavItemEditor navItemEditorB = mock(NavItemEditor.class);
        navTreeEditor.onItemEditStarted(new NavItemEditStartedEvent(navItemEditorB));
        assertEquals(navTreeEditor.getCurrentlyEditedItem(), navItemEditorB);
        verify(navItemEditorA).cancelEdition();
    }

    @Test
    public void testEditIsNotInvokedTwiceAfterCancel() {
        navTreeEditor.edit(NAV_TREE);

        // When edit is cancelled avoid the parent's editor to invoke edit again
        reset(navItemEditor);
        navItemEditor.startEdition();
        navItemEditor.cancelEdition();
        verify(navItemEditor, times(1)).edit(any());
    }

    @Test
    public void testOnNewTreeClicked() {

        navTreeEditor.newTree();

        verify(navTreeEditor).saveDefaultNavTree();
        verify(navTreeEditor).newGroup();
    }

    @Test
    public void testSaveDefaultNavTreeWhenNavigationManagerDoesNotHaveNavTree() {

        doReturn(false).when(navigationManager).hasNavTree();

        navTreeEditor.saveDefaultNavTree();

        verify(navTreeEditor).showLoading();
        verify(navigationManager).saveNavTree(any(NavTree.class), any(Command.class));
    }

    @Test
    public void testSaveDefaultNavTreeWhenNavigationManagerHasNavTree() {

        doReturn(true).when(navigationManager).hasNavTree();

        navTreeEditor.saveDefaultNavTree();

        verify(navTreeEditor, never()).showLoading();
        verify(navigationManager, never()).saveNavTree(any(NavTree.class), any(Command.class));
    }

    @Test
    public void testShowLoading() {

        navTreeEditor.showLoading();

        verify(loadingBox).show();
    }

    @Test
    public void testHideLoading() {

        navTreeEditor.hideLoading();

        verify(loadingBox).hide();
    }
}
