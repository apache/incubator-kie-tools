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

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.ValueBoxEditor.View;
import org.dashbuilder.common.client.editor.list.DropDownEditor;

/**
 * <p>The Prometheus Data Set attributes editor view.</p>
 *
 */
@Dependent
public class KafkaDataSetDefAttributesEditorView extends Composite implements KafkaDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, KafkaDataSetDefAttributesEditorView> {

        Binder BINDER = GWT.create(Binder.class);
    }

    KafkaDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    ValueBoxEditor.View hostView;

    @UiField(provided = true)
    ValueBoxEditor.View portView;

    @UiField(provided = true)
    DropDownEditor.View targetView;

    @UiField(provided = true)
    ValueBoxEditor.View filterView;

    @UiField(provided = true)
    ValueBoxEditor.View clientIdView;

    @UiField(provided = true)
    ValueBoxEditor.View nodeIdView;

    @UiField(provided = true)
    ValueBoxEditor.View topicView;

    @UiField(provided = true)
    ValueBoxEditor.View partitionView;

    @Override
    public void init(final KafkaDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(View hostView,
                            View portView,
                            DropDownEditor.View targetView,
                            View filterView,
                            View clientIdView,
                            View nodeIdView,
                            View topicView,
                            View partitionView) {
        this.hostView = hostView;
        this.portView = portView;
        this.targetView = targetView;
        this.filterView = filterView;
        this.clientIdView = clientIdView;
        this.nodeIdView = nodeIdView;
        this.topicView = topicView;
        this.partitionView = partitionView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void brokerFields() {
        disable(clientIdView, nodeIdView, topicView, partitionView);
    }

    @Override
    public void consumerConstraints() {
        enable(clientIdView, nodeIdView, topicView, partitionView);
    }

    @Override
    public void producerConstraints() {
        disable(partitionView);
        enable(clientIdView, nodeIdView, topicView);
    }

    void enable(IsWidget... isWidgets) {
        elementOp(el -> el.removeAttribute("disabled"), isWidgets);
    }

    void disable(IsWidget... isWidgets) {
        elementOp(el -> el.setAttribute("disabled", "true"), isWidgets);
    }

    void elementOp(Consumer<Element> action, IsWidget... isWidgets) {
        for (IsWidget isWidget : isWidgets) {
            NodeList<Element> inputs = isWidget.asWidget().getElement().getElementsByTagName("input");
            for (int i = 0; i < inputs.getLength(); i++) {
                action.accept(inputs.getItem(i));
            }
        }
    }
}