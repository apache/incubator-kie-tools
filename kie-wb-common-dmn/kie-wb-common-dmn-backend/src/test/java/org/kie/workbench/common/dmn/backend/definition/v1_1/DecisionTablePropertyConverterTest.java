/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.backend.definition.v1_1;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.v1_2.TDecisionTable;
import org.kie.dmn.model.v1_2.TOutputClause;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTablePropertyConverterTest {

    private static final String UUID = "uuid";

    private static final String NAME = "name";

    private static final String DESCRIPTION = "description";

    private static final String QNAME_LOCALPART = "local-part";

    @Test
    public void testWBFromDMNSingleOutputClauseTypeRef() {
        final org.kie.dmn.model.api.DecisionTable dmn = new TDecisionTable();
        final org.kie.dmn.model.api.OutputClause dmnOutputClause1 = new TOutputClause();

        dmn.setId(UUID);
        dmn.setDescription(DESCRIPTION);
        dmn.setTypeRef(new QName(QNAME_LOCALPART));

        dmnOutputClause1.setName(NAME);
        dmnOutputClause1.setTypeRef(new QName(QNAME_LOCALPART + "-oc1"));

        dmn.getOutput().add(dmnOutputClause1);

        final DecisionTable wb = DecisionTablePropertyConverter.wbFromDMN(dmn);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(UUID);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(DESCRIPTION);
        assertThat(wb.getTypeRef()).isNotNull();
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART);

        assertThat(wb.getOutput()).hasSize(1);
        final OutputClause wbOutputClause1 = wb.getOutput().get(0);
        assertThat(wbOutputClause1.getName()).isNull();
        assertThat(wbOutputClause1.getTypeRef()).isNull();
    }

    @Test
    public void testWBFromDMNMultipleOutputClauseTypeRef() {
        final org.kie.dmn.model.api.DecisionTable dmn = new TDecisionTable();
        final org.kie.dmn.model.api.OutputClause dmnOutputClause1 = new TOutputClause();
        final org.kie.dmn.model.api.OutputClause dmnOutputClause2 = new TOutputClause();

        dmn.setId(UUID);
        dmn.setDescription(DESCRIPTION);
        dmn.setTypeRef(new QName(QNAME_LOCALPART));

        dmnOutputClause1.setName(NAME + "-oc1");
        dmnOutputClause2.setName(NAME + "-oc2");
        dmnOutputClause1.setTypeRef(new QName(QNAME_LOCALPART + "-oc1"));
        dmnOutputClause2.setTypeRef(new QName(QNAME_LOCALPART + "-oc2"));

        dmn.getOutput().add(dmnOutputClause1);
        dmn.getOutput().add(dmnOutputClause2);

        final DecisionTable wb = DecisionTablePropertyConverter.wbFromDMN(dmn);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(UUID);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(DESCRIPTION);
        assertThat(wb.getTypeRef()).isNotNull();
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART);

        assertThat(wb.getOutput()).hasSize(2);
        final OutputClause wbOutputClause1 = wb.getOutput().get(0);
        final OutputClause wbOutputClause2 = wb.getOutput().get(1);
        assertThat(wbOutputClause1.getName()).isEqualTo(NAME + "-oc1");
        assertThat(wbOutputClause2.getName()).isEqualTo(NAME + "-oc2");
        assertThat(wbOutputClause1.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART + "-oc1");
        assertThat(wbOutputClause2.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART + "-oc2");
    }

    @Test
    public void testDMNFromWBSingleOutputClauseTypeRef() {
        final DecisionTable wb = new DecisionTable();
        final OutputClause outputClause1 = new OutputClause();

        wb.getId().setValue(UUID);
        wb.getDescription().setValue(DESCRIPTION);
        wb.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(NULL_NS_URI,
                                                                              QNAME_LOCALPART));

        outputClause1.setName(NAME);
        outputClause1.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(NULL_NS_URI,
                                                                                         QNAME_LOCALPART + "-oc1"));

        wb.getOutput().add(outputClause1);

        final org.kie.dmn.model.api.DecisionTable dmn = DecisionTablePropertyConverter.dmnFromWB(wb);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(UUID);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(dmn.getTypeRef()).isNotNull();
        assertThat(dmn.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART);

        assertThat(dmn.getOutput()).hasSize(1);
        final org.kie.dmn.model.api.OutputClause dmnOutputClause1 = dmn.getOutput().get(0);
        assertThat(dmnOutputClause1.getName()).isNull();
        assertThat(dmnOutputClause1.getTypeRef()).isNull();
    }

    @Test
    public void testDMNFromWBMultipleOutputClauseTypeRef() {
        final DecisionTable wb = new DecisionTable();
        final OutputClause wbOutputClause1 = new OutputClause();
        final OutputClause wbOutputClause2 = new OutputClause();

        wb.getId().setValue(UUID);
        wb.getDescription().setValue(DESCRIPTION);
        wb.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(NULL_NS_URI,
                                                                              QNAME_LOCALPART));

        wbOutputClause1.setName(NAME + "-oc1");
        wbOutputClause2.setName(NAME + "-oc2");
        wbOutputClause1.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(NULL_NS_URI,
                                                                                           QNAME_LOCALPART + "-oc1"));
        wbOutputClause2.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(NULL_NS_URI,
                                                                                           QNAME_LOCALPART + "-oc2"));

        wb.getOutput().add(wbOutputClause1);
        wb.getOutput().add(wbOutputClause2);

        final org.kie.dmn.model.api.DecisionTable dmn = DecisionTablePropertyConverter.dmnFromWB(wb);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(UUID);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(dmn.getTypeRef()).isNotNull();
        assertThat(dmn.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART);

        assertThat(dmn.getOutput()).hasSize(2);
        final org.kie.dmn.model.api.OutputClause dmnOutputClause1 = dmn.getOutput().get(0);
        final org.kie.dmn.model.api.OutputClause dmnOutputClause2 = dmn.getOutput().get(1);
        assertThat(dmnOutputClause1.getName()).isEqualTo(NAME + "-oc1");
        assertThat(dmnOutputClause2.getName()).isEqualTo(NAME + "-oc2");
        assertThat(dmnOutputClause1.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART + "-oc1");
        assertThat(dmnOutputClause2.getTypeRef().getLocalPart()).isEqualTo(QNAME_LOCALPART + "-oc2");
    }
}
