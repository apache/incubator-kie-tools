/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.callbacks;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Error Callback that allows Commands to be defined to handled Exceptions
 */
public class CommandWithThrowableDrivenErrorCallback extends HasBusyIndicatorDefaultErrorCallback {

    @FunctionalInterface
    public interface CommandWithThrowable extends ParameterizedCommand<Throwable> {

    }

    private final CommandWithThrowable defaultCommand;
    private final Map<Class<? extends Throwable>, CommandWithThrowable> commands = new HashMap<Class<? extends Throwable>, CommandWithThrowable>();

    public CommandWithThrowableDrivenErrorCallback(final HasBusyIndicator view,
                                                   final Map<Class<? extends Throwable>, CommandWithThrowable> commands) {
        this(view, commands, null);
    }

    public CommandWithThrowableDrivenErrorCallback(final HasBusyIndicator view,
                                                   final Map<Class<? extends Throwable>, CommandWithThrowable> commands,
                                                   final CommandWithThrowable defaultCommand) {
        super(view);
        this.commands.putAll(PortablePreconditions.checkNotNull("commands", commands));
        this.defaultCommand = defaultCommand;
    }

    public CommandWithThrowableDrivenErrorCallback(HasBusyIndicator view, CommandWithThrowable defaultCommand) {
        super(view);
        this.defaultCommand = defaultCommand;
    }

    @Override
    public boolean error(final Message message,
                         final Throwable throwable) {
        // The *real* Throwable is wrapped in an InvocationTargetException when ran as a Unit Test and invoked with Reflection.
        final Throwable _throwable = (throwable.getCause() == null ? throwable : throwable.getCause());
        for (Map.Entry<Class<? extends Throwable>, CommandWithThrowable> e : commands.entrySet()) {
            if (e.getKey().getName().equals(_throwable.getClass().getName())) {
                e.getValue().execute(_throwable);
                return false;
            }
        }

        if (defaultCommand != null) {
            defaultCommand.execute(throwable);
            return false;
        }

        return super.error(message, throwable);
    }
}
