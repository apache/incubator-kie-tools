/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PMMLIncludedModelHandlerTest {

    private static final String NODE1_UUID = "uuid1";

    private static final String NODE2_UUID = "uuid2";

    private static final String NODE3_UUID = "uuid3";

    private static final String DOCUMENT_NAME_ORIGINAL = "document-original";

    private static final String DOCUMENT_NAME_UPDATED = "document-updated";

    private static final String MODEL_NAME = "model";

    private static final String UNAFFECTED_DOCUMENT = "unaffected-document";

    private static final String UNAFFECTED_MODEL = "unaffected-model";

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNSession dmnSession;

    @Mock
    private ExpressionGridCache expressionGridCache;

    @Mock
    private BaseExpressionGrid expressionGrid1;

    @Mock
    private BaseExpressionGrid expressionGrid2;

    @Mock
    private BaseExpressionGrid expressionGrid3;

    private PMMLIncludedModelHandler handler;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(dmnSession);
        when(dmnSession.getExpressionGridCache()).thenReturn(expressionGridCache);
        when(expressionGridCache.getExpressionGrid(Mockito.<String>any())).thenReturn(Optional.empty());
        when(expressionGridCache.getExpressionGrid(eq(NODE1_UUID))).thenReturn(Optional.of(expressionGrid1));
        when(expressionGridCache.getExpressionGrid(eq(NODE2_UUID))).thenReturn(Optional.of(expressionGrid2));
        when(expressionGridCache.getExpressionGrid(eq(NODE3_UUID))).thenReturn(Optional.of(expressionGrid3));

        this.handler = new PMMLIncludedModelHandler(dmnGraphUtils, sessionManager);
    }

    @Test
    public void testUpdateDecisionWithTopLevelFunction() {
        final List<Decision> decisions = setupDecisionWithTopLevelFunction();

        handler.update(DOCUMENT_NAME_ORIGINAL, DOCUMENT_NAME_UPDATED);

        assertTopLevelLiteralExpression(decisions.get(0));
        assertTopLevelFunctionDefinition(decisions.get(1),
                                         DOCUMENT_NAME_UPDATED,
                                         MODEL_NAME);
        assertTopLevelFunctionDefinition(decisions.get(2),
                                         UNAFFECTED_DOCUMENT,
                                         UNAFFECTED_MODEL);

        verify(expressionGrid1).initialiseUiCells();
        verify(expressionGrid2).initialiseUiCells();
        verify(expressionGrid3).initialiseUiCells();
    }

    @Test
    public void testUpdateDecisionWithNestedFunction() {
        final List<Decision> decisions = setupDecisionWithNestedFunction();

        handler.update(DOCUMENT_NAME_ORIGINAL, DOCUMENT_NAME_UPDATED);

        assertNestedLiteralExpression(decisions.get(0));
        assertNestedFunctionDefinition(decisions.get(1),
                                       DOCUMENT_NAME_UPDATED,
                                       MODEL_NAME);
        assertNestedFunctionDefinition(decisions.get(2),
                                       UNAFFECTED_DOCUMENT,
                                       UNAFFECTED_MODEL);

        verify(expressionGrid1, never()).initialiseUiCells();
        verify(expressionGrid2, never()).initialiseUiCells();
        verify(expressionGrid3, never()).initialiseUiCells();
    }

    @Test
    public void testUpdateBusinessKnowledgeModelWithTopLevelFunction() {
        final List<BusinessKnowledgeModel> bkms = setupBusinessKnowledgeModelWithTopLevelFunction();

        handler.update(DOCUMENT_NAME_ORIGINAL, DOCUMENT_NAME_UPDATED);

        assertTopLevelLiteralExpression(bkms.get(0));
        assertPMMLContextDefinition(bkms.get(1).getEncapsulatedLogic(),
                                    DOCUMENT_NAME_UPDATED,
                                    MODEL_NAME);
        assertPMMLContextDefinition(bkms.get(2).getEncapsulatedLogic(),
                                    UNAFFECTED_DOCUMENT,
                                    UNAFFECTED_MODEL);

        verify(expressionGrid1).initialiseUiCells();
        verify(expressionGrid2).initialiseUiCells();
        verify(expressionGrid3).initialiseUiCells();
    }

    @Test
    public void testUpdateBusinessKnowledgeModelWithNestedFunction() {
        final List<BusinessKnowledgeModel> bkms = setupBusinessKnowledgeModelWithNestedFunction();

        handler.update(DOCUMENT_NAME_ORIGINAL, DOCUMENT_NAME_UPDATED);

        assertNestedLiteralExpression(bkms.get(0));
        assertNestedFunctionDefinition(bkms.get(1),
                                       DOCUMENT_NAME_UPDATED,
                                       MODEL_NAME);
        assertNestedFunctionDefinition(bkms.get(2),
                                       UNAFFECTED_DOCUMENT,
                                       UNAFFECTED_MODEL);

        verify(expressionGrid1, never()).initialiseUiCells();
        verify(expressionGrid2, never()).initialiseUiCells();
        verify(expressionGrid3, never()).initialiseUiCells();
    }

    @Test
    public void testDestroyDecisionWithTopLevelFunction() {
        final List<Decision> decisions = setupDecisionWithTopLevelFunction();

        handler.destroy(DOCUMENT_NAME_ORIGINAL);

        assertTopLevelLiteralExpression(decisions.get(0));
        assertTopLevelFunctionDefinition(decisions.get(1),
                                         "",
                                         "");
        assertTopLevelFunctionDefinition(decisions.get(2),
                                         UNAFFECTED_DOCUMENT,
                                         UNAFFECTED_MODEL);

        verify(expressionGrid1).initialiseUiCells();
        verify(expressionGrid2).initialiseUiCells();
        verify(expressionGrid3).initialiseUiCells();
    }

    @Test
    public void testDestroyDecisionWithNestedFunction() {
        final List<Decision> decisions = setupDecisionWithNestedFunction();

        handler.destroy(DOCUMENT_NAME_ORIGINAL);

        assertNestedLiteralExpression(decisions.get(0));
        assertNestedFunctionDefinition(decisions.get(1),
                                       "",
                                       "");
        assertNestedFunctionDefinition(decisions.get(2),
                                       UNAFFECTED_DOCUMENT,
                                       UNAFFECTED_MODEL);

        verify(expressionGrid1, never()).initialiseUiCells();
        verify(expressionGrid2, never()).initialiseUiCells();
        verify(expressionGrid3, never()).initialiseUiCells();
    }

    @Test
    public void testDestroyBusinessKnowledgeModelWithTopLevelFunction() {
        final List<BusinessKnowledgeModel> bkms = setupBusinessKnowledgeModelWithTopLevelFunction();

        handler.destroy(DOCUMENT_NAME_ORIGINAL);

        assertTopLevelLiteralExpression(bkms.get(0));
        assertPMMLContextDefinition(bkms.get(1).getEncapsulatedLogic(),
                                    "",
                                    "");
        assertPMMLContextDefinition(bkms.get(2).getEncapsulatedLogic(),
                                    UNAFFECTED_DOCUMENT,
                                    UNAFFECTED_MODEL);

        verify(expressionGrid1).initialiseUiCells();
        verify(expressionGrid2).initialiseUiCells();
        verify(expressionGrid3).initialiseUiCells();
    }

    @Test
    public void testDestroyBusinessKnowledgeModelWithNestedFunction() {
        final List<BusinessKnowledgeModel> bkms = setupBusinessKnowledgeModelWithNestedFunction();

        handler.destroy(DOCUMENT_NAME_ORIGINAL);

        assertNestedLiteralExpression(bkms.get(0));
        assertNestedFunctionDefinition(bkms.get(1),
                                       "",
                                       "");
        assertNestedFunctionDefinition(bkms.get(2),
                                       UNAFFECTED_DOCUMENT,
                                       UNAFFECTED_MODEL);

        verify(expressionGrid1, never()).initialiseUiCells();
        verify(expressionGrid2, never()).initialiseUiCells();
        verify(expressionGrid3, never()).initialiseUiCells();
    }

    private List<Decision> setupDecisionWithTopLevelFunction() {
        final List<DRGElement> drgElements = new ArrayList<>();
        final Decision decision1 = new Decision();
        decision1.setExpression(new LiteralExpression());

        final Decision decision2 = new Decision();
        decision2.setExpression(makeTopLevelPMMLFunctionDefinition(DOCUMENT_NAME_ORIGINAL, MODEL_NAME));

        final Decision decision3 = new Decision();
        decision3.setExpression(makeTopLevelPMMLFunctionDefinition(UNAFFECTED_DOCUMENT, UNAFFECTED_MODEL));

        decision1.getId().setValue(NODE1_UUID);
        decision2.getId().setValue(NODE2_UUID);
        decision3.getId().setValue(NODE3_UUID);

        drgElements.add(decision1);
        drgElements.add(decision2);
        drgElements.add(decision3);

        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        return Arrays.asList(decision1, decision2, decision3);
    }

    private List<Decision> setupDecisionWithNestedFunction() {
        final List<DRGElement> drgElements = new ArrayList<>();
        final Decision decision1 = new Decision();
        decision1.setExpression(makeNestedLiteralExpression());

        final Decision decision2 = new Decision();
        decision2.setExpression(makeNestedPMMLFunctionDefinition(DOCUMENT_NAME_ORIGINAL, MODEL_NAME));

        final Decision decision3 = new Decision();
        decision3.setExpression(makeNestedPMMLFunctionDefinition(UNAFFECTED_DOCUMENT, UNAFFECTED_MODEL));

        drgElements.add(decision1);
        drgElements.add(decision2);
        drgElements.add(decision3);

        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        return Arrays.asList(decision1, decision2, decision3);
    }

    private List<BusinessKnowledgeModel> setupBusinessKnowledgeModelWithTopLevelFunction() {
        final List<DRGElement> drgElements = new ArrayList<>();
        final BusinessKnowledgeModel bkm1 = new BusinessKnowledgeModel();
        final FunctionDefinition bkm1Function = new FunctionDefinition();
        bkm1Function.setKind(FunctionDefinition.Kind.FEEL);
        bkm1Function.setExpression(new LiteralExpression());
        bkm1.setEncapsulatedLogic(bkm1Function);

        final BusinessKnowledgeModel bkm2 = new BusinessKnowledgeModel();
        bkm2.setEncapsulatedLogic(makeTopLevelPMMLFunctionDefinition(DOCUMENT_NAME_ORIGINAL, MODEL_NAME));

        final BusinessKnowledgeModel bkm3 = new BusinessKnowledgeModel();
        bkm3.setEncapsulatedLogic(makeTopLevelPMMLFunctionDefinition(UNAFFECTED_DOCUMENT, UNAFFECTED_MODEL));

        bkm1.getId().setValue(NODE1_UUID);
        bkm2.getId().setValue(NODE2_UUID);
        bkm3.getId().setValue(NODE3_UUID);

        drgElements.add(bkm1);
        drgElements.add(bkm2);
        drgElements.add(bkm3);

        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        return Arrays.asList(bkm1, bkm2, bkm3);
    }

    private List<BusinessKnowledgeModel> setupBusinessKnowledgeModelWithNestedFunction() {
        final List<DRGElement> drgElements = new ArrayList<>();
        final BusinessKnowledgeModel bkm1 = new BusinessKnowledgeModel();
        final FunctionDefinition bkm1Function = new FunctionDefinition();
        bkm1Function.setKind(FunctionDefinition.Kind.FEEL);
        bkm1Function.setExpression(makeNestedLiteralExpression());
        bkm1.setEncapsulatedLogic(bkm1Function);

        final BusinessKnowledgeModel bkm2 = new BusinessKnowledgeModel();
        final FunctionDefinition bkm2Function = new FunctionDefinition();
        bkm2Function.setKind(FunctionDefinition.Kind.FEEL);
        bkm2Function.setExpression(makeNestedPMMLFunctionDefinition(DOCUMENT_NAME_ORIGINAL, MODEL_NAME));
        bkm2.setEncapsulatedLogic(bkm2Function);

        final BusinessKnowledgeModel bkm3 = new BusinessKnowledgeModel();
        final FunctionDefinition bkm3Function = new FunctionDefinition();
        bkm3Function.setKind(FunctionDefinition.Kind.FEEL);
        bkm3Function.setExpression(makeNestedPMMLFunctionDefinition(UNAFFECTED_DOCUMENT, UNAFFECTED_MODEL));
        bkm3.setEncapsulatedLogic(bkm3Function);

        drgElements.add(bkm1);
        drgElements.add(bkm2);
        drgElements.add(bkm3);

        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        return Arrays.asList(bkm1, bkm2, bkm3);
    }

    private Context makeNestedLiteralExpression() {
        final Context context = new Context();
        final ContextEntry contextEntry = new ContextEntry();
        final InformationItem variable = new InformationItem();
        variable.getName().setValue("variable");
        contextEntry.setVariable(variable);
        contextEntry.setExpression(new LiteralExpression());
        context.getContextEntry().add(contextEntry);

        return context;
    }

    private Context makeNestedPMMLFunctionDefinition(final String documentName,
                                                     final String modelName) {
        final Context context = new Context();
        final ContextEntry contextEntry = new ContextEntry();
        final InformationItem variable = new InformationItem();
        variable.getName().setValue("variable");
        contextEntry.setVariable(variable);
        contextEntry.setExpression(makeTopLevelPMMLFunctionDefinition(documentName, modelName));
        context.getContextEntry().add(contextEntry);

        return context;
    }

    private FunctionDefinition makeTopLevelPMMLFunctionDefinition(final String documentName,
                                                                  final String modelName) {
        final FunctionDefinition function = new FunctionDefinition();
        function.setKind(FunctionDefinition.Kind.PMML);

        final Context functionExpression = new Context();
        function.setExpression(functionExpression);

        final ContextEntry functionContextEntry1 = new ContextEntry();
        final InformationItem functionContextEntry1Variable = new InformationItem();
        final LiteralExpressionPMMLDocument functionContextEntry1Value = new LiteralExpressionPMMLDocument();
        functionContextEntry1Variable.getName().setValue(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT);
        functionContextEntry1.setVariable(functionContextEntry1Variable);
        functionContextEntry1Value.getText().setValue("\"" + documentName + "\"");
        functionContextEntry1.setExpression(functionContextEntry1Value);

        final ContextEntry functionContextEntry2 = new ContextEntry();
        final InformationItem functionContextEntry2Variable = new InformationItem();
        final LiteralExpressionPMMLDocumentModel functionContextEntry2Value = new LiteralExpressionPMMLDocumentModel();
        functionContextEntry2Variable.getName().setValue(LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL);
        functionContextEntry2.setVariable(functionContextEntry2Variable);
        functionContextEntry2Value.getText().setValue("\"" + modelName + "\"");
        functionContextEntry2.setExpression(functionContextEntry2Value);

        functionExpression.getContextEntry().add(functionContextEntry1);
        functionExpression.getContextEntry().add(functionContextEntry2);

        return function;
    }

    private void assertTopLevelLiteralExpression(final Decision decision) {
        assertThat(decision.getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) decision.getExpression()).getText().getValue()).isEmpty();
    }

    private void assertTopLevelLiteralExpression(final BusinessKnowledgeModel bkm) {
        final FunctionDefinition function = bkm.getEncapsulatedLogic();
        assertThat(function.getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) function.getExpression()).getText().getValue()).isEmpty();
    }

    private void assertNestedLiteralExpression(final Decision decision) {
        assertThat(decision.getExpression()).isInstanceOf(Context.class);
        final Context context = (Context) decision.getExpression();
        assertThat(context.getContextEntry()).hasSize(1);
        final ContextEntry contextEntry = context.getContextEntry().get(0);
        assertThat(contextEntry.getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEmpty();
    }

    private void assertNestedLiteralExpression(final BusinessKnowledgeModel bkm) {
        final FunctionDefinition function = bkm.getEncapsulatedLogic();
        assertThat(function.getExpression()).isInstanceOf(Context.class);
        final Context context = (Context) function.getExpression();
        assertThat(context.getContextEntry()).hasSize(1);
        final ContextEntry contextEntry = context.getContextEntry().get(0);
        assertThat(contextEntry.getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEmpty();
    }

    private void assertTopLevelFunctionDefinition(final Decision decision,
                                                  final String expectedDocumentValue,
                                                  final String expectedModelValue) {
        assertThat(decision.getExpression()).isInstanceOf(FunctionDefinition.class);
        assertPMMLContextDefinition((FunctionDefinition) decision.getExpression(),
                                    expectedDocumentValue,
                                    expectedModelValue);
    }

    private void assertNestedFunctionDefinition(final Decision decision,
                                                final String expectedDocumentValue,
                                                final String expectedModelValue) {
        assertThat(decision.getExpression()).isInstanceOf(Context.class);
        final Context context = (Context) decision.getExpression();
        assertThat(context.getContextEntry()).hasSize(1);
        final ContextEntry contextEntry = context.getContextEntry().get(0);
        assertThat(contextEntry.getExpression()).isInstanceOf(FunctionDefinition.class);
        assertPMMLContextDefinition((FunctionDefinition) contextEntry.getExpression(),
                                    expectedDocumentValue,
                                    expectedModelValue);
    }

    private void assertNestedFunctionDefinition(final BusinessKnowledgeModel bkm,
                                                final String expectedDocumentValue,
                                                final String expectedModelValue) {
        final FunctionDefinition function = bkm.getEncapsulatedLogic();
        assertThat(function.getExpression()).isInstanceOf(Context.class);
        final Context context = (Context) function.getExpression();
        assertThat(context.getContextEntry()).hasSize(1);
        final ContextEntry contextEntry = context.getContextEntry().get(0);
        assertThat(contextEntry.getExpression()).isInstanceOf(FunctionDefinition.class);
        assertPMMLContextDefinition((FunctionDefinition) contextEntry.getExpression(),
                                    expectedDocumentValue,
                                    expectedModelValue);
    }

    private void assertPMMLContextDefinition(final FunctionDefinition function,
                                             final String expectedDocumentValue,
                                             final String expectedModelValue) {
        assertThat(function.getExpression()).isInstanceOf(Context.class);
        final Context context = (Context) function.getExpression();

        assertThat(context.getContextEntry().get(0).getExpression()).isInstanceOf(LiteralExpressionPMMLDocument.class);
        final LiteralExpressionPMMLDocument functionDocument = (LiteralExpressionPMMLDocument) context.getContextEntry().get(0).getExpression();
        assertThat(functionDocument.getText().getValue()).isEqualTo(wrap(expectedDocumentValue));

        assertThat(context.getContextEntry().get(1).getExpression()).isInstanceOf(LiteralExpressionPMMLDocumentModel.class);
        final LiteralExpressionPMMLDocumentModel functionDocumentModel = (LiteralExpressionPMMLDocumentModel) context.getContextEntry().get(1).getExpression();
        assertThat(functionDocumentModel.getText().getValue()).isEqualTo(wrap(expectedModelValue));
    }

    private String wrap(final String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return StringUtils.createQuotedString(value);
    }
}
