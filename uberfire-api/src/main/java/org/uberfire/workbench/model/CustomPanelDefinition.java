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
package org.uberfire.workbench.model;

import java.util.Optional;

import com.google.gwt.user.client.ui.HasWidgets;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.mvp.PlaceRequest;

/**
 * A custom {@link PanelDefinition} that is associated with a HasWidgets
 * or HTMLElement container. See {@link PlaceManager#goTo(PlaceRequest, HasWidgets)}
 * and {@link PlaceManager#goTo(PlaceRequest, HTMLElement)}
 */
@JsType
public interface CustomPanelDefinition extends PanelDefinition {

    /**
     * Returns the HTMLElement container associated with the custom panel.
     */
    @JsIgnore
    Optional<HTMLElement> getHtmlElementContainer();

    /**
     * Returns the HasWidgets container associated with the custom panel.
     */
    @JsIgnore
    Optional<HasWidgets> getHasWidgetsContainer();
}
