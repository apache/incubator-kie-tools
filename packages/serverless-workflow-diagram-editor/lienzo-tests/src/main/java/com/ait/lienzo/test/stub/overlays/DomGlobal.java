package com.ait.lienzo.test.stub.overlays;

import java.util.Random;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.Blob;
import elemental2.dom.Element;
import elemental2.dom.ImageBitmap;
import elemental2.dom.Response;
import elemental2.dom.Window;
import elemental2.promise.Promise;

@StubClass("elemental2.dom.DomGlobal")
public class DomGlobal {

    public static final elemental2.dom.HTMLDocument document = new elemental2.dom.HTMLDocument();
    public static Window window = new elemental2.dom.Window();

    public static int requestAnimationFrame(
            elemental2.dom.FrameRequestCallback callback, Element element) {
        return new Random().nextInt();
    }

    public static double setTimeout(elemental2.dom.DomGlobal.SetTimeoutCallbackFn callback, double delay, Object... callbackParams) {
        return 0d;
    }

    public static void clearTimeout(double timerId) {
        // Do nothing
    }

    public static final Promise<Response> fetch(String input) {
        return new Promise<Response>((resolve, reject) ->
                                             new Response()
        );
    }

    public static final Promise<ImageBitmap> createImageBitmap(Blob image) {
        return new Promise<ImageBitmap>((resolve, reject) ->
                                                new ImageBitmap() {
                                                    @Override
                                                    public int getHeight() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public int getWidth() {
                                                        return 0;
                                                    }
                                                }
        );
    }
}
