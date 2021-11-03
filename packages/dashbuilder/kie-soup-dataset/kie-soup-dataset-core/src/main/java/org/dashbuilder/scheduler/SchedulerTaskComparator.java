/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.scheduler;

import java.util.Iterator;

import org.dashbuilder.comparator.AbstractComparatorByCriteria;
import org.dashbuilder.comparator.ComparatorUtils;

/**
 * Class used to compare scheduler tasks.
 */
public class SchedulerTaskComparator extends AbstractComparatorByCriteria {

    // Sort criteria
    public static final String TIME_TO_FIRE = "timeToFire";

    public SchedulerTaskComparator() {
        super();
    }

    public int compare(Object o1, Object o2) {
        if (o1 instanceof SchedulerTask == false) return 1;
        if (o2 instanceof SchedulerTask == false) return -1;
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        SchedulerTask entry1 = (SchedulerTask) o1;
        SchedulerTask entry2 = (SchedulerTask) o2;

        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps =  (Object[]) it.next();
            String criteriaId = (String) criteriaProps[0];
            int ordering = (Integer) criteriaProps[1];
            if (criteriaId.equals(TIME_TO_FIRE)) {
                int compById = ComparatorUtils.compare(entry1.getMillisTimeToFire(), entry2.getMillisTimeToFire(), ordering);
                if (compById != 0) return compById;
            }
        }
        // Comparison gives equality.
        return 0;
    }
}