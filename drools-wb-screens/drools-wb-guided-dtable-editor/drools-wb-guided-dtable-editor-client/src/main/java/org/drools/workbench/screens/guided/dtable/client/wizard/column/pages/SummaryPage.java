/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

/**
 * A summary page for the guided Decision Table Wizard
 */
@Dependent
public class SummaryPage extends BaseDecisionTableColumnPage {

    private Boolean includeAdvanced = Boolean.FALSE;

    private List<DecisionTableColumnPlugin> plugins = new ArrayList<>();

    private ManagedInstance<DecisionTableColumnPlugin> pluginManagedInstance;

    private View view;

    @Inject
    public SummaryPage(final ManagedInstance<DecisionTableColumnPlugin> pluginManagedInstance,
                       final View view,
                       final TranslationService translationService) {
        super(translationService);

        this.pluginManagedInstance = pluginManagedInstance;
        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.SummaryPage_NewColumn);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(true);
    }

    @Override
    public void prepareView() {
        view.init(this);

        setupPluginList();
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @PostConstruct
    public void loadPlugins() {
        final ArrayList<DecisionTableColumnPlugin> loadedPlugins = new ArrayList<DecisionTableColumnPlugin>() {{
            pluginManagedInstance.forEach(this::add);
        }};

        this.plugins = sortByTitle(loadedPlugins);
    }

    void openPage(final String selectedItemText) {
        if (selectedItemText.isEmpty() || presenter.isReadOnly()) {
            return;
        }

        final DecisionTableColumnPlugin plugin = findPluginByIdentifier(selectedItemText);

        wizard.start(plugin);
    }

    DecisionTableColumnPlugin findPluginByIdentifier(final String selectedItemText) {
        for (DecisionTableColumnPlugin plugin : plugins()) {
            if (plugin.getIdentifier().equals(selectedItemText)) {
                return plugin;
            }
        }

        throw new UnsupportedOperationException("The plugin " + selectedItemText + " does not have an implementation.");
    }

    List<DecisionTableColumnPlugin> pluginsByCategory() {
        return plugins()
                .stream()
                .filter(plugin -> includeAdvanced || plugin.getType() == DecisionTableColumnPlugin.Type.BASIC)
                .collect(Collectors.toList());
    }

    List<DecisionTableColumnPlugin> plugins() {
        return plugins;
    }

    List<DecisionTableColumnPlugin> sortByTitle(final List<DecisionTableColumnPlugin> plugins) {
        final ArrayList<DecisionTableColumnPlugin> sortedPlugins = new ArrayList<>(plugins);

        sortedPlugins.sort((plugin1, plugin2) -> {
            return plugin1.getTitle().compareTo(plugin2.getTitle());
        });

        return sortedPlugins;
    }

    void setIncludeAdvanced(final Boolean includeAdvanced) {
        this.includeAdvanced = includeAdvanced;

        setupPluginList();
    }

    private void setupPluginList() {
        view.loadPluginList(pluginsByCategory());
        view.setSelectedPlugin(currentPluginIdentifier());
    }

    private String currentPluginIdentifier() {
        final DecisionTableColumnPlugin plugin = Optional.ofNullable(plugin()).orElse(DecisionTableColumnPlugin.DEFAULT);

        return plugin.getIdentifier();
    }

    public interface View extends UberElement<SummaryPage> {

        void loadPluginList(final List<DecisionTableColumnPlugin> plugins);

        void setSelectedPlugin(final String identifier);
    }
}
