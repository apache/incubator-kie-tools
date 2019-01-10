package org.dashbuilder.renderer.c3.client.jsbinding;

import com.google.gwt.user.client.Element;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Bind the type that should be passed to c3.generate
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3ChartConf {  
    
    @JsOverlay
    static C3ChartConf create(C3ChartSize size, 
                                     C3ChartData data, 
                                     C3AxisInfo axis,
                                     C3Grid grid,
                                     C3Transition transition,
                                     C3Point point,
                                     C3Padding padding,
                                     C3Legend legend) {
        C3ChartConf instance = new C3ChartConf();
        instance.setSize(size);
        instance.setData(data);
        instance.setAxis(axis);
        instance.setGrid(grid);
        instance.setTransition(transition);
        instance.setPoint(point);
        instance.setPadding(padding);
        instance.setLegend(legend);
        return instance;
    }
    
    @JsProperty
    public native void setBindto(Element element);
    
    @JsProperty
    public native void setSize(C3ChartSize size);
    
    @JsProperty
    public native void setData(C3ChartData data);
    
    @JsProperty
    public native void setAxis(C3AxisInfo axis);
    
    @JsProperty
    public native C3AxisInfo getAxis();
    
    @JsProperty
    public native void setGrid(C3Grid grid);
    
    @JsProperty
    public native void setTransition(C3Transition transition);
    
    @JsProperty
    public native void setPoint(C3Point point);
    
    @JsProperty
    public native void setPadding(C3Padding padding);

    @JsProperty
    public native void setLegend(C3Legend legend);
    
    @JsProperty
    public native void setOnrendered(RenderedCallback callback);
    
    
    @JsFunction
    @FunctionalInterface
    public interface RenderedCallback {
        
        void callback();
    
    }
    
}