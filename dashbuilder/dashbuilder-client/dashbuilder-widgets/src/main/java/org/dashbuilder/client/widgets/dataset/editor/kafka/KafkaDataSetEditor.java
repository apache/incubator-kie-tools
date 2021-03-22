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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefColumnsFilterEditor;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefPreviewTable;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBackendCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefClientCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.KafkaDataSetDefEditor;
import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;

/**
 * <p>Kafka Data Set editor presenter.</p>
 * 
 */
@Dependent
public class KafkaDataSetEditor extends DataSetEditor<KafkaDataSetDef> implements KafkaDataSetDefEditor {

    KafkaDataSetDefAttributesEditor attributesEditor;

    @Inject
    public KafkaDataSetEditor(final DataSetDefBasicAttributesEditor basicAttributesEditor,
                              final KafkaDataSetDefAttributesEditor attributesEditor,
                              final DataSetDefColumnsFilterEditor columnsAndFilterEditor,
                              final DataSetDefPreviewTable previewTable,
                              final DataSetDefBackendCacheAttributesEditor backendCacheAttributesEditor,
                              final DataSetDefClientCacheAttributesEditor clientCacheAttributesEditor,
                              final DataSetDefRefreshAttributesEditor refreshEditor,
                              final DataSetClientServices clientServices,
                              final LoadingBox loadingBox,
                              final Event<ErrorEvent> errorEvent,
                              final Event<TabChangedEvent> tabChangedEvent,
                              final View view) {
        super(basicAttributesEditor, attributesEditor.view, columnsAndFilterEditor,
              previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor,
              refreshEditor, clientServices, loadingBox, errorEvent, tabChangedEvent, view);
        this.attributesEditor = attributesEditor;
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public void setValue(KafkaDataSetDef value) {
        super.setValue(value);
        attributesEditor.setValue(value);
    }

    @Override
    public LeafAttributeEditor<String> host() {
        return attributesEditor.host();
    }

    @Override
    public LeafAttributeEditor<String> port() {
        return attributesEditor.port();
    }

    @Override
    public LeafAttributeEditor<MetricsTarget> target() {
        return attributesEditor.target();
    }
    
    @Override
    public LeafAttributeEditor<String> filter() {
        return attributesEditor.filter();
    }

    @Override
    public LeafAttributeEditor<String> clientId() {
        return attributesEditor.clientId();
    }

    @Override
    public LeafAttributeEditor<String> nodeId() {
        return attributesEditor.nodeId();
    }

    @Override
    public LeafAttributeEditor<String> topic() {
        return attributesEditor.topic();
    }

    @Override
    public LeafAttributeEditor<String> partition() {
        return attributesEditor.partition();
    }

}
