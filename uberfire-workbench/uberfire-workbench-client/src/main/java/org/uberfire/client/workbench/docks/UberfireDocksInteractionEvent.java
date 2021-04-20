/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.docks;

import org.uberfire.workbench.events.UberFireEvent;

public class UberfireDocksInteractionEvent implements UberFireEvent {

    private final UberfireDock targetDock;

    private final UberfireDockPosition targetDockPosition;

    private final InteractionType type;

    public UberfireDocksInteractionEvent(UberfireDock targetDock,
                                         InteractionType type) {
        this.targetDock = targetDock;
        this.targetDockPosition = targetDock.getDockPosition();
        this.type = type;
    }

    public UberfireDocksInteractionEvent(final UberfireDockPosition position,
                                         final InteractionType type) {
        this.targetDock = null;
        this.targetDockPosition = position;
        this.type = type;
    }

    public UberfireDock getTargetDock() {
        return targetDock;
    }

    public UberfireDockPosition getTargetDockPosition() {
        return targetDockPosition;
    }

    public InteractionType getType() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UberfireDocksInteractionEvent that = (UberfireDocksInteractionEvent) o;

        if (targetDock != null ? !targetDock.equals(that.targetDock) : that.targetDock != null) {
            return false;
        }
        if (targetDockPosition != that.targetDockPosition) {
            return false;
        }
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = targetDock != null ? targetDock.hashCode() : 0;
        result = 31 * result + (targetDockPosition != null ? targetDockPosition.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = ~~result;
        return result;
    }

    public enum InteractionType {
        OPENED,
        CLOSED,
        RESIZED
    }
}
