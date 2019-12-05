/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popover;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.mvp.Command;

@ApplicationScoped
public class ErrorReportPopoverPresenter implements ErrorReportPopover.Presenter {

    @Inject
    protected ErrorReportPopoverView errorReportPopupView;

    @Override
    public void setup(final String errorTitleText,
                      final String errorContentText,
                      final String keepText,
                      final String applyText,
                      final Command applyCommand,
                      final int mx,
                      final int my,
                      final PopoverView.Position position) {
        errorReportPopupView.setup(errorTitleText, errorContentText, keepText, applyText, applyCommand, mx, my, position);
    }

    @Override
    public void setup(final String errorTitleText,
                      final String errorContentText,
                      final String keepText,
                      final int mx,
                      final int my,
                      final PopoverView.Position position) {
        errorReportPopupView.setup(errorTitleText, errorContentText, keepText, mx, my, position);
    }

    @Override
    public void show() {
        errorReportPopupView.show();
    }

    @Override
    public void hide() {
        errorReportPopupView.hide();
    }

    @Override
    public boolean isShown() {
        return errorReportPopupView.isShown();
    }

    @Override
    public int getActualHeight() {
        return errorReportPopupView.getActualHeight();
    }
}
