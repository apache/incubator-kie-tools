/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kogito.core.internal.handlers;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.engine.AutoCompletionManager;
import org.kogito.core.internal.engine.JavaEngine;
import org.kogito.core.internal.util.WorkspaceUtil;

/**
 * This abstract class defines a set of Handlers which relies on AutoCompletion feature to work. It requires the
 * {@code JavaEngine} and {@code AutoCompletionManager} defined to this project. Before every request requires to create the
 * the Activator java class file BEFORE and delete it eventually.
 * @param <T>
 */
public abstract class AutoCompletionHandler<T> extends Handler<T> {

    protected final JavaEngine javaEngine;
    protected final AutoCompletionManager autoCompletionManager;

    protected AutoCompletionHandler(String id, JavaEngine javaEngine, AutoCompletionManager autoCompletionManager) {
        super(id);
        this.javaEngine = javaEngine;
        this.autoCompletionManager = autoCompletionManager;
    }

    /**
     * The handle method creates the Activator java class file, it runs the internal Handle logic defined in SubClasses
     * implementation and then it delete the Activator file.
     * @param arguments
     * @param progress
     * @return
     */
    @Override
    public T handle(List<Object> arguments, IProgressMonitor progress) {
        JavaLanguageServerPlugin.logInfo("Creating Activator Java File: " + autoCompletionManager.getActivatorPath());
        WorkspaceUtil.createFile(autoCompletionManager.getActivatorPath());
        var result = internalHandler(arguments, progress);
        JavaLanguageServerPlugin.logInfo("Deleting Activator Java File: " + autoCompletionManager.getActivatorPath());
        WorkspaceUtil.deleteFile(autoCompletionManager.getActivatorPath());
        return result;
    }

    public abstract T internalHandler(List<Object> arguments, IProgressMonitor progress);
}