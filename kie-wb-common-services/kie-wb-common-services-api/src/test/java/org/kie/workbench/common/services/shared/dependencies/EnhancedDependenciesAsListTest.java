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
import java.util.List;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnhancedDependenciesAsListTest {

    private EnhancedDependencies         enhancedDependencies;
    private NormalEnhancedDependency     droolsCoreNormalDependency;
    private TransitiveEnhancedDependency xstreamTransitiveDependency;
    private NormalEnhancedDependency     xstreamNormalDependency;
    private TransitiveEnhancedDependency droolsApiTransitiveDependency1;
    private NormalEnhancedDependency     droolsCompilerNormalDependency;
    private TransitiveEnhancedDependency droolsApiTransitiveDependency2;

    @Before
    public void setUp() throws Exception {
        enhancedDependencies = new EnhancedDependencies();

        addDroolsCore();
        addXStream();
        addDroolsCompiler();
    }

    public void addDroolsCompiler() {
        droolsCompilerNormalDependency = new NormalEnhancedDependency( new Dependency( new GAV( "org.drools:drools-compiler:1.3" ) ),
                                                                       new HashSet<>() );

        droolsApiTransitiveDependency2 = new TransitiveEnhancedDependency( new Dependency( new GAV( "org.drools:drools-api:1.0" ) ),
                                                                           new HashSet<>() );
        droolsCompilerNormalDependency.addTransitiveDependency( droolsApiTransitiveDependency2 );
        enhancedDependencies.add( droolsCompilerNormalDependency );
    }

    private void addXStream() {
        xstreamNormalDependency = new NormalEnhancedDependency( new Dependency( new GAV( "org.xstream:xstream:1.0" ) ),
                                                                new HashSet<>() );
        enhancedDependencies.add( xstreamNormalDependency );
    }

    private void addDroolsCore() {
        droolsCoreNormalDependency = new NormalEnhancedDependency( new Dependency( new GAV( "org.drools:drools-core:1.3" ) ),
                                                                   new HashSet<>() );

        xstreamTransitiveDependency = new TransitiveEnhancedDependency( new Dependency( new GAV( "org.xstream:xstream:1.0" ) ),
                                                                        new HashSet<>() );
        droolsApiTransitiveDependency1 = new TransitiveEnhancedDependency( new Dependency( new GAV( "org.drools:drools-api:1.0" ) ),
                                                                           new HashSet<>() );

        droolsCoreNormalDependency.addTransitiveDependency( xstreamTransitiveDependency );
        enhancedDependencies.add( droolsCoreNormalDependency );
    }

    @Test
    public void testSize() throws Exception {
        assertEquals( 4, this.enhancedDependencies.asList().size() );
    }

    @Test
    public void testDefaultContent() throws Exception {
        final List<? extends EnhancedDependency> enhancedDependencies = this.enhancedDependencies.asList();

        assertTrue( enhancedDependencies.contains( droolsCoreNormalDependency ) );
        assertTrue( enhancedDependencies.contains( droolsApiTransitiveDependency1 )
                            || enhancedDependencies.contains( droolsApiTransitiveDependency2 ) );
        assertFalse( enhancedDependencies.contains( xstreamTransitiveDependency ) );
        assertTrue( enhancedDependencies.contains( xstreamNormalDependency ) );
        assertTrue( enhancedDependencies.contains( droolsCompilerNormalDependency ) );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testUnModifiable() throws Exception {
        this.enhancedDependencies.asList().clear();
    }

    @Test
    public void testRemoveNormalXStream() throws Exception {
        this.enhancedDependencies.remove( xstreamNormalDependency );

        final List<? extends EnhancedDependency> enhancedDependencies = this.enhancedDependencies.asList();

        assertTrue( enhancedDependencies.contains( xstreamTransitiveDependency ) );
        assertFalse( enhancedDependencies.contains( xstreamNormalDependency ) );
    }
}