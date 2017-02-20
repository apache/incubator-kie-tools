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

package org.kie.workbench.common.screens.datasource.management.client.util;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class PopupsUtil {

    @Inject
    private ValidationPopup validationPopup;

    public PopupsUtil() {
    }

    public void showInformationPopup( final String message ) {
        showOkButtonPopup( CommonConstants.INSTANCE.Information(), message );
    }

    public void showErrorPopup( final String message ) {
        showOkButtonPopup( CommonConstants.INSTANCE.Error(), message );
    }

    public void showYesNoPopup( final String title,
            final String message,
            final Command yesCommand,
            final String yesButtonText,
            final ButtonType yesButtonType,
            final Command noCommand,
            final String noButtonText,
            final ButtonType noButtonType ) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( title,
                message,
                yesCommand,
                yesButtonText,
                yesButtonType,
                noCommand,
                noButtonText,
                noButtonType,
                null,
                null,
                null );
        yesNoCancelPopup.setClosable( false );
        yesNoCancelPopup.show();
    }

    private static void showOkButtonPopup( final String title, final String message ) {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( title,
                message,
                new Command() {
                    @Override public void execute() {

                    }
                },
                CommonConstants.INSTANCE.OK(),
                null,
                null,
                null,
                null );

        yesNoCancelPopup.setClosable( false );
        yesNoCancelPopup.show();
    }

    public void showValidationMessages( final List<ValidationMessage> messages ) {
        validationPopup.showMessages( messages );
    }
}
