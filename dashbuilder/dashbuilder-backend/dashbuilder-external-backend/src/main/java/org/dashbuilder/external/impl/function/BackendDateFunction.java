/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.external.impl.function;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.dashbuilder.external.impl.BackendComponentFunction;

/**
 * Component backend function that returns the date.
 *
 */
@Dependent
public class BackendDateFunction implements BackendComponentFunction<String> {

    private static final String FORMAT_PARAM = "format";

    @Override
    public String exec(Map<String, Object> params) {
        Object pattern = params.get(FORMAT_PARAM);
        if (pattern != null) {
            return new SimpleDateFormat(pattern.toString()).format(new Date());
        }
        return new Date().toString();
    }

}