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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.Interval;

/**
 * List of the 60-second intervals present in a minute.
 */
public class IntervalListSecond extends IntervalList {

    protected Map<Integer,Interval> intervalMap;

    public IntervalListSecond(ColumnGroup columnGroup) {
        this(columnGroup, 60);
    }

    public IntervalListSecond(ColumnGroup columnGroup, int size) {
        super(columnGroup);
        intervalMap = new HashMap<Integer, Interval>();

        for (int i = 0; i < size; i++) {
            Interval interval = new Interval(Integer.toString(i), i);
            interval.setType(columnGroup.getIntervalSize());
            this.add(interval);

            intervalMap.put(i, interval);
        }
    }

    public Interval locateInterval(Object value) {
        Date d = (Date) value;
        int sec = d.getSeconds();
        if (sec > 59) {
            return intervalMap.get(59);
        } else {
            return intervalMap.get(sec);
        }
    }
}
