/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwt.event.shared.EventBus;
import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.factPattern.Connectives;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.mockito.Mockito.*;

public class OperatorsBaseTest {

    @Mock
    protected RuleModeller modeller;

    @Mock
    protected EventBus eventBus;

    @Mock
    protected FactPattern pattern;

    @Mock
    protected SingleFieldConstraint singleFieldConstraint;

    @Mock
    protected ConnectiveConstraint connectiveConstraint;

    @Mock
    private IncrementalDataModelService service;

    private Caller<IncrementalDataModelService> serviceCaller;

    @Mock
    protected AsyncPackageDataModelOracleImpl oracle;

    protected Connectives connectives;

    @Before
    public void setUp() throws Exception {
        ApplicationPreferences.setUp(new HashMap() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/MM/YYYY");
        }});

        serviceCaller = new CallerMock<>(service);
        oracle = new AsyncPackageDataModelOracleImpl(serviceCaller,
                                                     null);

        final ModelField[] modelFields = new ModelField[]{
                modelField("street",
                           DataType.TYPE_STRING),
                modelField("number",
                           DataType.TYPE_NUMERIC_INTEGER)};

        Map<String, ModelField[]> fields = new HashMap<>();
        fields.put("org.Address",
                   modelFields);

        oracle.addModelFields(fields);

        connectives = spy(new Connectives(modeller,
                                          eventBus,
                                          pattern,
                                          false));

        doReturn(oracle).when(connectives).getDataModelOracle();

        doReturn(oracle).when(modeller).getDataModelOracle();

        doReturn(Stream.of(connectiveConstraint).toArray(ConnectiveConstraint[]::new))
                .when(singleFieldConstraint).getConnectives();
    }
}
