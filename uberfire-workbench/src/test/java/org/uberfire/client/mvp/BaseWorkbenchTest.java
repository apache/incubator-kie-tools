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

import static org.mockito.Mockito.mock;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;

/**
 * Base class for tests requiring a dummy Workbench
 */
public abstract class BaseWorkbenchTest {

    protected PlaceHistoryHandler                  placeHistoryHandler;
    protected ActivityManager                      activityManager;
    protected PanelManager                         panelManager;
    protected PlaceManagerImpl                     placeManager;

    protected BeanFactory                          factory;
    protected Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;
    protected Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;
    protected Event<WorkbenchPartOnFocusEvent>     workbenchPartOnFocusEvent;
    protected Event<WorkbenchPartLostFocusEvent>   workbenchPartLostFocusEvent;
    protected Event<SelectWorkbenchPartEvent>      selectWorkbenchPartEvent;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        //General MVP
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );

        //CDI View-Presenter factory used by PanelManager
        factory = new MockBeanFactory();

        //Events used by PanelManager and PlaceManager
        workbenchPanelOnFocusEvent = mock( Event.class );
        workbenchPartBeforeCloseEvent = mock( Event.class );
        workbenchPartOnFocusEvent = mock( Event.class );
        workbenchPartLostFocusEvent = mock( Event.class );
        selectWorkbenchPartEvent = mock( Event.class );

        //Dummy Panel Manager\Workbench
        panelManager = new PanelManager( factory,
                                         workbenchPanelOnFocusEvent,
                                         workbenchPartBeforeCloseEvent,
                                         workbenchPartOnFocusEvent,
                                         workbenchPartLostFocusEvent,
                                         selectWorkbenchPartEvent );
        final PanelDefinition root = new PanelDefinitionImpl( true );

        panelManager.setRoot( root );

        //Dummy Place Manager
        placeManager = new PlaceManagerImpl( activityManager,
                                             placeHistoryHandler,
                                             selectWorkbenchPartEvent,
                                             panelManager );
    }

}
