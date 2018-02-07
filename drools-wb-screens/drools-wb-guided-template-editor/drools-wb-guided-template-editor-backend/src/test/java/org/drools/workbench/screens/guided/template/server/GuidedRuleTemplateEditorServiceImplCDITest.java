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

package org.drools.workbench.screens.guided.template.server;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class GuidedRuleTemplateEditorServiceImplCDITest extends CDITestSetup {

    private static final String ROOT = "templates/src/main/resources/org/kiegroup/";
    private static final String CARS = ROOT + "cars.template";

    @Mock
    private SyncBeanManager syncBeanManager;

    @Mock
    private SyncBeanDef syncBeanDef;

    @Spy
    @InjectMocks
    private AsyncPackageDataModelOracleFactory oracleFactory = new AsyncPackageDataModelOracleFactory();

    private AsyncPackageDataModelOracleImpl asyncOracle = new AsyncPackageDataModelOracleImpl(null, null);

    private GuidedRuleTemplateEditorService testedService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        doReturn(syncBeanDef).when(syncBeanManager).lookupBean(any());
        doReturn(asyncOracle).when(syncBeanDef).getInstance();

        testedService = getReference(GuidedRuleTemplateEditorService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testValidate() throws Exception {
        final List<ValidationMessage> messages = testedService.validate(getPath(CARS));

        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateAndLoad() throws Exception {
        final Path testedPath = getPath(CARS);
        final TemplateModel testedModel = testedService.load(testedPath);
        final List<ValidationMessage> messages = testedService.validate(testedPath,
                                                                        testedModel);

        Assertions.assertThat(messages).isEmpty();
        Assertions.assertThat(testedModel.getColsCount()).isEqualTo(3);
        Assertions.assertThat(testedModel.getRowsCount()).isEqualTo(4);
    }

    @Test
    public void testLoadContent() throws Exception {
        final Path testedPath = getPath(CARS);
        final GuidedTemplateEditorContent testedContent = testedService.loadContent(testedPath);

        oracleFactory.makeAsyncPackageDataModelOracle(testedPath, testedContent.getModel(), testedContent.getDataModel());

        final String declaredFactInEnum = "Car";
        final String declaredFieldInEnum = "price";
        final Map<String, String> actualFieldValues = new HashMap<String, String>() {{
            put("color", "red");
        }};
        final DropDownData dropDownData = asyncOracle.getEnums(declaredFactInEnum,
                                                               declaredFieldInEnum,
                                                               actualFieldValues);

        Assertions.assertThat(dropDownData.getQueryExpression()).isEqualTo("(new org.kiegroup.PriceHelper()).getPrices(\"@{color}\")");
        Assertions.assertThat(dropDownData.getValuePairs()).hasSize(1);
        Assertions.assertThat(dropDownData.getValuePairs()[0]).isEqualTo("color=red");
    }

    private Path getPath(final String resource) throws Exception {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}
