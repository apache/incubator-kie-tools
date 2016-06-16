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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.mvp.Command;

@Dependent
public class PriorityDropDown implements IsWidget {

    private enum Priority {

        VERY_HIGH(10),
        HIGH(5),
        NORMAL(0),
        LOW(-5),
        VERY_LOW(-10);

        int ordinal = 0;

        private static Priority[] _typeArray = values();

        public static Priority get(int idx) {
            return _typeArray[idx];
        }

        Priority(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public int getIndex() {
            for (int i = 0; i < _typeArray.length; i++) {
                Priority item = _typeArray[i];
                if (this.equals(item)) {
                    return i;
                }
            }
            return -1;
        }
    }

    LiveSearchDropDown liveSearchDropDown;
    List<String> priorityItemList;

    @Inject
    public PriorityDropDown(LiveSearchDropDown liveSearchDropDown) {
        this.liveSearchDropDown = liveSearchDropDown;
        this.priorityItemList = new ArrayList<>();
    }

    @PostConstruct
    private void init() {
        priorityItemList.add(UsersManagementWidgetsConstants.INSTANCE.priorityVeryHigh());
        priorityItemList.add(UsersManagementWidgetsConstants.INSTANCE.priorityHigh());
        priorityItemList.add(UsersManagementWidgetsConstants.INSTANCE.priorityNormal());
        priorityItemList.add(UsersManagementWidgetsConstants.INSTANCE.priorityLow());
        priorityItemList.add(UsersManagementWidgetsConstants.INSTANCE.priorityVeryLow());

        liveSearchDropDown.setSelectorHint(UsersManagementWidgetsConstants.INSTANCE.selectPriorityHint());
        liveSearchDropDown.setSearchEnabled(false);
        liveSearchDropDown.setSearchService((pattern, maxResults, callback) -> callback.afterSearch(priorityItemList));
    }

    @Override
    public Widget asWidget() {
        return liveSearchDropDown.asWidget();
    }

    public String getPriorityName(int priority) {
        Priority p = resolvePriority(priority);
        int idx = p.getIndex();
        return priorityItemList.get(idx);
    }

    public int getSelectedPriority() {
        String selected = liveSearchDropDown.getSelectedItem();
        if (selected == null) {
            return -1;
        }
        int idx = priorityItemList.indexOf(selected);
        return Priority.get(idx).getOrdinal();
    }

    public void setSelectedPriority(int ordinal) {
        Priority priority = resolvePriority(ordinal);
        String item = priorityItemList.get(priority.getIndex());
        liveSearchDropDown.setSelectedItem(item);
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

    public Priority resolvePriority(int priority) {
        if (priority < -5) {
            return Priority.VERY_LOW;
        }
        if (priority < 0) {
            return Priority.LOW;
        }
        if (priority == 0) {
            return Priority.NORMAL;
        }
        if (priority <= 5) {
            return Priority.HIGH;
        }
        return Priority.VERY_HIGH;
    }
}
