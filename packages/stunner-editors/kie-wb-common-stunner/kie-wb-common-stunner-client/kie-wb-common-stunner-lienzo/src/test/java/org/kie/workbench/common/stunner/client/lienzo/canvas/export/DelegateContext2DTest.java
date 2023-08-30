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

import java.util.HashMap;
import java.util.Optional;

import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.CompositeOperation;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.GWT;
import elemental2.dom.Element;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.Path2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegateContext2DTest {

    public static final String NODE_UUID = UUID.uuid();

    @Mock
    private DelegateContext2D delegateContext2D;

    @Mock
    private IContext2D context;

    @Mock
    private DelegateContext2D.Converter nativeClassConverter;

    private LinearGradient.LinearGradientJSO linearGradientJSO;

    private PatternGradient.PatternGradientJSO patternGradientJSO;

    private RadialGradient.RadialGradientJSO radialGradientJSO;

    private HTMLCanvasElement htmlElement;

    private HTMLCanvasElement element;

    @Mock
    private AbstractCanvasHandler canvasHandler;

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
    private Index graphIndex;

    @Mock
    private org.kie.workbench.common.stunner.core.graph.Element node;

    private final String SVG_NODE_ID = "node_id";

    @Before
    public void setUp() throws Exception {
        htmlElement = new HTMLCanvasElement();
        element = GWT.create(HTMLCanvasElement.class);
        when(nativeClassConverter.convert(any(Element.class), eq(HTMLCanvasElement.class)))
                .thenReturn(htmlElement);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(definitionSets);
        when(definitionSets.getDefinitionSetById(DEF_SET_ID)).thenReturn(defSet);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getSvgNodeId(defSet)).thenReturn(Optional.of(SVG_NODE_ID));
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(NODE_UUID)).thenReturn(node);

        delegateContext2D.canvasHandler = canvasHandler;
        delegateContext2D.context = context;
        delegateContext2D.nativeClassConverter = nativeClassConverter;
    }

    @Test
    public void testSaveContainer() {
        doCallRealMethod().when(delegateContext2D).saveContainer(NODE_UUID);
        delegateContext2D.saveContainer(NODE_UUID);
        verify(context, times(1)).saveGroup(new HashMap<String, String>() {{
            put(null, NODE_UUID);
            put(delegateContext2D.DEFAULT_NODE_ID, NODE_UUID);
        }});
    }

    @Test
    public void testRestoreContainer() {
        doCallRealMethod().when(delegateContext2D).restoreContainer();
        delegateContext2D.restoreContainer();
        verify(context, times(1)).restoreGroup();
    }

    @Test
    public void testSave() {
        doCallRealMethod().when(delegateContext2D).save();
        delegateContext2D.save();
        verify(context, times(1)).saveStyle();
    }

    @Test
    public void testRestore() {
        doCallRealMethod().when(delegateContext2D).restore();
        delegateContext2D.restore();
        verify(context, times(1)).restoreStyle();
    }

    @Test
    public void testbeginPath() {
        doCallRealMethod().when(delegateContext2D).beginPath();
        delegateContext2D.beginPath();
        verify(context, times(1)).beginPath();
    }

    @Test
    public void testClosePath() {
        doCallRealMethod().when(delegateContext2D).closePath();
        delegateContext2D.closePath();
        verify(context, times(1)).closePath();
    }

    @Test
    public void testMoveTo() {
        doCallRealMethod().when(delegateContext2D).moveTo(anyDouble(), anyDouble());
        delegateContext2D.moveTo(1d, 1d);
        verify(context, times(1)).moveTo(1d, 1d);
    }

    @Test
    public void testLineTo() {
        doCallRealMethod().when(delegateContext2D).lineTo(anyDouble(), anyDouble());
        delegateContext2D.lineTo(1d, 1d);
        verify(context, times(1)).lineTo(1d ,1d);
    }

    @Test
    public void testSetGlobalCompositeOperation() {
        doCallRealMethod().when(delegateContext2D).setGlobalCompositeOperation(any());
        delegateContext2D.setGlobalCompositeOperation(CompositeOperation.SOURCE_IN);
        verify(context, times(1))
                .setGlobalCompositeOperation(CompositeOperation.SOURCE_IN.getValue());
    }

    @Test
    public void testSetLineCap() {
        doCallRealMethod().when(delegateContext2D).setLineCap(any());
        delegateContext2D.setLineCap(LineCap.ROUND);
        verify(context, times(1)).setLineCap(LineCap.ROUND.getValue());
    }

    @Test
    public void testSetLineJoin() {
        doCallRealMethod().when(delegateContext2D).setLineJoin(any());
        delegateContext2D.setLineJoin(LineJoin.ROUND);
        verify(context, times(1)).setLineJoin(LineJoin.ROUND.getValue());
    }

    @Test
    public void testQuadraticCurveTo() {
        doCallRealMethod().when(delegateContext2D).quadraticCurveTo(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        delegateContext2D.quadraticCurveTo(1d, 1d, 1d, 1d);
        verify(context, times(1)).quadraticCurveTo(1d, 1d, 1d, 1d);
    }

    @Test
    public void testArc() {
        doCallRealMethod().when(delegateContext2D).arc(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
        delegateContext2D.arc(1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).arc(1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testArc1() {
        doCallRealMethod().when(delegateContext2D).arc(anyDouble(),
                                                       anyDouble(),
                                                       anyDouble(),
                                                       anyDouble(),
                                                       anyDouble(),
                                                       anyBoolean());
        delegateContext2D.arc(1d, 1d, 1d, 1d, 1d, true);
        verify(context, times(1))
                .arc(1d, 1d, 1d, 1d, 1d, true);
    }

    @Test
    public void testEllipse() {
        doCallRealMethod().when(delegateContext2D).ellipse(anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble());
        delegateContext2D.ellipse(1d, 1d, 1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).ellipse(1d, 1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testEllipse1() {
        doCallRealMethod().when(delegateContext2D).ellipse(anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyDouble(),
                                                           anyBoolean());
        delegateContext2D.ellipse(1d, 1d, 1d, 1d, 1d, 1d, 1d, true);
        verify(context, times(1)).ellipse(1d, 1d, 1d, 1d, 1d, 1d, 1d, true);
    }

    @Test
    public void testArcTo() {
        doCallRealMethod().when(delegateContext2D).arcTo(anyDouble(),
                                                         anyDouble(),
                                                         anyDouble(),
                                                         anyDouble(),
                                                         anyDouble());
        delegateContext2D.arcTo(1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).arcTo(1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testBezierCurveTo() {
        doCallRealMethod().when(delegateContext2D).bezierCurveTo(anyDouble(),
                                                                 anyDouble(),
                                                                 anyDouble(),
                                                                 anyDouble(),
                                                                 anyDouble(),
                                                                 anyDouble());
        delegateContext2D.bezierCurveTo(1d, 1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).bezierCurveTo(1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testClearRect() {
        doCallRealMethod().when(delegateContext2D).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        delegateContext2D.clearRect(1d, 1d, 1d, 1d);
        verify(context, times(1)).clearRect(1d, 1d, 1d, 1d);
    }

    @Test
    public void testClip() {
        doCallRealMethod().when(delegateContext2D).clip();
        delegateContext2D.clip();
        verify(context, times(1)).clip();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClip2() {
        final Path2D path = null;
        doCallRealMethod().when(delegateContext2D).clip(path);
        delegateContext2D.clip(path);
    }

    @Test
    public void testFill() {
        doCallRealMethod().when(delegateContext2D).fill();
        delegateContext2D.fill();
        verify(context, times(1)).fill();
    }

    @Test
    public void testStroke() {
        doCallRealMethod().when(delegateContext2D).stroke();
        delegateContext2D.stroke();
        verify(context, times(1)).stroke();
    }

    @Test
    public void testFillRect() {
        doCallRealMethod().when(delegateContext2D).fillRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        delegateContext2D.fillRect(1d, 1d, 1d, 1d);
        verify(context, times(1)).fillRect(1d, 1d, 1d, 1d);
    }

    @Test
    public void testFillText() {
        doCallRealMethod().when(delegateContext2D).fillText(anyString(), anyDouble(), anyDouble());
        delegateContext2D.fillText("text", 1d, 1d);
        verify(context, times(1)).fillText("text", 1d, 1d);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFillTextWithGradient() {
        doCallRealMethod().when(delegateContext2D).fillTextWithGradient(anyString(),
                                                                        anyDouble(),
                                                                        anyDouble(),
                                                                        anyDouble(),
                                                                        anyDouble(),
                                                                        anyDouble(),
                                                                        anyDouble(),
                                                                        anyString());
        delegateContext2D.fillTextWithGradient("text", 1d, 1d, 1d, 1d, 1d, 1d, "black");
    }

    @Test
    public void testFillText1() {
        doCallRealMethod().when(delegateContext2D).fillText(anyString(), anyDouble(), anyDouble());
        delegateContext2D.fillText("text", 1d, 1d);
        verify(context, times(1)).fillText("text", 1d, 1d);
    }

    @Test
    public void testSetFillColor() {
        doCallRealMethod().when(delegateContext2D).setFillColor(anyString());
        delegateContext2D.setFillColor("black");
        verify(context, times(1)).setFillStyle("black");
    }

    @Test
    public void testRect() {
        doCallRealMethod().when(delegateContext2D).rect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        delegateContext2D.rect(1d, 1d, 1d, 1d);
        verify(context, times(1)).rect(1d, 1d, 1d, 1d);
    }

    @Test
    public void testRotate() {
        doCallRealMethod().when(delegateContext2D).rotate(anyDouble());
        delegateContext2D.rotate(1d);
        verify(context, times(1)).rotate(1d);
    }

    @Test
    public void testScale() {
        doCallRealMethod().when(delegateContext2D).scale(anyDouble(), anyDouble());
        delegateContext2D.scale(1d, 1d);
        verify(context, times(1)).scale(1d, 1d);
    }

    @Test
    public void testSetStrokeColor() {
        doCallRealMethod().when(delegateContext2D).setStrokeColor(anyString());
        delegateContext2D.setStrokeColor("black");
        verify(context, times(1)).setStrokeStyle("black");
    }

    @Test
    public void testSetStrokeWidth() {
        doCallRealMethod().when(delegateContext2D).setStrokeWidth(anyDouble());
        delegateContext2D.setStrokeWidth(1d);
        verify(context, times(1)).setLineWidth(1d);
    }

    @Test
    public void testSetFillGradientLinear() {
        final String color = null;
        doCallRealMethod().when(delegateContext2D).setFillGradient(any(LinearGradient.class));
        doCallRealMethod().when(delegateContext2D).setFillColor(eq(color));
        delegateContext2D.setFillGradient(new LinearGradient(linearGradientJSO));
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void testSetFillGradientRadial() {
        final String color = null;
        doCallRealMethod().when(delegateContext2D).setFillGradient(any(RadialGradient.class));
        doCallRealMethod().when(delegateContext2D).setFillColor(eq(color));
        delegateContext2D.setFillGradient(new RadialGradient(radialGradientJSO));
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void testSetFillGradientPattern() {
        final String color = null;
        doCallRealMethod().when(delegateContext2D).setFillGradient(any(PatternGradient.class));
        doCallRealMethod().when(delegateContext2D).setFillColor(color);
        delegateContext2D.setFillGradient(new PatternGradient(patternGradientJSO));
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void testTransform() {
        doCallRealMethod().when(delegateContext2D).transform(anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble());
        delegateContext2D.transform(1d, 1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).transform(1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testTransform1() {
        doCallRealMethod().when(delegateContext2D).transform(any(Transform.class));
        doCallRealMethod().when(delegateContext2D).transform(anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble(),
                                                             anyDouble());
        delegateContext2D.transform(Transform.makeFromArray(new double[]{1d, 1d, 1d, 1d, 1d, 1d}));
        verify(context, times(1)).transform(1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testSetTransform() {
        doCallRealMethod().when(delegateContext2D).setTransform(any(Transform.class));
        doCallRealMethod().when(delegateContext2D).setTransform(anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble());
        delegateContext2D.setTransform(Transform.makeFromArray(new double[]{1d, 1d, 1d, 1d, 1d, 1d}));
        verify(context, times(1)).setTransform(1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testSetTransform1() {
        doCallRealMethod().when(delegateContext2D).setTransform(anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble());
        delegateContext2D.setTransform(1d, 1d, 1d, 1d, 1d, 1d);
        verify(context, times(1)).setTransform(1d, 1d, 1d, 1d, 1d, 1d);
    }

    @Test
    public void testSetToIdentityTransform() {
        doCallRealMethod().when(delegateContext2D).setToIdentityTransform();
        doCallRealMethod().when(delegateContext2D).setTransform(anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble(),
                                                                anyDouble());
        delegateContext2D.setToIdentityTransform();
        verify(context, times(1)).setTransform(1d, 0d, 0d, 1d, 0d, 0d);
    }

    @Test
    public void testSetTextFont() {
        doCallRealMethod().when(delegateContext2D).setTextFont(anyString());
        delegateContext2D.setTextFont("arial");
        verify(context, times(1)).setFont("arial");
    }

    @Test
    public void testSetTextBaseline() {
        doCallRealMethod().when(delegateContext2D).setTextBaseline(TextBaseLine.BOTTOM);
        delegateContext2D.setTextBaseline(TextBaseLine.BOTTOM);
        verify(context, times(1)).setTextBaseline(TextBaseLine.BOTTOM.getValue());
    }

    @Test
    public void testSetTextAlign() {
        doCallRealMethod().when(delegateContext2D).setTextAlign(TextAlign.LEFT);
        delegateContext2D.setTextAlign(TextAlign.LEFT);
        verify(context, times(1)).setTextAlign(TextAlign.LEFT.getValue());
    }

    @Test
    public void testStrokeText() {
        doCallRealMethod().when(delegateContext2D).strokeText(anyString(), anyDouble(), anyDouble());
        delegateContext2D.strokeText("text", 1d, 1d);
        verify(context, times(1)).strokeText("text", 1d, 1d);
    }

    @Test
    public void testSetGlobalAlpha() {
        doCallRealMethod().when(delegateContext2D).setGlobalAlpha(anyDouble());
        delegateContext2D.setGlobalAlpha(1d);
        verify(context, times(1)).setGlobalAlpha(1d);
    }

    @Test
    public void testTranslate() {
        doCallRealMethod().when(delegateContext2D).translate(anyDouble(), anyDouble());
        delegateContext2D.translate(1d, 1d);
        verify(context, times(1)).translate(1d, 1d);
    }

    @Test
    public void testSetShadow() {
        doCallRealMethod().when(delegateContext2D).setShadow(null);
        delegateContext2D.setShadow(null);
        verify(context).setShadowColor(Mockito.anyString());
        verify(context).setShadowOffsetX(Mockito.anyDouble());
        verify(context).setShadowOffsetY(Mockito.anyDouble());
        verify(context).setShadowBlur(Mockito.anyInt());

        delegateContext2D.setShadow(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsSupported() {
        doCallRealMethod().when(delegateContext2D).isSupported(anyString());
        delegateContext2D.isSupported("feature");
    }

    @Test
    public void testIsPointInPath() {
        doCallRealMethod().when(delegateContext2D).isPointInPath(anyDouble(), anyDouble());
        delegateContext2D.isPointInPath(1d, 1d);
        verify(context, times(1)).isPointInPath(1d, 1d);
    }

    @Test
    public void testGetImageData() {
        doCallRealMethod().when(delegateContext2D).getImageData(anyInt(), anyInt(), anyInt(), anyInt());
        delegateContext2D.getImageData(1, 1, 1, 1);
        verify(context, times(1)).getImageData(1, 1, 1, 1);
    }

    @Test
    public void testCreateImageData() {
        doCallRealMethod().when(delegateContext2D).createImageData(null);
        delegateContext2D.createImageData(null);
        verify(context, times(1)).createImageData(null);
    }

    @Test
    public void testCreateImageData1() {
        doCallRealMethod().when(delegateContext2D).createImageData(anyInt(), anyInt());
        delegateContext2D.createImageData(1, 1);
        verify(context, times(1)).createImageData(1, 1);
    }

    @Test
    public void testPutImageData() {
        doCallRealMethod().when(delegateContext2D).putImageData(null, 1, 1);
        delegateContext2D.putImageData(null, 1, 1);
        verify(context, times(1)).putImageData(null, 1, 1);
    }

    @Test
    public void testPutImageData1() {
        doCallRealMethod().when(delegateContext2D).putImageData(null, 1, 1, 1, 1, 1, 1);
        delegateContext2D.putImageData(null, 1, 1, 1, 1, 1, 1);
        verify(context, times(1)).putImageData(null, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void testMeasureText() {
        doCallRealMethod().when(delegateContext2D).measureText(anyString());
        delegateContext2D.measureText("text");
        verify(context, times(1)).measureText("text");
    }

    @Test
    public void testResetClip() {
        doCallRealMethod().when(delegateContext2D).resetClip();
        delegateContext2D.resetClip();
        verify(context, times(1)).resetClip();
    }

    @Test
    public void testSetMiterLimit() {
        doCallRealMethod().when(delegateContext2D).setMiterLimit(anyDouble());
        delegateContext2D.setMiterLimit(1d);
        verify(context, times(1)).setMiterLimit(1d);
    }

    @Test
    public void testSetLineDash() {
        doCallRealMethod().when(delegateContext2D).setLineDash(any(DashArray.class));
        delegateContext2D.setLineDash(new DashArray(new double[]{1, 1}));
        verify(context, times(1)).setLineDash(new double[]{1, 1});
    }

    @Test
    public void testSetLineDashOffset() {
        doCallRealMethod().when(delegateContext2D).setLineDashOffset(anyDouble());
        delegateContext2D.setLineDashOffset(1d);
        verify(context, times(1)).setLineDashOffset(1d);
    }

    @Test
    public void testGetBackingStorePixelRatio() {
        doCallRealMethod().when(delegateContext2D).getBackingStorePixelRatio();
        assertEquals(delegateContext2D.getBackingStorePixelRatio(), 1, 0);
    }

    @Test
    public void testDrawImage() {
        doCallRealMethod().when(delegateContext2D).drawImage(element, 1, 1);
        delegateContext2D.drawImage(element, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1);
    }

    @Test
    public void testDrawImage2() {
        doCallRealMethod().when(delegateContext2D).drawImage(element, 1, 1, 1, 1);
        delegateContext2D.drawImage(element, 1, 1, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1, 1, 1);
    }

    @Test
    public void testDrawImage3() {
        doCallRealMethod().when(delegateContext2D).drawImage(element, 1, 1, 1, 1, 1, 1, 1, 1);
        delegateContext2D.drawImage(element, 1, 1, 1, 1, 1, 1, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1, 1, 1, 1, 1, 1, 1);
    }
}