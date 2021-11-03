/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.dom.client.Element;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface D3 {

    Selection selection();

    Selection select(String path);

    Selection select(Object object);

    Selection selectAll(String path);

    Zoom zoom();

    CallbackFunction on(String event, CallbackFunction callbackFn);
    
    D3 on(String event, CallbackFunctionWithData callbackFn);

    D3 call(CallbackFunction function, Object... args);

    D3 attr(String name, Object value);
    
    D3 attr(String name, CallbackFunctionWithData callbackFn);

    Object attr(String name);

    Object style(String name);

    D3 style(String name, Object value);
    
    D3 style(String name, CallbackFunctionWithData callbackFn);

    D3 append(String content);

    D3 html(String content);

    D3 text(String content);
    
    D3 text(CallbackFunctionWithData callbackFn);
    
    D3 enter();
    
    D3 join(String obj);
    
    D3 data(Object[] data);
    
    D3 data(Object data);
    
    boolean geoContains(Object object, Double[] point);
    
    Node node();

    @JsProperty
    Transform getZoomIdentity();

    @JsProperty
    <T extends Event> T getEvent();

    Transition transition();
    
    Scale scaleQuantize();
    
    Scale scaleLinear();
    
    Scale scaleSqrt();
    
    @JsProperty
    String[][] getSchemeBlues();
    
    @JsProperty
    String[][] getSchemeReds();
    
    @JsProperty
    String[][] getSchemeGreens();

    @JsFunction
    @FunctionalInterface
    interface CallbackFunction {

        void execute();
    }
    
    @JsFunction
    @FunctionalInterface
    interface CallbackFunctionWithData {

        Object execute(Object data, int index, Element[] elements);
    }

    @JsType(isNative = true)
    interface Selection extends D3 {

        boolean empty();

        int size();

        Selection filter(String path);

    }

    @JsType(isNative = true)
    interface Zoom extends D3 {

        void scaleBy(D3 element, double scale);

        void scaleTo(D3 element, double scale);

        void scaleExtent(double[] scaleExtent);

        void translateExtent(double[][] translateExtent);

        void transform(D3 selection, Transform transform);
    }

    @JsType(isNative = true)
    interface Transform extends D3 {

        @JsProperty
        double getX();

        @JsProperty
        double getY();

        @JsProperty
        double getK();

        @JsProperty
        void setX(double x);

        @JsProperty
        void setY(double y);
    }

    @JsType(isNative = true)
    interface Transition extends D3 {

        D3 duration(double milis);
    }

    @JsType(isNative = true)
    interface Event {

        @JsProperty
        Object getCurrentTarget();
        
        @JsProperty
        int getPageX();
        
        @JsProperty
        int getPageY();
    }

    @JsType(isNative = true)
    interface ZoomEvent {

        @JsProperty
        Transform getTransform();
    }

    @JsType(isNative = true)
    interface Node {

        DOMRect getBoundingClientRect();

    }

    @JsType(isNative = true)
    interface DOMRect {

        @JsProperty
        double getX();

        @JsProperty
        double getY();

        @JsProperty
        double getWidth();

        @JsProperty
        double getHeight();
    }
    
    @JsType(isNative = true)
    interface Scale {

        Scale domain(Object[] domain);
        
        Object[] domain();
        
        Scale range(Object[] colors);
        
        Scale rangeRound(Object[] colors);
        
        Object[] range();
        
        String call(Scale self, Object... args);
        
        Object[] invertExtent(Object value);
    }    

    class Builder {

        @JsProperty(name = "d3", namespace = JsPackage.GLOBAL)
        public static native D3 get();
    }
}
