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


package org.kie.workbench.common.stunner.client.lienzo.canvas.export;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport.URLDataType.JPG;
import static org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport.URLDataType.PNG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasExportTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private WiresLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context2D;

    private LienzoCanvasExport tested;

    @Mock
    private LienzoCanvasExport.BoundsProvider boundsProvider;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    private final String DEF_SET_ID = "DEF_SET_ID";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private TypeDefinitionSetRegistry definitionSets;

    @Mock
    private Object defSet;

    @Mock
    private AdapterManager adapters;

    @Mock
    private DefinitionSetAdapter<Object> definitionSetAdapter;

    @Mock
    private HTMLDivElement layerElement;

    @Before
    public void setup() {
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(layerElement);
        when(layer.uuid()).thenReturn("someLayer");
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(layer.getWidth()).thenReturn(100);
        when(layer.getHeight()).thenReturn(200);
        when(boundsProvider.compute(eq(lienzoLayer), any(CanvasExportSettings.class))).thenReturn(new int[]{0, 0, 100, 200});
        when(scratchPad.getContext()).thenReturn(context2D);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(definitionSets);
        when(definitionSets.getDefinitionSetById(DEF_SET_ID)).thenReturn(defSet);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getSvgNodeId(defSet)).thenReturn(Optional.of("id"));

        this.tested = new LienzoCanvasExport(boundsProvider);
    }

    @Test
    public void testToJpgImageData() {
        tested.toImageData(canvasHandler,
                           CanvasURLExportSettings.build(JPG));
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.JPG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }

    @Test
    public void testToPngImageData() {
        tested.toImageData(canvasHandler,
                           CanvasURLExportSettings.build(PNG));
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.PNG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }

    @Test
    public void testWiresLayerBoundsProviderEmpty() {
        spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        WiresManager.get(layer);
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build());
        assertEquals(0, size0[0]);
        assertEquals(0, size0[1]);
        assertEquals(25, size0[2]);
        assertEquals(25, size0[3]);
    }

    @Test
    public void testWiresLayerBoundsProvider() {
        final WiresShape shape1 = spy(new WiresShape(new MultiPath().rect(0, 0, 50, 50)).setLocation(new Point2D(12, 44)));
        final WiresShape shape2 = spy(new WiresShape(new MultiPath().rect(0, 0, 100, 150)).setLocation(new Point2D(1, 3)));
        doNothing().when(shape1).shapeMoved();
        doNothing().when(shape2).shapeMoved();
        layer = spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        WiresManager wiresManager = WiresManager.get(layer);
        com.ait.lienzo.client.core.shape.wires.WiresLayer wiresLayer = wiresManager.getLayer();
        wiresLayer.add(shape1);
        wiresLayer.add(shape2);
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build());
        assertEquals(0, size0[0]);
        assertEquals(0, size0[1]);
        assertEquals(151, size0[2]);
        assertEquals(203, size0[3]);
    }

    @Test
    public void testWiresLayerBoundsProviderWithSize() {
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build(11, 33));
        assertEquals(0, size0[0]);
        assertEquals(0, size0[1]);
        assertEquals(36, size0[2]);
        assertEquals(58, size0[3]);
    }

    @Test
    public void testToContext2D() {
        Transform transform = new Transform().translate(11, 33).scaleWithXY(0.1, 0.3);
        when(viewport.getTransform()).thenReturn(transform);
        IContext2D iContext2D = tested.toContext2D(canvasHandler, CanvasExportSettings.build());
        assertNotNull(iContext2D);
        verify(layer, times(1)).draw(any(Context2D.class));
        ArgumentCaptor<Transform> transformArgumentCaptor = ArgumentCaptor.forClass(Transform.class);
        verify(viewport, times(2)).setTransform(transformArgumentCaptor.capture());
        verify(layer).draw();
        List<Transform> transforms = transformArgumentCaptor.getAllValues();
        Transform t0 = transforms.get(0);
        Transform t1 = transforms.get(1);
        assertEquals(0d, t0.getTranslateX(), 0d);
        assertEquals(0d, t0.getTranslateY(), 0d);
        assertEquals(1d, t0.getScaleX(), 0d);
        assertEquals(1d, t0.getScaleY(), 0d);
        assertEquals(11d, t1.getTranslateX(), 0d);
        assertEquals(33d, t1.getTranslateY(), 0d);
        assertEquals(0.1d, t1.getScaleX(), 0d);
        assertEquals(0.3d, t1.getScaleY(), 0d);
    }
}
