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

import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class ContextPropertyConverter {

    public static Context wbFromDMN(final org.kie.dmn.model.api.Context dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        Context result = new Context(id,
                                     description,
                                     typeRef);
        for (org.kie.dmn.model.api.ContextEntry ce : dmn.getContextEntry()) {
            ContextEntry ceConverted = ContextEntryPropertyConverter.wbFromDMN(ce);
            if (ceConverted != null) {
                ceConverted.setParent(result);
            }
            result.getContextEntry().add(ceConverted);
        }
        return result;
    }

    public static org.kie.dmn.model.api.Context dmnFromWB(final Context wb) {
        org.kie.dmn.model.api.Context result = new org.kie.dmn.model.v1_2.TContext();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        for (ContextEntry ce : wb.getContextEntry()) {
            org.kie.dmn.model.api.ContextEntry ceConverted = ContextEntryPropertyConverter.dmnFromWB(ce);
            if (ceConverted != null) {
                ceConverted.setParent(result);
            }
            result.getContextEntry().add(ceConverted);
        }
        return result;
    }
}
