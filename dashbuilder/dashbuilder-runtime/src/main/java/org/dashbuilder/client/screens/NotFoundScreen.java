/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.screens;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnOpen;

/**
 * Screen displayed when a perspective is not found.
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = NotFoundScreen.ID)
public class NotFoundScreen {

    public static final String ID = "NotFoundScreen";
    public static final String TARGET_PARAM = "_perspective";
    
    private static AppConstants i18n = AppConstants.INSTANCE;

    public interface View extends UberElemental<NotFoundScreen> {

        void setNotFoundPerspective(String perspectiveName);
    }

    @Inject
    View view;
    
    @OnOpen
    public void onOpen() {
        List<String> targetParams = Window.Location.getParameterMap().get(TARGET_PARAM);
        if (targetParams != null && !targetParams.isEmpty()) {
            String perspectiveName = targetParams.get(0);
            view.setNotFoundPerspective(perspectiveName);
        }
    }

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return i18n.notFoundScreenTitle();
    }

    @WorkbenchPartView
    public View workbenchPart() {
        return this.view;
    }

}