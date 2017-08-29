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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import com.google.gwt.user.client.ui.Composite;
import org.kie.workbench.common.screens.datamodeller.client.util.ErrorPopupHelper;
import org.uberfire.mvp.Command;

public abstract class MainEditorAbstractView<T>
        extends Composite
        implements MainEditorView<T> {

    protected T presenter;

    @Override
    public void init(T presenter) {
        this.presenter = presenter;
    }

    public void showErrorPopup(String message) {
        ErrorPopupHelper.showErrorPopup(message);
    }

    public void showErrorPopup(String message,
                               final Command afterShowCommand,
                               final Command afterCloseCommand) {
        ErrorPopupHelper.showErrorPopup(message,
                                        afterShowCommand,
                                        afterCloseCommand);
    }
}
