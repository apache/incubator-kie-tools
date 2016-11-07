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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;

import java.util.Iterator;
import java.util.List;

@Portable
public final class BatchCommandResultImpl<V> implements BatchCommandResult<V> {

    private final List<CommandResult<V>> results;
    private final Type type;
    private final String message;
    private final Iterable<V> violations;

    public BatchCommandResultImpl( @MapsTo( "results" ) List<CommandResult<V>> results,
                                   @MapsTo( "type" ) Type type,
                                   @MapsTo( "message" ) String message,
                                   @MapsTo( "violations" ) Iterable<V> violations ) {
        this.results = results;
        this.type = type;
        this.message = message;
        this.violations = violations;
    }

    @Override
    public Iterator<CommandResult<V>> iterator() {
        return results.iterator();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Iterable<V> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return "[Class=" + super.getClass().getSimpleName() + ", "
                + "Type=" + type.name() + ", "
                + "Message=" + message + ", "
                + "Results=" + results + ", "
                + "Violations=" + violations + "]";
    }

}
