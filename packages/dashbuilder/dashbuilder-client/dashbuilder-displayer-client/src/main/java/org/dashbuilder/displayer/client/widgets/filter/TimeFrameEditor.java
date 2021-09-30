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
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.date.TimeFrame;
import org.dashbuilder.dataset.date.TimeInstant;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class TimeFrameEditor implements FunctionParameterEditor {

    public interface View extends UberView<TimeFrameEditor> {

        void hideFirstMonthSelector();

        void showFirstMonthSelector();

        void clearFirstMonthSelector();

        void addFirstMonthItem(Month month);

        void setSelectedFirstMonthIndex(int index);

        int getSelectedFirstMonthIndex();
    }

    View view;
    SyncBeanManager beanManager;
    TimeFrame timeFrame = null;
    TimeInstantEditor fromEditor;
    TimeInstantEditor toEditor;
    Command onChangeCommand = new Command() { public void execute() {} };

    @Inject
    public TimeFrameEditor(View view, SyncBeanManager beanManager) {
        this.view = view;
        this.beanManager = beanManager;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public TimeInstantEditor getFromEditor() {
        return fromEditor;
    }

    public TimeInstantEditor getToEditor() {
        return toEditor;
    }

    public void init(TimeFrame tf, Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
        this.timeFrame = tf != null ? tf : TimeFrame.parse("begin[year] till end[year]");

        this.fromEditor = beanManager.lookupBean(TimeInstantEditor.class).newInstance();
        this.fromEditor.init(timeFrame.getFrom(), new Command() {
            public void execute() {
                fromEditor.getTimeInstant().setFirstMonthOfYear(getFirstMonthOfYear());
                timeFrame.setFrom(fromEditor.getTimeInstant());
                changeFirstMonthAvailability();
                fireChanges();
            }
        });
        this.toEditor = beanManager.lookupBean(TimeInstantEditor.class).newInstance();
        this.toEditor.init(timeFrame.getTo(), new Command() {
            public void execute() {
                toEditor.getTimeInstant().setFirstMonthOfYear(getFirstMonthOfYear());
                timeFrame.setTo(toEditor.getTimeInstant());
                changeFirstMonthAvailability();
                fireChanges();
            }
        });

        view.init(this);
        initFirstMonthSelector();
        changeFirstMonthAvailability();
    }

    protected void initFirstMonthSelector() {
        view.clearFirstMonthSelector();
        Month current = getFirstMonthOfYear();
        Month[] entries = Month.values();
        for (int i = 0; i < entries.length; i++) {
            Month entry = entries[i];
            view.addFirstMonthItem(entry);
            if (current != null && current.equals(entry)) {
                view.setSelectedFirstMonthIndex(i);
            }
        }
    }

    protected void changeFirstMonthAvailability() {
        view.hideFirstMonthSelector();

        if (isFirstMonthAvailable()) {
            view.showFirstMonthSelector();
        }
    }

    public boolean isFirstMonthAvailable() {

        TimeInstant instantFrom = timeFrame.getFrom();
        TimeInstant.TimeMode modeFrom = instantFrom.getTimeMode();
        if (modeFrom != null && !modeFrom.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantFrom.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                return true;
            }
        }
        TimeInstant instantTo = timeFrame.getTo();
        TimeInstant.TimeMode modeTo = instantTo.getTimeMode();
        if (modeTo != null && !modeTo.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantTo.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                return true;
            }
        }
        return false;
    }

    public Month getFirstMonthOfYear() {

        TimeInstant instantFrom = timeFrame.getFrom();
        TimeInstant.TimeMode modeFrom = instantFrom.getTimeMode();
        if (modeFrom != null && !modeFrom.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantFrom.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                return instantFrom.getFirstMonthOfYear();
            }
        }
        TimeInstant instantTo = timeFrame.getTo();
        TimeInstant.TimeMode modeTo = instantTo.getTimeMode();
        if (modeTo != null && !modeTo.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantTo.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                return instantTo.getFirstMonthOfYear();
            }
        }
        return null;
    }

    public void setFirstMonthOfYear(Month month) {

        TimeInstant instantFrom = timeFrame.getFrom();
        TimeInstant.TimeMode modeFrom = instantFrom.getTimeMode();
        if (modeFrom != null && !modeFrom.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantFrom.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                instantFrom.setFirstMonthOfYear(month);
            }
        }
        TimeInstant instantTo = timeFrame.getTo();
        TimeInstant.TimeMode modeTo = instantTo.getTimeMode();
        if (modeTo != null && !modeTo.equals(TimeInstant.TimeMode.NOW)) {
            DateIntervalType intervalType = instantTo.getIntervalType();
            if (intervalType != null && intervalType.getIndex() > DateIntervalType.MONTH.getIndex()) {
                instantTo.setFirstMonthOfYear(month);
            }
        }
    }

    @Override
    public void setFocus(boolean focus) {
    }

    public void changeFirstMonth() {
        int selectedIdx = view.getSelectedFirstMonthIndex();
        Month month = Month.getByIndex(selectedIdx + 1);
        setFirstMonthOfYear(month);
        fireChanges();
    }

    protected void fireChanges() {
        onChangeCommand.execute();
    }
}
