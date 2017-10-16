/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.api;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

/**
 * Convenient wrapper for a button that goes to a particular place when clicked.
 */
public class PlaceButton extends Composite {

    private final Button button = new Button();

    public PlaceButton(final PlaceManager placeManager,
                       final DefaultPlaceRequest goTo) {
        checkNotNull("placeManager",
                     placeManager);
        checkNotNull("goTo",
                     goTo);

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                placeManager.goTo(goTo);
            }
        });
        button.setText(goTo.toString());
        initWidget(button);
    }
}
