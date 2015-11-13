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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies an UberFire workbench panel within a templated perspective.
 * <p>
 * <h3>Prerequisites</h3>
 * This annotation can only be used within a class annotated with {@link WorkbenchPerspective}, and it must target a
 * field that implements the GWT HasWidgets interface. Further, the class this annotation is used in must not have a
 * method annotated with {@link Perspective}. See {@link WorkbenchPerspective} for details.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface WorkbenchPanel {

    /**
     * The Presenter type of the panel. Must be a Dependent-scoped Errai IOC bean which implements
     * WorkbenchPanelPresenter. The default is SimpleWorkbenchPanelPresenter.
     */
    Class<?/* TODO extends WorkbenchPanelPresenter */> panelType() default Void.class;

    /**
     * Specifies the PlaceRequests that should be used to populate this panel with parts when the containing perspective
     * is first launched. For panel types that only support a single part (such as StaticWorkbenchPanelPresenter), only
     * one item should be used here.
     * <p>
     * To specify a PlaceRequest with parameters, use standard URL query syntax:
     *
     * <pre>
     *   {@code @WorkbenchPanel(parts = "MyPlaceID?param1=val1&amp;param2=val2")}
     * </pre>
     *
     * Special characters can be escaped using URL encoding: for '%' use '%25'; for '&amp;' use '%26'; for '=' use '%3d';
     * for '?' use '%3f'.
     */
    String[] parts();

    /**
     * Makes this panel the first panel in the generated PerspectiveDefinition object. Some panel presenters may treat
     * their first child panel specially (for example, by forwarding addPanel requests to it). If no panel within a
     * perspective is explicitly marked as the default, a randomly selected panel will be first. It is an error to
     * define more than one default panel in a perspective.
     */
    boolean isDefault() default false;

}
