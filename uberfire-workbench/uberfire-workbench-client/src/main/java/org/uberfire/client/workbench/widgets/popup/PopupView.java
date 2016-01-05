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
package org.uberfire.client.workbench.widgets.popup;

import org.uberfire.client.mvp.PopupActivity;

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * API contract for the view container of {@link PopupActivity} activities. Implementations of this class must be
 * Dependent-scoped CDI beans.
 * <p>
 * Each application must have exactly one implementation of this interface in the classpath at compile time. Normally
 * this implementation will be part of a view module.
 */
public interface PopupView extends HasCloseHandlers<PopupView> {

    /**
     * Sets the main content of this popup dialog, replacing any content that was previously set.
     *
     * @param widget the content to add. Must not be null.
     */
    public void setContent( final IsWidget widget );

    /**
     * Sets the title text for this popup's dialog. Usually, the view will put this in a large font above the
     * main content.
     *
     * @param title The title text for the popup container.
     */
    public void setTitle( final String title );

    /**
     * Makes this popup container (and the main content along with it) visible on the workbench. Has no effect if this
     * popup is already visible.
     */
    public void show();

    /**
     * Makes this popup container(and the main content along with it) invisible. Has no effect if the popup is not
     * already showing.
     */
    public void hide();

}