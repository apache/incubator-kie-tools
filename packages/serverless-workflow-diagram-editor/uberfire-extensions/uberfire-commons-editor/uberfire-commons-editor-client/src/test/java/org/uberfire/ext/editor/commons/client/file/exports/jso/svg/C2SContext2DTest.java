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

package org.uberfire.ext.editor.commons.client.file.exports.jso.svg;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.core.JsArray;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.Element;
import elemental2.dom.ImageData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(JsArray.class)
public class C2SContext2DTest {

    private C2SContext2D c2SContext2D;

    @Mock
    private Element element;

    @Mock
    private JsArray groupStack;

    @Mock
    private JsArray stack;

    @Mock
    private CanvasRenderingContext2D nativeContext;

    private C2S c2S;

    @Mock
    private Element root;

    @Before
    public void setUp() throws Exception {
        c2S = spy(C2S.create(100, 100, nativeContext));
        c2SContext2D = new C2SContext2D(c2S);
        c2S.__groupStack = groupStack;
        when(groupStack.pop()).thenReturn(element);
        c2S.__stack = stack;
        c2S.__root = root;
    }

    @Test
    public void testSetViewBox() {
        final double width = 100;
        final double height = 100;
        c2S.setViewBox(width, height);
        verify(root).setAttribute("viewBox", "0 0 " + width + " " + height);
    }

    @Test
    public void getSerializedSvg() {
        c2SContext2D.getSerializedSvg();
        verify(c2S).getSerializedSvg();
    }

    @Test
    public void setFillStyle() {
        c2SContext2D.setFillStyle("black");
        verify(c2S).setFillStyle("black");
    }

    @Test
    public void setStrokeStyle() {
        c2SContext2D.setStrokeStyle("black");
        verify(c2S).setStrokeStyle("black");
    }

    @Test
    public void setLineWidth() {
        c2SContext2D.setLineWidth(1);
        verify(c2S).setLineWidth(1);
    }

    @Test
    public void setLineCap() {
        c2SContext2D.setLineCap("line");
        verify(c2S).setLineCap("line");
    }

    @Test
    public void setLineJoin() {
        c2SContext2D.setLineCap("line");
        verify(c2S).setLineCap("line");
    }

    @Test
    public void setImageSmoothingEnabled() {
        c2SContext2D.setImageSmoothingEnabled(true);
        verify(c2S).setImageSmoothingEnabled(true);
    }

    @Test
    public void setFont() {
        c2SContext2D.setFont("font");
        verify(c2S).setFont("font");
    }

    @Test
    public void setTextBaseline() {
        c2SContext2D.setTextBaseline("text");
        verify(c2S).setTextBaseline("text");
    }

    @Test
    public void setTextAlign() {
        c2SContext2D.setTextAlign("left");
        verify(c2S).setTextAlign("left");
    }

    @Test
    public void setGlobalAlpha() {
        c2SContext2D.setGlobalAlpha(1);
        verify(c2S).setGlobalAlpha(1);
    }

    @Test
    public void setShadowColor() {
        c2SContext2D.setShadowColor("blue");
        verify(c2S).setShadowColor("blue");
    }

    @Test
    public void setShadowOffsetX() {
        c2SContext2D.setShadowOffsetX(1);
        verify(c2S).setShadowOffsetX(1);
    }

    @Test
    public void setShadowOffsetY() {
        c2SContext2D.setShadowOffsetY(1);
        verify(c2S).setShadowOffsetY(1);
    }

    @Test
    public void setShadowBlur() {
        c2SContext2D.setShadowBlur(1);
        verify(c2S).setShadowBlur(1);
    }

    @Test
    public void setMiterLimit() {
        c2SContext2D.setMiterLimit(1);
        verify(c2S).setMiterLimit(1);
    }

    @Test
    public void setLineDashOffset() {
        c2SContext2D.setLineDashOffset(1);
        verify(c2S).setLineDashOffset(1);
    }

    @Test
    public void saveGroup() {
        final String key = "id";
        final String value = "value";
        final Map<String, String> id = new HashMap<String, String>() {{
            put(key, value);
        }};
        c2SContext2D.saveGroup(id);
        verify(c2S).saveGroup(id);
    }

    @Test
    public void restoreGroup() {
        c2SContext2D.restoreGroup();
        verify(c2S).restoreGroup();
    }

    @Test
    public void saveStyle() {
        c2SContext2D.saveStyle();
        verify(c2S).saveStyle();
    }

    @Test
    public void restoreStyle() {
        c2SContext2D.restoreStyle();
        verify(c2S).restoreStyle();
    }

    @Test
    public void save() {
        c2SContext2D.save();
        verify(c2S).save();
    }

    @Test
    public void restore() {
        c2SContext2D.restore();
        verify(c2S).restore();
    }

    @Test
    public void beginPath() {
        c2SContext2D.beginPath();
        verify(c2S).beginPath();
    }

    @Test
    public void closePath() {
        c2SContext2D.closePath();
        verify(c2S).closePath();
    }

    @Test
    public void moveTo() {
        c2SContext2D.moveTo(1, 1);
        verify(c2S).moveTo(1, 1);
    }

    @Test
    public void lineTo() {
        c2SContext2D.lineTo(1, 1);
        verify(c2S).lineTo(1, 1);
    }

    @Test
    public void setGlobalCompositeOperation() {
        c2SContext2D.setGlobalCompositeOperation("op");
        verify(c2S).setGlobalCompositeOperation("op");
    }

    @Test
    public void quadraticCurveTo() {
        c2SContext2D.quadraticCurveTo(1, 1, 1, 1);
        verify(c2S).quadraticCurveTo(1, 1, 1, 1);
    }

    @Test
    public void arc() {
        c2SContext2D.arc(1, 1, 1, 1, 1);
        verify(c2S).arc(1, 1, 1, 1, 1);
    }

    @Test
    public void arc1() {
        c2SContext2D.arc(1, 1, 1, 1, 1, true);
        verify(c2S).arc(1, 1, 1, 1, 1, true);
    }

    @Test
    public void ellipse() {
        c2SContext2D.ellipse(1, 1, 1, 1, 1, 1, 1);
        verify(c2S).ellipse(1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void ellipse1() {
        c2SContext2D.ellipse(1, 1, 1, 1, 1, 1, 1, true);
        verify(c2S).ellipse(1, 1, 1, 1, 1, 1, 1, true);
    }

    @Test
    public void arcTo() {
        c2SContext2D.arcTo(1, 1, 1, 1, 1);
        verify(c2S).arcTo(1, 1, 1, 1, 1);
    }

    @Test
    public void bezierCurveTo() {
        c2SContext2D.bezierCurveTo(1, 1, 1, 1, 1, 1);
        verify(c2S).bezierCurveTo(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void clearRect() {
        c2SContext2D.clearRect(1, 1, 1, 1);
        verify(c2S).clearRect(1, 1, 1, 1);
    }

    @Test
    public void clip() {
        c2SContext2D.clip();
        verify(c2S).clip();
    }

    @Test
    public void fill() {
        c2SContext2D.fill();
        verify(c2S).fill();
    }

    @Test
    public void stroke() {
        c2SContext2D.stroke();
        verify(c2S).stroke();
    }

    @Test
    public void fillRect() {
        c2SContext2D.fillRect(1, 1, 1, 1);
        verify(c2S).fillRect(1, 1, 1, 1);
    }

    @Test
    public void fillText() {
        c2SContext2D.fillText("text", 1, 1);
        verify(c2S).fillText("text", 1, 1);
    }

    @Test
    public void createLinearGradient() {
        c2SContext2D.createLinearGradient(1, 1, 1, 1);
        verify(c2S).createLinearGradient(1, 1, 1, 1);
    }

    @Test
    public void createRadialGradient() {
        c2SContext2D.createRadialGradient(1, 1, 1, 1, 1, 1);
        verify(c2S).createRadialGradient(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void rect() {
        c2SContext2D.rect(1, 1, 1, 1);
        verify(c2S).rect(1, 1, 1, 1);
    }

    @Test
    public void rotate() {
        c2SContext2D.rotate(1);
        verify(c2S).rotate(1);
    }

    @Test
    public void scale() {
        c2SContext2D.scale(1, 1);
        verify(c2S).scale(1, 1);
    }

    @Test
    public void transform() {
        c2SContext2D.transform(1, 1, 1, 1, 1, 1);
        verify(c2S).transform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void setTransform() {
        c2SContext2D.setTransform(1, 1, 1, 1, 1, 1);
        verify(c2S).setTransform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void strokeText() {
        c2SContext2D.setTransform(1, 1, 1, 1, 1, 1);
        verify(c2S).setTransform(1, 1, 1, 1, 1, 1);
    }

    @Test
    public void translate() {
        c2SContext2D.translate(1, 1);
        verify(c2S).translate(1, 1);
    }

    @Test
    public void isPointInPath() {
        c2SContext2D.isPointInPath(1, 1);
        verify(c2S).isPointInPath(1, 1);
    }

    @Test
    public void putImageData() {
        ImageData imageData = mock(ImageData.class);
        c2SContext2D.putImageData(imageData, 1, 1);
        verify(c2S).putImageData(imageData, 1, 1);
    }

    @Test
    public void putImageData1() {
        ImageData imageData = mock(ImageData.class);
        c2SContext2D.putImageData(imageData, 1, 1, 1, 1, 1, 1);
        verify(c2S).putImageData(imageData, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void resetClip() {
        c2SContext2D.resetClip();
        verify(c2S).resetClip();
    }

    @Test
    public void setLineDash() {
        double[] dashes = {1, 1};
        c2SContext2D.setLineDash(dashes);
        verify(c2S).setLineDash(dashes);
    }

    @Test
    public void measureText() {
        c2SContext2D.measureText("text");
        verify(c2S).measureText("text");
    }

    @Test
    public void createImageData() {
        ImageData imageData = mock(ImageData.class);
        c2SContext2D.createImageData(imageData);
        verify(c2S).createImageData(imageData);
    }

    @Test
    public void getImageData() {
        c2SContext2D.getImageData(1, 1, 1, 1);
        verify(c2S).getImageData(1, 1, 1, 1);
    }

    @Test
    public void createImageData1() {
        c2SContext2D.createImageData(1, 1);
        verify(c2S).createImageData(1, 1);
    }

    @Test
    public void drawImage() {
        Element image = mock(Element.class);
        c2SContext2D.drawImage(image, 1, 1, 1, 1, 1, 1, 1, 1);
        verify(c2S).drawImage(image, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void drawImage1() {
        Element image = mock(Element.class);
        c2SContext2D.drawImage(image, 1, 1);
        verify(c2S).drawImage(image, 1, 1);
    }

    @Test
    public void drawImage2() {
        Element image = mock(Element.class);
        c2SContext2D.drawImage(image, 1, 1, 1, 1);
        verify(c2S).drawImage(image, 1, 1, 1, 1);
    }

    @Test
    public void testGetDelegate() {
        Assert.assertTrue(C2S.class.isInstance(c2SContext2D.getDelegate()));
    }
}