/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.rule.backend.server;

import org.junit.Test;
import org.kie.workbench.common.services.backend.enums.EnumDropdownServiceImpl;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GuidedRuleEditorService.loadDropDownExpression
 */
public class EnumDropDownServiceTest {

    @Test
    public void testLoadDropDown() throws Exception {

        //Override code that converts Path to ClassLoader for test
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final EnumDropdownService service = new EnumDropdownServiceImpl() {
            @Override
            public String[] loadDropDownExpression( final Path resource,
                                                    final String[] valuePairs,
                                                    final String expression ) {
                return super.loadDropDownExpression( cl,
                                                     valuePairs,
                                                     expression );
            }
        };

        final String[] pairs = new String[]{ "f1=x", "f2=2" };
        final String expression = "['@{f1}', '@{f2}']";
        final String[] r = service.loadDropDownExpression( mock( Path.class ),
                                                           pairs,
                                                           expression );
        assertEquals( 2,
                      r.length );

        assertEquals( "x",
                      r[ 0 ] );
        assertEquals( "2",
                      r[ 1 ] );

    }

    @Test
    public void testLoadDropDownNoValuePairs() throws Exception {

        //Override code that converts Path to ClassLoader for test
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final EnumDropdownService service = new EnumDropdownServiceImpl() {
            @Override
            public String[] loadDropDownExpression( final Path resource,
                                                    final String[] valuePairs,
                                                    final String expression ) {
                return super.loadDropDownExpression( cl,
                                                     valuePairs,
                                                     expression );
            }
        };

        final String[] pairs = new String[]{ null };
        final String expression = "['@{f1}', '@{f2}']";
        final String[] r = service.loadDropDownExpression( mock( Path.class ),
                                                           pairs,
                                                           expression );

        assertEquals( 0,
                      r.length );
    }

}
