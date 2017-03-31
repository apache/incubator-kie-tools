package org.kie.workbench.common.stunner.bpmn.forms.service.adf.processing.processors.fields;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldType.MAX_PARAM;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssigneeFieldInitializerTest {

    private static final String MAX = "5";

    protected AssigneeEditorFieldDefinition field;

    protected AssigneeFieldInitializer initializer;

    @Mock
    protected FieldElement fieldElement;

    @Mock
    protected FormGenerationContext context;

    protected Map<String, String> fieldElementParams = new HashMap<>();

    @Before
    public void init() {
        initializer = new AssigneeFieldInitializer();
        field = new AssigneeEditorFieldDefinition();
        field = spy(field);
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
    }

    @Test
    public void testInitializeWithParams() {
        fieldElementParams.clear();
        fieldElementParams.put(MAX_PARAM,
                               MAX);

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setMax(any());

        assertEquals(Integer.valueOf(MAX),
                     field.getMax());
    }

    @Test
    public void testInitializeWithoutParams() {
        fieldElementParams.clear();
        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setMax(any());

        assertEquals(Integer.valueOf(-1),
                     field.getMax());
    }
}