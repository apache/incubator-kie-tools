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
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class TimeAmountEditor implements IsWidget {

    public interface View extends UberView<TimeAmountEditor> {

        void setQuantity(long quantity);

        long getQuantity();

        void clearIntervalTypeSelector();

        void addIntervalTypeItem(DateIntervalType type);

        void setSelectedTypeIndex(int typeIdx);

        int getSelectedTypeIndex();
    }

    public static List<DateIntervalType> INTERVAL_TYPES = Arrays.asList(
            DateIntervalType.SECOND,
            DateIntervalType.MINUTE,
            DateIntervalType.HOUR,
            DateIntervalType.DAY,
            DateIntervalType.WEEK,
            DateIntervalType.MONTH,
            DateIntervalType.QUARTER,
            DateIntervalType.YEAR,
            DateIntervalType.CENTURY);

    View view;
    TimeAmount timeAmount = null;
    Command onChangeCommand = new Command() { public void execute() {} };

    @Inject
    public TimeAmountEditor(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public TimeAmount getTimeAmount() {
        return timeAmount;
    }

    public void init(final TimeAmount ta, Command onChangeCommand) {
        this.onChangeCommand = onChangeCommand;
        this.timeAmount = ta != null ? ta : new TimeAmount();

        view.setQuantity(timeAmount.getQuantity());
        view.clearIntervalTypeSelector();
        for (int i=0; i< INTERVAL_TYPES.size(); i++) {
            DateIntervalType type = INTERVAL_TYPES.get(i);
            view.addIntervalTypeItem(type);
            if (timeAmount != null && timeAmount.getType().equals(type)) {
                view.setSelectedTypeIndex(i);
            }
        }
    }

    public long getQuantity() {
        return timeAmount.getQuantity();
    }

    public void decreaseQuantity() {
        long q = getQuantity()-1;
        changeQuantity(q);
        view.setQuantity(q);
    }

    public void increaseQuantity() {
        long q = getQuantity()+1;
        changeQuantity(q);
        view.setQuantity(q);
    }

    public void changeQuantity(String value) {
        if (StringUtils.isBlank(value)) {
            changeQuantity(0);
        } else {
            changeQuantity(Long.parseLong(value));
        }
    }

    public void changeIntervalType() {
        DateIntervalType type = INTERVAL_TYPES.get(view.getSelectedTypeIndex());
        timeAmount.setType(type);
        onChangeCommand.execute();
    }

    protected void changeQuantity(long q) {
        if (timeAmount == null) {
            timeAmount = new TimeAmount();
            DateIntervalType type = INTERVAL_TYPES.get(0);
            timeAmount.setType(type);
        }
        timeAmount.setQuantity(q);
        onChangeCommand.execute();
    }
}
