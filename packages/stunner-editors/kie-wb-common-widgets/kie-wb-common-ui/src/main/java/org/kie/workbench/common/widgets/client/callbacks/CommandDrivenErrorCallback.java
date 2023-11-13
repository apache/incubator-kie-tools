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

package org.kie.workbench.common.widgets.client.callbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;

/**
 * Error Callback that allows Commands to be defined to handled Exceptions
 */
public class CommandDrivenErrorCallback extends HasBusyIndicatorDefaultErrorCallback {

    private final Map<Class<? extends Throwable>, Command> commands = new HashMap<>();

    public CommandDrivenErrorCallback(final HasBusyIndicator view,
                                      final Map<Class<? extends Throwable>, Command> commands) {
        super(view);
        this.commands.putAll(Objects.requireNonNull(commands, "Parameter named 'commands' should be not null!"));
    }

    @Override
    public boolean error(final Object message,
                         final Throwable throwable) {
        // The *real* Throwable is wrapped in an InvocationTargetException when ran as a Unit Test and invoked with Reflection.
        final Throwable _throwable = (throwable.getCause() == null ? throwable : throwable.getCause());
        for (Map.Entry<Class<? extends Throwable>, Command> e : commands.entrySet()) {
            if (e.getKey().getName().equals(_throwable.getClass().getName())) {
                e.getValue().execute();
                return false;
            }
        }
        return super.error(message,
                           throwable);
    }
}
