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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorForDataObjectsTest extends AbstractFormDefinitionGeneratorTest {

    @Mock
    private Path userFormPath;

    @Mock
    private Path lineFormPath;

    @Mock
    private Path invoiceFormPath;

    private Form userForm;
    private Form lineForm;
    private Form invoiceForm;

    @Override
    protected void doInit() throws Exception {
        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> userForm = form, DATAOBJECTS_RESOURCES, USER_FORM, userFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(userForm, userFormPath)));

        initForm(form -> lineForm = form, DATAOBJECTS_RESOURCES, LINE_FORM, lineFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(lineForm, lineFormPath)));

        initForm(form -> invoiceForm = form, DATAOBJECTS_RESOURCES, INVOICE_FORM, invoiceFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(invoiceForm, invoiceFormPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        Assertions.assertThat(context.getSummaries())
                .isNotEmpty()
                .hasSize(3);

        Assertions.assertThat(context.getExtraSummaries())
                .isEmpty();

        // 3 legacyforms + 3 migrated forms
        verify(migrationServicesCDIWrapper, times(6)).write(any(Path.class), anyString(), anyString());

        context.getSummaries().forEach(summary -> {
            assertTrue(summary.getResult().isSuccess());
            switch (summary.getBaseFormName() + ".form") {
                case INVOICE_FORM:
                    verifyInvoiceForm(summary);
                    break;
                case USER_FORM:
                    verifyUserForm(summary);
                    break;
                case LINE_FORM:
                    verifyLineForm(summary);
                    break;
            }
        });
    }
}
