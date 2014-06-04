/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.mvp;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImplUnitTestWrapper;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.pmgr.nswe.NSWEExtendedBeanFactory;
import org.uberfire.client.workbench.widgets.statusbar.WorkbenchStatusBarPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import static org.mockito.Mockito.*;
import static org.uberfire.workbench.model.PanelType.*;

/**
 * Base class for tests requiring a dummy Workbench
 */
public abstract class BaseWorkbenchTest {

    protected PlaceHistoryHandler placeHistoryHandler;
    protected ActivityManager activityManager;
    protected PanelManager panelManager;
    protected PlaceManagerImpl placeManager;

    protected NSWEExtendedBeanFactory factory;
    protected Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;
    protected Event<PlaceGainFocusEvent> workbenchPartOnFocusEvent;
    protected Event<PlaceLostFocusEvent> workbenchPartLostFocusEvent;
    protected Event<SelectPlaceEvent> selectWorkbenchPartEvent;
    protected WorkbenchStatusBarPresenter statusBar;
    protected WorkbenchPanelPresenter workbenchPanelPresenter;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        //General MVP
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );

        //CDI View-Presenter factory used by PanelManager
        factory = new MockBeanFactory();

        //Events used by PanelManager and PlaceManager
        workbenchPartOnFocusEvent = mock( Event.class );
        workbenchPartLostFocusEvent = mock( Event.class );
        selectWorkbenchPartEvent = mock( Event.class );
        statusBar = mock( WorkbenchStatusBarPresenter.class );

        workbenchPanelPresenter = mock (SimpleWorkbenchPanelPresenter.class);


        //Dummy Panel Manager\Workbench
        panelManager = new PanelManagerImplUnitTestWrapper( factory,
                                             workbenchPartOnFocusEvent,
                                             workbenchPartLostFocusEvent,
                                             selectWorkbenchPartEvent,
                                             statusBar, workbenchPanelPresenter );
        final PanelDefinition root = new PanelDefinitionImpl( ROOT_SIMPLE );

        panelManager.setRoot( root );

        //Dummy Place Manager
        placeManager = new PlaceManagerImpl();
    }

}
