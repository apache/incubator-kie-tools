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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.multiple;

import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.HasErrorMessage;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorView;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public interface MultipleValuePairEditorView
        extends ValuePairEditorView<MultipleValuePairEditorView.Presenter>,
                HasErrorMessage {

    interface Presenter {

        void init( AnnotationValuePairDefinition valuePairDefinition );

        void onValueChange( Integer itemId );

        void onRemoveItem( Integer itemId );

        void onAddItem();

        ValuePairEditor<?> createValuePairEditor( AnnotationValuePairDefinition valuePairDefinition );

    }

    void init( AnnotationValuePairDefinition valuePairDefinition );

    void removeItemEditor( Integer itemId );

    ValuePairEditor<?> getItemEditor( Integer itemId );

    List<ValuePairEditor<?>> getItemEditors();

    ValuePairEditor<?> getAddItemEditor();

    Integer addItemEditor( ValuePairEditor<?> valuePairEditor );

    void showAlert( String message );

    void clear();

}
