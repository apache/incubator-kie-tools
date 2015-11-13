/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * Tests for behaviours that are required of all implementations of WorkbenchPanelPresenter.
 * <p>
 * Tests for concrete and abstract WorkbenchPanelPresenter implementations should extend this one.
 */
public abstract class AbstractWorkbenchPanelPresenterTest {

    @Mock protected PerspectiveManager mockPerspectiveManager;
    @Mock protected ActivityManager mockActivityManager;
    @Mock protected View mockPartView;
    @Mock protected WorkbenchPartPresenter mockPartPresenter;
    @Mock protected ContextActivity perspectiveContextActivity;

    protected final PerspectiveDefinition panelManagerPerspectiveDefinition = new PerspectiveDefinitionImpl();
    protected final PartDefinition partPresenterPartDefinition = new PartDefinitionImpl( new DefaultPlaceRequest( "belongs_to_mockPartPresenter" ) );
    protected final PanelDefinition panelPresenterPanelDefinition = new PanelDefinitionImpl();
    protected final ContextDefinition perspectiveContextDefinition = new ContextDefinitionImpl( new DefaultPlaceRequest( "Perspective Context" ) );

    /**
     * The individual test classes that extend this base class implement this method by returning the implementation of
     * AbstractDockingWorkbenchPanelPresenter that they want to test.
     * <p>
     * The presenter should usually be created using {@code @InjectMocks}, which will give the protected mock objects
     * set up by this base class to the presenter's constructor. Additionally, subclasses should be sure to set
     * {@link #panelPresenterPanelDefinition} as the panel's definition object (the inherited tests assume this).
     */
    abstract WorkbenchPanelPresenter getPresenterToTest();

    @Before
    public void setUp() {
        when( mockPerspectiveManager.getLivePerspectiveDefinition() ).thenReturn( panelManagerPerspectiveDefinition );
        panelManagerPerspectiveDefinition.setContextDefinition( perspectiveContextDefinition );
        when( mockActivityManager.getActivity( ContextActivity.class, perspectiveContextDefinition.getPlace() ) ).thenReturn( perspectiveContextActivity );
        when( mockPartView.getPresenter() ).thenReturn( mockPartPresenter );
        when( mockPartPresenter.getDefinition() ).thenReturn( partPresenterPartDefinition );
    }

    @Test
    public void addingPartShouldUpdateDefinition() throws Exception {
        WorkbenchPanelPresenter panelPresenter = getPresenterToTest();

        try {
            panelPresenter.addPart( mockPartPresenter );
        } catch ( UnsupportedOperationException e ) {
            // it's okay if the panel doesn't support parts
            return;
        }

        assertSame( panelPresenter.getDefinition(), mockPartPresenter.getDefinition().getParentPanel() );
        assertTrue( panelPresenter.getDefinition().getParts().contains( mockPartPresenter.getDefinition() ) );
    }

    @Test
    public void removingPartShouldUpdateDefinition() throws Exception {
        WorkbenchPanelPresenter panelPresenter = getPresenterToTest();

        try {
            panelPresenter.addPart( mockPartPresenter );
        } catch ( UnsupportedOperationException e ) {
            // it's okay if the panel doesn't support parts
            return;
        }

        panelPresenter.removePart( mockPartPresenter.getDefinition() );

        assertNull( mockPartPresenter.getDefinition().getParentPanel() );
        assertFalse( panelPresenter.getDefinition().getParts().contains( mockPartPresenter.getDefinition() ) );
    }

    /**
     * Tests that PanelManager avoids duplicating PartDefinitions inside already-populated PanelDefinitions when
     * building up a perspective.
     */
    @Test
    public void addingPartThatIsAlreadyInPanelDefShouldNotChangePanelDef() throws Exception {
        WorkbenchPanelPresenter panelPresenter = getPresenterToTest();

        panelPresenter.getDefinition().addPart( partPresenterPartDefinition );
        assertEquals( 1, panelPresenter.getDefinition().getParts().size() );

        try {
            panelPresenter.addPart( mockPartPresenter );
        } catch ( UnsupportedOperationException e ) {
            // it's okay if the panel doesn't support parts
            return;
        }

        // the rest of the add operation should have happened
        assertSame( panelPresenter.getDefinition(), mockPartPresenter.getDefinition().getParentPanel() );
        assertTrue( panelPresenter.getDefinition().getParts().contains( mockPartPresenter.getDefinition() ) );

        // there should still only be 1 part
        assertEquals( 1, panelPresenter.getDefinition().getParts().size() );
    }

}
