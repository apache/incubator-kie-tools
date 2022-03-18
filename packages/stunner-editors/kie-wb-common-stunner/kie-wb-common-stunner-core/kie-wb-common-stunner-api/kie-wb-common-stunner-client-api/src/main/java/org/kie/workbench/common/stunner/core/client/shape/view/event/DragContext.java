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

package org.kie.workbench.common.stunner.core.client.shape.view.event;

import org.uberfire.mvp.Command;

public final class DragContext {

    private final int dx;
    private final int dy;
    private final Command resetCommand;

    public DragContext(final int dx,
                       final int dy,
                       final Command resetCommand) {
        this.dx = dx;
        this.dy = dy;
        this.resetCommand = resetCommand;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void reset() {
        resetCommand.execute();
    }
}
