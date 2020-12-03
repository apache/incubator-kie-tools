package org.uberfire.ext.widgets.table.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class UFTableResources_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.table.client.resources.UFTableResources {
  private static UFTableResources_default_InlineClientBundleGenerator _instance0 = new UFTableResources_default_InlineClientBundleGenerator();
  private void CSSInitializer() {
    CSS = new org.uberfire.ext.widgets.table.client.resources.UFTableCss() {
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
        return (".GFVDQLFDPQ td div{white-space:" + ("nowrap")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.GFVDQLFDDR:first-child td{border-top:" + ("none")  + ";}.GFVDQLFDBR thead th{overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.GFVDQLFDAR{border-top:" + ("none")  + ";margin-bottom:" + ("0")  + ";}.GFVDQLFDCR{width:" + ("100%")  + ";height:" + ("300px")  + ";}.GFVDQLFDOQ{background-color:") + (("#fff")  + ";padding:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#d1d1d1")  + ";z-index:" + ("2000")  + ";}.GFVDQLFDOQ .checkbox{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";}");
      }
      public java.lang.String columnPickerButton() {
        return "GFVDQLFDNQ";
      }
      public java.lang.String columnPickerPopup() {
        return "GFVDQLFDOQ";
      }
      public java.lang.String dataGrid() {
        return "GFVDQLFDPQ";
      }
      public java.lang.String dataGridContent() {
        return "GFVDQLFDAR";
      }
      public java.lang.String dataGridHeader() {
        return "GFVDQLFDBR";
      }
      public java.lang.String dataGridMain() {
        return "GFVDQLFDCR";
      }
      public java.lang.String dataGridRow() {
        return "GFVDQLFDDR";
      }
    }
    ;
  }
  private static class CSSInitializer {
    static {
      _instance0.CSSInitializer();
    }
    static org.uberfire.ext.widgets.table.client.resources.UFTableCss get() {
      return CSS;
    }
  }
  public org.uberfire.ext.widgets.table.client.resources.UFTableCss CSS() {
    return CSSInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.table.client.resources.UFTableCss CSS;
  
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
      case 'CSS': return this.@org.uberfire.ext.widgets.table.client.resources.UFTableResources::CSS()();
    }
    return null;
  }-*/;
}
