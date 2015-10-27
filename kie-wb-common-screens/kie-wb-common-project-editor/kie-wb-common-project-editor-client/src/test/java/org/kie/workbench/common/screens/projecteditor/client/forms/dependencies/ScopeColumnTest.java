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

import com.google.gwt.dom.client.SelectElement;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.project.model.Dependency;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class ScopeColumnTest {

    private ScopeColumn scopeColumn;

    @Before
    public void setUp() throws Exception {
        scopeColumn = new ScopeColumn();
    }

    @Test
    public void testDefaultValue() throws Exception {
        assertEquals( "compile",
                      scopeColumn.getValue( new Dependency() ) );

    }

    @Test
    public void testValueSet() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setScope( "test" );

        assertEquals( "test",
                      scopeColumn.getValue( dependency ) );

    }
}