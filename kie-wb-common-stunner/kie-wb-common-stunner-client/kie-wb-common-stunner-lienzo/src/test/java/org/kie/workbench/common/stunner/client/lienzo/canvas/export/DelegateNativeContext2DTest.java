/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas.export;

import java.util.HashMap;
import java.util.Optional;

import com.ait.lienzo.client.core.Path2D;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import elemental2.dom.HTMLCanvasElement;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegateNativeContext2DTest {

    public static final String NODE_UUID = UUID.uuid();
    private DelegateNativeContext2D delegateNativeContext2D;

    @Mock
    private IContext2D context;

    @Mock
    private DelegateNativeContext2D.Converter nativeClassConverter;

    private LinearGradient.LinearGradientJSO linearGradientJSO;

    private PatternGradient.PatternGradientJSO patternGradientJSO;

    private RadialGradient.RadialGradientJSO radialGradientJSO;

    private HTMLCanvasElement htmlElement;

    private Element element;

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
        element = GWT.create(Element.class);
        when(nativeClassConverter.convert(any(Element.class), eq(HTMLCanvasElement.class))).thenReturn(htmlElement);
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

        delegateNativeContext2D = new DelegateNativeContext2D(context, canvasHandler, nativeClassConverter);
    }

    @Test
    public void initDeviceRatio() {
        delegateNativeContext2D.initDeviceRatio();
    }

    @Test
    public void saveContainer() {
        delegateNativeContext2D.saveContainer(NODE_UUID);
        verify(context, times(1)).saveGroup(new HashMap<String, String>() {{
            put(SVG_NODE_ID, NODE_UUID);
        }});
    }

    @Test
    public void restoreContainer() {
        delegateNativeContext2D.restoreContainer();
        verify(context, times(1)).restoreGroup();
    }

    @Test
    public void save() {
        delegateNativeContext2D.save();
        verify(context, times(1)).saveStyle();
    }

    @Test
    public void restore() {
        delegateNativeContext2D.restore();
        verify(context, times(1)).restoreStyle();
    }

    @Test
    public void beginPath() {
        delegateNativeContext2D.beginPath();
        verify(context, times(1)).beginPath();
    }

    @Test
    public void closePath() {
        delegateNativeContext2D.closePath();
        verify(context, times(1)).closePath();
    }

    @Test
    public void moveTo() {
        delegateNativeContext2D.moveTo(1, 1);
        verify(context, times(1)).moveTo(1, 1);
    }

    @Test
    public void lineTo() {
        delegateNativeContext2D.lineTo(1, 1);
        verify(context, times(1)).lineTo(1, 1);
    }

    @Test
    public void setGlobalCompositeOperation() {
        delegateNativeContext2D.setGlobalCompositeOperation("op");
        verify(context, times(1)).setGlobalCompositeOperation("op");
    }

    @Test
    public void setLineCap() {
        delegateNativeContext2D.setLineCap("linecap");
        verify(context, times(1)).setLineCap("linecap");
    }

    @Test
    public void setLineJoin() {
        delegateNativeContext2D.setLineJoin("line");
        verify(context, times(1)).setLineJoin("line");
    }

    @Test
    public void quadraticCurveTo() {
        delegateNativeContext2D.quadraticCurveTo(1, 1, 1, 1);
        verify(context, times(1)).quadraticCurveTo(1, 1, 1, 1);
    }

    @Test
    public void arc() {
        delegateNativeContext2D.arc(1, 1, 1, 1, 1);
        verify(context, times(1)).arc(1, 1, 1, 1, 1);
    }

    @Test
    public void arc1() {
        delegateNativeContext2D.arc(1, 1, 1, 1, 1, true);
        verify(context, times(1)).arc(1, 1, 1, 1, 1, true);
    }

    @Test
    public void ellipse() {
        delegateNativeContext2D.ellipse(1, 1, 1, 1, 1, 1, 1);
        verify(context, times(1)).ellipse(1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void ellipse1() {
        delegateNativeContext2D.ellipse(1, 1, 1, 1, 1, 1, 1, true);
        verify(context, times(1)).ellipse(1, 1, 1, 1, 1, 1, 1, true);
    }

    @Test
    public void arcTo() {
        delegateNativeContext2D.arcTo(1, 1, 1, 1, 1);
        verify(context, times(1)).arcTo(1, 1, 1, 1, 1);
    }

    @Test
    public void bezierCurveTo() {
        delegateNativeContext2D.bezierCurveTo(1, 1, 1, 1, 1, 1);
        verify(context, times(1)).bezierCurveTo(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void clearRect() {
        delegateNativeContext2D.clearRect(1, 1, 1, 1);
        verify(context, times(1)).clearRect(1, 1, 1, 1);
    }

    @Test
    public void clip() {
        delegateNativeContext2D.clip();
        verify(context, times(1)).clip();
    }

    @Test
    public void clip1() {
        assertTrue(delegateNativeContext2D.clip(buildPathPartList()));

        verify(context, times(1)).lineTo(1, 1);
        verify(context, times(1)).moveTo(2, 2);
        verify(context, times(1)).bezierCurveTo(1, 2, 3, 4, 5, 6);
        verify(context, times(1)).quadraticCurveTo(1, 2, 3, 4);
        verify(context, times(1)).ellipse(1, 2, 3, 4, 7, 5, 5 + 6, false);
        verify(context, never()).closePath();
        verify(context, never()).arcTo(1, 2, 3, 4, 5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clip2() {
        final Path2D.NativePath2D path = null;
        delegateNativeContext2D.clip(path);
    }

    @Test
    public void fill() {
        delegateNativeContext2D.fill();
        verify(context, times(1)).fill();
    }

    @Test
    public void stroke() {
        delegateNativeContext2D.stroke();
        verify(context, times(1)).stroke();
    }

    @Test
    public void fillRect() {
        delegateNativeContext2D.fillRect(1, 1, 1, 1);
        verify(context, times(1)).fillRect(1, 1, 1, 1);
    }

    @Test
    public void fillText() {
        delegateNativeContext2D.fillText("text", 1, 1);
        verify(context, times(1)).fillText("text", 1, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void fillTextWithGradient() {
        delegateNativeContext2D.fillTextWithGradient("text", 1, 1, 1, 1, 1, 1, "black");
    }

    @Test
    public void fillText1() {
        delegateNativeContext2D.fillText("text", 1, 1, 1);
        verify(context, times(1)).fillText("text", 1, 1);
    }

    @Test
    public void setFillColor() {
        delegateNativeContext2D.setFillColor("black");
        verify(context, times(1)).setFillStyle("black");
    }

    @Test
    public void rect() {
        delegateNativeContext2D.rect(1, 1, 1, 1);
        verify(context, times(1)).rect(1, 1, 1, 1);
    }

    @Test
    public void rotate() {
        delegateNativeContext2D.rotate(1);
        verify(context, times(1)).rotate(1);
    }

    @Test
    public void scale() {
        delegateNativeContext2D.scale(1, 1);
        verify(context, times(1)).scale(1, 1);
    }

    @Test
    public void setStrokeColor() {
        delegateNativeContext2D.setStrokeColor("black");
        verify(context, times(1)).setStrokeStyle("black");
    }

    @Test
    public void setStrokeWidth() {
        delegateNativeContext2D.setStrokeWidth(1);
        verify(context, times(1)).setLineWidth(1);
    }

    @Test
    public void setImageSmoothingEnabled() {
        delegateNativeContext2D.setImageSmoothingEnabled(true);
        verify(context, times(1)).setImageSmoothingEnabled(true);
    }

    @Test
    public void setFillGradientLinear() {
        delegateNativeContext2D.setFillGradient(linearGradientJSO);
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void setFillGradientRadial() {
        delegateNativeContext2D.setFillGradient(radialGradientJSO);
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void setFillGradientPattern() {
        delegateNativeContext2D.setFillGradient(patternGradientJSO);
        verify(context, times(1)).setFillStyle(null);
    }

    @Test
    public void transform() {
        delegateNativeContext2D.transform(1, 1, 1, 1, 1, 1);
        verify(context, times(1)).transform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void transform1() {
        delegateNativeContext2D.transform(Transform.TransformJSO.make(1, 1, 1, 1, 1, 1));
        verify(context, times(1)).transform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void setTransform() {
        delegateNativeContext2D.setTransform(Transform.TransformJSO.make(1, 1, 1, 1, 1, 1));
        verify(context, times(1)).setTransform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void setTransform1() {
        delegateNativeContext2D.setTransform(1, 1, 1, 1, 1, 1);
        verify(context, times(1)).setTransform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void setToIdentityTransform() {
        delegateNativeContext2D.setToIdentityTransform();
        verify(context, times(1)).setTransform(1, 0, 0, 1, 0, 0);
    }

    @Test
    public void setTextFont() {
        delegateNativeContext2D.setTextFont("arial");
        verify(context, times(1)).setFont("arial");
    }

    @Test
    public void setTextBaseline() {
        delegateNativeContext2D.setTextBaseline("base");
        verify(context, times(1)).setTextBaseline("base");
    }

    @Test
    public void setTextAlign() {
        delegateNativeContext2D.setTextAlign("left");
        verify(context, times(1)).setTextAlign("left");
    }

    @Test
    public void strokeText() {
        delegateNativeContext2D.strokeText("text", 1, 1);
        verify(context, times(1)).strokeText("text", 1, 1);
    }

    @Test
    public void setGlobalAlpha() {
        delegateNativeContext2D.setGlobalAlpha(1);
        verify(context, times(1)).setGlobalAlpha(1);
    }

    @Test
    public void translate() {
        delegateNativeContext2D.translate(1, 1);
        verify(context, times(1)).translate(1, 1);
    }

    @Test
    public void setShadow() {
        delegateNativeContext2D.setShadow(null);
        verify(context).setShadowColor(Mockito.anyString());
        verify(context).setShadowOffsetX(Mockito.anyInt());
        verify(context).setShadowOffsetY(Mockito.anyInt());
        verify(context).setShadowBlur(Mockito.anyInt());

        delegateNativeContext2D.setShadow(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isSupported() {
        delegateNativeContext2D.isSupported("feature");
    }

    @Test
    public void isPointInPath() {
        delegateNativeContext2D.isPointInPath(1, 1);
        verify(context, times(1)).isPointInPath(1, 1);
    }

    @Test
    public void getImageData() {
        delegateNativeContext2D.getImageData(1, 1, 1, 1);
        verify(context, times(1)).getImageData(1, 1, 1, 1);
    }

    @Test
    public void createImageData() {
        delegateNativeContext2D.createImageData(null);
        verify(context, times(1)).createImageData(null);
    }

    @Test
    public void createImageData1() {
        delegateNativeContext2D.createImageData(1, 1);
        verify(context, times(1)).createImageData(1, 1);
    }

    @Test
    public void putImageData() {
        delegateNativeContext2D.putImageData(null, 1, 1);
        verify(context, times(1)).putImageData(null, 1, 1);
    }

    @Test
    public void putImageData1() {
        delegateNativeContext2D.putImageData(null, 1, 1, 1, 1, 1, 1);
        verify(context, times(1)).putImageData(null, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void measureText() {
        delegateNativeContext2D.measureText("text");
        verify(context, times(1)).measureText("text");
    }

    @Test
    public void resetClip() {
        delegateNativeContext2D.resetClip();
        verify(context, times(1)).resetClip();
    }

    @Test
    public void setMiterLimit() {
        delegateNativeContext2D.setMiterLimit(1);
        verify(context, times(1)).setMiterLimit(1);
    }

    @Test
    public void setLineDash() {
        delegateNativeContext2D.setLineDash(NFastDoubleArrayJSO.make(1, 1));
        verify(context, times(1)).setLineDash(new double[]{1, 1});
    }

    @Test
    public void setLineDashOffset() {
        delegateNativeContext2D.setLineDashOffset(1);
        verify(context, times(1)).setLineDashOffset(1);
    }

    @Test
    public void getBackingStorePixelRatio() {
        assertEquals(delegateNativeContext2D.getBackingStorePixelRatio(), 1, 0);
    }

    @Test
    public void path() {
        assertTrue(delegateNativeContext2D.path(buildPathPartList()));

        verify(context, times(1)).lineTo(1, 1);
        verify(context, times(1)).moveTo(2, 2);
        verify(context, times(1)).bezierCurveTo(1, 2, 3, 4, 5, 6);
        verify(context, times(1)).quadraticCurveTo(1, 2, 3, 4);
        verify(context, times(1)).ellipse(1, 2, 3, 4, 7, 5, 5 + 6, false);
        verify(context, times(1)).closePath();
        verify(context, times(1)).arcTo(1, 2, 3, 4, 5);
    }

    private PathPartList.PathPartListJSO buildPathPartList() {
        PathPartList.PathPartListJSO list = PathPartList.PathPartListJSO.make();
        list.push(PathPartEntryJSO.make(1, NFastDoubleArrayJSO.make(1, 1)));
        list.push(PathPartEntryJSO.make(2, NFastDoubleArrayJSO.make(2, 2)));
        list.push(PathPartEntryJSO.make(3, NFastDoubleArrayJSO.make(1, 2, 3, 4, 5, 6)));
        list.push(PathPartEntryJSO.make(4, NFastDoubleArrayJSO.make(1, 2, 3, 4)));
        list.push(PathPartEntryJSO.make(5, NFastDoubleArrayJSO.make(1, 2, 3, 4, 5, 6, 7, 8)));
        list.push(PathPartEntryJSO.make(6, null));
        list.push(PathPartEntryJSO.make(7, NFastDoubleArrayJSO.make(1, 2, 3, 4, 5)));
        return list;
    }

    @Test
    public void getDelegate() {
        assertEquals(context, delegateNativeContext2D.getDelegate());
    }

    @Test
    public void drawImage() {
        delegateNativeContext2D.drawImage(element, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1);
    }

    @Test
    public void drawImage2() {
        delegateNativeContext2D.drawImage(element, 1, 1, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1, 1, 1);
    }

    @Test
    public void drawImage3() {
        delegateNativeContext2D.drawImage(element, 1, 1, 1, 1, 1, 1, 1, 1);
        verify(context, times(1)).drawImage(htmlElement, 1, 1, 1, 1, 1, 1, 1, 1);
    }
}