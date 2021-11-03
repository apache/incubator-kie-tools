/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

/**
 * 
 * This annotation works like {@code @WorkbenchEditor}, and should be used for client side only editors.
 * 
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
@Target({ElementType.TYPE})
public @interface WorkbenchClientEditor {

    /**
     * Identifier that should be unique within application.
     */
    String identifier();

}
