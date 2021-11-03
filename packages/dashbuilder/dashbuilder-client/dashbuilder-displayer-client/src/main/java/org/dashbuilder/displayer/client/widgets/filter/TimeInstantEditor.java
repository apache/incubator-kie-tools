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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.date.TimeInstant;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class TimeInstantEditor implements IsWidget {

    public interface View extends UberView<TimeInstantEditor> {

        void clearTimeModeSelector();

        void addTimeModeItem(TimeInstant.TimeMode timeMode);

        void setSelectedTimeModeIndex(int index);

        int getTimeModeSelectedIndex();

        void enableIntervalTypeSelector();

        void disableIntervalTypeSelector();

        void clearIntervalTypeSelector();

        void addIntervalTypeItem(DateIntervalType type);

        void setSelectedIntervalTypeIndex(int index);

        int getSelectedIntervalTypeIndex();
    }

    static List<DateIntervalType> INTERVAL_TYPES = Arrays.asList(
            DateIntervalType.MINUTE,
            DateIntervalType.HOUR,
            DateIntervalType.DAY,
            DateIntervalType.MONTH,
            DateIntervalType.QUARTER,
            DateIntervalType.YEAR,
            DateIntervalType.CENTURY,
            DateIntervalType.MILLENIUM);

    View view;
    TimeInstant timeInstant = null;
    TimeAmountEditor timeAmountEditor = null;
    Command onChangeCommand = new Command() { public void execute() {} };

    @Inject
    public TimeInstantEditor(View view, TimeAmountEditor timeAmountEditor) {
        this.timeAmountEditor = timeAmountEditor;
        this.timeInstant = new TimeInstant();
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public TimeInstant getTimeInstant() {
        return timeInstant;
    }

    public void init(final TimeInstant ti, final Command onChangeCommand) {
        this.timeInstant = ti != null ? ti : new TimeInstant();
        this.onChangeCommand = onChangeCommand;
        this.timeAmountEditor.init(timeInstant.getTimeAmount(), new Command() {
            public void execute() {
                timeInstant.setTimeAmount(timeAmountEditor.getTimeAmount());
                onChangeCommand.execute();
            }
        });
        initTimeModeSelector();
        initIntervalTypeSelector();
    }

    public TimeAmountEditor getTimeAmountEditor() {
        return timeAmountEditor;
    }

    protected void initTimeModeSelector() {
        view.clearTimeModeSelector();
        TimeInstant.TimeMode current = timeInstant.getTimeMode();
        TimeInstant.TimeMode[] modes = TimeInstant.TimeMode.values();
        for (int i=0; i<modes.length ; i++) {
            TimeInstant.TimeMode mode = modes[i];
            view.addTimeModeItem(mode);
            if (current != null && current.equals(mode)) {
                view.setSelectedTimeModeIndex(i);
            }
        }
    }

    protected void initIntervalTypeSelector() {
        view.disableIntervalTypeSelector();
        TimeInstant.TimeMode timeMode = timeInstant.getTimeMode();
        if (timeMode != null && !timeMode.equals(TimeInstant.TimeMode.NOW)) {
            view.enableIntervalTypeSelector();
            view.clearIntervalTypeSelector();
            DateIntervalType current = timeInstant.getIntervalType();
            for (int i = 0; i < INTERVAL_TYPES.size(); i++) {
                DateIntervalType type = INTERVAL_TYPES.get(i);
                view.addIntervalTypeItem(type);
                if (current != null && current.equals(type)) {
                    view.setSelectedIntervalTypeIndex(i);
                }
            }
        }
    }

    void changeTimeMode() {
        int selectedIdx = view.getTimeModeSelectedIndex();

        TimeInstant.TimeMode mode = TimeInstant.TimeMode.getByIndex(selectedIdx);
        timeInstant.setTimeMode(mode);
        TimeAmount timeAmount = timeInstant.getTimeAmount();
        if (timeAmount != null) {
            timeAmount.setQuantity(0);
        }

        onChangeCommand.execute();
        initIntervalTypeSelector();
    }

    void changeIntervalType() {
        int selectedIdx = view.getSelectedIntervalTypeIndex();
        DateIntervalType intervalType = INTERVAL_TYPES.get(selectedIdx);
        timeInstant.setIntervalType(intervalType);

        onChangeCommand.execute();
        initIntervalTypeSelector();
    }
}
