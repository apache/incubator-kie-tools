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

package org.kie.workbench.common.dmn.project.client.validation;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.validation.DMNDomainValidator;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNClientDiagramValidatorTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private TreeWalkTraverseProcessor treeWalkTraverseProcessor;

    @Mock
    private ModelValidator modelValidator;

    @Mock
    private ManagedInstance<DomainValidator> validators;

    @Mock
    private Diagram diagram;

    @Mock
    private Consumer<Collection<DiagramElementViolation<RuleViolation>>> violationsConsumer;

    @Mock
    private Collection<DiagramElementViolation<RuleViolation>> violations;

    @Mock
    private ServiceCallback<String> diagramXmlServiceCallback;

    @Mock
    private CallerMock<DMNDomainValidator> dmnDomainValidatorCaller;

    @Mock
    private DMNDomainValidator dmnDomainValidator;

    @Mock
    private DMNMarshallerService dmnMarshallerService;

    @Mock
    private RemoteCallback<Collection<DomainViolation>> callback;

    @Mock
    private ErrorCallback<Object> errorCallback;

    private DMNClientDiagramValidator validator;

    @Before
    public void setup() {
        when(dmnDomainValidatorCaller.call(any(RemoteCallback.class), any(ErrorCallback.class))).thenReturn(dmnDomainValidator);
        validator = spy(new DMNClientDiagramValidator(definitionManager, ruleManager, treeWalkTraverseProcessor, modelValidator, validators, dmnDomainValidatorCaller, dmnMarshallerService));
    }

    @Test
    public void testValidate() {

        final Consumer<Collection<DiagramElementViolation<RuleViolation>>> consumer = (e) -> {/* Nothing. */};

        doReturn(consumer).when(validator).getCollectionConsumer(diagram, violationsConsumer);
        doNothing().when(validator).superValidate(any(), any());

        validator.validate(diagram, violationsConsumer);

        verify(validator).superValidate(diagram, consumer);
    }

    @Test
    public void testGetCollectionConsumer() {

        doReturn(diagramXmlServiceCallback).when(validator).getContentServiceCallback(diagram, violationsConsumer, violations);

        validator.getCollectionConsumer(diagram, violationsConsumer).accept(violations);

        verify(dmnMarshallerService).marshall(diagram, diagramXmlServiceCallback);
    }

    @Test
    public void testGetContentServiceCallbackOnSuccess() {

        final String diagramXml = "<xml />";

        doReturn(callback).when(validator).onValidatorSuccess(violations, violationsConsumer);
        doReturn(errorCallback).when(validator).onValidatorError();

        validator.getContentServiceCallback(diagram, violationsConsumer, violations).onSuccess(diagramXml);

        verify(dmnDomainValidatorCaller).call(callback, errorCallback);
        verify(dmnDomainValidator).validate(diagram, diagramXml);
    }

    @Test
    public void testOnValidatorSuccess() {

        final ElementViolationImpl elementViolation = new ElementViolationImpl.Builder().build();
        final MarshallingMessage marshallingMessage = MarshallingMessage.builder().build();

        final Collection<DiagramElementViolation<RuleViolation>> diagramElementViolations = singletonList(elementViolation);
        final Collection<DomainViolation> response = singletonList(marshallingMessage);

        final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer = (collection) -> {
            assertEquals(1, collection.size());
        };

        validator.onValidatorSuccess(diagramElementViolations, resultConsumer).callback(response);
    }

    @Test
    public void testOnValidatorError() {
        doNothing().when(validator).logError(Mockito.<String>any());

        validator.onValidatorError().error(null, null);

        verify(validator).logError("Validation service error");
    }

    @Test
    public void testGetContentServiceCallbackError() {

        final ClientRuntimeError error = mock(ClientRuntimeError.class);

        doNothing().when(validator).logError(Mockito.<String>any());

        validator.getContentServiceCallback(diagram, violationsConsumer, violations).onError(error);

        verify(dmnDomainValidator, never()).validate(any(), Mockito.<String>any());
        verify(validator).logError("Marshaller service error");
    }
}
