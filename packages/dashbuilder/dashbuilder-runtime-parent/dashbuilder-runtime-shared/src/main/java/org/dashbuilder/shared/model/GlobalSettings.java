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

package org.dashbuilder.shared.model;

import java.util.Optional;

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.Mode;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GlobalSettings {

    private Mode mode = Mode.LIGHT;
    private DisplayerSettings settings;
    private ExternalDataSetDef def;
    private boolean allowUrlProperties;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public DisplayerSettings getSettings() {
        return settings;
    }

    public void setSettings(DisplayerSettings settings) {
        this.settings = settings;
    }

    public boolean isAllowUrlProperties() {
        return allowUrlProperties;
    }

    public void setAllowUrlProperties(boolean allowUrlProperties) {
        this.allowUrlProperties = allowUrlProperties;
    }

    public Optional<ExternalDataSetDef> getDataSetDef() {
        return Optional.ofNullable(def);
    }

    public void setDataSetDef(ExternalDataSetDef def) {
        this.def = def;
    }

}
