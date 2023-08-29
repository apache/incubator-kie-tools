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


package org.kie.workbench.common.forms.common.rendering.client.widgets.util;

import java.io.IOException;
import java.util.Map;

import com.google.gwt.text.shared.Renderer;

public class DefaultValueListBoxRenderer<T> implements Renderer<T> {

    public static final String NULL_STR = "null";

    private Map<T, String> values;

    public void setValues(Map<T, String> values) {
        this.values = values;
    }

    @Override
    public String render(T value) {

        if(values == null || !values.containsKey(value)) {
            return NULL_STR;
        }

        return values.get(value);
    }

    @Override
    public void render(T value,
                       Appendable appendable) throws IOException {
        appendable.append(render(value));
    }
}
