/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.perspective.generator;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.client.cdi.SingletonBeanDefinition;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorActivity;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorScreenActivity;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@ApplicationScoped
public class RuntimePerspectiveGenerator {

    @Inject
    private SyncBeanManager beanManager;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private LayoutGenerator layoutGenerator;

    public PerspectiveEditorActivity generatePerspective(LayoutTemplate layoutTemplate) {
        if (isANewPerspective(layoutTemplate)) {
            var screen = createNewScreen(layoutTemplate);
            return createNewPerspective(layoutTemplate, screen);
        } else {
            var screen = updateScreen(layoutTemplate);
            return updatePerspective(layoutTemplate, screen);
        }
    }

    public void unregisterPerspective(LayoutTemplate layoutTemplate) {
        deleteScreen(layoutTemplate);
        deletePerspective(layoutTemplate);
    }

    private void deleteScreen(LayoutTemplate perspective) {
        String activityID = PerspectiveEditorScreenActivity.buildScreenId(perspective.getName());
        activityBeansCache.removeActivity(activityID);
    }

    private void deletePerspective(LayoutTemplate perspective) {
        var perspectiveName = perspective.getName();
        activityBeansCache.removeActivity(perspectiveName);
    }

    private PerspectiveEditorScreenActivity createNewScreen(LayoutTemplate perspective) {
        PerspectiveEditorScreenActivity activity = new PerspectiveEditorScreenActivity(perspective,
                layoutGenerator);

        final Set<Annotation> qualifiers = new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS));
        final var beanDef = new SingletonBeanDefinition<>(
                activity,
                PerspectiveEditorScreenActivity.class,
                qualifiers,
                activity.getIdentifier(),
                true,
                WorkbenchScreenActivity.class,
                Activity.class);

        beanManager.registerBean(beanDef);
        beanManager.registerBeanTypeAlias(beanDef, Activity.class);
        beanManager.registerBeanTypeAlias(beanDef,
                WorkbenchScreenActivity.class);
        String activityID = activity.getIdentifier();
        activityBeansCache.removeActivity(activityID);
        activityBeansCache.addNewScreenActivity(beanManager.lookupBeans(activityID).iterator().next());
        return activity;
    }

    private PerspectiveEditorActivity createNewPerspective(LayoutTemplate perspective,
                                                           PerspectiveEditorScreenActivity screen) {
        final var activity = new RuntimePerspectiveEditorActivity(perspective, screen);

        var perspectiveName = perspective.getName();
        beanManager.registerBean(new SingletonBeanDefinition<>(activity,
                PerspectiveActivity.class,
                new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                perspectiveName,
                true));
        activityBeansCache.removeActivity(perspectiveName);
        activityBeansCache.addNewPerspectiveActivity(beanManager.lookupBeans(perspectiveName).iterator().next());
        return activity;
    }

    private PerspectiveEditorScreenActivity updateScreen(LayoutTemplate layoutTemplate) {
        final var perspectiveScreenId = PerspectiveEditorScreenActivity.buildScreenId(layoutTemplate.getName());
        final var activity = activityBeansCache.getActivity(perspectiveScreenId);
        final var screenActivity = (PerspectiveEditorScreenActivity) activity.getInstance();
        screenActivity.setLayoutTemplate(layoutTemplate);
        return screenActivity;
    }

    private PerspectiveEditorActivity updatePerspective(LayoutTemplate layoutTemplate,
                                                        PerspectiveEditorScreenActivity screen) {
        final var activity = activityBeansCache.getActivity(layoutTemplate.getName());
        final var perspectiveEditorActivity = (PerspectiveEditorActivity) activity.getInstance();
        perspectiveEditorActivity.update(layoutTemplate,
                screen);
        return perspectiveEditorActivity;
    }

    private boolean isANewPerspective(LayoutTemplate layoutTemplate) {
        return activityBeansCache.getActivity(layoutTemplate.getName()) == null;
    }

}
