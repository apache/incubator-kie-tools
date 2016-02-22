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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.HashSet;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RemoveColumnTest {


    private RemoveColumn    removeColumn;
    private SafeHtmlBuilder safeHtmlBuilder;

    @Before
    public void setUp() throws Exception {
        removeColumn = new RemoveColumn();
        safeHtmlBuilder = new SafeHtmlBuilder();
    }

    @Test
    public void testRenderScopeCompile() throws Exception {

        render( new NormalEnhancedDependency( new Dependency(),
                                              new HashSet<String>() ) );

        assertFalse( safeHtmlBuilder.toSafeHtml().asString().contains( " disabled=\"disabled\"" ) );
    }

    @Test
    public void testRenderScopeTransitive() throws Exception {

        render( new TransitiveEnhancedDependency( new Dependency(),
                                                  new HashSet<String>() ) );

        assertTrue( safeHtmlBuilder.toSafeHtml().asString().contains( " disabled=\"disabled\"" ) );
    }

    private void render( final EnhancedDependency dependency ) {

        removeColumn.render( mock( Cell.Context.class ),
                             dependency,
                             safeHtmlBuilder );
    }
}