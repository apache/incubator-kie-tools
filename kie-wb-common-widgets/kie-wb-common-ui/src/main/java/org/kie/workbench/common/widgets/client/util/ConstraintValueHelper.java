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

package org.kie.workbench.common.widgets.client.util;

public class ConstraintValueHelper {

    /**
     * 'Person.age' : ['M=Male', 'F=Female'] 'Person.expression' :
     * ['a\\=5=expression1', 'a\\=5*2=expression2']
     * <p/>
     * This will split the drop down item into a value and a key, e.g.
     * key=value. Equals signs in the expression can be delimited with "\\"
     */
    public static String[] splitValue(String v) {

        String[] s = new String[2];
        s[0] = v;
        s[1] = v;
        int equalsIndex = v.indexOf("=");
        int escapedEqualsIndex = v.indexOf("\\=");
        while ((equalsIndex - escapedEqualsIndex == 1)) {
            equalsIndex = v.indexOf("=",
                    equalsIndex + 1);
            escapedEqualsIndex = v.indexOf("\\=",
                    escapedEqualsIndex + 1);
        }
        if (equalsIndex != -1 && escapedEqualsIndex == -1) {
            String lhs = v.substring(0,
                    equalsIndex);
            String rhs = v.substring(equalsIndex + 1);
            s[0] = lhs;
            s[1] = rhs;
        }
        s[0] = s[0].replace("\\",
                "");
        s[1] = s[1].replace("\\",
                "");
        return s;
    }

}
