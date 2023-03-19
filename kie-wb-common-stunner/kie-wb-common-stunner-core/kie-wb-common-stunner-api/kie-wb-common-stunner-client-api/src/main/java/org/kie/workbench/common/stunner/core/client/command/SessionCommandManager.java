/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.command.CommandResult;

/**
 * Delegates the operations to the <code>CanvasCommandManager</code> given by a <code>ClientSession</code> instance.
 * Provides <code>CommandRequestLifecycle</code> support.
 */
public interface SessionCommandManager<H extends CanvasHandler>
        extends CanvasCommandManager<H>,
                CommandRequestLifecycle {

    CommandResult<CanvasViolation> undo(H context);
}
