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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base class for the implementation of custom comparators.
 */
public abstract class AbstractComparatorByCriteria implements ComparatorByCriteria {

    /**
     * Sort criteria list.
     * Each entry contains a criteria properties array of int[2] (criteria id. and ordering mode).
     * The position in the list determines the criteria priority in descending order: the first criteria
     * into the list is the most prioritary.
     */
    protected ArrayList sortCriterias;

    public AbstractComparatorByCriteria() {
        sortCriterias = new ArrayList();
    }

    // ComparatorByCriteria interface

    public void addSortCriteria(String criteriaId, int order) {
        Object[] criteriaProps = getSortCriteria(criteriaId);
        if (criteriaProps == null) criteriaProps = new Object[]{criteriaId, new Integer(order)};

        // Always add (or move if exists) specified criteria to the end of the list.
        sortCriterias.remove(criteriaProps);
        sortCriterias.add(criteriaProps);
    }

    public void removeSortCriteria(String criteriaId) {
        Object[] criteriaProps = getSortCriteria(criteriaId);
        if (criteriaProps != null) sortCriterias.remove(criteriaProps);
    }

    public void removeAllSortCriteria() {
        sortCriterias.clear();
    }

    public int getSortCriteriaPriority(String criteriaId) {
        Object[] criteriaProps = getSortCriteria(criteriaId);
        if (criteriaProps != null) return sortCriterias.indexOf(criteriaProps) + 1;
        return 0;
    }

    public int getSortCriteriaOrdering(String criteriaId) {
        return ((Integer) getSortCriteria(criteriaId)[1]).intValue();
    }

    public String[] getCriteriaIds() {
        String[] ids = new String[sortCriterias.size()];
        for (int i = 0; i < ids.length; i++) {
            Object[] criteriaProps = (Object[]) sortCriterias.get(i);
            ids[i] = (String) criteriaProps[0];
        }
        return ids;
    }

    protected Object[] getSortCriteria(String criteriaId) {
        Iterator it = sortCriterias.iterator();
        while (it.hasNext()) {
            Object[] criteriaProps = (Object[]) it.next();
            if (criteriaProps[0].equals(criteriaId)) return criteriaProps;
        }
        return null;
    }

    public boolean existCriteria(String criteriaId) {
        for (int i = 0; i < getCriteriaIds().length; i++) {
            String criteria = getCriteriaIds()[i];
            if (criteria.equals(criteriaId)) return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;

            AbstractComparatorByCriteria other = (AbstractComparatorByCriteria) obj;
            if (sortCriterias.size() != other.sortCriterias.size()) return false;

            for (int i = 0; i < sortCriterias.size(); i++) {
                Object[] criteriaProps = (Object[]) sortCriterias.get(i);
                Object[] otherCriteriaProps = (Object[]) other.sortCriterias.get(i);
                String thisCriteriaId = (String) criteriaProps[0];
                Integer thisOrder = (Integer) criteriaProps[1];
                String otherCriteriaId = (String) otherCriteriaProps[0];
                Integer otherOrder = (Integer) otherCriteriaProps[1];
                if (!otherCriteriaId.equals(thisCriteriaId) || thisOrder.intValue() != otherOrder) return false;
            }
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    // java.util.Comparable interface

    /**
     * To be implemented by subclasses.
     */
    public abstract int compare(Object o1, Object o2);
}
