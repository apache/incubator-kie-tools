package org.dashbuilder.client.widgets.dataset.editor.bean;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.map.MapEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BeanDataSetDefAttributesEditorTest {

    @Mock ValueBoxEditor<String> generatorClass;
    @Mock MapEditor paramaterMap;
    @Mock BeanDataSetDefAttributesEditor.View view;
    
    private BeanDataSetDefAttributesEditor presenter;
    
    
    @Before
    public void setup() {
        presenter = new BeanDataSetDefAttributesEditor(generatorClass, paramaterMap, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(generatorClass, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
    }
    
    @Test
    public void testGeneratorClass() {
        assertEquals(generatorClass, presenter.generatorClass());
    }

    @Test
    public void testParameterMap() {
        assertEquals(paramaterMap, presenter.paramaterMap());
    }

}
