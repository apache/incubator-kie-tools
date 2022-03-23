/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.menu.dev;

import org.gwtbootstrap3.client.ui.constants.IconType;

/**
 * Session command types only available on development.
 * Allows quickly creating buttons on the menu in any module but just creating
 * a bean for this type.
 * @See org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommandsBuilder
 */
public interface MenuDevCommand {

    String getText();

    IconType getIcon();

    void execute();
}
