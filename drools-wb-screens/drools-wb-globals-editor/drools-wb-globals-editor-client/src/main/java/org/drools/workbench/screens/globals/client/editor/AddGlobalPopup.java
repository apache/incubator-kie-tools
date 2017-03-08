/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.globals.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

@Dependent
public class AddGlobalPopup implements AddGlobalPopupView.Presenter {

    private AddGlobalPopupView view;

    private Command addCommand;

    private Command cancelCommand;

    @Inject
    public AddGlobalPopup( final AddGlobalPopupView view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public String getAlias() {
        return view.getInsertedAlias();
    }

    public String getClassName() {
        return view.getSelectedClassName();
    }

    public void show( final Command addCommand,
                      final Command cancelCommand,
                      final List<String> fullyQualifiedClassNames ) {
        this.addCommand = PortablePreconditions.checkNotNull( "addCommand",
                                                              addCommand );
        this.cancelCommand = PortablePreconditions.checkNotNull( "cancelCommand",
                                                                 cancelCommand );
        PortablePreconditions.checkNotNull( "fullyQualifiedClassNames",
                                            fullyQualifiedClassNames );

        view.clear();
        view.setClassNames( fullyQualifiedClassNames );
        view.show();
    }

    @Override
    public void onAliasInputChanged() {
        checkAliasValidationErrors();
    }

    @Override
    public void onClassNameSelectChanged() {
        checkClassNameValidationErrors();
    }

    private boolean checkAliasValidationErrors() {
        if ( aliasValidationErrorsPresent() ) {
            view.showAliasValidationError();
            return true;
        } else {
            view.hideAliasValidationError();
        }

        return false;
    }

    private boolean checkClassNameValidationErrors() {
        if ( classNameValidationErrorsPresent() ) {
            view.showClassNameValidationError();
            return true;
        } else {
            view.hideClassNameValidationError();
        }

        return false;
    }

    private boolean aliasValidationErrorsPresent() {
        String aliasValue = getAlias();

        return aliasValue == null || aliasValue.isEmpty();
    }

    private boolean classNameValidationErrorsPresent() {
        String classNameValue = getClassName();

        return classNameValue == null || classNameValue.isEmpty();
    }

    @Override
    public void onAddButtonClicked() {
        if ( !checkAliasValidationErrors() && !checkClassNameValidationErrors() ) {
            addCommand.execute();
            view.hide();
        }
    }

    @Override
    public void onCancelButtonClicked() {
        cancelCommand.execute();
        view.hide();
    }
}
