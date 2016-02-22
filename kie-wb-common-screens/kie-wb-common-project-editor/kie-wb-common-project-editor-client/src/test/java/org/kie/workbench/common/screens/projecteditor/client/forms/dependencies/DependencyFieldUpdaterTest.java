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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.mockito.Mock;

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
            @Override
            protected void reportXML() {
            }

            @Override
            protected void setValue( EnhancedDependency dep,
                                     String value ) {
            }

            @Override
            protected void reportEmpty() {

            }
        } );
    }

    @Test
    public void testValid() throws Exception {
        final NormalEnhancedDependency enhancedDependency = new NormalEnhancedDependency();
        fieldUpdater.update( 1,
                             enhancedDependency,
                             "value" );

        verify( fieldUpdater ).setValue( enhancedDependency,
                                         "value" );
        verify( cell, never() ).clearViewData( anyObject() );
    }

    @Test
    public void testNonValidDependencyClearsViewDataAndRefreshesView() throws Exception {
        final NormalEnhancedDependency enhancedDependency = new NormalEnhancedDependency();
        fieldUpdater.update( 1,
                             enhancedDependency,
                             "" );

        verify( cell ).clearViewData( enhancedDependency );
        verify( redrawCommand ).execute();
    }

    @Test
    public void testEmpty() throws Exception {
        fieldUpdater.update( 1,
                             new NormalEnhancedDependency(),
                             "" );

        verify( fieldUpdater ).reportEmpty();
    }

    @Test
    public void testXMLAsValueThatIsNotValid() throws Exception {
        fieldUpdater.update( 10,
                             new NormalEnhancedDependency(),
                             "<something>" );

        verify( fieldUpdater ).reportXML();
    }

}