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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorForDataObjectsWithErrorsTest extends AbstractFormDefinitionGeneratorTest {

    private static final String USER_BASIC_TYPE_DATA_HOLDER_FORM = "user_with_basic_dataHolder.form";
    private static final String USER_MULTIPLE_DATA_HOLDERS_FORM = "user_with_multiple_dataHolders.form";
    private static final String USER_NO_DATA_HOLDERS_FORM = "user_without_dataHolders.form";

    @Mock
    private Path basicDataHolderPath;

    @Mock
    private Path noDataHoldersPath;

    @Mock
    private Path multipleDataHoldersPath;

    private Form basicDataHolderForm;
    private Form noDataHoldersForm;
    private Form multipleDataHoldersForm;

    @Override
    protected void doInit() throws Exception {
        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> basicDataHolderForm = form, DATAOBJECTS_RESOURCES, USER_BASIC_TYPE_DATA_HOLDER_FORM, basicDataHolderPath);
        summaries.add(new FormMigrationSummary(new Resource<>(basicDataHolderForm, basicDataHolderPath)));

        initForm(form -> noDataHoldersForm = form, DATAOBJECTS_RESOURCES, USER_NO_DATA_HOLDERS_FORM, noDataHoldersPath);
        summaries.add(new FormMigrationSummary(new Resource<>(noDataHoldersForm, noDataHoldersPath)));

        initForm(form -> multipleDataHoldersForm = form, DATAOBJECTS_RESOURCES, USER_MULTIPLE_DATA_HOLDERS_FORM, multipleDataHoldersPath);
        summaries.add(new FormMigrationSummary(new Resource<>(multipleDataHoldersForm, multipleDataHoldersPath)));

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

        verify(migrationServicesCDIWrapper, never()).write(any(Path.class), anyString(), anyString());

        context.getSummaries().forEach(summary -> {
            assertFalse(summary.getResult().isSuccess());
            assertNull(summary.getNewForm());
        });
    }
}
