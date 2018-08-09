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

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import org.uberfire.client.mvp.UberView;

public interface NewPackagePopupView
        extends UberView<NewPackagePopupView.Presenter> {

    interface Presenter {

        void onCreatePackage();

        void onValueTyped();
    }

    String getPackageName();

    void setPackageName(String packageName);

    void show();

    void hide();

    void setErrorMessage(String errorMessage);

    void clearErrors();
}