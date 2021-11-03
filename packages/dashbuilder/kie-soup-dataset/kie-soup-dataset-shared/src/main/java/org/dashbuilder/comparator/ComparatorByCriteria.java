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

import java.util.Comparator;

/**
 * Interface addressed to enable the comparison of objects using multiple comparison criteria.
 */
public interface ComparatorByCriteria extends Comparator {

    // Sort order
    int ORDER_ASCENDING   = 1;
    int ORDER_DESCENDING  = -1;
    int ORDER_UNSPECIFIED = 0;

    /**
     * Specifies a sort criteria for the comparator.
     * @param criteriaId The criteria to set.
     * @param order The sort order. See <i>ORDER_</i> constants defined.
     */
    void addSortCriteria(String criteriaId, int order);

    /**
     * Removes a sort criteria.
     * @param criteriaId The criteria to remove.
     */
    void removeSortCriteria(String criteriaId);

    /**
     * Removes all sort criterias.
     */
    void removeAllSortCriteria();

    /**
     * Retrieves the ordering specified for a given criteria.
     * @param criteriaId The criteria to set. See <i>CRITERIA_</i> constants defined.
     */
    int getSortCriteriaOrdering(String criteriaId);

    /**
     * Retrieves the current priority for a given criteria.
     * Priority is set when the criteria is specified. The first criteria specified is the most prioritary.
     * @param criteriaId The criteria to set.
     * @return The priority from 1 to &lt;max. number of criterias&gt;.
     * <br>0 if criteria is not specified in this comparator.
     */
    int getSortCriteriaPriority(String criteriaId);

    /**
     * Retrieve ids. for all criterias specified.
     */
    String[] getCriteriaIds();

    /**
     * Check if a given criteria is applied in comparator.
     */
    boolean existCriteria(String criteriaId);
}
