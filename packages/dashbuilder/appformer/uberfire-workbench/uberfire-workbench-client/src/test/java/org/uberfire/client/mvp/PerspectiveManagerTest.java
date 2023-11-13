/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.LayoutPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveManagerTest {

    @Mock
    PlaceManager placeManager;
    @Mock
    PanelManager panelManager;
    @Mock
    ActivityManager activityManager;
    @Mock
    WorkbenchServicesProxy wbServices;
    @Mock
    Event<PerspectiveChange> perspectiveChangeEvent;

    @Mock
    ActivityBeansCache activityBeansCache;

    @Mock
    SyncBeanManager iocManager;

    @Spy
    @InjectMocks
    PerspectiveManagerImpl perspectiveManager;

    // useful mocks provided by setup method
    private PerspectiveDefinition ozDefinition;
    private PerspectiveActivity oz;
    private PlaceRequest pr;
    private ParameterizedCommand<PerspectiveDefinition> doWhenFinished;
    private ParameterizedCommand<PerspectiveDefinition> doAfterFetch;
    private Command doWhenFinishedSave;
    private PerspectiveManagerImpl.FetchPerspectiveCommand fetchCommand;
    private List<PartDefinitionImpl> partDefinitionsRoot;
    private List<PartDefinitionImpl> partDefinitionRootChild1;
    private List<PartDefinitionImpl> partDefinitionRootChild2;
    private List<PartDefinitionImpl> partDefinitionRootChild2Child;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        ozDefinition = new PerspectiveDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());

        oz = mock(PerspectiveActivity.class);
        pr = mock(PlaceRequest.class);
        when(oz.getDefaultPerspectiveLayout()).thenReturn(ozDefinition);
        when(oz.getIdentifier()).thenReturn("oz_perspective");
        when(oz.isTransient()).thenReturn(true);
        doWhenFinished = mock(ParameterizedCommand.class);
        doAfterFetch = spy(new ParameterizedCommand<PerspectiveDefinition>() {

            @Override
            public void execute(final PerspectiveDefinition parameter) {}
        });
        doWhenFinishedSave = mock(Command.class);

        fetchCommand = spy(perspectiveManager.new FetchPerspectiveCommand(pr,
                oz,
                doAfterFetch));

        // simulate "finished saving" callback on wbServices.save()
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Command callback = (Command) invocation.getArguments()[2];
                callback.execute();
                return null;
            }
        }).when(wbServices).save(any(),
                any(),
                any());

        // simulate "no saved state found" result on wbServices.loadPerspective()
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ParameterizedCommand<?> callback = (ParameterizedCommand<?>) invocation.getArguments()[1];
                callback.execute(null);
                return null;
            }
        }).when(wbServices).loadPerspective(any(),
                any());

        // XXX should look at why PanelManager needs to return an alternative panel sometimes.
        // would be better if addWorkbenchPanel returned void.
        when(panelManager.addWorkbenchPanel(any(),
                any(),
                any())).thenAnswer(new Answer<PanelDefinition>() {

                    @Override
                    public PanelDefinition answer(InvocationOnMock invocation) {
                        return (PanelDefinition) invocation.getArguments()[1];
                    }
                });
    }

    @Test
    public void shouldReportNullPerspectiveInitially() throws Exception {
        assertNull(perspectiveManager.getCurrentPerspective());
    }

    @Test
    public void shouldReportNewPerspectiveAsCurrentAfterSwitching() throws Exception {
        perspectiveManager.switchToPerspective(pr,
                oz,
                doWhenFinished);

        assertSame(oz,
                perspectiveManager.getCurrentPerspective());
    }

    @Test
    public void shouldSaveNonTransientPerspectives() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl(DockingWorkbenchPanelPresenter.class
                .getName());

        PerspectiveActivity kansas = mock(PerspectiveActivity.class);
        when(kansas.getDefaultPerspectiveLayout()).thenReturn(kansasDefinition);
        when(kansas.getIdentifier()).thenReturn("kansas_perspective");
        when(kansas.isTransient()).thenReturn(false);

        perspectiveManager.switchToPerspective(pr,
                kansas,
                doWhenFinished);
        perspectiveManager.savePerspectiveState(doWhenFinishedSave);

        verify(wbServices).save(eq("kansas_perspective"),
                eq(kansasDefinition),
                eq(doWhenFinishedSave));
    }

    @Test
    public void shouldNotSaveTransientPerspectives() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl(DockingWorkbenchPanelPresenter.class
                .getName());

        PerspectiveActivity kansas = mock(PerspectiveActivity.class);
        when(kansas.getDefaultPerspectiveLayout()).thenReturn(kansasDefinition);
        when(kansas.isTransient()).thenReturn(true);

        perspectiveManager.switchToPerspective(pr,
                kansas,
                doWhenFinished);
        perspectiveManager.savePerspectiveState(doWhenFinishedSave);

        verify(wbServices,
                never()).save(any(String.class),
                        eq(kansasDefinition),
                        any(Command.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLoadNewNonTransientPerspectiveState() throws Exception {
        when(oz.isTransient()).thenReturn(false);

        perspectiveManager.switchToPerspective(pr,
                                               oz,
                                               doWhenFinished);

        verify(wbServices).loadPerspective(eq("oz_perspective"),
                                           any(ParameterizedCommand.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotLoadNewTransientPerspectiveState() throws Exception {
        when(oz.isTransient()).thenReturn(true);

        perspectiveManager.switchToPerspective(pr,
                                               oz,
                                               doWhenFinished);

        verify(wbServices,
               never()).loadPerspective(eq("oz_perspective"),
                                        any(ParameterizedCommand.class));
    }

    @Test
    public void shouldExecuteCallbackWhenDoneLaunchingPerspective() throws Exception {
        perspectiveManager.switchToPerspective(pr,
                oz,
                doWhenFinished);

        verify(doWhenFinished).execute(ozDefinition);
    }

    @Test
    public void shouldFireEventWhenLaunchingNewPerspective() throws Exception {
        perspectiveManager.switchToPerspective(pr,
                oz,
                doWhenFinished);

        verify(perspectiveChangeEvent).fire(refEq(new PerspectiveChange(pr,
                ozDefinition,
                "oz_perspective")));
    }

    @Test
    public void shouldAddAllPanelsToPanelManager() throws Exception {
        PanelDefinition westPanel = new PanelDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());
        PanelDefinition eastPanel = new PanelDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());
        PanelDefinition northPanel = new PanelDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());
        PanelDefinition southPanel = new PanelDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());
        PanelDefinition southWestPanel = new PanelDefinitionImpl(DockingWorkbenchPanelPresenter.class.getName());

        ozDefinition.getRoot().appendChild(CompassPosition.WEST,
                westPanel);
        ozDefinition.getRoot().appendChild(CompassPosition.EAST,
                eastPanel);
        ozDefinition.getRoot().appendChild(CompassPosition.NORTH,
                northPanel);
        ozDefinition.getRoot().appendChild(CompassPosition.SOUTH,
                southPanel);
        southPanel.appendChild(CompassPosition.WEST,
                southWestPanel);

        // we assume this will be set correctly (verified elsewhere)
        perspectiveManager.switchToPerspective(pr,
                oz,
                doWhenFinished);

        verify(panelManager).addWorkbenchPanel(ozDefinition.getRoot(),
                westPanel,
                CompassPosition.WEST);
        verify(panelManager).addWorkbenchPanel(ozDefinition.getRoot(),
                eastPanel,
                CompassPosition.EAST);
        verify(panelManager).addWorkbenchPanel(ozDefinition.getRoot(),
                northPanel,
                CompassPosition.NORTH);
        verify(panelManager).addWorkbenchPanel(ozDefinition.getRoot(),
                southPanel,
                CompassPosition.SOUTH);
        verify(panelManager).addWorkbenchPanel(southPanel,
                southWestPanel,
                CompassPosition.WEST);
    }

    @Test
    public void shouldDestroyAllOldPanelsWhenSwitchingRoot() throws Exception {
        PerspectiveDefinition fooPerspectiveDef = new PerspectiveDefinitionImpl(DockingWorkbenchPanelPresenter.class
                .getName());
        PanelDefinition rootPanel = fooPerspectiveDef.getRoot();
        PanelDefinition fooPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        PanelDefinition fooChildPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        PanelDefinition barPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());
        PanelDefinition bazPanel = new PanelDefinitionImpl(LayoutPanelPresenter.class.getName());

        rootPanel.appendChild(fooPanel);
        rootPanel.appendChild(barPanel);
        rootPanel.appendChild(bazPanel);

        fooPanel.appendChild(fooChildPanel);

        PerspectiveActivity fooPerspective = mock(PerspectiveActivity.class);
        when(fooPerspective.getDefaultPerspectiveLayout()).thenReturn(fooPerspectiveDef);
        when(fooPerspective.isTransient()).thenReturn(true);

        perspectiveManager.switchToPerspective(pr,
                fooPerspective,
                doWhenFinished);
        perspectiveManager.switchToPerspective(pr,
                oz,
                doWhenFinished);

        verify(panelManager).removeWorkbenchPanel(fooPanel);
        verify(panelManager).removeWorkbenchPanel(fooChildPanel);
        verify(panelManager).removeWorkbenchPanel(barPanel);
        verify(panelManager).removeWorkbenchPanel(bazPanel);
        verify(panelManager,
                never()).removeWorkbenchPanel(rootPanel);
    }

    @Test
    public void fetchPerspectiveCommandForAnInvalidDefinitionShouldLoadedPerspectiveDefinitionTest() throws Exception {

        when(oz.isTransient()).thenReturn(false);

        when(fetchCommand.isAValidDefinition(any())).thenReturn(false);
        fetchCommand.execute();

        assertEquals(oz,
                     perspectiveManager.getCurrentPerspective());
        verify(doAfterFetch).execute(eq(ozDefinition));
    }

    @Test
    public void fetchPerspectivesForTransientPerspectivesShouldAlwaysLoadDefaultLayoutTest() throws Exception {

        fetchCommand.execute();

        assertEquals(oz,
                perspectiveManager.getCurrentPerspective());
        verify(doAfterFetch).execute(eq(ozDefinition));
    }

    @Test
    public void isAValidPerspectiveDefinitionTest() throws Exception {
        createPartDefinitions();

        when(activityBeansCache.hasActivity(any())).thenReturn(true);

        assertTrue(fetchCommand.isAValidDefinition(createPerspectiveDefinition()));
        verify(activityBeansCache,
                times(getTotalOfPartDefinitions())).hasActivity(any());
    }

    @Test
    public void isAnInvalidPerspectiveDefinitionTest() throws Exception {
        assertFalse(fetchCommand.isAValidDefinition(null));
    }

    @Test
    public void isAnInvalidPerspectiveDefinition2Test() throws Exception {
        createPartDefinitions();
        when(activityBeansCache.hasActivity(any())).thenReturn(true);
        when(activityBeansCache.hasActivity("part3-rootChild2")).thenReturn(false);

        assertFalse(fetchCommand.isAValidDefinition(createPerspectiveDefinition()));
    }

    @Test
    public void getDefaultPerspectiveIdentifierTest() {
        List<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = new ArrayList<>();
        final SyncBeanDef<AbstractWorkbenchPerspectiveActivity> otherPerspectiveBeanDef = getPerspectiveBeanDef(
                "otherPerspectiveBeanDef",
                false);
        final SyncBeanDef<AbstractWorkbenchPerspectiveActivity> homePerspectiveBeanDef = getPerspectiveBeanDef(
                "homePerspectiveBeanDef",
                true);
        perspectives.add(otherPerspectiveBeanDef);
        perspectives.add(homePerspectiveBeanDef);
        doReturn(perspectives.iterator()).when(perspectiveManager).getPerspectivesIterator();

        final String defaultPerspectiveIdentifier = perspectiveManager.getDefaultPerspectiveIdentifier();

        assertEquals(homePerspectiveBeanDef.getInstance().getIdentifier(),
                defaultPerspectiveIdentifier);
        verify(iocManager).destroyBean(otherPerspectiveBeanDef.getInstance());
        verify(iocManager,
                never()).destroyBean(homePerspectiveBeanDef.getInstance());
    }

    private SyncBeanDef<AbstractWorkbenchPerspectiveActivity> getPerspectiveBeanDef(final String identifier,
                                                                                    final boolean isDefault) {
        final AbstractWorkbenchPerspectiveActivity perspectiveActivity = mock(
                AbstractWorkbenchPerspectiveActivity.class);
        doReturn(identifier).when(perspectiveActivity).getIdentifier();
        doReturn(isDefault).when(perspectiveActivity).isDefault();

        final SyncBeanDef<AbstractWorkbenchPerspectiveActivity> perspectiveBeanDef = mock(SyncBeanDef.class);
        doReturn(perspectiveActivity).when(perspectiveBeanDef).getInstance();

        return perspectiveBeanDef;
    }

    private PerspectiveDefinition createPerspectiveDefinition() {
        PerspectiveDefinitionImpl perspectiveDefinition = new PerspectiveDefinitionImpl();
        PanelDefinition root = perspectiveDefinition.getRoot();
        partDefinitionsRoot.forEach(p -> root.addPart(p));

        PanelDefinitionImpl rootChild1 = new PanelDefinitionImpl(
                "org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter");
        partDefinitionRootChild1.forEach(p -> rootChild1.addPart(p));

        PanelDefinitionImpl rootChild2 = new PanelDefinitionImpl(
                "org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter");
        partDefinitionRootChild2.forEach(p -> rootChild2.addPart(p));

        PanelDefinitionImpl rootChild2Child = new PanelDefinitionImpl(
                "org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter");

        partDefinitionRootChild2Child.forEach(p -> rootChild2Child.addPart(p));

        root.insertChild(mock(Position.class),
                rootChild1);
        rootChild2.insertChild(mock(Position.class),
                rootChild2Child);
        root.insertChild(mock(Position.class),
                rootChild2);

        return perspectiveDefinition;
    }

    private void createPartDefinitions() {
        partDefinitionsRoot = Arrays.asList(new PartDefinitionImpl(new DefaultPlaceRequest("part1")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part2")));

        partDefinitionRootChild1 = Arrays.asList(new PartDefinitionImpl(new DefaultPlaceRequest("part1-rootChild1")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part2-rootChild1")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part3-rootChild1")));

        partDefinitionRootChild2 = Arrays.asList(new PartDefinitionImpl(new DefaultPlaceRequest("part1-rootChild2")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part2-rootChild2")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part3-rootChild2")));

        partDefinitionRootChild2Child = Arrays.asList(new PartDefinitionImpl(new DefaultPlaceRequest(
                "part1-rootChild2Child")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part2-rootChild2Child")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part3-rootChild2Child")),
                new PartDefinitionImpl(new DefaultPlaceRequest("part4-rootChild2Child")));
    }

    private int getTotalOfPartDefinitions() {
        int total = partDefinitionsRoot.size() +
                    partDefinitionRootChild1.size() +
                    partDefinitionRootChild2.size() +
                    partDefinitionRootChild2Child.size();
        return total;
    }
}
