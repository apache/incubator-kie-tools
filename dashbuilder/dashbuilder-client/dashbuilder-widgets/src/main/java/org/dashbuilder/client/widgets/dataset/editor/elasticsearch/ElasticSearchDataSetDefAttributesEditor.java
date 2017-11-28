/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.editor.elasticsearch;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Elastic Search Data Set specific attributes editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class ElasticSearchDataSetDefAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.ElasticSearchDataSetDefAttributesEditor {

    public interface View extends UberView<ElasticSearchDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(ValueBoxEditor.View serverUrlView, ValueBoxEditor.View clusterNameView,
                         ValueBoxEditor.View indexView, ValueBoxEditor.View typeView);
        
    }

    ValueBoxEditor<String> serverURL;
    ValueBoxEditor<String> clusterName;
    ValueBoxEditor<String> index;
    ValueBoxEditor<String> type;
    public View view;

    @Inject
    public ElasticSearchDataSetDefAttributesEditor(final ValueBoxEditor<String> serverURL,
                                                   final ValueBoxEditor<String> clusterName,
                                                   final ValueBoxEditor<String> index,
                                                   final ValueBoxEditor<String> type,
                                                   final View view) {
        this.serverURL = serverURL;
        this.clusterName = clusterName;
        this.index = index;
        this.type = type;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        // Initialize the Bean specific attributes editor view.
        view.init(this);
        view.initWidgets(serverURL.view, clusterName.view, index.view, type.view);
        serverURL.addHelpContent(DataSetEditorConstants.INSTANCE.el_server_url(),
                DataSetEditorConstants.INSTANCE.el_server_url_description(),
                Placement.BOTTOM);
        clusterName.addHelpContent(DataSetEditorConstants.INSTANCE.el_cluster_name(),
                DataSetEditorConstants.INSTANCE.el_cluster_name_description(),
                Placement.BOTTOM);
        index.addHelpContent(DataSetEditorConstants.INSTANCE.el_index(),
                DataSetEditorConstants.INSTANCE.el_index_description(),
                Placement.BOTTOM);
        type.addHelpContent(DataSetEditorConstants.INSTANCE.el_type(),
                DataSetEditorConstants.INSTANCE.el_type_description(),
                Placement.BOTTOM);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public ValueBoxEditor<String> serverURL() {
        return serverURL;
    }

    @Override
    public ValueBoxEditor<String> clusterName() {
        return clusterName;
    }

    @Override
    public ValueBoxEditor<String> index() {
        return index;
    }

    @Override
    public ValueBoxEditor<String> type() {
        return type;
    }   

}
