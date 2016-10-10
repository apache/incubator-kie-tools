/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.impl;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@NonPortable
public class BatchCommandResultBuilder<V> {

    private final List<CommandResult<V>> results = new LinkedList<>();

    public BatchCommandResultBuilder() {
    }

    public BatchCommandResultBuilder<V> add( final CommandResult<V> result ) {
        this.results.add( result );
        return this;
    }

    public BatchCommandResult<V> build() {
        CommandResult.Type type = CommandResult.Type.INFO;
        String message = "Found" + results.size() + " violations.";
        final Collection<V> violations = new LinkedList<V>();
        for ( CommandResult<V> rr : results ) {
            if ( hasMoreSeverity( rr.getType(), type ) ) {
                type = rr.getType();

            }
            final Iterable<V> rrIter = rr.getViolations();
            if ( null != rrIter ) {
                final Iterator<V> rrIt = rrIter.iterator();
                while ( rrIt.hasNext() ) {
                    final V v = rrIt.next();
                    violations.add( v );

                }

            }

        }
        return new BatchCommandResultImpl<>( results, type, message, violations );
    }

    private boolean hasMoreSeverity( final CommandResult.Type type, final CommandResult.Type reference ) {
        return type.getSeverity() > reference.getSeverity();

    }

}
