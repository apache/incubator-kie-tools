/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.guvnor.ala.pipeline.Input;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

import static org.mockito.Mockito.*;

public class BuildPipelineTestBase
        implements BuildPipelineTestConstants {

    protected ResourceChangeRequest changes[] = {
            new BuildPipelineTestBase.ResourceChangeRequest( RESOURCE_URI_1, "ADD" ),
            new BuildPipelineTestBase.ResourceChangeRequest( RESOURCE_URI_2, "ADD,UPDATE"),
            new BuildPipelineTestBase.ResourceChangeRequest( RESOURCE_URI_3, "ADD,UPDATE,DELETE")
    };

    /**
     * @return the Pipeline input for a project full build.
     */
    public static Input createFullBuildInput( String rootPathUri ) {
        Input input = new Input();
        input.put( LocalSourceConfig.ROOT_PATH, rootPathUri );
        input.put( LocalBuildConfig.BUILD_TYPE, LocalBuildConfig.BuildType.FULL_BUILD.name( ) );
        return input;
    }

    /**
     * @return the Pipeline input for an incremental build for one resource.
     */
    public static Input createIncrementalBuildInput( String rootPathUri,
                                                     String resourceUri,
                                                     String buildType ) {
        Input input = new Input();
        input.put( LocalSourceConfig.ROOT_PATH, rootPathUri );
        input.put( LocalBuildConfig.RESOURCE, resourceUri );
        input.put( LocalBuildConfig.BUILD_TYPE, buildType );
        return input;
    }

    /**
     * @return the Pipeline input for a set of batch changes.
     */
    public static Input createBatchChangesInput( String rootPathUri,
                                                 String buildType, ResourceChangeRequest ... changes ) {
        Input input = new Input();
        input.put( LocalSourceConfig.ROOT_PATH, rootPathUri );
        input.put( LocalBuildConfig.BUILD_TYPE, buildType );
        for ( ResourceChangeRequest change : changes ) {
            input.put( LocalBuildConfig.RESOURCE_CHANGE + change.getUri(), change.getChanges() );
        }
        return input;
    }

    /**
     * @return the Pipeline input for a full build and deploy.
     */
    public static Input createFullBuildAndDeployInput( String rootPathUri, String deploymentType, boolean suppressHandlers ) {
        Input input = new Input( );
        input.put( LocalSourceConfig.ROOT_PATH, rootPathUri );
        input.put( LocalBuildConfig.BUILD_TYPE, LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY.name( ) );
        input.put( LocalBuildConfig.DEPLOYMENT_TYPE, deploymentType );
        input.put( LocalBuildConfig.SUPPRESS_HANDLERS, Boolean.toString( suppressHandlers ) );
        return input;
    }

    public static Map< Path, Collection< ResourceChange > > createResourceChanges( BuildPipelineTestBase.ResourceChangeRequest ... changes ) {
        Map< Path, Collection< ResourceChange > > resourceChanges = new HashMap<>( );
        for ( BuildPipelineTestBase.ResourceChangeRequest change : changes ) {
            Path resource = Paths.convert( org.uberfire.java.nio.file.Paths.get( change.getUri() ) );
            resourceChanges.put( resource, createChanges( change.getChanges() ) );
        }
        return resourceChanges;
    }

    public static Collection< ResourceChange > createChanges( String plainChanges ) {
        return Arrays.stream( plainChanges.split( "," ) )
                .map( s -> {
                    ResourceChange resourceChange = mock( ResourceChange.class );
                    when( resourceChange.getType() ).thenReturn( ResourceChangeType.valueOf( s.trim( ) ) );
                    return resourceChange;
                } )
                .collect( Collectors.toList( ) );
    }

    public static class ResourceChangeRequest {

        private String uri;

        private String changes;

        public ResourceChangeRequest( String uri, String changes ) {
            this.uri = uri;
            this.changes = changes;
        }

        public String getUri( ) {
            return uri;
        }

        public String getChanges( ) {
            return changes;
        }
    }
}
