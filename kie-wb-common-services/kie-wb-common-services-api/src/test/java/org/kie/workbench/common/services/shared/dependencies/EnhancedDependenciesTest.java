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

package org.kie.workbench.common.services.shared.dependencies;

import java.util.HashSet;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnhancedDependenciesTest {

    private EnhancedDependencies enhancedDependencies;

    @Before
    public void setUp() throws Exception {
        enhancedDependencies = new EnhancedDependencies();
        final NormalEnhancedDependency enhancedDependency = new NormalEnhancedDependency( new Dependency( new GAV( "org.hamcrest",
                                                                                                                   "hamcrest-core",
                                                                                                                   "1.3" ) ),
                                                                                          new HashSet<>() );
        enhancedDependency.addTransitiveDependency( new TransitiveEnhancedDependency( new Dependency( new GAV( "hi:something:1.0" ) ),
                                                                                      new HashSet<String>() ) );
        enhancedDependencies.add( enhancedDependency );
    }

    @Test
    public void testRemoveRemovesByDependencyGAV() throws Exception {

        assertTrue( enhancedDependencies.remove( new NormalEnhancedDependency( new Dependency( new GAV( "org.hamcrest",
                                                                                                        "hamcrest-core",
                                                                                                        "1.3" ) ),
                                                                               new HashSet<String>() ) ) );

        assertTrue( enhancedDependencies.isEmpty() );
    }

    @Test
    public void testContainsChecksByGAV() throws Exception {
        assertTrue( enhancedDependencies.contains( new NormalEnhancedDependency( new Dependency( new GAV( "org.hamcrest",
                                                                                                          "hamcrest-core",
                                                                                                          "1.3" ) ),
                                                                                 new HashSet<String>() ) ) );
    }

    @Test
    public void testUpdate() throws Exception {
        enhancedDependencies.update( new NormalEnhancedDependency( new Dependency( new GAV( "org.hamcrest",
                                                                                            "hamcrest-core",
                                                                                            "1.3" ) ),
                                                                   new HashSet<String>() ) );
        NormalEnhancedDependency enhancedDependency = ( NormalEnhancedDependency ) enhancedDependencies.get( new GAV( "org.hamcrest",
                                                                                                                      "hamcrest-core",
                                                                                                                      "1.3" ) );
        assertTrue( enhancedDependency.getTransitiveDependencies().isEmpty() );
    }

    @Test
    public void testClear() throws Exception {
        enhancedDependencies.clear();

        assertTrue( enhancedDependencies.isEmpty() );
    }
}