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

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

@Dependent
public class PerspectiveDropDown implements IsWidget {

    ActivityBeansCache activityBeansCache;
    LiveSearchDropDown liveSearchDropDown;
    PerspectiveNameProvider perspectiveNameProvider;
    Set<String> perspectiveIdsExcluded;
    LiveSearchService searchService = (pattern, maxResults, callback) -> {

        List<String> result = new ArrayList<>();
        for (SyncBeanDef<Activity> beanDef : activityBeansCache.getPerspectiveActivities()) {
            String perspectiveName = beanDef.getName();
            if (perspectiveIdsExcluded == null || !perspectiveIdsExcluded.contains(perspectiveName)) {
                String name = getItemName(perspectiveName);
                if (name.toLowerCase().contains(pattern.toLowerCase())) {
                    result.add(name);
                }
            }
        }

        if (maxResults > 0 && maxResults < result.size()) {
            result = result.subList(0,
                                    maxResults);
        }
        Collections.sort(result);
        callback.afterSearch(result);
    };

    @Inject
    public PerspectiveDropDown(ActivityBeansCache activityBeansCache,
                               LiveSearchDropDown liveSearchDropDown) {
        this.activityBeansCache = activityBeansCache;
        this.liveSearchDropDown = liveSearchDropDown;
        this.perspectiveNameProvider = null;
    }

    @PostConstruct
    private void init() {
        liveSearchDropDown.setSelectorHint(CommonConstants.INSTANCE.PerspectiveSelectHint());
        liveSearchDropDown.setSearchHint(CommonConstants.INSTANCE.PerspectiveSearchHint());
        liveSearchDropDown.setNotFoundMessage(CommonConstants.INSTANCE.PerspectiveNotFound());
        liveSearchDropDown.setSearchService(searchService);
    }

    @Override
    public Widget asWidget() {
        return liveSearchDropDown.asWidget();
    }

    public void setPerspectiveNameProvider(PerspectiveNameProvider perspectiveNameProvider) {
        this.perspectiveNameProvider = perspectiveNameProvider;
    }

    public void setPerspectiveIdsExcluded(Set<String> perspectiveIdsExcluded) {
        this.perspectiveIdsExcluded = perspectiveIdsExcluded;
    }

    public PerspectiveActivity getDefaultPerspective() {
        for (SyncBeanDef beanDef : activityBeansCache.getPerspectiveActivities()) {
            PerspectiveActivity p = (PerspectiveActivity) beanDef.getInstance();
            if (p.isDefault()) {
                return p;
            }
        }
        return null;
    }

    public PerspectiveActivity getSelectedPerspective() {
        String selected = liveSearchDropDown.getSelectedItem();
        if (selected == null) {
            return null;
        }
        for (SyncBeanDef beanDef : activityBeansCache.getPerspectiveActivities()) {
            PerspectiveActivity p = (PerspectiveActivity) beanDef.getInstance();
            String name = getItemName(p);
            if (selected.equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void setSelectedPerspective(String perspectiveId) {
        String item = getItemName(perspectiveId);
        liveSearchDropDown.setSelectedItem(item);
    }

    public void setSelectedPerspective(PerspectiveActivity selectedPerspective) {
        String item = getItemName(selectedPerspective);
        liveSearchDropDown.setSelectedItem(item);
    }

    public void setMaxItems(int maxItems) {
        liveSearchDropDown.setMaxItems(maxItems);
    }

    public void setWidth(int minWidth) {
        liveSearchDropDown.setWidth(minWidth);
    }

    public void setOnChange(Command onChange) {
        liveSearchDropDown.setOnChange(onChange);
    }

    public void clear() {
        liveSearchDropDown.clear();
    }

    public String getItemName(PerspectiveActivity p) {
        return getItemName(p.getIdentifier());
    }

    public String getItemName(String perspectiveId) {
        if (perspectiveNameProvider != null) {
            return perspectiveNameProvider.getPerspectiveName(perspectiveId);
        } else {
            String fullName = perspectiveId;
            int lastDot = fullName.lastIndexOf(".");
            return lastDot != -1 ? fullName.substring(lastDot + 1) : fullName;
        }
    }
}
