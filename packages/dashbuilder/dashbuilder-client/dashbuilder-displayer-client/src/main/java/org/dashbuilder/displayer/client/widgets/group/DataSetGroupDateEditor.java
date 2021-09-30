/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.group;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.displayer.client.events.DataSetGroupDateChanged;
import org.uberfire.client.mvp.UberView;

@Dependent
public class DataSetGroupDateEditor implements IsWidget {

    public interface View extends UberView<DataSetGroupDateEditor> {

        void setFixedModeValue(boolean enabled);

        boolean getFixedModeValue();

        void clearIntervalTypeSelector();

        void addIntervalTypeItem(DateIntervalType entry);

        void setSelectedIntervalTypeIndex(int index);

        int getSelectedIntervalTypeIndex();

        void setFirstDayVisibility(boolean visible);

        void clearFirstDaySelector();

        void addFirstDaySelectorItem(DayOfWeek entry);

        void setSelectedFirstDayIndex(int index);

        int getSelectedFirstDayIndex();

        void setFirstMonthVisibility(boolean visible);

        void clearFirstMonthSelector();

        void addFirstMonthSelectorItem(Month entry);

        void setSelectedFirstMonthIndex(int index);

        int getSelectedFirstMonthIndex();

        void setEmptyIntervalsValue(boolean enabled);

        boolean getEmptyIntervalsValue();

        void setMaxIntervalsVisibility(boolean visible);

        void setMaxIntervalsValue(String max);

        String getMaxIntervalsValue();
    }

    View view = null;
    ColumnGroup columnGroup = null;
    Event<DataSetGroupDateChanged> changeEvent = null;

    @Inject
    public DataSetGroupDateEditor(View view, Event<DataSetGroupDateChanged> changeEvent) {
        this.view = view;
        this.changeEvent = changeEvent;
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public ColumnGroup getColumnGroup() {
        return columnGroup;
    }

    public void init(ColumnGroup columnGroup) {
        this.columnGroup = columnGroup;
        if (columnGroup != null) {
            if (isFixedStrategy()) {
                gotoFixedMode();
            } else {
                gotoDynamicMode();
            }
        }
    }

    public boolean isFixedStrategy() {
        return GroupStrategy.FIXED.equals(columnGroup.getStrategy());
    }

    protected void initIntervalTypeSelector() {
        view.clearIntervalTypeSelector();
        DateIntervalType current = DateIntervalType.getByName(columnGroup.getIntervalSize());
        List<DateIntervalType> entries = getListOfIntervalTypes();
        for (int i = 0; i < entries.size(); i++) {
            DateIntervalType entry = entries.get(i);
            view.addIntervalTypeItem(entry);
            if (current != null && current.equals(entry)) {
                view.setSelectedIntervalTypeIndex(i);
            }
        }
    }

    public List<DateIntervalType> getListOfIntervalTypes() {
        if (isFixedStrategy()) {
            return DateIntervalType.FIXED_INTERVALS_SUPPORTED;
        }
        return Arrays.asList(DateIntervalType.values());
    }

    protected void initFirstDayListBox() {
        view.setFirstDayVisibility(true);
        view.clearFirstDaySelector();
        DayOfWeek current = columnGroup.getFirstDayOfWeek();
        DayOfWeek[] entries = DayOfWeek.values();
        for (int i = 0; i < entries.length; i++) {
            DayOfWeek entry = entries[i];
            view.addFirstDaySelectorItem(entry);
            if (current != null && current.equals(entry)) {
                view.setSelectedFirstDayIndex(i);
            }
        }
    }

    protected void initFirstMonthListBox() {
        view.setFirstMonthVisibility(true);
        view.clearFirstMonthSelector();
        Month current = columnGroup.getFirstMonthOfYear();
        Month[] entries = Month.values();
        for (int i = 0; i < entries.length; i++) {
            Month entry = entries[i];
            view.addFirstMonthSelectorItem(entry);
            if (current != null && current.equals(entry)) {
                view.setSelectedFirstMonthIndex(i);
            }
        }
    }

    protected void initMaxIntervalsTextBox() {
        view.setMaxIntervalsVisibility(true);
        view.setMaxIntervalsValue(Integer.toString(columnGroup.getMaxIntervals()));
    }

    protected void initEmptyIntervalsFlag() {
        view.setEmptyIntervalsValue(columnGroup.areEmptyIntervalsAllowed());
    }

    protected void resetCommon() {
        view.setFixedModeValue(isFixedStrategy());
        view.setMaxIntervalsVisibility(false);
        view.setFirstDayVisibility(false);
        view.setFirstMonthVisibility(false);

        initIntervalTypeSelector();
        initEmptyIntervalsFlag();
    }

    public void gotoDynamicMode() {
        resetCommon();
        initMaxIntervalsTextBox();
    }

    public void gotoFixedMode() {
        resetCommon();

        DateIntervalType current = DateIntervalType.getByName(columnGroup.getIntervalSize());
        if (DateIntervalType.DAY_OF_WEEK.equals(current)) {
            initFirstDayListBox();
        }
        else if (DateIntervalType.MONTH.equals(current)) {
            initFirstMonthListBox();
        }
    }

    public void onFixedStrategyChanged() {
        columnGroup.setFirstMonthOfYear(null);
        columnGroup.setFirstDayOfWeek(null);

        if (view.getFixedModeValue()) {

            // Reset current interval type selected if not allowed.
            DateIntervalType intervalType = DateIntervalType.getByIndex(view.getSelectedIntervalTypeIndex());
            if (!DateIntervalType.FIXED_INTERVALS_SUPPORTED.contains(intervalType)) {
                view.setSelectedIntervalTypeIndex(DateIntervalType.MONTH.getIndex());
                columnGroup.setIntervalSize(DateIntervalType.MONTH.name());
            }
            columnGroup.setStrategy(GroupStrategy.FIXED);
            gotoFixedMode();
        } else {
            columnGroup.setStrategy(GroupStrategy.DYNAMIC);
            gotoDynamicMode();
        }
        changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
    }

    void onIntervalTypeSelected() {
        DateIntervalType intervalType = DateIntervalType.getByIndex(view.getSelectedIntervalTypeIndex());
        if (isFixedStrategy()) {
            intervalType = DateIntervalType.FIXED_INTERVALS_SUPPORTED.get(view.getSelectedIntervalTypeIndex());
        }

        columnGroup.setIntervalSize(intervalType.name());
        columnGroup.setFirstMonthOfYear(null);
        columnGroup.setFirstDayOfWeek(null);

        view.setFirstMonthVisibility(false);
        view.setFirstDayVisibility(false);

        if (GroupStrategy.FIXED.equals(columnGroup.getStrategy())) {
            if (DateIntervalType.MONTH.equals(DateIntervalType.getByName(columnGroup.getIntervalSize()))) {
                view.setFirstMonthVisibility(true);
                initFirstMonthListBox();
            }
            else if (DateIntervalType.DAY_OF_WEEK.equals(DateIntervalType.getByName(columnGroup.getIntervalSize()))) {
                view.setFirstDayVisibility(true);
                initFirstDayListBox();
            }
        }
        changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
    }

    void onEmptyIntervalsChanged() {
        columnGroup.setEmptyIntervalsAllowed(view.getEmptyIntervalsValue());
        changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
    }

    void onMaxIntervalsChanged() {
        try {
            columnGroup.setMaxIntervals(Integer.parseInt(view.getMaxIntervalsValue()));
            changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
        } catch (Exception e) {
            // Just ignore
        }
    }

    void onFirstDaySelected() {
        DayOfWeek dayOfWeek = DayOfWeek.getByIndex(view.getSelectedFirstDayIndex()+1);
        columnGroup.setFirstDayOfWeek(dayOfWeek);
        changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
    }

    void onFirstMonthSelected() {
        Month month = Month.getByIndex(view.getSelectedFirstMonthIndex()+1);
        columnGroup.setFirstMonthOfYear(month);
        changeEvent.fire(new DataSetGroupDateChanged(columnGroup));
    }
}
