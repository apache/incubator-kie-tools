package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class NativeHorizontalScrollbar_ResourcesTransparant_default_InlineClientBundleGenerator implements com.google.gwt.user.client.ui.NativeHorizontalScrollbar.ResourcesTransparant {
  private static NativeHorizontalScrollbar_ResourcesTransparant_default_InlineClientBundleGenerator _instance0 = new NativeHorizontalScrollbar_ResourcesTransparant_default_InlineClientBundleGenerator();
  private void nativeHorizontalScrollbarStyleInitializer() {
    nativeHorizontalScrollbarStyle = new com.google.gwt.user.client.ui.NativeHorizontalScrollbar.Style() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "nativeHorizontalScrollbarStyle";
      }
      public String getText() {
        return (".GFVDQLFDJI{opacity:" + ("0.7")  + ";filter:" + ("alpha(opacity = 70)")  + ";-webkit-transition:" + ("opacity"+ " " +"350ms")  + ";-moz-transition:" + ("opacity"+ " " +"350ms")  + ";-o-transition:" + ("opacity"+ " " +"350ms")  + ";transition:" + ("opacity"+ " " +"350ms")  + ";}.GFVDQLFDJI:hover{opacity:" + ("1")  + ";filter:" + ("alpha(opacity = 100)")  + ";}");
      }
      public java.lang.String nativeHorizontalScrollbar() {
        return "GFVDQLFDJI";
      }
    }
    ;
  }
  private static class nativeHorizontalScrollbarStyleInitializer {
    static {
      _instance0.nativeHorizontalScrollbarStyleInitializer();
    }
    static com.google.gwt.user.client.ui.NativeHorizontalScrollbar.Style get() {
      return nativeHorizontalScrollbarStyle;
    }
  }
  public com.google.gwt.user.client.ui.NativeHorizontalScrollbar.Style nativeHorizontalScrollbarStyle() {
    return nativeHorizontalScrollbarStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.user.client.ui.NativeHorizontalScrollbar.Style nativeHorizontalScrollbarStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      nativeHorizontalScrollbarStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("nativeHorizontalScrollbarStyle", nativeHorizontalScrollbarStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'nativeHorizontalScrollbarStyle': return this.@com.google.gwt.user.client.ui.NativeHorizontalScrollbar.ResourcesTransparant::nativeHorizontalScrollbarStyle()();
    }
    return null;
  }-*/;
}
