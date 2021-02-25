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

package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.HasWidgets;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@JsType
public interface PlaceManager {

    @JsIgnore
    void goTo(final PlaceRequest place,
              final HasWidgets addTo);

    @JsIgnore
    void bootstrapPerspective();

    @JsMethod
    void closePlace(final PlaceRequest placeToClose);

    @JsMethod(name = "closePlaceWithCommand")
    void closePlace(final PlaceRequest placeToClose,
                    final Command onAfterClose);
}
