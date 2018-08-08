/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeFactory;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.treegrid.DataTypeTreeGrid;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldConverter;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

@ApplicationScoped
public class DataTypeModal extends Elemental2Modal<DataTypeModal.View> {

    private final DataTypeTreeGrid treeGrid;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final DataTypeFactory dataTypeFactory;

    private final QNameFieldConverter qNameFieldConverter;

    @Inject
    public DataTypeModal(final View view,
                         final DataTypeTreeGrid treeGrid,
                         final ItemDefinitionUtils itemDefinitionUtils,
                         final DataTypeFactory dataTypeFactory,
                         final QNameFieldConverter qNameFieldConverter) {
        super(view);
        this.treeGrid = treeGrid;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.dataTypeFactory = dataTypeFactory;
        this.qNameFieldConverter = qNameFieldConverter;
    }

    @PostConstruct
    public void setup() {
        super.setup();
        getView().setup(treeGrid);
    }

    public void show(final String value) {

        final String dataTypeName = extractItemDefinitionName(value);
        final Optional<ItemDefinition> itemDefinition = itemDefinitionUtils.findByName(dataTypeName);

        itemDefinition.ifPresent(i -> {
            final DataType dataType = dataTypeFactory.makeDataType(i);
            treeGrid.setupItems(dataType);
            superShow();
        });
    }

    void superShow() {
        super.show();
    }

    private String extractItemDefinitionName(final String value) {
        final QName qName = qNameFieldConverter.toModelValue(value);
        return qName.getLocalPart();
    }

    public interface View extends Elemental2Modal.View<DataTypeModal> {

        void setup(final DataTypeTreeGrid treeGrid);
    }
}
