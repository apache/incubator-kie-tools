/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.api.client.maps;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.maps.util.HasConflicts;
import org.drools.workbench.services.verifier.api.client.maps.util.HasRedundancy;
import org.drools.workbench.services.verifier.api.client.maps.util.RedundancyResult;
import org.drools.workbench.services.verifier.api.client.relations.Conflict;
import org.drools.workbench.services.verifier.api.client.relations.IsConflicting;
import org.drools.workbench.services.verifier.api.client.relations.IsRedundant;

public class LeafInspectorList<T extends IsConflicting & IsRedundant>
        extends InspectorList<T>
        implements HasConflicts,
                   HasRedundancy {

    public LeafInspectorList( final AnalyzerConfiguration configuration ) {
        super( configuration );
    }

    @Override
    public Conflict hasConflicts() {
        int index = 1;
        for ( final T inspector : this ) {
            for ( int j = index; j < size(); j++ ) {
                if ( inspector.conflicts( get( j ) ) ) {
                    return new Conflict( inspector,
                                         get( j ) );
                }
            }
            index++;
        }

        return Conflict.EMPTY;
    }

    @Override
    public RedundancyResult hasRedundancy() {

        for ( int i = 0; i < size(); i++ ) {

            final T inspector = get( i );

            for ( int j = i + 1; j < size(); j++ ) {
                final T other = get( j );
                if ( inspector.isRedundant( other ) ) {
                    return new RedundancyResult( inspector,
                                                 get( j ) );
                }
            }
        }

        return RedundancyResult.EMPTY;
    }
}
