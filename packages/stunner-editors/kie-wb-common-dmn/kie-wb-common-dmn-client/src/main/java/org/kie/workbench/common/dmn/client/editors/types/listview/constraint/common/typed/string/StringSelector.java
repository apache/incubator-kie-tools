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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.string;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class StringSelector extends BaseSelector {

    @Inject
    public StringSelector(final View view) {
        super(view);
    }

    @Override
    public void setValue(final String value) {
        super.setValue(removeDoubleQuotesWrapper(value));
    }

    @Override
    public String getValue() {
        return convertToString(super.getValue());
    }

    private String convertToString(final String value) {

        final String prefix = "\"";
        final String suffix = "\"";

        if (isEmpty(value)) {
            return "";
        }
        if (value.startsWith(prefix) && value.endsWith(suffix)) {
            return value;
        }
        return prefix + value + suffix;
    }

    private String removeDoubleQuotesWrapper(final String value) {
        if (isEmpty(value)) {
            return "";
        }
        return value.replaceAll("(^\\\")|(\\\"$)", "");
    }
}
