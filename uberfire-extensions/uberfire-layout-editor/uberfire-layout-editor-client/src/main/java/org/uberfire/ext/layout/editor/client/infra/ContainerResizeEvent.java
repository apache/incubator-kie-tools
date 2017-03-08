/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

public class ContainerResizeEvent {

    private int columnHashCode;
    private int rowHashCode;
    private Direction direction = Direction.LEFT;

    public ContainerResizeEvent(int columnHashCode,
                                int rowHashCode) {

        this.columnHashCode = columnHashCode;
        this.rowHashCode = rowHashCode;
    }

    public ContainerResizeEvent() {
    }

    public int getRowHashCode() {
        return rowHashCode;
    }

    public int getColumnHashCode() {
        return columnHashCode;
    }

    public ContainerResizeEvent left() {
        this.direction = Direction.LEFT;
        return this;
    }

    public ContainerResizeEvent right() {
        this.direction = Direction.RIGHT;
        return this;
    }

    public boolean isLeft() {
        return direction == Direction.LEFT;
    }

    public Direction getDirection() {
        return direction;
    }

    private enum Direction {
        LEFT,
        RIGHT;
    }

    ;
}
