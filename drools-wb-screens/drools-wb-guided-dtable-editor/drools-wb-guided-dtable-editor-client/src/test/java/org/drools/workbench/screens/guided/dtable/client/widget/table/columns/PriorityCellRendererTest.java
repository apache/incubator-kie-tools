/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class PriorityCellRendererTest {

    @Mock
    private Text text;

    @Test
    public void empty() throws
                       Exception {

        whenValueIs( "" );
        thenTextIsSetTo( "" );
    }

    @Test
    public void testNull() throws
                       Exception {

        whenValueIs( null );
        thenTextIsSetTo( "" );
    }

    @Test
    public void zero() throws
                       Exception {

        whenValueIs( "0" );
        thenTextIsSetTo( "" );
    }

    private void thenTextIsSetTo( final String text ) {
        verify( this.text ).setText( text );
    }

    private void whenValueIs( final String value ) {
        final PriorityListUiColumn.PriorityCellRenderer priorityCellRenderer = new PriorityListUiColumn.PriorityCellRenderer( mock( SingletonDOMElementFactory.class ) );
        priorityCellRenderer.doRenderCellContent( text,
                                                  value,
                                                  mock( GridBodyCellRenderContext.class ) );
    }
}