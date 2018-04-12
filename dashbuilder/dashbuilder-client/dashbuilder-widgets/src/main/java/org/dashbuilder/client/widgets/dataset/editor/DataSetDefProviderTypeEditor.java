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
package org.dashbuilder.client.widgets.dataset.editor;

import static org.dashbuilder.dataprovider.DataSetProviderType.BEAN;
import static org.dashbuilder.dataprovider.DataSetProviderType.CSV;
import static org.dashbuilder.dataprovider.DataSetProviderType.ELASTICSEARCH;
import static org.dashbuilder.dataprovider.DataSetProviderType.SQL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.widgets.common.DataSetEditorPlugin;
import org.dashbuilder.client.widgets.dataset.event.DataSetDefCreationRequestEvent;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.list.HorizImageListEditor;
import org.dashbuilder.common.client.editor.list.ImageListEditor;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.resources.bundles.DataSetClientResources;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.UberView;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>Data Set provider type editor presenter.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefProviderTypeEditor implements IsWidget,
                                                     org.dashbuilder.dataset.client.editor.DataSetDefProviderTypeEditor {

    public interface View extends UberView<DataSetDefProviderTypeEditor> {

        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(IsWidget listEditorView);
    }

    HorizImageListEditor<DataSetProviderType> provider;
    Event<DataSetDefCreationRequestEvent> createEvent;
    public View view;
    ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin;

    @Inject
    public DataSetDefProviderTypeEditor(final HorizImageListEditor<DataSetProviderType> provider,
                                        final Event<DataSetDefCreationRequestEvent> createEvent,
                                        final View view,
                                        final ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin) {
        this.provider = provider;
        this.createEvent = createEvent;
        this.view = view;
        this.dataSetEditorPlugin = dataSetEditorPlugin;
    }

    @PostConstruct
    public void init() {
        view.init(this);

        // Initialize the acceptable values for DataSetProviderType.
        final Collection<ImageListEditor<DataSetProviderType>.Entry> entries = getDefaultEntries();
        provider.setEntries(entries);
        view.initWidgets(provider.view);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void onItemClicked(@Observes ValueChangeEvent<DataSetProviderType> event) {
        PortablePreconditions.checkNotNull("ValueChangeEvent<DataSetProviderType>",
                                           event);
        if (event.getContext().equals(provider)) {
            createEvent.fire(new DataSetDefCreationRequestEvent(this,
                                                                event.getValue()));
        }
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public void showErrors(final List<EditorError> errors) {
        // Defaults to no-operation. Errors are delegated to the ImageListEditor component.
    }

    @Override
    public HorizImageListEditor<DataSetProviderType> provider() {
        return provider;
    }

    /**
     * The provider types supported by this editor
     */
    DataSetProviderType[] CORE_TYPES = new DataSetProviderType[]{BEAN, CSV, SQL, ELASTICSEARCH};

    protected Collection<ImageListEditor<DataSetProviderType>.Entry> getDefaultEntries() {
        final DataSetProviderType[] providerTypes = CORE_TYPES;
        final Collection<ImageListEditor<DataSetProviderType>.Entry> entries = new ArrayList<>(providerTypes.length);
        for (final DataSetProviderType type : providerTypes) {
            final String title = getTypeSelectorTitle(type);
            final String text = getTypeSelectorText(type);
            final SafeUri uri = getTypeSelectorImageUri(type);
            final ImageListEditor<DataSetProviderType>.Entry entry = provider.newEntry(type,
                                                                                       uri,
                                                                                       new SafeHtmlBuilder().appendEscaped(title).toSafeHtml(),
                                                                                       new SafeHtmlBuilder().appendEscaped(text).toSafeHtml());
            entries.add(entry);
        }  
        
        if (!dataSetEditorPlugin.isUnsatisfied()) {
            
            for (DataSetEditorPlugin pluginEditor : dataSetEditorPlugin) {
                final String title = pluginEditor.getTypeSelectorTitle();
                final String text = pluginEditor.getTypeSelectorText();
                final SafeUri uri = pluginEditor.getTypeSelectorImageUri();
                final ImageListEditor<DataSetProviderType>.Entry entry = provider.newEntry(pluginEditor.getProviderType(),
                                                                                           uri,
                                                                                           new SafeHtmlBuilder().appendEscaped(title).toSafeHtml(),
                                                                                           new SafeHtmlBuilder().appendEscaped(text).toSafeHtml());
                entries.add(entry);
            }
        }
        
        return entries;
    }

    String getTypeSelectorTitle(final DataSetProviderType type) {
        if (BEAN.equals(type)) {
            return DataSetEditorConstants.INSTANCE.bean();
        }
        if (CSV.equals(type)) {
            return DataSetEditorConstants.INSTANCE.csv();
        }
        if (SQL.equals(type)) {
            return DataSetEditorConstants.INSTANCE.sql();
        }
        if (ELASTICSEARCH.equals(type)) {
            return DataSetEditorConstants.INSTANCE.elasticSearch();
        }
        return null;
    }

    String getTypeSelectorText(final DataSetProviderType type) {
        if (BEAN.equals(type)) {
            return DataSetEditorConstants.INSTANCE.bean_description();
        }
        if (CSV.equals(type)) {
            return DataSetEditorConstants.INSTANCE.csv_description();
        }
        if (SQL.equals(type)) {
            return DataSetEditorConstants.INSTANCE.sql_description();
        }
        if (ELASTICSEARCH.equals(type)) {
            return DataSetEditorConstants.INSTANCE.elasticSearch_description();
        }
        return null;
    }

    SafeUri getTypeSelectorImageUri(final DataSetProviderType type) {
        if (BEAN.equals(type)) {
            return DataSetClientResources.INSTANCE.images().javaIcon160().getSafeUri();
        }
        if (CSV.equals(type)) {
            return DataSetClientResources.INSTANCE.images().csvIcon160().getSafeUri();
        }
        if (SQL.equals(type)) {
            return DataSetClientResources.INSTANCE.images().sqlIcon160().getSafeUri();
        }
        if (ELASTICSEARCH.equals(type)) {
            return DataSetClientResources.INSTANCE.images().elIcon160().getSafeUri();
        }

        return null;
    }
}
