/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.editor;

import javax.annotation.PostConstruct;

import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;

@EntryPoint
public class DisplayerEditorEntryPoint {

    /**
     * https://issues.jboss.org/browse/DASHBUILDE-105 => Sometimes, due to unknown reasons, the Displayer editor
     * does not show all the attributes in the "Display" tab The fix is to force the DisplayerAttributeDef static
     * fields to initialize on startup.
     */
    @PostConstruct
    public void init() {
        DisplayerAttributeDef def = DisplayerAttributeDef.TITLE;
    }
}
