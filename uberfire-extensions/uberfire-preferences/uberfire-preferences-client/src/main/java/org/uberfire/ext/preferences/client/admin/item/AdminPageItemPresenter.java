/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.admin.item;

import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;

public class AdminPageItemPresenter {

    public interface View extends UberElement<AdminPageItemPresenter> {

    }

    private final View view;

    private final PlaceManager placeManager;

    private AdminTool adminTool;

    @Inject
    public AdminPageItemPresenter( final View view,
                                   final PlaceManager placeManager ) {
        this.view = view;
        this.placeManager = placeManager;
    }

    public void setup( final AdminTool adminTool ) {
        this.adminTool = adminTool;
        view.init( this );
    }

    public void enter() {
        adminTool.getOnClickCommand().execute();
    }

    public AdminTool getAdminTool() {
        return adminTool;
    }

    public View getView() {
        return view;
    }
}
