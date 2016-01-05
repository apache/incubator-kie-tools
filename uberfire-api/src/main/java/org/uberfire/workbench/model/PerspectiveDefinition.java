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
package org.uberfire.workbench.model;

/**
 * Meta-data defining a Perspective. A Perspective is a set of Panels and Parts arranged within the Workbench. The
 * Workbench has exactly one active Perspective at a time. The Perspective contains multiple Panels. Each Panel contains
 * multiple Parts, one of which can be visible at a time. Panels can also contain child panels which are all visible at
 * the same time are are physically located within the bounds of the parent panel. Each Part is associated with one
 * PlaceRequest.
 * <p>
 * Implementations of this interface must be marked with Errai's {@code @Portable} annotation.
 */
public interface PerspectiveDefinition {

    /**
     * Get the name of the Perspective.
     * @return The name of the Perspective.
     */
    public String getName();

    /**
     * Set the name of the Perspective.
     * @param name The name of the Perspective.
     */
    public void setName( final String name );

    /**
     * Get the root Panel for this Perspective. The root Panel contains all
     * child Panels. A Perspective is based on a single root Panel.
     * @return The root Panel.
     */
    public PanelDefinition getRoot();

    void setContextDefinition( final ContextDefinition contextDefinition );

    ContextDefinition getContextDefinition();

    ContextDisplayMode getContextDisplayMode();

    void setContextDisplayMode( final ContextDisplayMode contextDisplayMode );
}
