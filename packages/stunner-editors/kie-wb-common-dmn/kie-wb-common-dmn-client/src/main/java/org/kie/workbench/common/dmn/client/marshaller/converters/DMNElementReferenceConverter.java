/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import org.kie.workbench.common.dmn.api.definition.model.DMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;

public class DMNElementReferenceConverter {

    public static DMNElementReference wbFromDMN(final JSITDMNElementReference dmn) {
        final DMNElementReference result = new DMNElementReference();
        result.setHref(dmn.getHref());
        return result;
    }

    public static JSITDMNElementReference dmnFromWB(final DMNElementReference wb) {
        final JSITDMNElementReference result = JSITDMNElementReference.newInstance();
        result.setHref(wb.getHref());

        return result;
    }
}
