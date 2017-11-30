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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.HashMap;

import javax.enterprise.inject.Instance;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.MockInstanceImpl;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({RootPanel.class, Text.class, AnchorElement.class})
@RunWith(GwtMockitoTestRunner.class)
public class FromAccumulateCompositeFactPatternWidgetTest {

    private static final String REDHAT_PACKAGE = "com.redhat";
    private static final String REDHAT_SUBPACKAGE = "com.redhat.rule";
    private static final String FACT_TYPE_CAR = "Car";
    private static final String FACT_TYPE_PERSON = "Person";

    private PackageDataModelOracleBaselinePayload dataModelPayload;

    @Mock
    private Path resourcePath;

    @Mock
    private SyncBeanDef syncBeanDef;

    @Mock
    private SyncBeanManager syncBeanManager;

    @Spy
    @InjectMocks
    private AsyncPackageDataModelOracleFactory asyncPackageDataModelOracleFactory;

    @Mock
    private RuleModeller ruleModeller;

    @Mock
    private IncrementalDataModelService incrementalDataModelService;

    private Instance<DynamicValidator> validatorInstance;

    private AsyncPackageDataModelOracle oracle;

    @Mock
    private EventBus eventBus;

    @Mock
    private FromAccumulateCompositeFactPattern pattern;

    @Mock
    private ListBox listBox;

    private RuleModel ruleModelWithImportedNumber;

    private FromAccumulateCompositeFactPatternWidget fromAccumulateWidget;

    @Before
    public void setUp() throws Exception {
        // listBox is used for verification fo added available fact types into UI
        GwtMockito.useProviderForType(ListBox.class, aClass -> listBox);

        // Mock partially the AsyncPackageDataModelOracle
        validatorInstance = new MockInstanceImpl<>();
        oracle = spy(new AsyncPackageDataModelOracleImpl(new CallerMock<>(incrementalDataModelService), validatorInstance));

        // Mock partially the AsyncPackageDataModelOracleFactory and ensure oracle will contain data form payload below
        doReturn(syncBeanDef).when(syncBeanManager).lookupBean(AsyncPackageDataModelOracle.class);
        doReturn(oracle).when(syncBeanDef).getInstance();
        doReturn(oracle).when(ruleModeller).getDataModelOracle();

        // Prepare base payload that simulates two Facts in the package, Person and Car
        dataModelPayload = new PackageDataModelOracleBaselinePayload();
        dataModelPayload.setModelFields(new HashMap<String, ModelField[]>() {{
            final String fqnPerson = REDHAT_PACKAGE + "." + FACT_TYPE_PERSON;
            final String fqnCar = REDHAT_PACKAGE + "." + FACT_TYPE_CAR;
            final String fqnNumber = Number.class.getName();
            put(fqnPerson, new ModelField[]{modelField(DataType.TYPE_THIS, fqnPerson)});
            put(fqnCar, new ModelField[]{modelField(DataType.TYPE_THIS, fqnCar)});
            put(fqnNumber, new ModelField[]{modelField(DataType.TYPE_THIS, fqnNumber)});
        }});

        // Prevent NPE in FromAccumulateCompositeFactPatternWidget constructor
        when(pattern.useFunctionOrCode()).thenReturn(FromAccumulateCompositeFactPattern.USE_FUNCTION);

        fromAccumulateWidget = new FromAccumulateCompositeFactPatternWidget(ruleModeller,
                                                                            eventBus,
                                                                            pattern);

        ruleModelWithImportedNumber = new RuleModel() {{
            setImports(new Imports() {{
                addImport(new Import(Number.class));
            }});
        }};
    }

    @Test
    public void testShowFactTypeSelectorFactsInSamePackage() throws Exception {
        dataModelPayload.setPackageName(REDHAT_PACKAGE);
        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(resourcePath, dataModelPayload);

        fromAccumulateWidget.showFactTypeSelector();

        verify(listBox).addItem(GuidedRuleEditorResources.CONSTANTS.Choose());
        verify(listBox).addItem(FACT_TYPE_PERSON);
        verify(listBox).addItem(FACT_TYPE_CAR);
        verify(listBox, times(3)).addItem(anyString());
    }

    @Test
    public void testShowFactTypeSelectorFactsNotInSamePackage() throws Exception {
        dataModelPayload.setPackageName(REDHAT_SUBPACKAGE);
        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(resourcePath, dataModelPayload);

        fromAccumulateWidget.showFactTypeSelector();

        verify(listBox).addItem(GuidedRuleEditorResources.CONSTANTS.Choose());
        verify(listBox, times(1)).addItem(anyString());
    }

    @Test
    public void testShowFactTypeSelectorFactsInSamePackageAndImportedNumber() throws Exception {
        dataModelPayload.setPackageName(REDHAT_PACKAGE);
        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(
                resourcePath, ruleModelWithImportedNumber, dataModelPayload);

        fromAccumulateWidget.showFactTypeSelector();

        verify(listBox).addItem(GuidedRuleEditorResources.CONSTANTS.Choose());
        verify(listBox).addItem(FACT_TYPE_PERSON);
        verify(listBox).addItem(FACT_TYPE_CAR);
        verify(listBox).addItem(Number.class.getSimpleName());
        verify(listBox, times(4)).addItem(anyString());
    }

    @Test
    public void testShowFactTypeSelectorFactsNotInSamePackageAndImportedNumber() throws Exception {
        dataModelPayload.setPackageName(REDHAT_SUBPACKAGE);
        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(
                resourcePath, ruleModelWithImportedNumber, dataModelPayload);

        fromAccumulateWidget.showFactTypeSelector();

        verify(listBox).addItem(GuidedRuleEditorResources.CONSTANTS.Choose());
        verify(listBox).addItem(Number.class.getSimpleName());
        verify(listBox, times(2)).addItem(anyString());
    }
}
