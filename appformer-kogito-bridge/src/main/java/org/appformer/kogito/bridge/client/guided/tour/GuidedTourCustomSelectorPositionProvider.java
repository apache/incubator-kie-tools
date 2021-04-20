/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;

/**
 * Provides a {@link JsType} native API for JavaScript consumers that may request positions with a custom query selector.
 */
@JsType(namespace = JsPackage.GLOBAL, name = "JsInterop__Envelope__GuidedTour__GuidedTourCustomSelectorPositionProvider")
public class GuidedTourCustomSelectorPositionProvider {

    private static GuidedTourCustomSelectorPositionProvider instance;

    private Map<String, PositionProviderFunction> providers = new HashMap<>();

    private GuidedTourCustomSelectorPositionProvider() {

    }

    /**
     * Gets an instance of the {@link GuidedTourCustomSelectorPositionProvider}
     */
    public static GuidedTourCustomSelectorPositionProvider getInstance() {
        if (instance == null) {
            instance = new GuidedTourCustomSelectorPositionProvider();
        }
        return instance;
    }

    /**
     * Gets a {@link Rect} position for a given custom 'querySelector', which must follow this convention:
     * 'TYPE:::ELEMENT'. E.g. Graph:::Node-1, Iframe:::body, Any:::Element1.
     * @param querySelector a custom query selector
     * @return the position of the selected element
     */
    public Rect getPosition(final String querySelector) {

        final String[] querySelectorParts = getQuerySelectorParts(querySelector);
        final boolean isValidQuerySelector = querySelectorParts.length == 2;

        if (isValidQuerySelector) {

            final String type = querySelectorParts[0];
            final String selector = querySelectorParts[1];
            final Optional<PositionProviderFunction> positionProvider = Optional.ofNullable(providers.get(type));

            if (positionProvider.isPresent()) {
                return positionProvider.get().call(selector);
            } else {
                DomGlobal.console.warn("[Guided Tour - Position Provider] The position provider could not be found: " + type);
            }
        } else {
            DomGlobal.console.warn("[Guided Tour - Position Provider] Invalid custom query selector: " + querySelector);
        }
        return none();
    }

    Rect none() {
        return Rect.NONE();
    }

    /**
     * Registers a {@link PositionProviderFunction} with its type.
     * @param type the prefix for the position provider function
     * @param positionProviderFunction a function that returns a {@link Rect} for a given 'selector'
     */
    public void registerPositionProvider(final String type,
                                         final PositionProviderFunction positionProviderFunction) {
        providers.put(type, positionProviderFunction);
    }

    private String[] getQuerySelectorParts(final String selector) {
        if (selector == null) {
            return new String[0];
        }
        final String separator = ":::";
        return selector.split(separator);
    }

    @JsFunction
    public interface PositionProviderFunction {

        Rect call(final String selector);
    }
}
