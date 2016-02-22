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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EnhancedDependencies
        implements Iterable<EnhancedDependency> {

    private final HashMap<GAV, EnhancedDependency> enhancedDependencies = new HashMap<>();

    public EnhancedDependencies() {
    }

    public EnhancedDependencies( final List<EnhancedDependency> enhancedDependencies ) {
        for ( final EnhancedDependency enhancedDependency : enhancedDependencies ) {
            add( enhancedDependency );
        }
    }

    public void add( final EnhancedDependency enhancedDependency ) {
        this.enhancedDependencies.put( enhancedDependency.getDependency(),
                                       enhancedDependency );
    }

    public boolean remove( final EnhancedDependency enhancedDependency ) {
        final EnhancedDependency remove = enhancedDependencies.remove( enhancedDependency.getDependency() );
        return remove != null;
    }

    public boolean isEmpty() {
        return enhancedDependencies.isEmpty();
    }

    public boolean contains( final EnhancedDependency enhancedDependency ) {
        return enhancedDependencies.containsKey( enhancedDependency.getDependency() );
    }

    public void update( final EnhancedDependency enhancedDependency ) {
        remove( enhancedDependency );
        add( enhancedDependency );
    }

    public EnhancedDependency get( final GAV gav ) {
        return enhancedDependencies.get( gav );
    }

    @Override
    public Iterator<EnhancedDependency> iterator() {
        return enhancedDependencies.values().iterator();
    }

    /**
     * @return A list of enhanced dependencies including the top level "normal" dependencies and
     * the transient dependencies for the "normal" dependencies.
     * If a dependency is both declared in the pom and transient the one declared in the pom in included.
     */
    public List<? extends EnhancedDependency> asList() {
        final ArrayList<EnhancedDependency> result = new ArrayList<>( enhancedDependencies.values() );

        for ( final EnhancedDependency enhancedDependency : enhancedDependencies.values() ) {
            if ( enhancedDependency instanceof NormalEnhancedDependency ) {
                final NormalEnhancedDependency normalEnhancedDependency = ( NormalEnhancedDependency ) enhancedDependency;

                for ( final EnhancedDependency transitiveDependency : normalEnhancedDependency.getTransitiveDependencies().asList() ) {
                    if ( !enhancedDependencies.containsKey( transitiveDependency.getDependency() ) ) {
                        result.add( transitiveDependency );
                    }
                }
            }
        }

        return Collections.unmodifiableList( result );
    }

    public int size() {
        return enhancedDependencies.size();
    }

    public void addAll( final Collection<EnhancedDependency> transitiveDependencies ) {
        for ( final EnhancedDependency transitiveDependency : transitiveDependencies ) {
            add( transitiveDependency );
        }

    }

    public void clear() {
        enhancedDependencies.clear();
    }
}
