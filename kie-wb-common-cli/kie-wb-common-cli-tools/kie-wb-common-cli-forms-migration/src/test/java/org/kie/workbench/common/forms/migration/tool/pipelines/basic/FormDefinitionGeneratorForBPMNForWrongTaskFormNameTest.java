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
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNAnalyzer;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNProcess;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
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
public class FormDefinitionGeneratorForBPMNForWrongTaskFormNameTest extends AbstractFormDefinitionGeneratorTest {

    private static final String WRONG_PROCESS_FORM = "invoices.invoices.wrong-taskform.form";

    private static final String WRONG_TASK_FORM = "wrong-taskform.form";

    @Mock
    private Path wrongProcessPath;

    @Mock
    private Path wrongTaskPath;

    private Form wrongProcessForm;
    private Form wrongTaskForm;

    @Override
    protected void doInit() throws Exception {

        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> wrongProcessForm = form, BPMN_RESOURCES, WRONG_PROCESS_FORM, wrongProcessPath);
        summaries.add(new FormMigrationSummary(new Resource<>(wrongProcessForm, wrongProcessPath)));

        initForm(form -> wrongTaskForm = form, BPMN_RESOURCES, WRONG_TASK_FORM, wrongTaskPath);
        summaries.add(new FormMigrationSummary(new Resource<>(wrongTaskForm, wrongTaskPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);

        generator = new FormDefinitionGenerator(DataObjectFormAdapter::new, this::getBPMNAdapter);
    }

    @Override
    protected List<JBPMFormModel> getProcessFormModels() {
        BPMNAnalyzer analyzer = new BPMNAnalyzer();
        BPMNProcess process = analyzer.read(this.getClass().getResourceAsStream(BPMN_RESOURCES + INVOICES_BPMN));
        return process.getFormModels();
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        Assertions.assertThat(context.getSummaries())
                .isNotEmpty()
                .hasSize(2);

        Assertions.assertThat(context.getExtraSummaries())
                .isEmpty();

        verify(migrationServicesCDIWrapper, never()).write(any(Path.class), anyString(), anyString());

        context.getSummaries().forEach(summary -> {
            assertFalse(summary.getResult().isSuccess());
            assertNull(summary.getNewForm());
        });
    }
}
