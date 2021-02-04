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

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableSourceServiceTest {

    @Mock
    Path path;

    @Mock
    Package packageMock;

    @Mock
    FileSystem fileSystem;

    @Mock
    KieModuleService moduleService;

    @Mock
    FileDiscoveryService discoveryService;

    @Mock
    GuidedDTableResourceTypeDefinition resourceTypeDefinition;

    @Mock
    GuidedDecisionTableEditorService guidedDecisionTableEditorService;

    @Mock
    IOService ioService;

    @Mock
    FileDiscoveryService fileDiscoveryService;

    GuidedDecisionTable52 model;

    GuidedDecisionTableSourceService service;

    Pattern52 pattern;

    ConditionCol52 nameEqualToLiteralCondition;

    List<List<DTCellValue52>> data;

    @Before
    public void setUp() throws Exception {
        service = new GuidedDecisionTableSourceService(resourceTypeDefinition,
                                                       guidedDecisionTableEditorService,
                                                       ioService,
                                                       fileDiscoveryService,
                                                       moduleService);

        // Simulates that no DSL files are present
        when(moduleService.resolvePackage(any())).thenReturn(packageMock);
        when(fileSystem.supportedFileAttributeViews()).thenReturn(new HashSet<String>());
        when(path.getFileSystem()).thenReturn(fileSystem);
        when(path.toString()).thenReturn("/");
        when(path.getFileName()).thenReturn(path);
        when(path.toUri()).thenReturn(new URI("/"));

        model = new GuidedDecisionTable52();

        model.setPackageName("com.sample");
        model.setImports(new Imports(Arrays.asList(new Import("com.sample.Person"))));
        model.setRowNumberCol(new RowNumberCol52());
        model.setDescriptionCol(new DescriptionCol52());

        pattern = new Pattern52();
        pattern.setBoundName("$p");
        pattern.setFactType("Person");

        nameEqualToLiteralCondition = new ConditionCol52();
        nameEqualToLiteralCondition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        nameEqualToLiteralCondition.setHeader("name equals to");
        nameEqualToLiteralCondition.setFactField("name");
        nameEqualToLiteralCondition.setOperator("==");

        pattern.setChildColumns(Arrays.asList(nameEqualToLiteralCondition));

        model.setConditionPatterns(Arrays.asList(pattern));

        data = new ArrayList<>();
    }

    @Test
    public void testOtherwise() throws Exception {
        addRow(1,
               "John",
               false);
        addRow(2,
               "Peter",
               false);
        addRow(3,
               null,
               true);

        model.setData(data);

        String source = service.getSource(path,
                                          model);
        assertTrue("Expected: name == \"John\"",
                   source.contains("$p : Person( name == \"John\" )"));
        assertTrue("Expected: name == \"Peter\"",
                   source.contains("$p : Person( name == \"Peter\" )"));
        assertTrue("Expected: name not in ( \"John\", \"Peter\" )",
                   source.contains("$p : Person( name not in ( \"John\", \"Peter\" )"));
    }

    @Test
    public void testOtherwiseTwoSameValues() throws Exception {
        addRow(1,
               "John",
               false);
        addRow(2,
               "John",
               false);
        addRow(3,
               null,
               true);

        model.setData(data);

        String source = service.getSource(path,
                                          model);
        assertTrue("Expected: name not in ( \"John\" )",
                   source.contains("$p : Person( name not in ( \"John\" )"));
    }

    @Test
    public void testOtherwiseEmptyValue() throws Exception {
        addRow(1,
               "John",
               false);
        addRow(2,
               "",
               false);
        addRow(3,
               null,
               true);

        model.setData(data);

        String source = service.getSource(path,
                                          model);
        assertTrue("Expected: name not in ( \"John\", \"\" )",
                   source.contains("$p : Person( name not in ( \"John\", \"\" )"));
    }

    @Test
    public void testOtherwiseEmptyAndNullValue() throws Exception {
        addRow(1,
               "",
               false);
        addRow(2,
               null,
               false);
        addRow(3,
               null,
               true);

        model.setData(data);

        String source = service.getSource(path,
                                          model);
        assertTrue("Expected: name not in ( \"\" )",
                   source.contains("$p : Person( name not in ( \"\" )"));
    }

    @Test
    public void testOtherwiseTwoTimes() throws Exception {
        addRow(1,
               "John",
               false);
        addRow(2,
               null,
               true);
        addRow(3,
               "Peter",
               false);
        addRow(4,
               null,
               true);

        model.setData(data);

        String source = service.getSource(path,
                                          model);
        assertTrue("Expected: name not in ( \"John\", \"Peter\" )",
                   source.contains("$p : Person( name not in ( \"John\", \"Peter\" )"));
        source.replaceFirst("John",
                            "");
        assertTrue("Expected: name not in ( \"John\", \"Peter\" )",
                   source.contains("$p : Person( name not in ( \"John\", \"Peter\" )"));
    }

    @Test
    public void testCustomRuleName() throws Exception {
        adRowWithCustomRuleName(1,
                                "John is important",
                                "John");
        model.setData(data);
        String source = service.getSource(path,
                                          model);

        assertTrue("Expected custom rule name to be present",
                   source.contains("rule \"John is important\"\n"));
    }

    @Test
    public void testFormulaFieldBinding() throws Exception {
        final ConditionCol52 ageEqualToFormulaCondition = new ConditionCol52();
        ageEqualToFormulaCondition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        ageEqualToFormulaCondition.setHeader("age equals to");
        ageEqualToFormulaCondition.setFactField("age");
        ageEqualToFormulaCondition.setOperator("==");
        ageEqualToFormulaCondition.setBinding("$age");

        pattern.setChildColumns(Arrays.asList(ageEqualToFormulaCondition));
        model.setConditionPatterns(Arrays.asList(pattern));
        addRow(1, "1 + 1");
        model.setData(data);

        final String source = service.getSource(path, model);
        assertTrue(source.contains("$p : Person( $age : age == ( 1 + 1 ) )"));
    }

    private void addRow(int rowNumber,
                        String nameEqualToCostraint,
                        boolean isOtherwise) {
        data.add(new ArrayList<DTCellValue52>() {{
            add(new DTCellValue52(rowNumber));
            add(new DTCellValue52(""));
            add(new DTCellValue52("row " + rowNumber));
            if (!isOtherwise) {
                add(new DTCellValue52(nameEqualToCostraint));
            } else {
                DTCellValue52 otherwise = new DTCellValue52();
                otherwise.setOtherwise(true);
                add(otherwise);
            }
        }});
    }

    private void addRow(int rowNumber,
                        String... constraintValues) {

        adRowWithCustomRuleName(rowNumber, "", constraintValues);
    }

    private void adRowWithCustomRuleName(int rowNumber,
                                         String customRuleName,
                                         String... constraintValues) {
        data.add(new ArrayList<DTCellValue52>() {{
            add(new DTCellValue52(rowNumber));
            add(new DTCellValue52(customRuleName));
            add(new DTCellValue52("row " + rowNumber));
            Stream.of(constraintValues).forEach(value -> add(new DTCellValue52(value)));
        }});
    }
}
