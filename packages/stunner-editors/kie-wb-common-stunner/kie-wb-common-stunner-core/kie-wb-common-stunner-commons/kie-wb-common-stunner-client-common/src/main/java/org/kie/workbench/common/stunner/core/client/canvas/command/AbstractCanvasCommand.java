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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Base type for commands that update the canvas.
 */
public abstract class AbstractCanvasCommand implements CanvasCommand<AbstractCanvasHandler> {

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        return buildResult();
    }

    protected CommandResult<CanvasViolation> buildResult() {
        return CanvasCommandResultBuilder.SUCCESS;
    }

    public static String toUUIDs(final Collection<? extends Element> elements) {
        return elements.stream()
                .map(AbstractCanvasCommand::toUUID)
                .collect(Collectors.joining(","));
    }

    public static String toUUID(final Element<?> element) {
        return null != element ? element.getUUID() : "null";
    }

    protected boolean checkShapeNotNull(final AbstractCanvasHandler context, final String uuid) {
        return Objects.nonNull(context.getCanvas().getShape(uuid));
    }
}
