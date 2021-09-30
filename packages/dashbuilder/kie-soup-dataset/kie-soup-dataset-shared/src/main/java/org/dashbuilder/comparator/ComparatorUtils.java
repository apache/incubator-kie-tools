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
package org.dashbuilder.comparator;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Helper class containing methods for the comparison between different types.
 */
public class ComparatorUtils {

    /**
     * Compares two comparable objects.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Comparable o1, Comparable o2, int ordering) {
        // Compare
        int comp = 0;
        if (o1 == null && o2 != null) comp = -1;
        else if (o1 != null && o2 == null) comp = 1;
        else if (o1 == null && o2 == null) comp = 0;
        else {
            comp = o1.compareTo(o2);
            if (comp > 0) comp = 1;
            else if (comp < 0) comp = -1;
        }

        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }

    /**
     * Compares two dates.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Date o1, Date o2, int ordering) {
        // Compare
        int comp = 0;
        if (o1 == null && o2 != null) comp = -1;
        else if (o1 != null && o2 == null) comp = 1;
        else if (o1 == null && o2 == null) comp = 0;
        else {
            long thisTime = o1.getTime();
            long anotherTime = o2.getTime();
            comp = (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
        }
        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }

    /**
     * Compares two booleans.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Boolean o1, Boolean o2, int ordering) {
        // Compare
        int comp = 0;
        if (o1 == null && o2 != null) comp = -1;
        else if (o1 != null && o2 == null) comp = 1;
        else if (o1 == null && o2 == null) comp = 0;
        else {
            if (o1.booleanValue() == o2.booleanValue()) comp = 0;
            else if (o1.booleanValue()) comp = 1;
            else comp = -1;
        }
        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }

    /**
     * Compares two collections.
     * A collection is considered greater than other if it has one element greater than all other collection elements.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Collection o1, Collection o2, int ordering) {
        // Compare
        int comp = 0;
        if (o1 == null && o2 != null) comp = -1;
        else if (o1 != null && o2 == null) comp = 1;
        else if (o1 == null && o2 == null) comp = 0;
        else if (o1.isEmpty() && !o2.isEmpty()) comp = -1;
        else if (!o1.isEmpty() && o2.isEmpty()) comp = 1;
        else {
            // Compare o1 elements vs o2
            int o1comp = 0;
            Iterator it = o1.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                o1comp = compare(value, o2, ordering);
                if (o1comp != 0) {
                    if (o1comp == -1) comp = -1;
                    if (o1comp == 1 && comp != -1) comp = 1; // -1 is prioritary.
                }
            }
            // Compare o2 elements vs o1
            int o2comp = 0;
            it = o2.iterator();
            while (comp == 0 && it.hasNext()) {
                Object value = it.next();
                o2comp = compare(value, o1, ordering);
                if (o2comp != 0) {
                    if (o2comp == -1) comp = 1;
                    if (o2comp == 1 && comp != -1) comp = -1; // -1 is prioritary.
                }
            }
        }
        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }

    /**
     * Compare an element with the collection elements.
     * The element is greater than the collection if it is greater than ALL of its elements.
     * The element is smaller than the collection if it is smaller than ALL of its elements.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Object obj, Collection col, int ordering) {
        // Compare
        int comp = 0;
        if (obj == null && col == null) comp = 0;
        else if (obj == null && col != null && !col.isEmpty()) comp = -1;
        else if (obj != null && (col == null || col.isEmpty())) comp = 1;
        else {
            // Both collections have the same size.
            Iterator it = col.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                comp += compare(obj, value, ordering);
            }
            // Check comparison hits.
            if (comp == col.size()) comp = 1;
            else if (comp == (col.size() * -1)) comp = -1;
            else comp = 0;
        }
        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }

    /**
     * Compares two objects.
     * Only Object satisfying the following interfaces can be compared: Comparable, Boolean and Collection.
     *
     * @param ordering: 1=ascending, -1=descending
     */
    public static int compare(Object o1, Object o2, int ordering) {
        // Compare
        int comp = 0;
        if (o1 == null && o2 != null) comp = -1;
        else if (o1 != null && o2 == null) comp = 1;
        else if (o1 == null && o2 == null) comp = 0;
        else {
            if (o1 instanceof Boolean && o2 instanceof Boolean) {
                return ComparatorUtils.compare((Boolean) o1, (Boolean) o2, ordering);
            }
            if (o1 instanceof Date && o2 instanceof Date) {
                return ComparatorUtils.compare((Date) o1, (Date) o2, ordering);
            } else if (o1 instanceof Collection && o2 instanceof Collection) {
                return ComparatorUtils.compare((Collection) o1, (Collection) o2, ordering);
            } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
                return ComparatorUtils.compare((Comparable) o1, (Comparable) o2, ordering);
            }
        }
        // Resolve ordering
        if (ordering == -1) return comp * ordering;
        return comp;
    }
}
