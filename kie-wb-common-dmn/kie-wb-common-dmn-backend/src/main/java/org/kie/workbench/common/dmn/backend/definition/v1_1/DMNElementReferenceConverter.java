/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElementReference;

public class DMNElementReferenceConverter {

    public static DMNElementReference wbFromDMN(final org.kie.dmn.model.api.DMNElementReference dmn) {
        DMNElementReference result = new DMNElementReference();
        result.setHref(dmn.getHref());
        return result;
    }

    public static org.kie.dmn.model.api.DMNElementReference dmnFromWB(final DMNElementReference wb) {
        org.kie.dmn.model.api.DMNElementReference result = new org.kie.dmn.model.v1_2.TDMNElementReference();
        result.setHref(wb.getHref());

        return result;
    }
}
