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

package org.uberfire.ext.plugin.client.perspective.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.components.popup.AddTag;
import org.uberfire.ext.plugin.client.perspective.editor.events.PerspectiveEditorFocusEvent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.PerspectiveEditorSettings;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.TargetDivList;
import org.uberfire.ext.plugin.client.security.PluginController;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

@Dependent
@WorkbenchEditor(identifier = PerspectiveEditorPresenter.ID, supportedTypes = {PerspectiveLayoutPluginResourceType.class}, priority = Integer.MAX_VALUE)
public class PerspectiveEditorPresenter extends BaseEditor<LayoutTemplate, DefaultMetadata> {

    public static final String ID = "Perspective Editor";

    @Inject
    private View perspectiveEditorView;

    @Inject
    private LayoutEditorPlugin layoutEditorPlugin;

    @Inject
    private Event<NotificationEvent> ufNotification;

    @Inject
    private PerspectiveLayoutPluginResourceType resourceType;

    @Inject
    private Caller<PerspectiveServices> perspectiveServices;

    @Inject
    private PluginNameValidator pluginNameValidator;

    @Inject
    private PluginController pluginController;

    @Inject
    private PerspectiveEditorSettings perspectiveEditorSettings;

    @Inject
    private SyncBeanManager beanManager;
    @Inject
    private LayoutDragComponentPalette layoutDragComponentPalette;
    @Inject
    Event<PerspectiveEditorFocusEvent> perspectiveEditorFocusEvent;

    private Plugin plugin;

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {

        super.baseView = perspectiveEditorView;

        // This is only used to define the "name" used by @WorkbenchPartTitle which is called by Uberfire after @OnStartup
        // but before the async call in "loadContent()" has returned. When the *real* plugin is loaded this is overwritten
        final String name = place.getParameter("name",
                                               "");
        plugin = new Plugin(name,
                            PluginType.PERSPECTIVE_LAYOUT,
                            path);

        // Show the available menu options according to the permissions set
        List<MenuItems> menuItems = new ArrayList<>();
        addMenuItem(menuItems,
                    SAVE,
                    pluginController.canUpdate(plugin));
        addMenuItem(menuItems,
                    COPY,
                    pluginController.canCreatePerspectives());
        addMenuItem(menuItems,
                    RENAME,
                    pluginController.canUpdate(plugin));
        addMenuItem(menuItems,
                    DELETE,
                    pluginController.canDelete(plugin));

        // Init the editor
        init(path,
             place,
             resourceType,
             true,
             false,
             menuItems);

        // Init the drag component palette
        initLayoutDragComponentGroups();

        // Init the layout editor
        this.layoutEditorPlugin.init(name,
                org.uberfire.ext.plugin.client.resources.i18n.CommonConstants.INSTANCE.EmptyTitleText(),
                org.uberfire.ext.plugin.client.resources.i18n.CommonConstants.INSTANCE.EmptySubTitleText(),
                LayoutTemplate.Style.PAGE);

        // Enable the preview feature in the perspective editor
        this.layoutEditorPlugin.setPreviewEnabled(true);
        this.layoutEditorPlugin.setElementSelectionEnabled(true);
        this.perspectiveEditorView.setupLayoutEditor(layoutEditorPlugin.asWidget());
    }

    @OnFocus
    public void onFocus() {
        perspectiveEditorFocusEvent.fire(new PerspectiveEditorFocusEvent(layoutEditorPlugin));
    }

    @OnClose
    public void onClose() {
        layoutEditorPlugin.clear();
    }

    protected void addMenuItem(List<MenuItems> menuItems,
                               MenuItems item,
                               boolean add) {
        if (add) {
            menuItems.add(item);
        }
    }

    public void initLayoutDragComponentGroups() {
        layoutDragComponentPalette.clear();
        layoutDragComponentPalette.addDraggableGroups(scanPerspectiveDragGroups());
    }

    private Collection<LayoutComponentPaletteGroupProvider> scanPerspectiveDragGroups() {
        List<PerspectiveEditorComponentGroupProvider> result = beanManager.lookupBeans(PerspectiveEditorComponentGroupProvider.class).stream()
                .map(SyncBeanDef::getInstance)
                .filter(this::shouldRemoveGroup)
                .collect(Collectors.toList());

        // Sort the results
        Collections.sort(result);

        return result.stream().collect(Collectors.toList());
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        return super.makeMenuBar().then(v -> {
            if (perspectiveEditorSettings.isTagsEnabled()) {
                menuBuilder.addNewTopLevelMenu(MenuFactory.newTopLevelMenu(CommonConstants.INSTANCE.Tags())
                                                       .respondsWith(() -> {
                                                           AddTag addTag = new AddTag(PerspectiveEditorPresenter.this);
                                                           addTag.show();
                                                       })
                                                       .endMenu()
                                                       .build().getItems().get(0));
            }

            return promises.resolve();
        });
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentModelHash());
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return org.uberfire.ext.plugin.client.resources.i18n.CommonConstants.INSTANCE.PerspectiveEditor() + " [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorPresenter> getWidget() {
        return perspectiveEditorView;
    }

    @Override
    protected void loadContent() {
        baseView.hideBusyIndicator();
        layoutEditorPlugin.load(versionRecordManager.getCurrentPath(), this::afterLoad);
    }

    @Override
    protected Supplier<LayoutTemplate> getContentSupplier() {
        return layoutEditorPlugin::getLayout;
    }

    protected void afterLoad() {
        setOriginalHash(getCurrentModelHash());
        plugin = new Plugin(layoutEditorPlugin.getLayout().getName(),
                            PluginType.PERSPECTIVE_LAYOUT,
                            versionRecordManager.getCurrentPath());
    }

    @Override
    protected void save() {
        layoutEditorPlugin.save(versionRecordManager.getCurrentPath(),
                                getSaveSuccessCallback(getCurrentModelHash()));
        concurrentUpdateSessionInfo = null;
    }

    public int getCurrentModelHash() {
        return layoutEditorPlugin.getLayout().hashCode();
    }

    @Override
    protected void onRename() {
        Path currentPath = versionRecordManager.getCurrentPath();
        layoutEditorPlugin.load(currentPath, this::afterRename);
    }

    protected void afterRename() {
        this.afterLoad();
        changeTitleNotification.fire(new ChangeTitleWidgetEvent(place,
                                                                getTitleText(),
                                                                getTitle()));
    }

    @Override
    public Validator getRenameValidator() {
        return pluginNameValidator;
    }

    @Override
    public Validator getCopyValidator() {
        return pluginNameValidator;
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return perspectiveServices;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<LayoutTemplate, DefaultMetadata>> getSaveAndRenameServiceCaller() {
        return perspectiveServices;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return perspectiveServices;
    }
    
    protected boolean shouldRemoveGroup(PerspectiveEditorComponentGroupProvider group) {
        if (group != null) {
            LayoutDragComponentGroup componentGroup = group.getComponentGroup();
            if (componentGroup != null) {
                Map<String, LayoutDragComponent> components = componentGroup.getComponents();
                return components != null && !components.isEmpty();
            }
        }
        return true;
    }

    public void saveProperty(String key,
                             String value) {
        layoutEditorPlugin.addLayoutProperty(key,
                                             value);
    }

    public String getLayoutProperty(String key) {
        return layoutEditorPlugin.getLayoutProperty(key);
    }

    public List<String> getAllTargetDivs() {
        return TargetDivList.list(layoutEditorPlugin.getLayout());
    }
    
    public interface View extends BaseEditorView,
                                  UberView<PerspectiveEditorPresenter> {

        void setupLayoutEditor(Widget widget);
    }
}
