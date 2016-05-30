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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import java.util.List;

import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.BaseEditorView;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.Command;

public interface JPADataObjectEditorView
        extends BaseEditorView<JPADataObjectEditorView.Presenter> {

    interface Presenter {

        void onEntityFieldChange( String newValue );

        void onTableNameChange( String newValue );
    }

    String ENTITY_FIELD = "ENTITY_FIELD";

    String TABLE_NAME_FIELD = "TABLE_NAME_FIELD";

    void loadPropertyEditorCategories( List<PropertyEditorCategory> categories );

    void setLastOpenAccordionGroupTitle( String accordionGroupTitle );

    void showYesNoCancelPopup( String title,
            String message,
            Command yesCommand,
            String yesButtonText,
            ButtonType yesButtonType,
            Command noCommand,
            String noButtonText,
            ButtonType noButtonType,
            Command cancelCommand,
            String cancelButtonText,
            ButtonType cancelButtonType );
}