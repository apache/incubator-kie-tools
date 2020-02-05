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

package com.ait.lienzo.client.core.shape.wires.proxy;

public interface WiresProxy {

    /**
     * Start point, the proxy shape will appear at this location.
     * @param x The value for the absolute X coordinate.
     * @param y The value for the absolute Y coordinate.
     */
    void start(double x, double y);

    /**
     * Move the proxy shape.
     * @param dx The X diff, since the start point.
     * @param dy The Y diff, since the start point.
     */
    void move(double dx, double dy);

    /**
     * Completes moving the proxy shape operation.
     */
    void end();

    /**
     * Destroys the proxy and its state.
     */
    void destroy();
}
