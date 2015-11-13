/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;

/**
 * Indicates that the target class defines a Perspective in the workbench.
 * <p>
 * There are two options for defining the arrangement of panels and parts within the perspective: either
 * programmatically build a {@link PerspectiveDefinition} object, or declare panel structure and content using Errai UI
 * templates. Note that you cannot mix the two approaches.
 *
 * <h3>Programmatic Perspective Definition</h3>
 * To define the perspective layout programmatically, create a zero-argument method annotated with {@code @Perspective}
 * that returns a {@link PerspectiveDefinition}.
 *
 * <h3>Templated Perspective Definition</h3>
 * To declare perspective layout using templates, make the class an Errai UI templated component, and then add the
 * {@link WorkbenchPanel} annotation to one or more of its {@code @DataField} widgets. This designates them as panel
 * containers and allows you to specify which parts should be added to them when the perspective launches.
 *
 * <h3>Perspective Lifecycle</h3>
 * WorkbenchPerspectives receive the standard set of lifecycle calls for a Workbench component:
 * <ul>
 * <li>{@code @OnStartup}</li>
 * <li>{@code @OnOpen}</li>
 * <li>{@code @OnClose}</li>
 * <li>{@code @OnShutdown}</li>
 * </ul>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WorkbenchPerspective {

    /**
     * The place ID to associate with this perspective.
     *
     * @see PlaceRequest
     */
    String identifier();

    /**
     * Indicates that this perspective should be opened by default when the workbench first starts. Exactly one
     * perspective in the whole application should be marked as default.
     */
    boolean isDefault() default false;

    /**
	 * If true (the default), every time this perspective is displayed, it
	 * should start fresh with the {@link PerspectiveDefinition} returned by the
	 * method annotated with {@code @Perspective}. If false, the framework will
	 * store the structure of the perspective (panel arrangements and part
	 * placement as modified by the user opening and closing tabs, dragging
	 * parts around, and resizing split panels) on the server individually for
	 * each user, and use that stored definition in preference to the one
	 * returned by the {@code @Perspective} method.
	 */
    boolean isTransient() default true;
}
