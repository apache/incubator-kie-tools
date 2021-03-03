/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.mvp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenterImpl;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static org.uberfire.plugin.PluginUtil.toInteger;

@SharedSingleton
public class PlaceManagerImpl implements PlaceManager {

    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<>();
    private final Map<PlaceRequest, CustomPanelDefinition> dockPanels = new HashMap<>();

    @Inject
    private ActivityManager activityManager;
    @Inject
    private PanelManager panelManager;
    @Inject
    private WorkbenchLayout workbenchLayout;
    @Inject
    private SyncBeanManager iocManager;

    @Override
    public void bootstrapRootPanel() {
        final BiParameterizedCommand<PanelDefinition, PlaceRequest> command = (panelDef, editorPlace) -> {
            panelManager.setRoot(panelDef);

            final Activity editorActivity = resolveActivity(editorPlace, ActivityResourceType.EDITOR);
            if (editorActivity == null) {
                return;
            }

            launchActivity(editorPlace,
                           editorActivity,
                           new PartDefinitionImpl(editorPlace),
                           panelDef);

            workbenchLayout.onResize();
        };

        final PlaceRequest editorPlaceRequest = resolveEditorPlaceRequest();
        final PanelDefinitionImpl rootPanel = new PanelDefinitionImpl(WorkbenchPanelPresenterImpl.class.getName());
        rootPanel.setRoot(true);
        rootPanel.addPart(new PartDefinitionImpl(editorPlaceRequest));
        command.execute(rootPanel, editorPlaceRequest);
    }

    private DefaultPlaceRequest resolveEditorPlaceRequest() {
        final Collection<SyncBeanDef<EditorActivity>> editors = iocManager.lookupBeans(EditorActivity.class);

        if (editors.size() != 1) {
            throw new RuntimeException("There must be exactly one instance of EditorActivity.");
        }

        return new DefaultPlaceRequest(editors.iterator().next().getInstance().getIdentifier());
    }

    @Override
    public void goToDock(PlaceRequest place,
                         HasWidgets addTo) {
        final Activity dockActivity = resolveActivity(place, ActivityResourceType.DOCK);
        if (place == null || dockActivity == null) {
            return;
        }

        final CustomPanelDefinition dockPanel = panelManager.addCustomPanel(addTo);
        dockPanels.put(place,
                       dockPanel);
        launchActivity(place,
                       dockActivity,
                       new PartDefinitionImpl(place),
                       dockPanel);
    }

    private Activity resolveActivity(final PlaceRequest place,
                                     final ActivityResourceType type) {
        final Set<Activity> activities = activityManager.getActivities(place);
        if (activities.size() != 1) {
            throw new RuntimeException("There shouldn't be more than one activity associated with a place request.");
        }

        final Activity resolvedActivity = activities.iterator().next();
        if (!resolvedActivity.isType(type.name())) {
            return null;
        }

        existingWorkbenchActivities.put(place,
                                        resolvedActivity);
        return resolvedActivity;
    }

    private void launchActivity(final PlaceRequest place,
                                final Activity activity,
                                final PartDefinition part,
                                final PanelDefinition panel) {
        panelManager.addWorkbenchPart(place,
                                      part,
                                      panel,
                                      activity.getWidget(),
                                      toInteger(panel.getWidthAsInt()),
                                      toInteger(panel.getHeightAsInt()));
        try {
            activity.onOpen();
        } catch (Exception ex) {
            closePlace(place, null);
        }
    }

    @Override
    public void closePlace(final PlaceRequest place,
                           final Command onAfterClose) {
        if (place == null) {
            return;
        }
        final Activity activity = existingWorkbenchActivities.get(place);
        if (activity == null) {
            return;
        }

        activity.onClose();

        panelManager.removePartForPlace(place);
        existingWorkbenchActivities.remove(place);
        activityManager.destroyActivity(activity);

        // currently, we force all custom panels as Static panels, so they can only ever contain the one part we put in them.
        // we are responsible for cleaning them up when their place closes.
        PanelDefinition customPanelDef = dockPanels.remove(place);
        if (customPanelDef != null) {
            panelManager.removeWorkbenchPanel(customPanelDef);
        }

        if (onAfterClose != null) {
            onAfterClose.execute();
        }
    }
}
