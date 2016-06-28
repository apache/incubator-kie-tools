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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldAction;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy002() throws Exception {

        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Rambo" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy003() throws Exception {

        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "org.test1.Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "org.test2.Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy004() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.BOOLEAN, true );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.STRING, "true" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy005() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.BOOLEAN, true );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.STRING, "false" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy006() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "age" ), DataType.DataTypes.NUMERIC_INTEGER, 20 );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "age" ), DataType.DataTypes.STRING, "20" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy007() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "age" ), DataType.DataTypes.NUMERIC_INTEGER, 20 );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "age" ), DataType.DataTypes.STRING, "10" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy008() throws Exception {
        Date date = new Date();
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "birthDay" ), DataType.DataTypes.DATE, date );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "birthDay" ), DataType.DataTypes.STRING, format( date ) );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy009() throws Exception {

        Date value = new Date();

        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "birthDay" ), DataType.DataTypes.DATE, value );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Integer", "birthDay" ), DataType.DataTypes.STRING, "29-Dec-1981" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testConflict001() throws Exception {

        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Rambo" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict002() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.BOOLEAN, true );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "Boolean", "isOldEnough" ), DataType.DataTypes.STRING, "false" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict001() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Address", "String", "street" ), DataType.DataTypes.STRING, "Rambo" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict002() throws Exception {
        ActionInspector a = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );
        ActionInspector b = createSetActionInspector( new Field( mock( ObjectField.class ), "Person", "String", "name" ), DataType.DataTypes.STRING, "Toni" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testNoConflict003() throws Exception {
        ActionInspector a = createSetActionInspector( new FieldAction( new Field( mock( ObjectField.class ), "Person", "String", "name" ), mock( Column.class ), DataType.DataTypes.BOOLEAN, new Values( true ) ) );
        ActionInspector b = createSetActionInspector( new FieldAction( new Field( mock( ObjectField.class ), "Person", "String", "name" ), mock( Column.class ), DataType.DataTypes.STRING, new Values( true ) ) );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    private ActionInspector createSetActionInspector( final Field field,
                                                      final DataType.DataTypes dataType,
                                                      final Comparable comparable ) {
        return new FieldActionInspector( new FieldAction( field,
                                                          mock( Column.class ),
                                                          dataType,
                                                          new Values( comparable )) );
    }

    private ActionInspector createSetActionInspector( final Action action ) {
        return new ActionInspector( action ) {
            @Override
            protected String format( Date dateValue ) {
                return ActionInspectorConflictTest.this.format( dateValue );
            }
        };
    }

    private String format( final Date dateValue ) {
        return new SimpleDateFormat( ApplicationPreferences.getDroolsDateFormat() ).format( dateValue );
    }
}