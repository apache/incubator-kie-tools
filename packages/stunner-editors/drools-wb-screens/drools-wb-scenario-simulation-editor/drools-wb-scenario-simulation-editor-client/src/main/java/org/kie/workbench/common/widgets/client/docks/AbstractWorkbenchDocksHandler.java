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

import org.uberfire.mvp.Command;

public abstract class AbstractWorkbenchDocksHandler implements WorkbenchDocksHandler {

    private boolean shouldRefresh;

    private boolean shouldDisable;

    private Command updateDocksCommand;

    @Override
    public void init(Command updateDocksCommand) {
        this.updateDocksCommand = updateDocksCommand;
    }

    @Override
    public boolean shouldRefreshDocks() {
        return shouldRefresh;
    }

    @Override
    public boolean shouldDisableDocks() {
        return shouldDisable;
    }

    protected void refreshDocks(boolean shouldRefresh,
                                boolean shouldDisable) {
        this.shouldRefresh = shouldRefresh;
        this.shouldDisable = shouldDisable;

        if (updateDocksCommand != null) {
            updateDocksCommand.execute();
        }
    }
}