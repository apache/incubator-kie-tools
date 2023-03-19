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
package org.dashbuilder.dataset.engine.group;

import java.util.List;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.engine.DataSetHandler;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.Interval;

/**
 * Interval builder for label columns which generates one interval per label.
 */
public class IntervalBuilderDynamicLabel implements IntervalBuilder {

    public IntervalList build(DataSetHandler ctx, ColumnGroup columnGroup) {
        IntervalListLabel intervalList = new IntervalListLabel(columnGroup);
        String columnId = columnGroup.getSourceId();
        List values = ctx.getDataSet().getColumnById(columnId).getValues();
        List<Integer> rows = ctx.getRows();
        return intervalList.indexValues(values, rows);
    }

    public IntervalList build(DataColumn dataColumn) {
        ColumnGroup columnGroup = dataColumn.getColumnGroup();
        IntervalListLabel intervalList = new IntervalListLabel(columnGroup);
        List values = dataColumn.getValues();
        return intervalList.indexValues(values, null);
    }

    private class IntervalListLabel extends IntervalList {

        private IntervalListLabel(ColumnGroup columnGroup) {
            super(columnGroup);
        }

        public void indexValue(Object value, int row) {
            Interval interval = locateInterval(value);
            if (interval == null) {
                // TODO: create a composite interval when the maxIntervals are reached.
                String name = (value == null ? null : value.toString());
                int index = this.size();
                this.add(interval = new Interval(name, index));
            }
            interval.getRows().add(row);
        }

        public Interval locateInterval(Object value) {
            for (Interval interval : this) {
                if (interval.getName() == value) {
                    return interval;
                }
                if (interval.getName() == null) {
                    continue;
                }
                String name = (value == null ? null : value.toString());
                if (interval.getName().equals(name)) {
                    return interval;
                }
            }
            return null;
        }
    }
}
