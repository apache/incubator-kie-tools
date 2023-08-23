/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.util.StringUtils;

public class NameUtils {

    private static final String WHITESPACE_STRING = " ";

    private static final String NORMALIZE_TO_SINGLE_WHITE_SPACE = "\\s+";

    private NameUtils() {
        //Private constructor as recommended by Sonarcloud
    }

    public static String normaliseName(final String name) {
        if (Objects.isNull(name)) {
            return "";
        }
        String value = name;
        if (StringUtils.nonEmpty(value)) {
            value = value.trim();
            value = value.replaceAll(NORMALIZE_TO_SINGLE_WHITE_SPACE, WHITESPACE_STRING);
        }

        return value;
    }
}
