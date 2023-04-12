package org.dashbuilder.displayer.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import static org.dashbuilder.displayer.DisplayerSubType.AREA;
import static org.dashbuilder.displayer.DisplayerSubType.BAR;
import static org.dashbuilder.displayer.DisplayerSubType.BAR_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.LINE;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_MARKERS;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_REGIONS;
import static org.dashbuilder.displayer.DisplayerSubType.SMOOTH;
import static org.dashbuilder.displayer.DisplayerType.AREACHART;
import static org.dashbuilder.displayer.DisplayerType.BARCHART;
import static org.dashbuilder.displayer.DisplayerType.LINECHART;
import static org.dashbuilder.displayer.DisplayerType.MAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RendererManagerTest {

    private static final String REND2_NAME = "renderer 2";

    private static final String REND1_NAME = "renderer 1";

    private static final String REND2_UUID = "rend2";

    private static final String REND1_UUID = "rend1";
    
    private static final String DEFAULT_REND_FOR_BARCHART_UUID = REND1_UUID;

    @Mock
    SyncBeanManager beanManager;
    
    @Mock
    DisplayerSettings displayerSettings;
    
    RendererManager rendererManager;
    
    int totalBeans;
    
    
    @Before
    public void setUp() {
        rendererManager = new RendererManager(beanManager);
        mockConstants();
        Map<DisplayerType, List<DisplayerSubType>> typesAndSubTypes1 = new HashMap<>();
        typesAndSubTypes1.put(BARCHART, Arrays.asList(BAR));
        typesAndSubTypes1.put(AREACHART, Arrays.asList(AREA));
        typesAndSubTypes1.put(LINECHART, Arrays.asList(LINE));
        
        Map<DisplayerType, List<DisplayerSubType>> typesAndSubTypes2 = new HashMap<>();
        typesAndSubTypes2.put(LINECHART, Arrays.asList(LINE, SMOOTH));
        typesAndSubTypes2.put(BARCHART, Arrays.asList(BAR, BAR_STACKED));
        
        List<SyncBeanDef<RendererLibrary>> rendererLibrariesBeans = Arrays.asList(
                mockSyncBeanForRendererLib(REND1_NAME, REND1_UUID, Arrays.asList(BARCHART, AREACHART), typesAndSubTypes1, true),
                mockSyncBeanForRendererLib(REND2_NAME, REND2_UUID,  Collections.emptyList(), typesAndSubTypes2, false)
        );
        totalBeans = rendererLibrariesBeans.size();
        when(beanManager.lookupBeans(RendererLibrary.class))
                        .thenReturn(rendererLibrariesBeans);
        rendererManager.init();
    }

    @Test
    public void renderersForType2Test() {
        List<RendererLibrary> renderersForType = rendererManager.getRenderersForType(null, null);
        assertEquals(totalBeans, renderersForType.size());
        renderersForType = rendererManager.getRenderersForType(BARCHART, null);
        assertEquals(2, renderersForType.size());
        renderersForType = rendererManager.getRenderersForType(AREACHART, AREA);
        assertEquals(1, renderersForType.size());
        renderersForType = rendererManager.getRenderersForType(BARCHART, BAR);
        assertEquals(2, renderersForType.size());
        renderersForType = rendererManager.getRenderersForType(MAP, null);
        assertEquals(0, renderersForType.size());
        renderersForType = rendererManager.getRenderersForType(MAP, MAP_REGIONS);
        assertEquals(0, renderersForType.size());
    }
    
    @Test
    public void renderersForType2SubTypesTest() {
        List<RendererLibrary> renderersForSubType = rendererManager.getRenderersForType(null, AREA);
        assertEquals(1, renderersForSubType.size());
        renderersForSubType = rendererManager.getRenderersForType(null, LINE);
        assertEquals(2, renderersForSubType.size());
        renderersForSubType = rendererManager.getRenderersForType(null, MAP_REGIONS);
        assertEquals(0, renderersForSubType.size());        
        
    }

    @Test
    public void rendererForTypeTest() {
        List<RendererLibrary> renderers = rendererManager.getRenderers();
        assertEquals(totalBeans, renderers.size());
        assertEquals(2, rendererManager.getRenderersForType(BARCHART).size());
        assertEquals(1, rendererManager.getRenderersForType(AREACHART).size());
    }
    
    @Test
    public void rendererForSubTypeTest() {
        assertEquals(2, rendererManager.getRenderersForType(BARCHART).size());
        assertEquals(1, rendererManager.getRenderersForType(AREACHART).size());
    }
     
    @Test
    public void rendererByNameTest() {
        assertNotNull(rendererManager.getRendererByName(REND1_NAME));
    }
    
    @Test(expected=RuntimeException.class)
    public void rendererByNameNotFoundTest() {
        assertNotNull(rendererManager.getRendererByName("NO NAME"));
    }
    
    @Test
    public void rendererByUUIDTest() {
        assertNotNull(rendererManager.getRendererByUUID(REND1_UUID));
    }
    
    @Test(expected=RuntimeException.class)
    public void rendererByUUIDNotFoundTest() {
        assertNotNull(rendererManager.getRendererByUUID("NO UUID"));
    }
    
    @Test
    public void typeSupportedTest() {
        assertTrue(rendererManager.isTypeSupported(BARCHART));
        assertTrue(!rendererManager.isTypeSupported(MAP));
    }
    
    
    @Test
    public void rendererByDisplayerTest() {
        DisplayerSettings settings = mock(DisplayerSettings.class);
        when(settings.getRenderer()).thenReturn(REND1_UUID);
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(BAR);
        RendererLibrary lib = rendererManager.getRendererForDisplayer(settings);
        assertNotNull(lib);
        assertEquals(REND1_UUID, lib.getUUID());
    }
    
    @Test
    public void rendererByDisplayerWithoutUUIDTest() {
        DisplayerSettings settings = mock(DisplayerSettings.class);
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(BAR);
        RendererLibrary lib = rendererManager.getRendererForDisplayer(settings);
        assertNotNull(lib);
        assertEquals(DEFAULT_REND_FOR_BARCHART_UUID, lib.getUUID());
    }
    
    @Test(expected=RuntimeException.class)
    public void rendererByDisplayerTypeNotSupportedTest() {
        DisplayerSettings settings = mock(DisplayerSettings.class);
        when(settings.getType()).thenReturn(MAP);
        when(settings.getSubtype()).thenReturn(MAP_MARKERS);
        rendererManager.getRendererForDisplayer(settings);
    }
    
    @Test
    public void defaultRendererTest() {
        assertNotNull(rendererManager.getDefaultRenderer(BARCHART));
        assertNull(rendererManager.getDefaultRenderer(LINECHART));
    }
    
    
    @Test
    public void defaultRendererWithUserBadSettingTest() {
        rendererManager.init();
        defaultRendererTest();
    }
    
    private SyncBeanDef<RendererLibrary> mockSyncBeanForRendererLib(String name,
                                                                    String uuid,
                                                                    List<DisplayerType> defaultTypes,
                                                                    Map<DisplayerType, List<DisplayerSubType>> typesAndSubTypes,
                                                                    boolean offline) {
        SyncBeanDef<RendererLibrary> libBean = mock(SyncBeanDef.class);
        RendererLibrary lib = mock(RendererLibrary.class);
        when(lib.getName()).thenReturn(name);
        when(lib.getUUID()).thenReturn(uuid);
        when(lib.isOffline()).thenReturn(offline);
        List<DisplayerType> supportedTypesList = typesAndSubTypes.keySet().stream().collect(Collectors.toList());
        when(lib.getSupportedTypes()).thenReturn(supportedTypesList);
        typesAndSubTypes.forEach((type, subTypes) -> when(lib.getSupportedSubtypes(type)).thenReturn(subTypes));
        defaultTypes.forEach(type -> when(lib.isDefault(type)).thenReturn(true));
        when(libBean.getInstance()).thenReturn(lib);
        return libBean;
    }    
  
    
    private void mockConstants() {
        rendererManager.i18n = mock(CommonConstants.class,
                                    (Answer) invocation -> {
                                            return RETURNS_DEFAULTS.answer(invocation);
                                    });
    }
    
}
