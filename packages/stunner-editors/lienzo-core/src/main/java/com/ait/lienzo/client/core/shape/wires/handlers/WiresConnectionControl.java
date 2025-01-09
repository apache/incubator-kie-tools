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
 * Connection control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types used to fire those functions and call be reused programatically as well.
 * <p>
 * The default event handlers used on wires connection registrations delegate to this control, so developers
 * can create custom connection controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 */
public interface WiresConnectionControl extends WiresMoveControl {

    void destroy();
}