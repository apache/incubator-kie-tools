/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type;

import org.kie.workbench.common.forms.model.FieldType;

public class SliderFieldType implements FieldType {

    public static final String NAME = "Slider";

    public static final String MIN_PARAM = "min";
    public static final String MAX_PARAM = "max";
    public static final String STEP_PARAM = "step";
    public static final String PRECISION_PARAM = "precision";

    @Override
    public String getTypeName() {
        return NAME;
    }
}
