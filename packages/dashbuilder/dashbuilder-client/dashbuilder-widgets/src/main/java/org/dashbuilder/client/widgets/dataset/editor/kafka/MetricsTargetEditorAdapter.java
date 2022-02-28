/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.editor.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorError;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;

/**
 * Adapts values from a DropDownEditor to work with MetricsTarget enum.
 *
 */
@Dependent
public class MetricsTargetEditorAdapter implements LeafAttributeEditor<MetricsTarget> {

    private DropDownEditor editor;

    @Inject
    public MetricsTargetEditorAdapter(DropDownEditor editor) {
        this.editor = editor;
        updateEntries();
    }

    public void showErrors(List<EditorError> errors) {
        editor.showErrors(errors);
    }

    @Override
    public void setValue(MetricsTarget value) {
        editor.setValue(value.name());
    }

    @Override
    public MetricsTarget getValue() {
        return MetricsTarget.valueOf(editor.getValue());
    }

    public DropDownEditor getDropDownEditor() {
        return this.editor;
    }

    private void updateEntries() {
        var entries = Arrays.stream(MetricsTarget.values())
                .map(e -> editor.newEntry(e.name(), e.name()))
                .collect(Collectors.toList());
        editor.setEntries(entries);
        editor.setValue(MetricsTarget.BROKER.name());
    }

}
