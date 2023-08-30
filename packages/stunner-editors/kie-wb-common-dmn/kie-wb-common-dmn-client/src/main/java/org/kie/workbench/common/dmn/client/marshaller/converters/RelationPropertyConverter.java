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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

public class RelationPropertyConverter {

    public static Relation wbFromDMN(final JSITRelation dmn,
                                     final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        final List<InformationItem> convertedColumns = new ArrayList<>();
        final List<JSITInformationItem> jsiColumns = dmn.getColumn();
        for (int i = 0; i < jsiColumns.size(); i++) {
            final JSITInformationItem jsiColumn = Js.uncheckedCast(jsiColumns.get(i));
            convertedColumns.add(InformationItemPropertyConverter.wbFromDMN(jsiColumn));
        }

        final List<org.kie.workbench.common.dmn.api.definition.model.List> convertedRows = new ArrayList<>();
        final List<JSITList> jsiRows = dmn.getRow();
        for (int i = 0; i < jsiRows.size(); i++) {
            final JSITList jsiRow = Js.uncheckedCast(jsiRows.get(i));
            convertedRows.add(ListPropertyConverter.wbFromDMN(jsiRow, hasComponentWidthsConsumer));
        }

        final Relation result = new Relation(id, description, typeRef, convertedColumns, convertedRows);
        for (InformationItem column : convertedColumns) {
            if (Objects.nonNull(column)) {
                column.setParent(result);
            }
        }
        for (org.kie.workbench.common.dmn.api.definition.model.List row : convertedRows) {
            if (Objects.nonNull(row)) {
                row.setParent(result);
            }
        }
        return result;
    }

    public static JSITRelation dmnFromWB(final Relation wb,
                                         final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final JSITRelation result = JSITRelation.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (InformationItem iitem : wb.getColumn()) {
            final JSITInformationItem iitemConverted = InformationItemPropertyConverter.dmnFromWB(iitem);
            result.addColumn(iitemConverted);
        }

        for (org.kie.workbench.common.dmn.api.definition.model.List list : wb.getRow()) {
            final JSITList listConverted = ListPropertyConverter.dmnFromWB(list, componentWidthsConsumer);
            result.addRow(listConverted);
        }

        return result;
    }
}
