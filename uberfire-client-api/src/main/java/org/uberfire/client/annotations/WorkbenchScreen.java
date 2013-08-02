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

/**
 * Classes annotated with this are considered WorkbenchParts that perform
 * display some form of read-only content. If the content can be edited and
 * saved developers should consider using {@code @WorkbenchEditor}.
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
 * In this latter case the {@code @WorkbenchScreen} need not implement
 * {@code com.google.gwt.user.client.ui.IsWidget}.
 * </p>
 * <p>
 * WorkbechEditors can receive the following life-cycle calls:
 * <ul>
 * <li>{@code @OnClose}</li>
 * <li>{@code @OnFocus}</li>
 * <li>{@code @OnLostFocus}</li>
 * <li>{@code @OnMayClose}</li>
 * <li>{@code @OnStartup}</li>
 * <li>{@code @OnOpen}</li>
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WorkbenchScreen {

    String identifier();

}
