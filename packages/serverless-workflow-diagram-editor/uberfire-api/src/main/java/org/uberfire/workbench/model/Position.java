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

package org.uberfire.workbench.model;

import jsinterop.annotations.JsType;

/**
 * Tells a PanelManager implementation where to place a part within a panel. Each PanelManager has its own layout
 * system, and implements its own unique set of Position objects (for example, the North-South-East-West panel manager
 * uses compass directions, and the Templated panel manager uses element names).
 * <p>
 * All implementations of this interface must be marked as {@link Portable}.
 */
@JsType
public interface Position {

    String getName();
}
