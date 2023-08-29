/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.workbench;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import elemental2.dom.DomGlobal;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.util.CSSLocatorsUtils;
import org.uberfire.client.util.JSFunctions;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;

@EntryPoint
public class WorkbenchEntryPoint {

    @Inject
    private UberfireDocksContainer uberfireDocksContainer;
    @Inject
    private SyncBeanManager iocManager;

    private final DockLayoutPanel rootContainer = new DockLayoutPanel(Unit.PX);
    private final Map<String, Activity> idActivityMap = new HashMap<>();

    @AfterInitialization
    private void afterInitialization() {
        WorkbenchResources.INSTANCE.CSS().ensureInjected();

        setupRootContainer();

        DomGlobal.window.addEventListener("resize", evt -> {
            resizeTo(DomGlobal.window.innerWidth, DomGlobal.window.innerHeight);
        });

        Scheduler.get().scheduleDeferred(this::resize);

        JSFunctions.notifyJSReady();
    }

    @PostConstruct
    private void postConstruct() {
        JSFunctions.nativeRegisterGwtEditorProvider();
    }

    public void openDock(final PlaceRequest place,
                         final HasWidgets container) {
        final Activity dockActivity = openActivity(place.getIdentifier());
        if (!dockActivity.isType(ActivityResourceType.DOCK.name())) {
            throw new RuntimeException("The place should be associated with a dock activity. " + place);
        }

        final SimpleLayoutPanel panel = createPanel(dockActivity.getWidget());
        panel.addAttachHandler(attachEvent -> {
            if (attachEvent.isAttached()) {
                return;
            }
            Scheduler.get().scheduleFinally(() -> closeDock(dockActivity, container, panel));
        });

        container.add(panel);
        resize();
    }

    protected void closeDock(final Activity dockActivity,
                             final HasWidgets container,
                             final SimpleLayoutPanel panel) {
        final Activity activity = idActivityMap.remove(dockActivity.getIdentifier());
        if (activity != null) {
            activity.onClose();
            final SyncBeanDef<Activity> bean = getBean(Activity.class, dockActivity.getIdentifier());
            if (bean.getScope() == Dependent.class) {
                iocManager.destroyBean(activity);
            }
        }
        container.remove(panel);
    }

    protected SimpleLayoutPanel createPanel(final IsWidget widget) {
        final SimpleLayoutPanel panel = new SimpleLayoutPanel();
        panel.getElement().addClassName(CSSLocatorsUtils.buildLocator("qe", "static-workbench-panel-view"));

        final ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(widget);
        scrollPanel.getElement().getFirstChildElement().setClassName("uf-scroll-panel");

        panel.setWidget(scrollPanel);
        Layouts.setToFillParent(panel);
        return panel;
    }

    private void setupRootContainer() {
        uberfireDocksContainer.setup(rootContainer,
                                     () -> Scheduler.get().scheduleDeferred(this::resize));

        Layouts.setToFillParent(rootContainer);
        RootLayoutPanel.get().add(rootContainer);

        final SyncBeanDef<EditorActivity> editorBean = getBean(EditorActivity.class, null);
        JSFunctions.nativeRegisterGwtClientBean(editorBean.getName(), editorBean);

        final Activity editorActivity = openActivity(editorBean.getName());
        rootContainer.add(createPanel(editorActivity.getWidget()));
        resize();
    }

    private <T extends Activity> SyncBeanDef<T> getBean(Class<T> type, final String name) {
        final Optional<SyncBeanDef<T>> optionalActivity = iocManager.lookupBeans(type)
                .stream()
                .filter(bean -> bean.isActivated() && (name == null || bean.getName().equals(name)))
                .findFirst();

        if (!optionalActivity.isPresent()) {
            throw new RuntimeException("Activity not found" + (name != null ? ": " + name : ""));
        }

        return optionalActivity.get();
    }

    private Activity openActivity(final String name) {
        final Activity activity = getBean(Activity.class,
                                          name).getInstance();
        idActivityMap.put(activity.getIdentifier(), activity);
        activity.onStartup(new DefaultPlaceRequest(name));
        activity.onOpen();
        return activity;
    }

    private void resize() {
        resizeTo(Window.getClientWidth(),
                 Window.getClientHeight());
    }

    private void resizeTo(int width,
                          int height) {
        rootContainer.setPixelSize(width,
                                   height);
        rootContainer.onResize();
    }
}
