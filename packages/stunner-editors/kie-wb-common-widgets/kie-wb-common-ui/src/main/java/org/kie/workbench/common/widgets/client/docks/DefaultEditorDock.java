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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.mvp.PlaceRequest;

public class DefaultEditorDock {

    private Instance<EditorDock> dock;

    public DefaultEditorDock() {
    }

    @Inject
    public DefaultEditorDock(final Instance<EditorDock> dock) {
        this.dock = dock;
    }

    public boolean isSetup() {
        if (!dock.isUnsatisfied()) {
            return dock.get().isSetup();
        } else {
            return true;
        }
    }

    public void setup(final String identifier,
                      final PlaceRequest placeRequest) {
        if (!dock.isUnsatisfied()) {
            dock.get().setup(identifier,
                             placeRequest);
        }
    }

    public void show() {
        if (!dock.isUnsatisfied()) {
            dock.get().show();
        }
    }

    public void hide() {
        if (!dock.isUnsatisfied()) {
            dock.get().hide();
        }
    }
}
