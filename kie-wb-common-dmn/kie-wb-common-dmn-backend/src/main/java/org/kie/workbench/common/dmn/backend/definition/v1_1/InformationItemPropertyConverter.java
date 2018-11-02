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

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class InformationItemPropertyConverter {

    public static InformationItem wbFromDMN(final org.kie.dmn.model.api.InformationItem dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        InformationItem result = new InformationItem(id,
                                                     description,
                                                     name,
                                                     typeRef);
        return result;
    }

    public static org.kie.dmn.model.api.InformationItem dmnFromWB(final InformationItem wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.InformationItem result = new org.kie.dmn.model.v1_2.TInformationItem();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setName(wb.getName().getValue());
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        return result;
    }
}