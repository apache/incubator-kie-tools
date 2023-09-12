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


package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;

public interface ClipboardControl<E extends Element, C extends Canvas, S extends ClientSession> extends CanvasControl<C> {

    ClipboardControl<E, C, S> set(E... item);

    ClipboardControl<E, C, S> remove(E... item);

    Collection<E> getElements();

    ClipboardControl<E, C, S> clear();

    boolean hasElements();

    String getParent(String uuid);

    List<Command> getRollbackCommands();

    ClipboardControl<E, C, S> setRollbackCommand(Command... command);

    Map<String, EdgeClipboard> getEdgeMap();

    EdgeClipboard buildNewEdgeClipboard(final String source, final Connection sourceConnection, final String target, final Connection targetConnection);
}