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
 * Classes annotated with this are considered Workbench popups.
 * <p>
 * At its simplest form the Class should extend
 * {@code com.google.gwt.user.client.ui.PopupPanel} and provide a method
 * annotated with {@code @OnOpen}.
 * </p>
 * <p>
 * Developers wishing to separate view from logic (perhaps by implementing the
 * MVP pattern) can further provide a zero-argument method annotated with
 * {@code @WorkbenchPartView} with return type
 * {@code com.google.gwt.user.client.ui.PopupPanel}.
 * </p>
 * <p>
 * In this latter case the {@code @WorkbenchPopup} need not extend
 * {@code com.google.gwt.user.client.ui.PopupPanel}.
 * </p>
 * <p>
 * WorkbechPopups can receive the following life-cycle calls:
 * <ul>
 * <li>{@code @OnOpen}</li>
 * </p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WorkbenchPopup {

    String identifier();

}
