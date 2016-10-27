/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.navigation.navigator;

import org.uberfire.mvp.Command;

public interface NavigatorItem<T> {

    /**
     * Shows the item.
     *
     * @param item     Item to show.
     * @param width    Width in PX.
     * @param height   Height in PX
     * @param callback Callback when item selected ( usually on click or touch )
     */
    void show( T item, int width, int height, Command callback );

    String getName();

    NavigatorItemView getView();

    void onItemSelected();

}
