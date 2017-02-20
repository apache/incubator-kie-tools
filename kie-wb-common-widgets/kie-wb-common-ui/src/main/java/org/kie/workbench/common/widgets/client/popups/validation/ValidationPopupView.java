/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.client.mvp.UberElement;

public interface ValidationPopupView extends UberElement<ValidationPopupView.Presenter> {

    interface Presenter {

        void onYesButtonClicked();

        void onCancelButtonClicked();
    }

    void setYesButtonText( final String text );

    void setCancelButtonText( final String text );

    void showYesButton( final boolean show );

    void showCancelButton( final boolean show );

    void setValidationMessages( final List<ValidationMessage> messages );

    void show();

    void hide();
}
