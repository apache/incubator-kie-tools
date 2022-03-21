/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popover;

import java.util.Optional;

public interface PopoverView extends org.jboss.errai.ui.client.local.api.IsElement {

    enum Position {
        LEFT,
        RIGHT,
        TOP
    }

    /**
     * Method to set/update status of the elements <b>before</b> actually showing the view.
     * Implemented to decouple this setup from the actual <b>show</b>, to be able to eventually add other modifications
     * (e.g. change vertical position based on the actual height, that is available only <b>after</b> this method has been invoked)
     * @param editorTitle
     * @param mx
     * @param my
     * @param position
     */
    void setup(final Optional<String> editorTitle, final int mx, final int my, final Position position);

    /**
     * Method that actually <b>show</b> the view
     */
    void show();

    boolean isShown();

    void hide();
}
