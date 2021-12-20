package org.kie.lienzo.client;

import jsinterop.annotations.JsType;

@JsType
public class JsCanvasExamples {

    BaseLienzoExamples examples;

    public void goToExample(int index) {
        examples.goToTest(index);
    }
}
