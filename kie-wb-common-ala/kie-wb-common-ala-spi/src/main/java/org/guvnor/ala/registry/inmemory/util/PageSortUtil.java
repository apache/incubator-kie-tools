/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.inmemory.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class for providing pages sorting.
 */
public class PageSortUtil {

    public static <T> List<T> pageSort(Collection<T> values,
                                       Comparator<T> comparator,
                                       Integer page,
                                       Integer pageSize,
                                       String sort,
                                       boolean sortOrder) {
        if (page < 0) {
            throw new IllegalStateException("Page must be greater or equals than 0");
        }
        if (pageSize < 1) {
            throw new IllegalStateException("PageSize must be greater than 0");
        }
        // If the page is 0 and the amount of values is less than the pageSize, just return the values
        if (page == 0 && values.size() <= pageSize) {
            List<T> result = new ArrayList<>(values);
            sort(result,
                 comparator,
                 sort,
                 sortOrder);
            return result;
        }
        // if the values are less than  (pageSize * page) means that in the requested page there is no item
        if (values.size() <= pageSize * page) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(pageSize);
        List<T> allValues = new ArrayList<>(values);
        for (int i = page * pageSize; i < (page * pageSize) + pageSize; i++) {
            if (allValues.size() > i) {
                result.add(allValues.get(i));
            }
        }
        sort(result,
             comparator,
             sort,
             sortOrder);
        return result;
    }

    private static <T> void sort(List<T> list,
                                 Comparator<T> comparator,
                                 String sort,
                                 boolean sortOrder) {
        if (sort != null && !sort.equals("")) {
            list.sort(comparator);
            if (!sortOrder) {
                Collections.reverse(list);
            }
        }
    }
}
