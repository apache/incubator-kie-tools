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

package org.uberfire.client.authz;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchClientEditorActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;

/**
 * An interface for checking access to workbench resources (perspectives, screens, editors, ...)
 * using a fluent styled API.
 * <p>
 * <p>Example usage:</p>
 * <pre>
 * {@code Button deleteButton;
 *   WorkbenchController workbenchController;
 *   PerspectiveActivity perspective1;
 *
 *   workbenchController.perspective(perspective1).delete()
 *     .granted(() -> deleteButton.setEnabled(true))
 *     .denied(() -> deleteButton.setEnabled(false))
 * }</pre>
 */
public interface WorkbenchController {

    /**
     * Creates a brand new instance for checking global perspective actions.
     */
    PerspectiveCheck perspectives();

    /**
     * Creates a brand new instance for checking global screen actions.
     */
    ActivityCheck screens();

    /**
     * Creates a brand new instance for checking global popup screen actions.
     * @return A handler for dealing with the perspective check API.
     */
    ActivityCheck popupScreens();

    /**
     * Creates a brand new instance for checking global splash screen actions.
     * @return A handler for dealing with the perspective check API.
     */
    ActivityCheck splashScreens();

    /**
     * Creates a brand new instance for checking global perspective actions actions.
     * @return A handler for dealing with the perspective check API.
     */
    ActivityCheck editors();

    /**
     * Creates a brand new instance for checking actions over {@link PerspectiveActivity} instances.
     */
    PerspectiveCheck perspective(PerspectiveActivity perspective);

    /**
     * Creates a brand new instance for checking actions over {@link WorkbenchScreenActivity} instances.
     */
    ActivityCheck screen(WorkbenchScreenActivity screen);

    /**
     * Creates a brand new instance for checking actions over {@link PopupActivity} instances.
     */
    ActivityCheck popupScreen(PopupActivity popup);

    /**
     * Creates a brand new instance for checking actions over {@link WorkbenchEditorActivity} instances.
     */
    ActivityCheck editor(WorkbenchEditorActivity editor);

    /**
     * Creates a brand new instance for checking actions over {@link WorkbenchClientEditorActivity} instances.
     */
    ActivityCheck editor(WorkbenchClientEditorActivity editor);

    /**
     * Creates a brand new instance for checking actions over {@link SplashScreenActivity} instances.
     */
    ActivityCheck splashScreen(SplashScreenActivity splash);

    /**
     * Creates a brand new instance for checking actions over {@link PerspectiveActivity} instances.
     */
    PerspectiveCheck perspective(String perspectiveId);

    /**
     * Creates a brand new instance for checking actions over {@link WorkbenchScreenActivity} instances.
     */
    ActivityCheck screen(String screenId);

    /**
     * Creates a brand new instance for checking actions over {@link PopupActivity} instances.
     */
    ActivityCheck popupScreen(String popupId);

    /**
     * Creates a brand new instance for checking actions over {@link WorkbenchEditorActivity} instances.
     */
    ActivityCheck editor(String editorId);

    /**
     * Creates a brand new instance for checking actions over {@link SplashScreenActivity} instances.
     */
    ActivityCheck splashScreen(String splashId);
}