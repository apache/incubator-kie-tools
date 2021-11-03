/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.annotations.processors;

import java.util.function.Consumer;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchPerspective(identifier = "PerspectiveTest8")
public class PerspectiveTest8 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
    }

}
