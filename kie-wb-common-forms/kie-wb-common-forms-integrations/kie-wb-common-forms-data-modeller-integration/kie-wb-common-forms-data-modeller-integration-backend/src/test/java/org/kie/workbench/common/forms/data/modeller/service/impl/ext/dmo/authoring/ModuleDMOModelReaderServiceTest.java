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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.authoring;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.model.Source;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;

import static org.junit.Assert.assertTrue;

public class ModuleDMOModelReaderServiceTest extends AbstractModuleDMOModelReaderServiceTest {

    @BeforeClass
    public static void setUp() throws Exception {
        initialize();

        buildModules("module1");
    }

    @Test
    public void testExtractAllModels() {
        testReadModels(ModelReader::readAllFormModels);
    }

    @Test
    public void testReadModuleModels() {
        testReadModels(ModelReader::readModuleFormModels);
    }

    private void testReadModels(Function<ModelReader, Collection<DataObjectFormModel>> formula) {
        ModelReader modelReader = resolveModelReader();

        Assertions.assertThat(modelReader)
                .isNotNull();

        Collection<DataObjectFormModel> models = formula.apply(modelReader);

        Assertions.assertThat(models)
                .isNotNull()
                .hasSize(3);

        Optional<DataObjectFormModel> clientModel = findModel(models, CLIENT_TYPE);

        assertTrue(clientModel.isPresent());

        validateClientModel(clientModel.get(), Source.INTERNAL);

        Optional<DataObjectFormModel> lineModel = findModel(models, LINE_TYPE);

        assertTrue(lineModel.isPresent());

        validateLineModel(lineModel.get(), Source.INTERNAL);

        Optional<DataObjectFormModel> expenseModel = findModel(models, EXPENSE_TYPE);

        assertTrue(expenseModel.isPresent());

        validateExpenseModel(expenseModel.get(), Source.INTERNAL);
    }

    @Override
    protected Source getDefaultSource() {
        return Source.INTERNAL;
    }
}
