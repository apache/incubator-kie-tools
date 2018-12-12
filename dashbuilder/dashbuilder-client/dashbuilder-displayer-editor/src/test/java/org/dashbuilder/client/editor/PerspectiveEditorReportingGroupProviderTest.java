package org.dashbuilder.client.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class PerspectiveEditorReportingGroupProviderTest {

    @Mock
    RendererManager rendererManager;
    
    @Mock
    SyncBeanManager beanManager;
    
    @InjectMocks
    PerspectiveEditorReportingGroupProvider perspectiveEditorReportingGroupProvider;
    
    @Mock
    RendererLibrary rendererLibrary;
    
    @Mock
    SyncBeanDef<DisplayerDragComponent> displayerDragComponentBeanDef;
    
    @Mock DisplayerDragComponent displayerDragComponent;
    
    @Before
    public void setUp() {
        when(beanManager.lookupBean(any(DisplayerDragComponent.class.getClass())))
                        .thenReturn(displayerDragComponentBeanDef);
        when(displayerDragComponentBeanDef.getInstance()).thenReturn(displayerDragComponent);
    }
    
    @Test
    public void groupShouldBeEmptyWhenTheresNoRendererTest() {
        LayoutDragComponentGroup componentGroup = perspectiveEditorReportingGroupProvider.getComponentGroup();
        assertTrue(componentGroup.getLayoutDragComponentIds().isEmpty());
    }
    
    @Test
    public void groupShouldHaveNComponentWhenTheresRendererTest() {
        when(rendererManager.isTypeSupported(DisplayerType.BARCHART)).thenReturn(true);
        when(rendererManager.isTypeSupported(DisplayerType.LINECHART)).thenReturn(true);
        LayoutDragComponentGroup componentGroup = perspectiveEditorReportingGroupProvider.getComponentGroup();
        assertEquals(componentGroup.getLayoutDragComponentIds().size(), 2);
    }
    
    
}
