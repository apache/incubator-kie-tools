/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.util;

import java.util.Comparator;

public final class SortHelper {

    public static final Comparator<String> ALPHABETICAL_ORDER_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( final String str1,
                            final String str2 ) {
            if ( str1 == null && str2 == null ) {
                return 0;
            } else if ( str1 == null && str2 != null ) {
                return -1;
            } else if ( str1 != null && str2 == null ) {
                return 1;
            }

            int res = String.CASE_INSENSITIVE_ORDER.compare( str1, str2 );

            if (res == 0) {
                res = str1.compareTo( str2 );
            }

            return res;
        }
    };

}
