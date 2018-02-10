/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.editor;

import java.util.Collection;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.StyleInjector;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.event.NewPluginRegistered;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.event.PluginUnregistered;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.model.RuntimePlugin;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

public abstract class RuntimePluginBaseEditor extends BaseEditor<Plugin, DefaultMetadata> {

    protected Plugin plugin;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PluginNameValidator pluginNameValidator;

    @Inject
    private Event<NewPluginRegistered> newPluginRegisteredEvent;

    @Inject
    private Event<PluginUnregistered> pluginUnregisteredEvent;

    @Inject
    private SavePopUpPresenter savePopUpPresenter;

    public RuntimePluginBaseEditor() {
        // Zero-parameter constructor for CDI proxies
    }

    protected RuntimePluginBaseEditor(final BaseEditorView baseView) {
        super(baseView);
    }

    protected abstract PluginType getPluginType();

    protected abstract ClientResourceType getResourceType();

    @Override
    protected Supplier<Plugin> getContentSupplier() {
        return this::getContent;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        init(path,
             place,
             getResourceType(),
             true,
             false,
             SAVE,
             COPY,
             RENAME,
             DELETE);

        // This is only used to define the "name" used by @WorkbenchPartTitle which is called by Uberfire after @OnStartup
        // but before the async call in "loadContent()" has returned. When the *real* plugin is loaded this is overwritten
        this.plugin = new Plugin(place.getParameter("name",
                                                    ""),
                                 getPluginType(),
                                 path);

        this.place = place;
    }

    protected void onPlugInRenamed(@Observes final PluginRenamed pluginRenamed) {
        if (pluginRenamed.getOldPluginName().equals(plugin.getName()) &&
                pluginRenamed.getPlugin().getType().equals(plugin.getType())) {
            this.plugin = new Plugin(pluginRenamed.getPlugin().getName(),
                                     getPluginType(),
                                     pluginRenamed.getPlugin().getPath());
            changeTitleNotification.fire(new ChangeTitleWidgetEvent(place,
                                                                    getTitleText(),
                                                                    getTitle()));
        }
    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return pluginServices;
    }

    protected Caller<? extends SupportsSaveAndRename<Plugin, DefaultMetadata>> getSaveAndRenameServiceCaller() {
        return getPluginServices();
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return pluginServices;
    }

    @Override
    protected void loadContent() {
        getPluginServices().call(new RemoteCallback<PluginContent>() {

            @Override
            public void callback(final PluginContent response) {
                view().setFramework(response.getFrameworks());
                view().setupContent(response,
                                    new ParameterizedCommand<Media>() {

                                        @Override
                                        public void execute(final Media media) {
                                            getPluginServices().call().deleteMedia(media);
                                        }
                                    });
                view().hideBusyIndicator();
                setOriginalHash(getContent().hashCode());
            }
        }).getPluginContent(getCurrentPath());
    }

    ObservablePath getCurrentPath() {
        return versionRecordManager.getCurrentPath();
    }

    public Plugin getContent() {
        return new PluginSimpleContent(view().getContent(),
                                       view().getTemplate(),
                                       view().getCss(),
                                       view().getCodeMap(),
                                       view().getFrameworks(),
                                       view().getContent().getLanguage());
    }

    protected void save() {
        savePopUpPresenter.show(getCurrentPath(),
                                getSaveCommand());
        concurrentUpdateSessionInfo = null;
    }

    ParameterizedCommand<String> getSaveCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute(final String commitMessage) {
                getPluginServices().call(getSaveSuccessCallback(getContent().hashCode())).save(
                        getContent(),
                        commitMessage);
                view().onSave();
            }
        };
    }

    public boolean mayClose() {
        view().onClose();
        return super.mayClose(getContent().hashCode());
    }

    abstract RuntimePluginBaseView view();

    Caller<PluginServices> getPluginServices() {
        return pluginServices;
    }

    Integer getOriginalHash() {
        return originalHash;
    }

    @Override
    public Validator getRenameValidator() {
        return pluginNameValidator;
    }

    @Override
    public Validator getCopyValidator() {
        return pluginNameValidator;
    }

    public void onPluginSaved(@Observes PluginSaved pluginSaved) {
        registerPlugin(pluginSaved.getPlugin());
    }

    public void onPluginAdded(@Observes PluginAdded pluginAdded) {
        registerPlugin(pluginAdded.getPlugin());
    }

    public void onPluginDeleted(@Observes PluginDeleted pluginDeleted) {
        unregisterPlugin(pluginDeleted.getPluginName(),
                         pluginDeleted.getPluginType());
    }

    public void onPluginRenamed(@Observes PluginRenamed pluginRenamed) {
        unregisterPlugin(pluginRenamed.getOldPluginName(),
                         pluginRenamed.getOldPluginType());
        registerPlugin(pluginRenamed.getPlugin());
    }

    void unregisterPlugin(String name,
                          PluginType type) {
        pluginUnregisteredEvent.fire(new PluginUnregistered(name,
                                                            type));
    }

    void registerPlugin(Plugin plugin) {

        pluginServices.call(new RemoteCallback<Collection<RuntimePlugin>>() {

            @Override
            public void callback(final Collection<RuntimePlugin> runtimePlugins) {
                for (final RuntimePlugin plugin : runtimePlugins) {
                    ScriptInjector.fromString(plugin.getScript()).setWindow(TOP_WINDOW).inject();
                    StyleInjector.inject(plugin.getStyle(),
                                         true);
                }
                newPluginRegisteredEvent.fire(new NewPluginRegistered(plugin.getName(),
                                                                      plugin.getType()));
            }
        }).listPluginRuntimePlugins(plugin.getPath());
    }
}
