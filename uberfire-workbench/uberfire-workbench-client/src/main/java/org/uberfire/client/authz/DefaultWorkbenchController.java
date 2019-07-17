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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchClientEditorActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
public class DefaultWorkbenchController implements WorkbenchController {

    AuthorizationManager authorizationManager;
    User user;
    @Inject
    public DefaultWorkbenchController(AuthorizationManager authorizationManager,
                                      User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public static DefaultWorkbenchController get() {
        return IOC.getBeanManager().lookupBean(DefaultWorkbenchController.class).getInstance();
    }

    @Override
    public PerspectiveCheck perspectives() {
        return new PerspectiveCheck(authorizationManager,
                                    ActivityResourceType.PERSPECTIVE,
                                    user);
    }

    @Override
    public ActivityCheck screens() {
        return new ActivityCheck(authorizationManager,
                                 ActivityResourceType.SCREEN,
                                 user);
    }

    @Override
    public ActivityCheck popupScreens() {
        return new ActivityCheck(authorizationManager,
                                 ActivityResourceType.POPUP,
                                 user);
    }

    @Override
    public ActivityCheck splashScreens() {
        return new ActivityCheck(authorizationManager,
                                 ActivityResourceType.SPLASH,
                                 user);
    }

    @Override
    public ActivityCheck editors() {
        return new ActivityCheck(authorizationManager,
                                 ActivityResourceType.EDITOR,
                                 user);
    }

    @Override
    public PerspectiveCheck perspective(PerspectiveActivity perspective) {
        return new PerspectiveCheck(authorizationManager,
                                    perspective,
                                    user);
    }

    @Override
    public ActivityCheck screen(WorkbenchScreenActivity screen) {
        return new ActivityCheck(authorizationManager,
                                 screen,
                                 user);
    }

    @Override
    public ActivityCheck popupScreen(PopupActivity popup) {
        return new ActivityCheck(authorizationManager,
                                 popup,
                                 user);
    }

    @Override
    public ActivityCheck editor(WorkbenchEditorActivity editor) {
        return new ActivityCheck(authorizationManager,
                                 editor,
                                 user);
    }

    @Override
    public ActivityCheck editor(WorkbenchClientEditorActivity editor) {
        return new ActivityCheck(authorizationManager,
                                 editor,
                                 user);
    }

    @Override
    public ActivityCheck splashScreen(SplashScreenActivity splash) {
        return new ActivityCheck(authorizationManager,
                                 splash,
                                 user);
    }

    @Override
    public PerspectiveCheck perspective(String perspectiveId) {
        ResourceRef ref = new ResourceRef(perspectiveId,
                                          ActivityResourceType.PERSPECTIVE);
        return new PerspectiveCheck(authorizationManager,
                                    ref,
                                    user);
    }

    @Override
    public ActivityCheck screen(String screenId) {
        ResourceRef ref = new ResourceRef(screenId,
                                          ActivityResourceType.SCREEN);
        return new PerspectiveCheck(authorizationManager,
                                    ref,
                                    user);
    }

    @Override
    public ActivityCheck popupScreen(String popupId) {
        ResourceRef ref = new ResourceRef(popupId,
                                          ActivityResourceType.POPUP);
        return new PerspectiveCheck(authorizationManager,
                                    ref,
                                    user);
    }

    @Override
    public ActivityCheck editor(String editorId) {
        ResourceRef ref = new ResourceRef(editorId,
                                          ActivityResourceType.EDITOR);
        return new PerspectiveCheck(authorizationManager,
                                    ref,
                                    user);
    }

    @Override
    public ActivityCheck splashScreen(String splashId) {
        ResourceRef ref = new ResourceRef(splashId,
                                          ActivityResourceType.SPLASH);
        return new PerspectiveCheck(authorizationManager,
                                    ref,
                                    user);
    }
}