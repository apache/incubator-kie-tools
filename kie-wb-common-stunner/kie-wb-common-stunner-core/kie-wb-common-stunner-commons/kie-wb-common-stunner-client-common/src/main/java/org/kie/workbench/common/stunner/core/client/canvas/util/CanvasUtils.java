/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.Collections;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolationImpl;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandResultImpl;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.violations.BoundsExceededViolation;

public class CanvasUtils {

    public static boolean areBoundsExceeded(final AbstractCanvasHandler canvasHandler,
                                            final Bounds bounds) {
        final CanvasPanel canvasPanel = canvasHandler.getAbstractCanvas().getView().getPanel();
        return !GraphUtils.checkBoundsExceeded(canvasPanel.getLocationConstraints(),
                                               bounds);
    }

    public static CommandResult<CanvasViolation> createBoundsExceededCommandResult(final AbstractCanvasHandler canvasHandler,
                                                                                   final Bounds bounds) {
        final CanvasViolation cv = CanvasViolationImpl.Builder.build(new BoundsExceededViolation(bounds)
                                                                             .setUUID(canvasHandler.getUuid()));

        return new CommandResultImpl<>(
                CommandResult.Type.ERROR,
                Collections.singleton(cv)
        );
    }
}
