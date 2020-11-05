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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DependencyLoaderLoadFailureTest {

    @GwtMock
    private CommonConstants commonConstants;

    @Mock
    private EnhancedDependenciesManager manager;

    @Mock
    private Caller dependencyServiceCaller;

    @Mock
    private DependencyService dependencyService;

    @Mock
    private DependencyLoaderView view;

    private DependencyLoader dependencyLoader;

    @Before
    public void setUp() throws Exception {
        dependencyLoader = new DependencyLoader( view,
                                                 new SyncPromises(),
                                                 dependencyServiceCaller );
        dependencyLoader.init( manager );

        when( dependencyServiceCaller.call( any( RemoteCallback.class ),
                                            any( ErrorCallback.class ) ) ).thenReturn( dependencyService );
    }

    @Test
    public void testFailureOnLoad() throws Exception {

        dependencyLoader.addToQueue( makeDependency( "org.junit", "junit", "1.0" ) );

        dependencyLoader.load();

        failLoad();

        final EnhancedDependencies enhancedDependencies = getUpdatedEnhancedDependencies();

        verify( view ).showBusyIndicator( "Loading" );
        verify( view ).hideBusyIndicator();

        assertEquals( 1, enhancedDependencies.size() );
        assertNotNull( enhancedDependencies.get( new GAV( "org.junit", "junit", "1.0" ) ) );
    }

    private void failLoad() {
        ArgumentCaptor<ErrorCallback> errorCallbackArgumentCaptor = ArgumentCaptor.forClass( ErrorCallback.class );
        verify( dependencyServiceCaller ).call( any( RemoteCallback.class ),
                                                errorCallbackArgumentCaptor.capture() );

        errorCallbackArgumentCaptor.getValue().error( null, null );
    }

    private Dependency makeDependency( final String groupId,
                                       final String artifactId,
                                       final String version ) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId( groupId );
        dependency.setArtifactId( artifactId );
        dependency.setVersion( version );
        return dependency;
    }

    private EnhancedDependencies getUpdatedEnhancedDependencies() {
        ArgumentCaptor<EnhancedDependencies> argumentCaptor = ArgumentCaptor.forClass( EnhancedDependencies.class );

        verify( manager ).onEnhancedDependenciesUpdated( argumentCaptor.capture() );

        return argumentCaptor.getValue();
    }

}