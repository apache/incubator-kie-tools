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


package org.kie.workbench.common.widgets.client.docks;

import java.util.Collection;

import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.mvp.Command;

/**
 * Component that handles the state of the authoring docks
 */
public interface WorkbenchDocksHandler {

    /**
     * Initializes the handler with the callback {@link Command}. This {@link Command} will be executed any time
     * the docks must be refreshed
     */
    void init(Command updateDocksCommand);

    /**
     * Determines if the docks should be refreshed or not.
     */
    boolean shouldRefreshDocks();

    /**
     * Determines if the docks should be disabled or not
     */
    boolean shouldDisableDocks();

    /**
     * Provides a Collection containing the {@link UberfireDock} that should be displayed when this handler is active
     */
    Collection<UberfireDock> provideDocks(String perspectiveIdentifier);
}