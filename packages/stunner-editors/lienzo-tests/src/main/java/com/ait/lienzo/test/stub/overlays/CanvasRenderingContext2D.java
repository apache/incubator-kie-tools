package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.ImageData;

@StubClass("elemental2.dom.CanvasRenderingContext2D")
public class CanvasRenderingContext2D extends BaseRenderingContext2D {

    public String font;
    public String setTextAlign;
    public String setTextBaseline;

    public void setFont(String font) {
        this.font = font;
    }

    public void setTextAlign(String setTextAlign) {
        this.setTextAlign = setTextAlign;
    }

    public void setTextBaseline(String setTextBaseline) {
        this.setTextBaseline = setTextBaseline;
    }

    public elemental2.dom.TextMetrics measureText(String text) {
        return new elemental2.dom.TextMetrics();
    }

    public interface FillStyleUnionType {

        static elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType of(Object o) {
            return null;
        }
    }

    public ImageData getImageData(int sx, int sy, int sw, int sh) {
        return new ImageData(100, 100);
    }
}
