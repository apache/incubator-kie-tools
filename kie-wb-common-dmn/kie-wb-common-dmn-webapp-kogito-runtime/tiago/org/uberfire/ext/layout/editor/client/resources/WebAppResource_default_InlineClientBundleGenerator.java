package org.uberfire.ext.layout.editor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class WebAppResource_default_InlineClientBundleGenerator implements org.uberfire.ext.layout.editor.client.resources.WebAppResource {
  private static WebAppResource_default_InlineClientBundleGenerator _instance0 = new WebAppResource_default_InlineClientBundleGenerator();
  private void CSSInitializer() {
    CSS = new org.uberfire.ext.layout.editor.client.resources.LayoutEditorCss() {
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
        return "CSS";
      }
      public String getText() {
        return (".GFVDQLFDOM{border:" + ("1px"+ " " +"solid"+ " " +"dodgerblue")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"15px"+ " " +"10px")  + ";padding:" + ("10px")  + ";height:" + ("50px")  + ";}.GFVDQLFDPM{border:" + ("1px"+ " " +"solid"+ " " +"lightgray")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"15px"+ " " +"10px")  + ";padding:" + ("10px")  + ";height:" + ("50px")  + ";}.GFVDQLFDAN{border:" + ("1px"+ " " +"solid"+ " " +"lightgray")  + ";margin:" + ("10px")  + ";}");
      }
      public java.lang.String dropBorder() {
        return "GFVDQLFDOM";
      }
      public java.lang.String dropInactive() {
        return "GFVDQLFDPM";
      }
      public java.lang.String rowContainer() {
        return "GFVDQLFDAN";
      }
    }
    ;
  }
  private static class CSSInitializer {
    static {
      _instance0.CSSInitializer();
    }
    static org.uberfire.ext.layout.editor.client.resources.LayoutEditorCss get() {
      return CSS;
    }
  }
  public org.uberfire.ext.layout.editor.client.resources.LayoutEditorCss CSS() {
    return CSSInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.layout.editor.client.resources.LayoutEditorCss CSS;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      CSS(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("CSS", CSS());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'CSS': return this.@org.uberfire.ext.layout.editor.client.resources.WebAppResource::CSS()();
    }
    return null;
  }-*/;
}
