/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.Collection;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;

import static org.junit.Assert.*;

public class DependencyTestUtils {

    public static Dependency makeDependency( final String groupID,
                                             final String artifactID,
                                             final String version ) {
        return new Dependency( new GAV( groupID,
                                        artifactID,
                                        version ) );
    }

    public static Dependency makeDependency( final String groupID,
                                             final String artifactID,
                                             final String version,
                                             final String scope ) {
        final Dependency dependency = new Dependency( new GAV( groupID,
                                                               artifactID,
                                                               version ) );
        dependency.setScope( scope );

        return dependency;
    }

    public static void assertContains( final EnhancedDependencies dependencies,
                                       final String groupID,
                                       final String artifactID,
                                       final String version ) {
        for ( EnhancedDependency enhancedDependency : dependencies ) {
            Dependency dependency = enhancedDependency.getDependency();
            if ( areNullSafeEquals( artifactID, dependency.getArtifactId() )
                    && areNullSafeEquals( groupID, dependency.getGroupId() )
                    && areNullSafeEquals( version, dependency.getVersion() ) ) {
                return;
            }
        }

        fail( "Could not find " + groupID + ":" + artifactID + ":" + version );
    }

    private static boolean areNullSafeEquals( final String value,
                                              final String otherValue ) {
        if ( value == null && otherValue == null ) {
            return true;
        } else if ( value == null || otherValue == null ) {
            return false;
        } else {
            return otherValue.equals( value );
        }
    }

    public static Dependency assertContains( final EnhancedDependencies dependencies,
                                             final String groupID,
                                             final String artifactID,
                                             final String version,
                                             final String scope ) {
        for ( EnhancedDependency enhancedDependency : dependencies ) {
            Dependency dependency = enhancedDependency.getDependency();
            if ( areNullSafeEquals( dependency.getArtifactId(), artifactID )
                    && areNullSafeEquals( dependency.getGroupId(), groupID )
                    && areNullSafeEquals( dependency.getVersion(), version )
                    && areNullSafeEquals( dependency.getScope(), scope ) ) {
                return dependency;
            }
        }

        fail( "Could not find " + groupID + ":" + artifactID + ":" + version );
        return null;
    }

    public static void assertNotContains( final Collection<Dependency> dependencies,
                                          final String groupID,
                                          final String artifactID,
                                          final String version ) {
        for ( Dependency dependency : dependencies ) {
            if ( areNullSafeEquals( artifactID, dependency.getArtifactId() )
                    && areNullSafeEquals( groupID, dependency.getGroupId() )
                    && areNullSafeEquals( version, dependency.getVersion() ) ) {
                fail( "Could find " + groupID + ":" + artifactID + ":" + version );
            }
        }
    }
}
