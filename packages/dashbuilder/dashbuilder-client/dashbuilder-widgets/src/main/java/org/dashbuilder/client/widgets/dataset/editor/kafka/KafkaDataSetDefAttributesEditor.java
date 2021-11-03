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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

/**
 * <p>Kafka Data Set specific attributes editor presenter.</p>
 * 
 */
@Dependent
public class KafkaDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.KafkaDataSetDefAttributesEditor {

    public interface View extends UberView<KafkaDataSetDefAttributesEditor> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View hostView,
                         ValueBoxEditor.View portView,
                         DropDownEditor.View targetView,
                         ValueBoxEditor.View clientIdView,
                         ValueBoxEditor.View filterView,
                         ValueBoxEditor.View nodeIdView,
                         ValueBoxEditor.View topicView,
                         ValueBoxEditor.View partitionView);

        void brokerFields();

        void consumerConstraints();

        void producerConstraints();

    }

    DataSetClientServices dataSetClientServices;
    ValueBoxEditor<String> host;
    ValueBoxEditor<String> port;
    MetricsTargetEditorAdapter target;
    ValueBoxEditor<String> filter;
    ValueBoxEditor<String> clientId;
    ValueBoxEditor<String> nodeId;
    ValueBoxEditor<String> topic;
    ValueBoxEditor<String> partition;

    public View view;
    KafkaDataSetDef value;

    @Inject
    public KafkaDataSetDefAttributesEditor(final DataSetClientServices dataSetClientServices,
                                           final View view,
                                           final ValueBoxEditor<String> host,
                                           final ValueBoxEditor<String> port,
                                           final MetricsTargetEditorAdapter target,
                                           final ValueBoxEditor<String> filter,
                                           final ValueBoxEditor<String> clientId,
                                           final ValueBoxEditor<String> nodeId,
                                           final ValueBoxEditor<String> topic,
                                           final ValueBoxEditor<String> partition) {
        this.dataSetClientServices = dataSetClientServices;
        this.view = view;
        this.host = host;
        this.port = port;
        this.target = target;
        this.filter = filter;
        this.clientId = clientId;
        this.nodeId = nodeId;
        this.topic = topic;
        this.partition = partition;
    }

    @PostConstruct
    public void init() {
        // Initialize the Bean specific attributes editor view.
        view.init(this);
        view.initWidgets(host.view, port.view, target.getDropDownEditor().view, filter.view, clientId.view, nodeId.view, topic.view, partition.view);

        host.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_host(),
                            DataSetEditorConstants.INSTANCE.kafka_host_description(),
                            Placement.BOTTOM);
        port.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_port(),
                            DataSetEditorConstants.INSTANCE.kafka_port_description(),
                            Placement.BOTTOM);
        target.getDropDownEditor().addHelpContent(DataSetEditorConstants.INSTANCE.kafka_target(),
                                                  DataSetEditorConstants.INSTANCE.kafka_target_description(),
                                                  Placement.TOP);
        filter.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_filter(),
                              DataSetEditorConstants.INSTANCE.kafka_filter_description(),
                              Placement.BOTTOM);
        clientId.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_clientId(),
                                DataSetEditorConstants.INSTANCE.kafka_clientId_description(),
                                Placement.BOTTOM);
        nodeId.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_nodeId(),
                              DataSetEditorConstants.INSTANCE.kafka_nodeId_description(),
                              Placement.BOTTOM);
        topic.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_topic(),
                             DataSetEditorConstants.INSTANCE.kafka_topic_description(),
                             Placement.BOTTOM);
        partition.addHelpContent(DataSetEditorConstants.INSTANCE.kafka_partition(),
                                 DataSetEditorConstants.INSTANCE.kafka_partition_description(),
                                 Placement.BOTTOM);

        target.getDropDownEditor().setValue(MetricsTarget.BROKER.name());

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final KafkaDataSetDef value) {
        this.value = value;
        target.setValue(value.getTarget());
        updateViewFields(this.value.getTarget());
    }

    @Override
    public void setDelegate(final EditorDelegate<KafkaDataSetDef> delegate) {

    }

    @Override
    public LeafAttributeEditor<String> host() {
        return host;
    }

    @Override
    public LeafAttributeEditor<String> port() {
        return port;
    }

    @Override
    public LeafAttributeEditor<MetricsTarget> target() {
        return target;
    }

    @Override
    public LeafAttributeEditor<String> filter() {
        return filter;
    }

    @Override
    public LeafAttributeEditor<String> clientId() {
        return clientId;
    }

    @Override
    public LeafAttributeEditor<String> nodeId() {
        return nodeId;
    }

    @Override
    public LeafAttributeEditor<String> topic() {
        return topic;
    }

    @Override
    public LeafAttributeEditor<String> partition() {
        return partition;
    }

    public void onTargetChanged(@Observes ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent.getContext() == target.getDropDownEditor() &&
            valueChangeEvent.getValue() != null) {
            MetricsTarget target = MetricsTarget.valueOf(valueChangeEvent.getValue().toString());
            updateViewFields(target);
        }
    }
    
    private void updateViewFields(MetricsTarget target) {
        switch (target) {
            case BROKER:
                view.brokerFields();
                break;
            case CONSUMER:
                view.consumerConstraints();
                break;
            case PRODUCER:
                view.producerConstraints();
                break;
        }
    }
}