/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.command.client;

import java.util.Collection;
import java.util.LinkedList;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;

@NonPortable
public abstract class CommandResultBuilder<V> {

    private CommandResult.Type type = CommandResult.Type.INFO;
    private final Collection<V> violations = new LinkedList<>();

    public static final CommandResult SUCCESS = new CommandResultImpl<>(CommandResult.Type.INFO,
                                                                        new LinkedList<>()
    );

    public static final CommandResult FAILED = new CommandResultImpl<>(CommandResult.Type.ERROR,
                                                                       new LinkedList<>()
    );

    public abstract CommandResult.Type getType(final V violation);

    public CommandResultBuilder() {
    }

    public CommandResultBuilder(final Collection<V> violations) {
        this.violations.addAll(violations);
    }

    public CommandResultBuilder<V> addViolation(final V violation) {
        this.violations.add(violation);
        return this;
    }

    public CommandResultBuilder<V> addViolations(final Collection<V> violations) {
        this.violations.addAll(violations);
        return this;
    }

    public CommandResultBuilder<V> setType(final CommandResult.Type type) {
        this.type = type;
        return this;
    }

    public CommandResult<V> build() {
        violations.forEach(v -> {
            final CommandResult.Type violationType = getType(v);
            if (violationType.getSeverity() > this.type.getSeverity()) {
                this.type = violationType;
            }
        });
        return new CommandResultImpl<>(this.type,
                                       this.violations);
    }
}
