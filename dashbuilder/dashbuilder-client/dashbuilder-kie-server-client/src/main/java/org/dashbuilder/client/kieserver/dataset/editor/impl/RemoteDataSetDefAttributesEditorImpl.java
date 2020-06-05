/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.kieserver.dataset.editor.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.kieserver.dataset.editor.RemoteDataSetDefAttributesEditor;
import org.dashbuilder.client.kieserver.resources.i18n.KieServerClientConstants;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.dashbuilder.kieserver.KieServerConnectionInfoProvider;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.mvp.UberView;

/**
 * <p>KIE Server/Remote Data Set specific attributes editor presenter.</p>
 */
@Dependent
public class RemoteDataSetDefAttributesEditorImpl implements IsWidget, RemoteDataSetDefAttributesEditor {

    public interface View extends UberView<RemoteDataSetDefAttributesEditorImpl> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(DropDownEditor.View queryTarget,
                         DropDownEditor.View serverTemplateId,
                         ValueBoxEditor.View dbSourceName,
                         ValueBoxEditor.View dbSQL);

    }

    DropDownEditor queryTarget;
    DropDownEditor serverTemplateId;
    ValueBoxEditor<String> dataSource;

    ValueBoxEditor<String> dbSQL;
    public View view;

    private Caller<KieServerConnectionInfoProvider> kieServerConnectionInfoProvider;

    @Inject
    public RemoteDataSetDefAttributesEditorImpl(final DropDownEditor queryTarget,
                                                final DropDownEditor serverTemplateId,
                                                final ValueBoxEditor<String> dataSource,
                                                final ValueBoxEditor<String> dbSQL,
                                                final View view,
                                                final Caller<KieServerConnectionInfoProvider> specManagementService) {
        this.queryTarget = queryTarget;
        this.serverTemplateId = serverTemplateId;
        this.dataSource = dataSource;
        this.dbSQL = dbSQL;
        this.view = view;

        this.kieServerConnectionInfoProvider = specManagementService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(queryTarget.view, serverTemplateId.view, dataSource.view, dbSQL.view);

        queryTarget.setSelectHint(KieServerClientConstants.INSTANCE.remoteQueryTargetHint());
        List<DropDownEditor.Entry> entries = Stream.of("CUSTOM",
                                                       "PROCESS",
                                                       "TASK",
                                                       "BA_TASK",
                                                       "PO_TASK",
                                                       "JOBS",
                                                       "FILTERED_PROCESS",
                                                       "FILTERED_BA_TASK",
                                                       "FILTERED_PO_TASK")
                                                   .map(s -> queryTarget.newEntry(s, s)).collect(Collectors.toList());
        queryTarget.setEntries(entries);

        queryTarget.addHelpContent(KieServerClientConstants.INSTANCE.remoteQueryTarget(),
                                   KieServerClientConstants.INSTANCE.remoteQueryTargetDescription(),
                                   Placement.RIGHT); //bottom placement would interfere with the dropdown

        serverTemplateId.setSelectHint(KieServerClientConstants.INSTANCE.remoteServerTemplateHint());

        kieServerConnectionInfoProvider.call((List<String> serverTemplates) -> onServerTemplateLoad(serverTemplates)).serverTemplates();

        serverTemplateId.addHelpContent(KieServerClientConstants.INSTANCE.remoteServerTemplate(),
                                        KieServerClientConstants.INSTANCE.remoteServerTemplateDescription(),
                                        Placement.RIGHT);

        dataSource.addHelpContent(KieServerClientConstants.INSTANCE.remoteDataSetEditor(),
                                  KieServerClientConstants.INSTANCE.remoteDatasourceDescription(),
                                  Placement.BOTTOM);

        dbSQL.addHelpContent(KieServerClientConstants.INSTANCE.remoteDataSetEditor(),
                             KieServerClientConstants.INSTANCE.remoteDatasourceDescription(),
                             Placement.BOTTOM);
    }

    private void onServerTemplateLoad(List<String> templates) {
        List<DropDownEditor.Entry> entries = templates.stream().map(st -> serverTemplateId.newEntry(st, st)).collect(Collectors.toList());
        serverTemplateId.setEntries(entries);
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public DropDownEditor queryTarget() {
        return queryTarget;
    }

    @Override
    public DropDownEditor serverTemplateId() {
        return serverTemplateId;
    }

    @Override
    public ValueBoxEditor<String> dataSource() {
        return dataSource;
    }

    @Override
    public ValueBoxEditor<String> dbSQL() {
        return dbSQL;
    }

    @Override
    public void flush() {
        // empty
    }

    @Override
    public void onPropertyChange(final String... paths) {
        // empty
    }

    @Override
    public void setDelegate(final EditorDelegate<RemoteDataSetDef> delegate) {
        // No delegation required.
    }

    @Override
    public void setValue(RemoteDataSetDef value) {
        queryTarget.setValue(value.getQueryTarget());
        serverTemplateId.setValue(value.getServerTemplateId());
    }

}