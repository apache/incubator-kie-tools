/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dashbuilder.client.external;

import java.util.Arrays;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;

/**
 * Beta CSV parser which need to be improved according to the rfc4180 specification.
 *
 */
@ApplicationScoped
public class CSVParser implements Function<String, String> {
    public String toJsonArray(String csvContent) {
        StringBuffer ans = new StringBuffer("[");
        var lines = csvContent.split("\n");
        Arrays.stream(lines).skip(1).forEach(line -> {
            StringBuffer s = new StringBuffer("[\"");
            int commacount = 0;
            int i = 0;
            while (i < line.length()) {
                if (line.charAt(i) != ',' && line.charAt(i) != '\"') {
                    s.append(line.charAt(i));
                } else if (line.length() > i + 1 && line.charAt(i) == '\"') {
                    i++;
                    commacount++;
                    while (true) {
                        if (line.charAt(i) == '\"') {
                            commacount++;
                        }
                        if (commacount % 2 == 0
                                && (i + 1 >= line.length() || line.charAt(i + 1) == ',')) {
                            break;
                        }
                        s.append(line.charAt(i));
                        i++;
                    }
                    commacount = 0;
                } else if (line.charAt(i) == ',') {
                    s.append("\"" + "," + "\"");
                }
                i++;
            }
            s.append("\"]");
            String s1 = s.toString();
            int z = 1;
            while (z < s1.length()) {
                if (s1.substring(z - 1, z + 1).equals("\"\"")
                        && (z + 1 < s1.length() && s1.charAt(z + 1) != ',')) {
                    s1 = s1.substring(0, z - 1) + "\\\"" + s1.substring(z + 1);
                }
                z++;
            }
            ans.append(s1 + (","));
        });
        String ans1 = ans.toString();
        ans1 = ans1.substring(0, ans.length() - 1);
        ans1 += "]";
        ans1 = ans1.replace("True", "true");
        ans1 = ans1.replace("False", "false");
        return ans1;
    }

    @Override
    public String apply(String input) {
        return toJsonArray(input);
    }

}
