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
 * Positions to which a WorkbenchPanel can be added to the Workbench
 */
@JsType
public enum CompassPosition implements Position {

    NONE,
    //Don't add anywhere
    NORTH,
    //North internal edge of a Parent panel
    SOUTH,
    //South internal edge of a Parent panel
    EAST,
    //East internal edge of a Parent panel
    WEST,
    //West internal edge of a Parent panel
    SELF,
    //Add to the Parent panel
    ROOT,
    //Add to the Workbench root
    CENTER; // Add to the panel center

    @Override
    public String getName() {
        return name();
    }
}
