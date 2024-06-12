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

package com.ait.lienzo.client.core.shape.wires.handlers;

/**
 * Control that performs operations in wires objects (shapes, connectors, etc).
 */
public interface WiresControl {

    /**
     * Execute the control's logic.
     */
    void execute();

    /**
     * Clear the control's state.
     * No operations expected for wires objects.
     */
    void clear();

    /**
     * Return the wires object/s to it's initial state
     * and clears current control's state.
     */
    void reset();

    /**
     * Destroys the control instance
     */
    void destroy();
}
