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

package org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard;

import java.util.Collection;
import java.util.List;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;

public interface ClipboardControl<E extends Element, C extends Canvas, S extends ClientSession> extends CanvasControl<C> {

    ClipboardControl<E, C, S> set(final E... item);

    ClipboardControl<E, C, S> remove(final E... item);

    Collection<E> getElements();

    ClipboardControl<E, C, S> clear();

    boolean hasElements();

    String getParent(String uuid);

    List<Command> getRollbackCommands();

    ClipboardControl<E, C, S> setRollbackCommand(Command... command);
}