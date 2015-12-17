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

import java.util.List;

import org.uberfire.commons.data.Pair;

public interface MainDataObjectFieldEditorView
        extends MainEditorView<MainDataObjectFieldEditorView.Presenter> {

    interface Presenter {

        void onNameChange();

        void onLabelChange();

        void onDescriptionChange();

        void onTypeChange();

        void onTypeMultipleChange();

    }

    String getName();

    void setName( String name );

    void setNameOnError( boolean onError );

    void selectAllNameText();

    String getLabel();

    void setLabel( String label );

    String getDescription();

    void setDescription( String description );

    String getType();

    void setType( String type );

    boolean getMultipleType();

    void setMultipleType( boolean multipleType );

    void setMultipleTypeEnabled( boolean enabled );

    void setReadonly( boolean readonly );

    void initTypeList( List<Pair<String, String>> options, String selectedValue, boolean includeEmptyItem );

}
