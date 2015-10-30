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

import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Classes annotated with this are considered WorkbenchParts that perform some
 * "editor" function for the specified file-type.
 * <p>
 * At its simplest form the Class should implement
 * {@code com.google.gwt.user.client.ui.IsWidget} (e.g. extend
 * {@code com.google.gwt.user.client.ui.Composite}) and provide a method
 * annotated with {@code @WorkbenchPartTitle}.
 * </p>
 * <p>
 * Developers wishing to separate view from logic (perhaps by implementing the
 * MVP pattern) can further provide a zero-argument method annotated with
 * {@code @WorkbenchPartView} with return type
 * {@code com.google.gwt.user.client.ui.IsWidget}.
 * </p>
 * <p>
 * In this latter case the {@code @WorkbenchEditor} need not implement
 * {@code com.google.gwt.user.client.ui.IsWidget}.
 * </p>
 * <p>
 * WorkbechEditors can receive the following life-cycle calls:
 * <ul>
 * <li>{@code @OnStartup(org.drools.guvnor.vfs.Path)}</li>
 * <li>{@code @OnOpen}</li>
 * <li>{@code @OnFocus}</li>
 * <li>{@code @OnLostFocus}</li>
 * <li>{@code @OnMayClose}</li>
 * <li>{@code @OnClose}</li>
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WorkbenchEditor {

    /**
     * Identifier that should be unique within application.
     */
    String identifier();

    /**
     * Array that defines all supported types of this editor.
     */
    Class<? extends ClientResourceType>[] supportedTypes() default { };

    /**
     * Defines the priority of editor over type resolution, editors with same supported type will be resolved by priority.
     */
    int priority() default 0;

    /**
     * By default, a Workbench Editor will show up in the current active perspective. If this parameter is specified,
     * this screen will only be shown on the given perspective. An attempt to navigate to this editor when
     * a different perspective is active will first result in a switch to the owning perspective, then the editor will
     * be shown in that perspective.
     */
    Class<?> owningPerspective() default void.class;

    /**
     * Defines the preferred height. Preferred means that this Height will be used only if this screen
     * is the trigger to create a new panel, if panel already exists this information is ignored.
     */
    int preferredHeight() default -1;

    /**
     * Defines the preferred width. Preferred means that this Width will be used only if this screen
     * is the trigger to create a new panel, if panel already exists this information is ignored.
     */
    int preferredWidth() default -1;
    
    /**
     * Defines how and if locks are acquired when using this editor. By default, a pessimistic locking 
     * strategy is used, allowing edits by only one user at a time.
     */
    LockingStrategy lockingStrategy() default LockingStrategy.PESSIMISTIC; 
    
    /**
     * Locking strategies define how and if locks are acquired when using editors.
     */
    public static enum LockingStrategy {
        OPTIMISTIC, // No locks are acquired, editor implementations need their own conflict resolution logic (if desired).
        PESSIMISTIC // Locks are acquired allowing edits by only one user at a time
    }
}
