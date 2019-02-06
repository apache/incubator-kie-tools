/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import java.util.List;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;

/**
 * Main interface for renderer implementations.
 *
 * <p>A renderer library must:</p>
 * <ul>
 *     <li>Perform all the required initializations before any displayer can be drawn</li>
 *     <li>Declare the displayer types & subtypes supported</li>
 *     <li>Take care of the initialization of displayer instances</li>
 * </ul>
 */
public interface RendererLibrary {

    /**
     * The unique universal identifier of the rederer
     */
    String getUUID();

    /**
     * The renderer display name
     */
    String getName();

    /**
     * Flag indicating if this renderer can act as the default one for the given displayer type.
     * <p>Default renderers are used when a displayer does not explicitly specifies one</p>
     */
    boolean isDefault(DisplayerType type);

    /**
     * The list of supported types
     */
    List<DisplayerType> getSupportedTypes();

    /**
     * The list of supported sub-types
     */
    List<DisplayerSubType> getSupportedSubtypes(DisplayerType displayerType);

    /**
     * Initalize a displayer instance with the specified configuration.
     */
    Displayer lookupDisplayer(DisplayerSettings displayer);

    /**
     * Draw a list of displayers
     */
    void draw(List<Displayer> displayerList);

    /**
     * Re-draw a list of displayers
     */
    void redraw(List<Displayer> displayerList);
    
    default boolean isOffline() {
        return true;
    }
}
