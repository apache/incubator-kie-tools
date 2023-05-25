/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.def;

public enum ExternalServiceType {

    PROMETHEUS("$.data.(\n" +
            "    {\n" +
            "        \"columns\": result[0].(\n" +
            "            [\n" +
            "                {\"id\" : \"timestamp\", \"type\": \"number\"},\n" +
            "                {\"id\" : \"value\", \"type\": \"number\"},\n" +
            "                $keys(metric).({\"id\" : $, \"type\": \"label\"})\n" +
            "            ];\n" +
            "        ),\n" +
            "        \"values\": (\n" +
            "            resultType = \"scalar\" ? [result[0] * 1000, result[1]] :\n" +
            "            resultType = \"matrix\" ? result.( $metric := metric.*; values.[ $[0] * 1000, $[1], $metric ] ) :\n" +
            "            resultType = \"vector\" ?  result.[ value[0] * 1000, value[1],  metric.* ]\n" +
            "        )\n" +
            "    }\n" +
            ")");

    private ExternalServiceType(String expression) {
        this.expression = expression;
    }

    private String expression;

    public String getExpression() {
        return expression;
    }

    public static ExternalServiceType byName(String type) {
        if (type != null) {
            for (var t : ExternalServiceType.values()) {
                if (t.name().equalsIgnoreCase(type)) {
                    return t;
                }
            }
        }
        return null;
    }
}
