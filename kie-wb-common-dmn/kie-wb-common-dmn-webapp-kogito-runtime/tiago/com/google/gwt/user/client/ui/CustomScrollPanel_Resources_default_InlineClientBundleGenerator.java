package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CustomScrollPanel_Resources_default_InlineClientBundleGenerator implements com.google.gwt.user.client.ui.CustomScrollPanel.Resources {
  private static CustomScrollPanel_Resources_default_InlineClientBundleGenerator _instance0 = new CustomScrollPanel_Resources_default_InlineClientBundleGenerator();
  private void customScrollPanelStyleInitializer() {
    customScrollPanelStyle = new com.google.gwt.user.client.ui.CustomScrollPanel.Style() {
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
        return "customScrollPanelStyle";
      }
      public String getText() {
        return (".GFVDQLFDII{background:" + ("#efefef")  + ";}");
      }
      public java.lang.String customScrollPanel() {
        return "GFVDQLFDHI";
      }
      public java.lang.String customScrollPanelCorner() {
        return "GFVDQLFDII";
      }
    }
    ;
  }
  private static class customScrollPanelStyleInitializer {
    static {
      _instance0.customScrollPanelStyleInitializer();
    }
    static com.google.gwt.user.client.ui.CustomScrollPanel.Style get() {
      return customScrollPanelStyle;
    }
  }
  public com.google.gwt.user.client.ui.CustomScrollPanel.Style customScrollPanelStyle() {
    return customScrollPanelStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.user.client.ui.CustomScrollPanel.Style customScrollPanelStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      customScrollPanelStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("customScrollPanelStyle", customScrollPanelStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'customScrollPanelStyle': return this.@com.google.gwt.user.client.ui.CustomScrollPanel.Resources::customScrollPanelStyle()();
    }
    return null;
  }-*/;
}
