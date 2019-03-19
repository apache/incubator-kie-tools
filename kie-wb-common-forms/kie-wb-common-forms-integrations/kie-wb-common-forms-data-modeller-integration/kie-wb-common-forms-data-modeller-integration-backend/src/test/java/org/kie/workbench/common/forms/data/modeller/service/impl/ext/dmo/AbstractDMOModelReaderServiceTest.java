/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.model.Source;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.AbstractModelFinderTest;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.util.ModelReaderUtil;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldLabelEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldPlaceHolderEntry;

public abstract class AbstractDMOModelReaderServiceTest<SOURCE, SERVICE extends ModelReaderService<SOURCE>> extends AbstractModelFinderTest {

    protected abstract Class<SERVICE> getServiceType();

    protected abstract SOURCE getSource();

    protected abstract Source getDefaultSource();

    @Test
    public void testExtractClientModel() {
        launchSingleModelTest(modelReader -> validateClientModel(modelReader.readFormModel(CLIENT_TYPE), getDefaultSource()));
    }

    @Test
    public void testExtractLineModel() {
        launchSingleModelTest(modelReader -> validateLineModel(modelReader.readFormModel(LINE_TYPE), getDefaultSource()));
    }

    @Test
    public void testExtractExpenseModel() {
        launchSingleModelTest(modelReader -> validateExpenseModel(modelReader.readFormModel(EXPENSE_TYPE), getDefaultSource()));
    }

    protected void launchSingleModelTest(Consumer<ModelReader> modelReaderConsumer) {
        ModelReader modelReader = resolveModelReader();

        Assertions.assertThat(modelReader)
                .isNotNull();

        modelReaderConsumer.accept(modelReader);
    }

    protected ModelReader resolveModelReader() {
        SERVICE service = weldContainer.select(getServiceType()).get();

        Assertions.assertThat(service)
                .isNotNull();

        ModelReader modelReader = service.getModelReader(getSource());

        Assertions.assertThat(modelReader)
                .isNotNull();

        return modelReader;
    }

    protected void validateClientModel(final DataObjectFormModel clientModel, final Source source) {
        verifyModel(clientModel, "Client", CLIENT_TYPE, source, 2);
        verifyModelProperty(clientModel, CLIENT_NAME, String.class.getName(), TypeKind.BASE, false, CLIENT_NAME_LABEL);
        verifyModelProperty(clientModel, CLIENT_LAST_NAME, String.class.getName(), TypeKind.BASE, false, CLIENT_LAST_NAME_LABEL);
    }

    protected void validateLineModel(final DataObjectFormModel lineModel, final Source source) {
        verifyModel(lineModel, "Line", LINE_TYPE, source, 3);
        verifyModelProperty(lineModel, LINE_PRODUCT, String.class.getName(), TypeKind.BASE, false, LINE_PRODUCT_LABEL);
        verifyModelProperty(lineModel, LINE_PRICE, Double.class.getName(), TypeKind.BASE, false, LINE_PRICE_LABEL);
        verifyModelProperty(lineModel, LINE_DATE, Date.class.getName(), TypeKind.BASE, false, LINE_DATE_LABEL);
    }

    protected void validateExpenseModel(final DataObjectFormModel expenseModel, final Source source) {
        verifyModel(expenseModel, "Expense", EXPENSE_TYPE, source, 3);
        verifyModelProperty(expenseModel, EXPENSE_CLIENT, CLIENT_TYPE, TypeKind.OBJECT, false, EXPENSE_CLIENT_LABEL);
        verifyModelProperty(expenseModel, EXPENSE_LINES, LINE_TYPE, TypeKind.OBJECT, true, EXPENSE_LINES_LABEL);
        verifyModelProperty(expenseModel, EXPENSE_DATE, Date.class.getName(), TypeKind.BASE, false, EXPENSE_DATE_LABEL);
    }

    protected void validateAddressModel(final DataObjectFormModel addressModel, final Source source) {
        verifyModel(addressModel, "Address", ADDRESS_TYPE, source, 5);
        verifyModelProperty(addressModel, ADDRESS_STREET, String.class.getName(), TypeKind.BASE, false, ADDRESS_STREET_LABEL);
        verifyModelProperty(addressModel, ADDRESS_NUM, Integer.class.getName(), TypeKind.BASE, false, ADDRESS_NUM_LABEL);
        verifyModelProperty(addressModel, ADDRESS_CP, String.class.getName(), TypeKind.BASE, false, ADDRESS_CP_LABEL);
        verifyModelProperty(addressModel, ADDRESS_CITY, String.class.getName(), TypeKind.BASE, false, ADDRESS_CITY_LABEL);
        verifyModelProperty(addressModel, ADDRESS_MAIN_ADDRESS, Boolean.class.getName(), TypeKind.BASE, false, ADDRESS_MAIN_ADDRESS_LABEL);
    }

    private void verifyModel(final DataObjectFormModel model, final String name, final String type, final Source source, final int properties) {
        Assertions.assertThat(model)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("className", type)
                .hasFieldOrPropertyWithValue("source", source);

        Assertions.assertThat(model.getProperties())
                .isNotNull()
                .hasSize(properties);

        Assertions.assertThat(model.getProperty(ModelReaderUtil.SERIAL_VERSION_UID))
                .isNull();

        Assertions.assertThat(model.getProperty("id"))
                .isNull();
    }

    private void verifyModelProperty(final DataObjectFormModel model, final String property, final String type, final TypeKind kind, final boolean multiple, final String label) {
        ModelProperty modelProperty = model.getProperty(property);

        Assertions.assertThat(modelProperty)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", property);

        TypeInfo propertyType = modelProperty.getTypeInfo();

        Assertions.assertThat(propertyType)
                .isNotNull()
                .hasFieldOrPropertyWithValue("className", type)
                .hasFieldOrPropertyWithValue("type", kind)
                .hasFieldOrPropertyWithValue("multiple", multiple);

        Assertions.assertThat(modelProperty.getMetaData().getEntries())
                .isNotNull()
                .hasSize(2);

        Assertions.assertThat(modelProperty.getMetaData().getEntry(FieldLabelEntry.NAME))
                .hasFieldOrPropertyWithValue("value", label);

        Assertions.assertThat(modelProperty.getMetaData().getEntry(FieldPlaceHolderEntry.NAME))
                .hasFieldOrPropertyWithValue("value", label);
    }

    protected Optional<DataObjectFormModel> findModel(Collection<DataObjectFormModel> models, String type) {
        return models.stream()
                .filter(model -> model.getType().equals(type))
                .findAny();
    }
}
