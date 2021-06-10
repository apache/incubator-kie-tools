package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.core.Uint8ClampedArray;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/30/19
 */
@StubClass("elemental2.dom.ImageData")
public class ImageData {

    public Uint8ClampedArray data;

    public int width = 0;
    public int height = 0;

    public ImageData(double d1, double d2) {
        data = new Uint8ClampedArray(28);
    }
}
