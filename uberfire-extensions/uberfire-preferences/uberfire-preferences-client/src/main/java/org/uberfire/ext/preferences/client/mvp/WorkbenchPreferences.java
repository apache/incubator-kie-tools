/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.mvp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with this are considered WorkbenchParts that display some form of non-editable (but possibly still
 * interactive) content, and should define a configuration screen of some sort.
 * <p/>
 * All classes annotated with {@code @WorkbenchConfigurationScreen} must have a declared or inherited method annotated
 * with {@code @WorkbenchPartTitle}.
 * <p/>
 * There are two options for providing the configuration screen's view:
 * <ol>
 * <li>the class implements {@code com.google.gwt.user.client.ui.IsWidget} (often by extending {@code com.google.gwt.user.client.ui.Composite})
 * <li>the class declares or inherits a zero-argument method annotated with {@code @WorkbenchPartView} that returns the
 * {@code com.google.gwt.user.client.ui.IsWidget} or preferably
 * {@code org.jboss.errai.common.client.api.IsElement} that handles the view. In this case the class need not
 * implement {@code com.google.gwt.user.client.ui.IsWidget}.
 * </ol>
 * <p/>
 * Developers wishing to separate view from logic via the MVP pattern will choose the second option.
 * <p/>
 * WorkbenchConfigurationScreens can receive the following life-cycle calls:
 * <ul>
 * <li>{@code @OnStartup}</li>
 * <li>{@code @OnOpen}</li>
 * <li>{@code @OnFocus}</li>
 * <li>{@code @OnLostFocus}</li>
 * <li>{@code @OnMayClose}</li>
 * <li>{@code @OnClose}</li>
 * <li>{@code @OnShutdown}</li>
 * </ul>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WorkbenchPreferences {

    /**
     * Identifier that should be unique within application.
     */
    String identifier();

    /**
     * Defines the preferred height. Preferred means that this Height will be used only if this configuration screen
     * is the trigger to create a new panel, if panel already exists this information is ignored.
     */
    int preferredHeight() default -1;

    /**
     * Defines the preferred width. Preferred means that this Width will be used only if this configuration screen
     * is the trigger to create a new panel, if panel already exists this information is ignored.
     */
    int preferredWidth() default -1;
}