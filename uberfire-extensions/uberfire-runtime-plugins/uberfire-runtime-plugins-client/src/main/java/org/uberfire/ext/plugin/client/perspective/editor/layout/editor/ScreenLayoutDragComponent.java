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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditScreen;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.event.NewPluginRegistered;
import org.uberfire.ext.plugin.event.PluginUnregistered;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class ScreenLayoutDragComponent implements PerspectiveEditorDragComponent,
                                                  HasModalConfiguration {

    public static final String PLACE_NAME_PARAMETER = "Place Name";
    protected List<String> availableWorkbenchScreensIds = new ArrayList<String>();
    @Inject
    private PlaceManager placeManager;
    private ModalConfigurationContext configContext;

    @PostConstruct
    public void setup() {
        updateScreensList();
    }

    @Override
    public String getDragComponentTitle() {
        return CommonConstants.INSTANCE.ScreenComponent();
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return getShowWidget(ctx);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {

        FlowPanel panel = GWT.create(FlowPanel.class);
        panel.asWidget().getElement().addClassName("uf-perspective-col");
        panel.asWidget().getElement().addClassName("screen dnd component");
        Map<String, String> properties = extractScreenProperties(ctx);

        String placeName = properties.get(PLACE_NAME_PARAMETER);
        if (placeName == null) {
            return null;
        }

        placeManager.goTo(new DefaultPlaceRequest(placeName,
                                                  properties),
                          panel);
        return panel;
    }

    private Map<String, String> extractScreenProperties(RenderingContext ctx) {
        Map<String, String> properties = ctx.getComponent().getProperties();
        Map<String, String> newProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            newProperties.put(key,
                              properties.get(key));
        }
        //TODO probably UF bug on insert and remove a screen in the same perspective
        newProperties.put("random",
                          "" + (new Random().nextLong()));
        return newProperties;
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        this.configContext = ctx;
        return new EditScreen(ctx,
                              availableWorkbenchScreensIds);
    }

    public void observeEditComponentEventFromPropertyEditor(@Observes PropertyEditorChangeEvent event) {

        PropertyEditorFieldInfo property = event.getProperty();
        if (property.getEventId().equalsIgnoreCase(EditScreen.PROPERTY_EDITOR_KEY)) {
            configContext.setComponentProperty(property.getLabel(),
                                               property.getCurrentStringValue());
        }
    }

    public void onNewPluginRegistered(@Observes NewPluginRegistered newPluginRegistered) {
        if (newPluginRegistered.getType().equals(PluginType.SCREEN) &&
                !availableWorkbenchScreensIds.contains(newPluginRegistered.getName())) {
            getActivityBeansInfo().addActivityBean(availableWorkbenchScreensIds,
                                                   newPluginRegistered.getName());
        }
    }

    public void onPluginUnregistered(@Observes PluginUnregistered pluginUnregistered) {
        if (pluginUnregistered.getType().equals(PluginType.SCREEN)) {
            availableWorkbenchScreensIds.remove(pluginUnregistered.getName());
        }
    }

    protected void updateScreensList() {
        final ActivityBeansInfo activityBeansInfo = getActivityBeansInfo();
        availableWorkbenchScreensIds = activityBeansInfo.getAvailableWorkbenchScreensIds();
    }

    ActivityBeansInfo getActivityBeansInfo() {
        final SyncBeanDef<ActivityBeansInfo> activityBeansInfoIOCBeanDef = IOC.getBeanManager()
                .lookupBean(ActivityBeansInfo.class);
        return activityBeansInfoIOCBeanDef.getInstance();
    }

    List<String> getAvailableWorkbenchScreensIds() {
        return availableWorkbenchScreensIds;
    }
}
