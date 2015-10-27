/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencyFieldUpdater;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencyGridViewImpl;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.WaterMarkEditTextCell;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DependencyFieldUpdaterTest {

    @Mock
    private WaterMarkEditTextCell cell;

    @Mock
    private DependencyGridViewImpl.RedrawCommand redrawCommand;

    private DependencyFieldUpdater fieldUpdater;

    @Before
    public void setUp() throws Exception {
        fieldUpdater = spy( new DependencyFieldUpdater( cell,
                                                        redrawCommand ) {
            @Override protected void setValue( Dependency dep,
                                               String value ) {
                // Going to use Group id for testing, doesn't really matter.
                dep.setGroupId( value );
            }

            @Override protected void reportXML() {
            }

            @Override protected void reportEmpty() {

            }
        } );
    }

    @Test
    public void testValid() throws Exception {
        Dependency dependency = new Dependency();
        fieldUpdater.update( 1,
                             dependency,
                             "value" );

        assertEquals( "value", dependency.getGroupId() );
    }

    @Test
    public void testEmpty() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setGroupId( "I'm here" );
        fieldUpdater.update( 1,
                             dependency,
                             "" );

        assertEquals( "I'm here", dependency.getGroupId() );
        verify( fieldUpdater ).reportEmpty();
        verify( cell ).clearViewData( dependency );
        verify( redrawCommand ).execute();
    }

    @Test
    public void testXMLAsValue() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setGroupId( "I'm here" );
        fieldUpdater.update( 1,
                             dependency,
                             "<something>" );

        assertEquals( "I'm here", dependency.getGroupId() );
        verify( fieldUpdater ).reportXML();
        verify( cell ).clearViewData( dependency );
        verify( redrawCommand ).execute();
    }
}