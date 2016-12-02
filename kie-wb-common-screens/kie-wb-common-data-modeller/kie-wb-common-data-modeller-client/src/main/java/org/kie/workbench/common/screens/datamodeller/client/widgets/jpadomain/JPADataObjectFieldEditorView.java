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

import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.BaseEditorView;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

public interface JPADataObjectFieldEditorView
        extends BaseEditorView<JPADataObjectFieldEditorView.Presenter> {

    interface Presenter {

        void onIdentifierFieldChange( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue );

        void onColumnFieldChange( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue );

        void onGeneratedValueFieldChange( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue );

        void onSequenceGeneratorFieldChange( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue );

        void onRelationTypeFieldChange( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue );
    }

    String IDENTIFIER_FIELD = "IDENTIFIER_FIELD";

    String GENERATED_VALUE_FIELD = "GENERATED_VALUE_FIELD";

    String SEQUENCE_GENERATOR_FIELD = "SEQUENCE_GENERATOR_FIELD";

    String COLUMN_NAME_FIELD = "COLUMN_NAME_FIELD";

    String COLUMN_UNIQUE_FIELD = "COLUMN_UNIQUE_FIELD";

    String COLUMN_NULLABLE_FIELD = "COLUMN_NULLABLE_FIELD";

    String COLUMN_INSERTABLE_FIELD = "COLUMN_INSERTABLE_FIELD";

    String COLUMN_UPDATABLE_FIELD = "COLUMN_UPDATABLE_FIELD";

    String RELATIONSHIP_TYPE_FIELD = "RELATIONSHIP_TYPE_FIELD";

    void loadPropertyEditorCategories( List< PropertyEditorCategory > categories );
}