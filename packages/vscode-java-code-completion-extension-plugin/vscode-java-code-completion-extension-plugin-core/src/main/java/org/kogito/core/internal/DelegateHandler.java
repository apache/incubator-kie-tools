/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.engine.ActivatorManager;
import org.kogito.core.internal.engine.JavaEngine;
import org.kogito.core.internal.engine.AutoCompletionManager;
import org.kogito.core.internal.handlers.GetAccessorsHandler;
import org.kogito.core.internal.handlers.GetClassesHandler;
import org.kogito.core.internal.handlers.Handler;
import org.kogito.core.internal.handlers.HandlerConstants;
import org.kogito.core.internal.handlers.IsLanguageServerAvailableHandler;

public class DelegateHandler implements IDelegateCommandHandler {

    private static final JavaEngine JAVA_ENGINE = new JavaEngine();
    private static final ActivatorManager ACTIVATOR_MANAGER = new ActivatorManager();
    private static final AutoCompletionManager AUTOCOMPLETE_HANDLER = new AutoCompletionManager(ACTIVATOR_MANAGER);
    private static final IsLanguageServerAvailableHandler LANGUAGE_SERVER_AVAILABLE_HANDLER =
            new IsLanguageServerAvailableHandler(HandlerConstants.IS_AVAILABLE, ACTIVATOR_MANAGER);
    private static final List<Handler<?>> handlers = List.of(
            new GetClassesHandler(HandlerConstants.GET_CLASSES, JAVA_ENGINE, AUTOCOMPLETE_HANDLER),
            new GetAccessorsHandler(HandlerConstants.GET_ACCESSORS, JAVA_ENGINE, AUTOCOMPLETE_HANDLER));

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) {
        JavaLanguageServerPlugin.logInfo(commandId);

        if (LANGUAGE_SERVER_AVAILABLE_HANDLER.canHandle(commandId)) {
            return LANGUAGE_SERVER_AVAILABLE_HANDLER.handle(arguments, progress);
        }

        if (ACTIVATOR_MANAGER.isActivatorEnabled()) {
            return handlers.stream()
                    .filter(handler -> handler.canHandle(commandId))
                    .findFirst()
                    .map(handler -> handler.handle(arguments, progress))
                    .orElseThrow(() -> new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId)));
        } else {
            throw new IllegalStateException("Activator is not working correctly");
        }
    }
}
