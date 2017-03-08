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
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;

/**
 * A helper class providing static methods on top of the {@link WorkbenchController} functionality.
 * <p>
 * <p>Example usage:</p>
 * <pre>
 * {@code import static org.uberfire.client.authz.WorkbenchControllerHelper.*;
 *
 *   Button deleteButton;
 *   PerspectiveActivity perspective1;
 *
 *   perspective(perspective1).delete()
 *     .denied(() -> deleteButton.setVisible(false))
 * }</pre>
 */
public class WorkbenchControllerHelper {

    /**
     * See {@link WorkbenchController#perspective(PerspectiveActivity)}
     */
    public static PerspectiveCheck perspectives() {
        return DefaultWorkbenchController.get().perspectives();
    }

    /**
     * See {@link WorkbenchController#perspective(PerspectiveActivity)}
     */
    public static PerspectiveCheck perspective(PerspectiveActivity perspective) {
        return DefaultWorkbenchController.get().perspective(perspective);
    }

    /**
     * See {@link WorkbenchController#screens()}
     */
    public static ActivityCheck screens() {
        return DefaultWorkbenchController.get().screens();
    }

    /**
     * See {@link WorkbenchController#popupScreens()}
     */
    public static ActivityCheck popupScreens() {
        return DefaultWorkbenchController.get().popupScreens();
    }

    /**
     * See {@link WorkbenchController#splashScreens()}
     */
    public static ActivityCheck splashScreens() {
        return DefaultWorkbenchController.get().splashScreens();
    }

    /**
     * See {@link WorkbenchController#editors()}
     */
    public static ActivityCheck editors() {
        return DefaultWorkbenchController.get().editors();
    }

    /**
     * See {@link WorkbenchController#screen(WorkbenchScreenActivity)}
     */
    public static ActivityCheck screen(WorkbenchScreenActivity screen) {
        return DefaultWorkbenchController.get().screen(screen);
    }

    /**
     * See {@link WorkbenchController#popupScreen(PopupActivity)}
     */
    public static ActivityCheck popupScreen(PopupActivity popup) {
        return DefaultWorkbenchController.get().popupScreen(popup);
    }

    /**
     * See {@link WorkbenchController#editor(WorkbenchEditorActivity)}
     */
    public static ActivityCheck editor(WorkbenchEditorActivity editor) {
        return DefaultWorkbenchController.get().editor(editor);
    }

    /**
     * See {@link WorkbenchController#splashScreen(SplashScreenActivity)}
     */
    public static ActivityCheck splashScreen(SplashScreenActivity splash) {
        return DefaultWorkbenchController.get().splashScreen(splash);
    }
}