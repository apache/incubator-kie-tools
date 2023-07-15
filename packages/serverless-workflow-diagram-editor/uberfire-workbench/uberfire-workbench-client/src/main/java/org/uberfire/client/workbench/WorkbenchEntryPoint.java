/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.util.JSFunctions;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@EntryPoint
public class WorkbenchEntryPoint {

    @Inject
    private SyncBeanManager iocManager;

    private final Map<String, Activity> idActivityMap = new HashMap<>();

    @AfterInitialization
    private void afterInitialization() {
        WorkbenchResources.INSTANCE.CSS().ensureInjected();
        setupRootContainer();
        JSFunctions.notifyJSReady();
    }

    @PostConstruct
    private void postConstruct() {
        JSFunctions.nativeRegisterGwtEditorProvider();
    }


    private void setupRootContainer() {
        HTMLDivElement root = (HTMLDivElement) DomGlobal.document.createElement("div");
        root.id = "root-container";
        root.className = "root-container";
        DomGlobal.document.body.appendChild(root);

        final SyncBeanDef<EditorActivity> editorBean = getBean(EditorActivity.class, null);
        JSFunctions.nativeRegisterGwtClientBean(editorBean.getName(), editorBean);
        openActivity(editorBean.getName());
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

}
