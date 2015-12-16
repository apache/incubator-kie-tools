/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActionInspectorConflictTest {

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    @Test
    public void testRedundancy001() throws Exception {

        ActionInspector a = createSetActionInspector( "person", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "person", "name", "Toni" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy002() throws Exception {

        ActionInspector a = createSetActionInspector( "person", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "person", "name", "Rambo" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy003() throws Exception {

        ActionInspector a = createSetActionInspector( "person1", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "person2", "name", "Toni" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy004() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", new DTCellValue52( true ) );
        ActionInspector b = createSetActionInspector( "person", "name", "true" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy005() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", new DTCellValue52( true ) );
        ActionInspector b = createSetActionInspector( "person", "name", "false" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy006() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "age", new DTCellValue52( 20 ) );
        ActionInspector b = createSetActionInspector( "person", "age", "20" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy007() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "age", new DTCellValue52( 20 ) );
        ActionInspector b = createSetActionInspector( "person", "age", "10" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy008() throws Exception {
        Date date = new Date();
        ActionInspector a = createSetActionInspector( "person", "birthDay", new DTCellValue52( date ) );
        ActionInspector b = createSetActionInspector( "person", "birthDay", format( date ) );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy009() throws Exception {

        Date value = new Date();

        ActionInspector a = createSetActionInspector( "person", "birthDay", new DTCellValue52( value ) );
        ActionInspector b = createSetActionInspector( "person", "birthDay", "29-Dec-1981" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testConflict001() throws Exception {

        ActionInspector a = createSetActionInspector( "person", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "person", "name", "Rambo" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict002() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", new DTCellValue52( true ) );
        ActionInspector b = createSetActionInspector( "person", "name", "false" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict001() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "address", "street", "Rambo" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict002() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", "Toni" );
        ActionInspector b = createSetActionInspector( "person", "name", "Toni" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict003() throws Exception {
        ActionInspector a = createSetActionInspector( "person", "name", new DTCellValue52( true ) );
        ActionInspector b = createSetActionInspector( "person", "name", "true" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    private ActionInspector createSetActionInspector( String boundName,
                                                      String factField,
                                                      String stringValue ) {
        return createSetActionInspector( stringValue,
                                         createActionSetFieldCol( boundName,
                                                                  factField ) );
    }

    private ActionInspector createSetActionInspector( String boundName,
                                                      String factField,
                                                      DTCellValue52 cell ) {
        return new ActionInspector( new FactFieldColumnActionInspectorKey( createActionSetFieldCol( boundName,
                                                                                                    factField ) ),
                                    cell ) {
            @Override
            protected String format( Date dateValue ) {
                return ActionInspectorConflictTest.this.format( dateValue );
            }
        };
    }

    private String format( Date dateValue ) {
        return new SimpleDateFormat( ApplicationPreferences.getDroolsDateFormat() ).format( dateValue );
    }

    private ActionInspector createSetActionInspector( String stringValue,
                                                      ActionSetFieldCol52 actionCol ) {
        return new ActionInspector( new FactFieldColumnActionInspectorKey( actionCol ),
                                    createStringValueCell( stringValue ) ) {
            @Override
            protected String format( Date dateValue ) {
                return ActionInspectorConflictTest.this.format( dateValue );
            }
        };
    }

    private ActionSetFieldCol52 createActionSetFieldCol( String boundName,
                                                         String factField ) {
        ActionSetFieldCol52 actionCol = new ActionSetFieldCol52();
        actionCol.setBoundName( boundName );
        actionCol.setFactField( factField );
        return actionCol;
    }

    private DTCellValue52 createStringValueCell( String stringValue ) {
        DTCellValue52 value = new DTCellValue52();
        value.setStringValue( stringValue );
        return value;
    }
}