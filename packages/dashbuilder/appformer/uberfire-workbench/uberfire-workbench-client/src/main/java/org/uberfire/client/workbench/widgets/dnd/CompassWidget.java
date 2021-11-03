/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.widgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import org.uberfire.workbench.model.Position;

/**
 * A pop-up widget with arrows in the four cardinal directions, each of which is a separate drop target. The center of
 * the widget is a fifth drop target representing the parent widget itself. The compass centers itself over its parent's
 * Drop Target when displayed.
 */
public interface CompassWidget extends DropController {

    Position getDropPosition();
}
