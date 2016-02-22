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

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class EnhancedDependenciesManagerResetTest {

    @Mock
    private DependencyLoader loader;

    @InjectMocks
    private EnhancedDependenciesManager manager;

    @Test
    public void testReset() throws Exception {

        final Callback callback = init( makePOM( makeDependency() ) );

        final EnhancedDependencies shownEnhancedDependencies = getEnhancedDependencies( callback );

        assertEquals( 1, shownEnhancedDependencies.size() );

        final Callback callback2 = init( makePOM() );

        final EnhancedDependencies shownEnhancedDependencies2 = getEnhancedDependencies( callback2 );

        assertEquals( 0, shownEnhancedDependencies2.size() );
    }

    private Callback init( final POM pom ) {
        final Callback callback = mock( Callback.class );
        manager.init( pom,
                      callback );

        final EnhancedDependencies enhancedDependencies = new EnhancedDependencies();
        for ( Dependency dependency : pom.getDependencies() ) {
            enhancedDependencies.add( new NormalEnhancedDependency( dependency,
                                                                    new HashSet<String>() ) );
        }

        manager.onEnhancedDependenciesUpdated( enhancedDependencies );
        return callback;
    }


    private Dependency makeDependency() {
        final Dependency dependency = new Dependency();
        dependency.setScope( "compile" );

        return dependency;
    }

    private POM makePOM( final Dependency... dependencies ) {
        final POM pom = new POM();

        for ( Dependency dependency : dependencies ) {
            pom.getDependencies().add( dependency );
        }

        return pom;
    }

    private EnhancedDependencies getEnhancedDependencies( final Callback<EnhancedDependencies> callback1 ) {
        ArgumentCaptor<EnhancedDependencies> dependenciesArgumentCaptor = ArgumentCaptor.forClass( EnhancedDependencies.class );
        verify( callback1 ).callback( dependenciesArgumentCaptor.capture() );
        return dependenciesArgumentCaptor.getValue();
    }


}
