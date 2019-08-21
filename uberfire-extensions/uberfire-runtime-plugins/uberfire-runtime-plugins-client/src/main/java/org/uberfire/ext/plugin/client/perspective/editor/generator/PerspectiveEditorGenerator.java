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

package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@EntryPoint
@ApplicationScoped
public class PerspectiveEditorGenerator {

    private SyncBeanManager beanManager;
    private ActivityBeansCache activityBeansCache;
    private LayoutGenerator layoutGenerator;
    private Caller<PerspectiveServices> perspectiveServices;

    @Inject
    public PerspectiveEditorGenerator(SyncBeanManager beanManager,
                                      ActivityBeansCache activityBeansCache,
                                      LayoutGenerator layoutGenerator,
                                      Caller<PerspectiveServices> perspectiveServices) {
        this.beanManager = beanManager;
        this.activityBeansCache = activityBeansCache;
        this.layoutGenerator = layoutGenerator;
        this.perspectiveServices = perspectiveServices;
    }

    @PostConstruct
    public void loadPerspectives() {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }

        perspectiveServices.call((Collection<LayoutTemplate> response) -> {
            response.forEach(this::generatePerspective);
        }).listLayoutTemplates();
    }

    public void generatePerspective(String layoutEditorModel) {
        perspectiveServices.call((LayoutTemplate perspective) -> {
            if (perspective != null) {
                generatePerspective(perspective);
            }
        }).convertToLayoutTemplate(layoutEditorModel);
    }

    public PerspectiveEditorActivity generatePerspective(LayoutTemplate layoutTemplate) {
        if (isANewPerspective(layoutTemplate)) {
            PerspectiveEditorScreenActivity screen = createNewScreen(layoutTemplate);
            return createNewPerspective(layoutTemplate,
                                 screen);
        } else {
            PerspectiveEditorScreenActivity screen = updateScreen(layoutTemplate);
            return updatePerspective(layoutTemplate,
                              screen);
        }
    }

    private PerspectiveEditorActivity updatePerspective(LayoutTemplate layoutTemplate,
                                   PerspectiveEditorScreenActivity screen) {
        final SyncBeanDef<Activity> activity = activityBeansCache.getActivity(layoutTemplate.getName());
        final PerspectiveEditorActivity perspectiveEditorActivity = (PerspectiveEditorActivity) activity.getInstance();
        perspectiveEditorActivity.update(layoutTemplate,
                                         screen);
        return perspectiveEditorActivity;
    }

    public void removePerspective(String perspectiveName) {
        String perspectiveScreenId = PerspectiveEditorScreenActivity.buildScreenId(perspectiveName);
        activityBeansCache.removeActivity(perspectiveName);
        activityBeansCache.removeActivity(perspectiveScreenId);
    }

    private PerspectiveEditorScreenActivity updateScreen(LayoutTemplate layoutTemplate) {
        final String perspectiveScreenId = PerspectiveEditorScreenActivity.buildScreenId(layoutTemplate.getName());
        final SyncBeanDef<Activity> activity = activityBeansCache.getActivity(perspectiveScreenId);
        final PerspectiveEditorScreenActivity screenActivity = (PerspectiveEditorScreenActivity) activity.getInstance();
        screenActivity.setLayoutTemplate(layoutTemplate);
        return screenActivity;
    }

    private PerspectiveEditorActivity createNewPerspective(LayoutTemplate perspective,
                                      PerspectiveEditorScreenActivity screen) {
        final PerspectiveEditorActivity activity = new PerspectiveEditorActivity(perspective,
                                                                                 screen);

        beanManager.registerBean(new SingletonBeanDefinition<>(activity,
                                                             PerspectiveActivity.class,
                                                             new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                                                             perspective.getName(),
                                                             true));

        activityBeansCache.addNewPerspectiveActivity(beanManager.lookupBeans(perspective.getName()).iterator().next());
        return activity;
    }

    private PerspectiveEditorScreenActivity createNewScreen(LayoutTemplate perspective) {
        PerspectiveEditorScreenActivity activity = new PerspectiveEditorScreenActivity(perspective,
                                                                                       layoutGenerator);

        final Set<Annotation> qualifiers = new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS));
        final SingletonBeanDefinition<PerspectiveEditorScreenActivity, PerspectiveEditorScreenActivity> beanDef =
                new SingletonBeanDefinition<>(
                        activity,
                        PerspectiveEditorScreenActivity.class,
                        qualifiers,
                        activity.getIdentifier(),
                        true,
                        WorkbenchScreenActivity.class,
                        Activity.class);

        beanManager.registerBean(beanDef);
        beanManager.registerBeanTypeAlias(beanDef,
                                          Activity.class);
        beanManager.registerBeanTypeAlias(beanDef,
                                          WorkbenchScreenActivity.class);

        activityBeansCache.addNewScreenActivity(beanManager.lookupBeans(activity.getIdentifier()).iterator().next());
        return activity;
    }

    private boolean isANewPerspective(LayoutTemplate layoutTemplate) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity(layoutTemplate.getName());
        return activity == null;
    }

    // Sync up with changes in backend

    private void onPlugInAdded(@Observes final PluginAdded event) {
        PortablePreconditions.checkNotNull("PluginAdded event", event);
        Plugin plugin = event.getPlugin();
        perspectiveServices.call((RemoteCallback<LayoutTemplate>) this::generatePerspective)
                .getLayoutTemplate(plugin);
    }

    private void onPlugInSaved(@Observes final PluginSaved event) {
        PortablePreconditions.checkNotNull("PluginSaved event", event);
        Plugin plugin = event.getPlugin();
        perspectiveServices.call((RemoteCallback<LayoutTemplate>) this::generatePerspective)
                .getLayoutTemplate(plugin);
    }

    private void onPlugInRenamed(@Observes final PluginRenamed event) {
        PortablePreconditions.checkNotNull("PluginRenamed event", event);
        Plugin plugin = event.getPlugin();
        removePerspective(event.getOldPluginName());

        perspectiveServices.call((RemoteCallback<LayoutTemplate>) this::generatePerspective)
                .getLayoutTemplate(plugin);
    }

    private void onPlugInDeleted(@Observes final PluginDeleted event) {
        PortablePreconditions.checkNotNull("PluginDeleted event", event);
        if (PluginType.PERSPECTIVE_LAYOUT.equals(event.getPluginType())) {
            String pluginName = event.getPluginName();
            removePerspective(pluginName);
        }
    }
}
