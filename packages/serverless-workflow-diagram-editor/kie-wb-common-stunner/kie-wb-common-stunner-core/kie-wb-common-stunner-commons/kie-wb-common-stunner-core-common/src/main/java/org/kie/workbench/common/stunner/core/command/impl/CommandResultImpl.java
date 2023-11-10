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


package org.kie.workbench.common.stunner.core.command.impl;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.command.CommandResult;

public final class CommandResultImpl<V> implements CommandResult<V> {

    private final Type type;
    private final Collection<V> violations;

    public CommandResultImpl(final Type type,
                             final Collection<V> violations) {
        this.violations = violations;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Iterable<V> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return "[Class=" + super.getClass().getSimpleName() + ", "
                + "Type=" + type.name() + ", "
                + "Violations=" + violations + "]";
    }
}
