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

import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class RelationPropertyConverter {

    public static Relation wbFromDMN(final org.kie.dmn.model.api.Relation dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        List<org.kie.dmn.model.api.InformationItem> column = dmn.getColumn();
        List<org.kie.dmn.model.api.List> row = dmn.getRow();

        List<InformationItem> convertedColumn = column.stream().map(InformationItemPropertyConverter::wbFromDMN).collect(Collectors.toList());
        List<org.kie.workbench.common.dmn.api.definition.v1_1.List> convertedRow = row.stream().map(ListPropertyConverter::wbFromDMN).collect(Collectors.toList());

        Relation result = new Relation(id, description, typeRef, convertedColumn, convertedRow);
        for (InformationItem c : convertedColumn) {
            if (c != null) {
                c.setParent(result);
            }
        }
        for (org.kie.workbench.common.dmn.api.definition.v1_1.List r : convertedRow) {
            if (r != null) {
                r.setParent(result);
            }
        }
        return result;
    }

    public static org.kie.dmn.model.api.Relation dmnFromWB(final Relation wb) {
        org.kie.dmn.model.api.Relation result = new org.kie.dmn.model.v1_2.TRelation();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        for (InformationItem iitem : wb.getColumn()) {
            org.kie.dmn.model.api.InformationItem iitemConverted = InformationItemPropertyConverter.dmnFromWB(iitem);
            if (iitemConverted != null) {
                iitemConverted.setParent(result);
            }
            result.getColumn().add(iitemConverted);
        }

        for (org.kie.workbench.common.dmn.api.definition.v1_1.List list : wb.getRow()) {
            org.kie.dmn.model.api.List listConverted = ListPropertyConverter.dmnFromWB(list);
            if (listConverted != null) {
                listConverted.setParent(result);
            }
            result.getRow().add(listConverted);
        }

        return result;
    }
}
