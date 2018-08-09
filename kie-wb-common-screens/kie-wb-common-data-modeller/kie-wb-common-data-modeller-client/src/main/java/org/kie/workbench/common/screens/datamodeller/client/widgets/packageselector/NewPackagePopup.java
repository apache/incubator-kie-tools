/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.mvp.Command;

@Dependent
public class NewPackagePopup
        implements NewPackagePopupView.Presenter {

    private ValidatorService validatorService;

    private NewPackagePopupView view;

    private String packageName;

    private Command afterAddCommand;

    @Inject
    public NewPackagePopup(NewPackagePopupView view,
                           ValidatorService validatorService) {
        this.view = view;
        view.init(this);
        this.validatorService = validatorService;
    }

    public String getPackageName() {
        return packageName;
    }

    private void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private void clear() {
        packageName = null;
        view.setPackageName(null);
        view.clearErrors();
    }

    @Override
    public void onCreatePackage() {
        final String[] packageName = {toLowerCase(DataModelerUtils.trim(view.getPackageName()))};
        validatorService.isValidPackageIdentifier(packageName[0],
                                                  new ValidatorCallback() {
                                                      @Override
                                                      public void onFailure() {
                                                          view.setErrorMessage(Constants.INSTANCE.validation_error_invalid_package_identifier(packageName[0]));
                                                      }

                                                      @Override
                                                      public void onSuccess() {
                                                          setPackageName(packageName[0]);
                                                          view.hide();
                                                          if (afterAddCommand != null) {
                                                              afterAddCommand.execute();
                                                          }
                                                      }
                                                  });
    }

    @Override
    public void onValueTyped() {
        view.setPackageName(toLowerCase(view.getPackageName()));
    }

    public void show(Command afterAddCommand) {
        this.afterAddCommand = afterAddCommand;
        clear();
        view.show();
    }

    private String toLowerCase(String value) {
        return value != null ? value.toLowerCase() : null;
    }
}