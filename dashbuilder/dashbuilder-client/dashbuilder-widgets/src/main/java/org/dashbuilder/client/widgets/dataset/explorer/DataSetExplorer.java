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
package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.dashbuilder.client.widgets.common.DataSetEditorPlugin;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.dashbuilder.dataprovider.DataSetProviderType.*;

/**
 * <p>Data Set Explorer widget.</p>
 *
 * @since 0.3.0
 */
@Dependent
public class DataSetExplorer implements IsWidget {

    public interface View extends UberView<DataSetExplorer> {

        View addPanel(final DataSetPanel.View panelView);

        View clear();
    }

    List<DataSetProviderType> SUPPORTED_TYPES = Arrays.asList(BEAN,
                                                              CSV,
                                                              ELASTICSEARCH,
                                                              SQL);

    Instance<DataSetPanel> panelInstances;
    DataSetClientServices clientServices;
    View view;
    List<DataSetPanel> panels = new LinkedList<DataSetPanel>();
    
    ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin;

    @Inject
    public DataSetExplorer(final Instance<DataSetPanel> panelInstances,
                           final DataSetClientServices clientServices,
                           final View view,
                           final ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin) {
        this.panelInstances = panelInstances;
        this.clientServices = clientServices;
        this.view = view;
        this.dataSetEditorPlugin = dataSetEditorPlugin;
    }

    @PostConstruct
    public void init() {
                
        if (!dataSetEditorPlugin.isUnsatisfied()) {
            List<DataSetProviderType> allTypes = new ArrayList<DataSetProviderType>(SUPPORTED_TYPES);
            
            for (DataSetEditorPlugin plugin : dataSetEditorPlugin) {
                allTypes.add(plugin.getProviderType());
            }
            
            SUPPORTED_TYPES = allTypes;
        }
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show() {
        clear();

        clientServices.getPublicDataSetDefs(dataSetDefs -> {
            if (dataSetDefs != null && !dataSetDefs.isEmpty()) {
                dataSetDefs.stream()
                        .filter(DataSetExplorer.this::isSupported)
                        .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                        .forEach(DataSetExplorer.this::addDataSetDef);
            }
        });
    }

    private boolean isSupported(DataSetDef def) {
        return SUPPORTED_TYPES.contains(def.getProvider());
    }

    private void addDataSetDef(final DataSetDef def) {
        // Check panel for the given data set does not exists yet and it is supported as well
        if (getDataSetPanel(def.getUUID()) == null) {
            final DataSetPanel panel = panelInstances.get();
            panels.add(panel);
            panel.show(def,
                       "dataSetsExplorerPanelGroup");
            view.addPanel(panel.view);
        }
    }

    private void updateDataSetDef(final DataSetDef def) {
        DataSetPanel panel = getDataSetPanel(def.getUUID());
        if (panel != null) {
            panel.show(def,
                       "dataSetsExplorerPanelGroup");
            panel.close();
        }
    }

    private DataSetPanel getDataSetPanel(final String uuid) {
        if (uuid != null) {
            for (final DataSetPanel panel : panels) {
                if (panel.getDataSetDef().getUUID().equals(uuid)) {
                    return panel;
                }
            }
        }
        return null;
    }

    private void clear() {
        panels.clear();
        view.clear();
    }

    // Be aware of data set lifecycle events

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        checkNotNull("event",
                     event);

        final DataSetDef def = event.getDataSetDef();
        if (def != null && def.isPublic() && isSupported(def)) {
            // Reload the whole data set panels list.
            show();
        }
    }

    void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        checkNotNull("event",
                     event);

        final DataSetDef def = event.getNewDataSetDef();
        if (def != null && def.isPublic()) {
            updateDataSetDef(def);
        }
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        checkNotNull("event",
                     event);
        final DataSetDef def = event.getDataSetDef();
        if (def != null && def.isPublic()) {
            // Reload the whole data set panels list.
            show();
        }
    }
}
