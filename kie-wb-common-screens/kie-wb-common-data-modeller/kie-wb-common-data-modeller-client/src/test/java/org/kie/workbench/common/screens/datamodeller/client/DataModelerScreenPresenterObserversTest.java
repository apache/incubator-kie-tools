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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;

import static org.mockito.Mockito.*;

public class DataModelerScreenPresenterObserversTest
    extends DataModelerScreenPresenterTestBase {

    private String testObject1Title;

    private String testObject1Tooltip;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        //expected title when testObject1 is selected.
        testObject1Title =  "'TestObject1Label (TestObject1)'"
                + Constants.INSTANCE.modelEditor_general_properties_label();

        //expected tooltip when testObject1 is selected.
        testObject1Tooltip = "org.test.TestObject1";

        boolean loadTypesInfo = true; //not relevant for this test.
        EditorModelContent content = createContent( loadTypesInfo, false );

        when ( versionRecordManager.getCurrentPath() ).thenReturn( path );
        when( modelerService.loadContent( path, loadTypesInfo ) ).thenReturn( content );
        when( javaSourceEditor.getContent() ).thenReturn( content.getSource() );

        //types info is not loaded into the DataModelerWBContext.
        when( dataModelerWBContext.isTypesInfoLoaded() ).thenReturn( false );

        //just for convenience, since the DataModelerContext is initialized by taking this definitions from the DMWC.
        when ( dataModelerWBContext.getAnnotationDefinitions() ).thenReturn( testAnnotationDefs );
        when( dataModelerWBContext.getPropertyTypes() ).thenReturn( testTypeDefs );

        //let's the presenter to be initialized properly.
        presenter.onStartup( path, placeRequest );

        //emulate current editor context is the one loaded into de DataModelerWBContext at this moment.
        when( dataModelerWBContext.getActiveContext() ).thenReturn( presenter.context );
    }

    /**
     * The following test checks that the title for the main properties panel on the view is properly set when the
     * DataObject is selected.
     */
    @Test
    public void titleChangeOnObjectSelectionTest() {

        //emulates the arrival of an event produced by some of the "domain editors" or the "data object browser"
        //that are working on this context. Typically when the user clicks on the "object link" in the
        //"data object browser" in order manage the data object properties.
        presenter.onDataObjectSelectedEvent(
                new DataObjectSelectedEvent( dataModelerWBContext.getActiveContext().getContextId(), "", testObject1 ) );

        //expected title and tooltip given that TestObject1 was selected.
        String title = testObject1Title;
        String tooltip = testObject1Tooltip;

        //Finally the domainContainerTitle must have been set two times, one when the data object was initially loaded.
        //and now when the event arrived.
        verify( view, times( 2 ) ).setDomainContainerTitle( title, tooltip );
    }

    /**
     * The following test checks that the title for the main properties panel on the view is properly set when the
     * a field is selected.
     */
    @Test
    public void titleChangeOnFieldSelectionTest() {

        //expected initial invocation when the data object was loaded.
        String title = testObject1Title;
        String tooltip = testObject1Tooltip;
        verify( view, times( 1 ) ).setDomainContainerTitle( title, tooltip );

        //Now we emulate the arrival of an event produced by some of the "domain editors" or the "data object browser"
        //that are working on this context. Typically when the user clicks on a given field in the fields table.
        presenter.onDataObjectFieldSelectedEvent( new DataObjectFieldSelectedEvent(
                dataModelerWBContext.getActiveContext().getContextId(),
                "",
                testObject1,
                testObject1.getProperty( "field1" ) ) );

        //expected title and tooltip given that "field1" was selected.
        title = "'field1'" + Constants.INSTANCE.modelEditor_general_properties_label();
        tooltip = "org.test.TestObject1.field1";
        verify( view, times( 1 ) ).setDomainContainerTitle( title, tooltip );

        //now emulate the user selecting "field2"
        presenter.onDataObjectFieldSelectedEvent( new DataObjectFieldSelectedEvent(
                dataModelerWBContext.getActiveContext().getContextId(),
                "",
                testObject1,
                testObject1.getProperty( "field2" ) ) );

        //expected title and tooltip given that "field2" was selected.
        title = "'field2'" + Constants.INSTANCE.modelEditor_general_properties_label();
        tooltip = "org.test.TestObject1.field2";
        verify( view, times( 1 ) ).setDomainContainerTitle( title, tooltip );
    }
}
