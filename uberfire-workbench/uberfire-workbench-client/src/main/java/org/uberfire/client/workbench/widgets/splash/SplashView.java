/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench.widgets.splash;

import org.uberfire.client.mvp.SplashScreenActivity;

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * API Contract for the view container of {@link SplashScreenActivity} activities.
 */
public interface SplashView extends HasCloseHandlers<SplashView> {

    /**
     * Sets the contents for this splash screen view, replacing any contents that were there already.
     *
     * @param widget
     *            The widget to display in the body area of the splash screen dialog.
     * @param height
     *            The height to give the content area in the splash screen dialog.
     */
    public void setContent( final IsWidget widget,
                            final Integer height );

    /**
     * Sets the title text for this splash screen's popup. Usually, the view will put this in a large font above the
     * main content.
     *
     * @param title The title text for the splash screen's popup container.
     */
    public void setTitle( final String title );

    /**
     * Returns true if the user has indicated that they want to see this splash screen next time its interceptor says it
     * should be displayed.
     *
     * @return true if the user has indicated that they want to see this splash screen next time it could be displayed;
     *         false if the user has indicated they do not want to see this splash screen next time; null if the user
     *         has not yet made a decision either way. The framework will keep the existing "show again" preference if
     *         this method returns null.
     */
    public Boolean showAgain();

    /**
     * Makes this splash screen container (and the main content along with it) visible on the workbench. Has no effect
     * if this splash screen is already visible.
     */
    public void show();

    /**
     * Makes this splash screen container(and the main content along with it) invisible. Has no effect if the splash
     * screen is not already showing.
     */
    public void hide();

    public boolean isAttached();
}