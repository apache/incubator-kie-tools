/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.uberfire.workbench.model.PerspectiveDefinition;

/**
 * Classes annotated with this are considered Perspective providers.
 * <p>
 * Developers will need to provide a zero-argument method annotated with
 * {@code @Perspective} with return type
 * {@code org.uberfire.client.workbench.model.PerspectiveDefinition}.
 * </p>
 * <p>
 * WorkbenchPerspectives can receive the following life-cycle calls:
 * <ul>
 * <li>{@code @OnClose}</li>
 * <li>{@code @OnStartup}</li>
 * <li>{@code @OnOpen}</li>
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WorkbenchPerspective {

    String identifier();

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
