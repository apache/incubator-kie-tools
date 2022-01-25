/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.assets.dropdown;

import java.util.Map;

public class KieAssetsDropdownItem {

    private final String text;

    private final String subText;

    private final String value;

    private final Map<String, String> metaData;

    public KieAssetsDropdownItem(final String text,
                                 final String subText,
                                 final String value,
                                 final Map<String, String> metaData) {
        this.text = text;
        this.subText = subText;
        this.value = value;
        this.metaData = metaData;
    }

    public String getText() {
        return text;
    }

    public String getSubText() {
        return subText;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }
}
