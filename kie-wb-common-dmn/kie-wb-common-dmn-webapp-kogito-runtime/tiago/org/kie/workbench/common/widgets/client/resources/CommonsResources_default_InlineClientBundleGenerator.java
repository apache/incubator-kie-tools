package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CommonsResources_default_InlineClientBundleGenerator implements org.kie.workbench.common.widgets.client.resources.CommonsResources {
  private static CommonsResources_default_InlineClientBundleGenerator _instance0 = new CommonsResources_default_InlineClientBundleGenerator();
  private void cssInitializer() {
    css = new org.kie.workbench.common.widgets.client.resources.CommonsCss() {
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
        return "css";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDCJ{border-radius:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#ccc")  + ";}.GFVDQLFDDJ{background:" + ("#e6f1f6")  + ";margin:" + ("10px")  + ";text-align:" + ("center")  + ";display:" + ("table-cell")  + ";vertical-align:" + ("middle")  + ";}.GFVDQLFDEJ{margin-right:" + ("200px")  + ";margin-left:" + ("200px")  + ";color:" + ("#08c")  + ";border-top:") + (("1px"+ " " +"solid"+ " " +"#08c")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#08c")  + ";padding-top:" + ("20px")  + ";padding-bottom:" + ("20px")  + ";}")) : ((".GFVDQLFDCJ{border-radius:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#ccc")  + ";}.GFVDQLFDDJ{background:" + ("#e6f1f6")  + ";margin:" + ("10px")  + ";text-align:" + ("center")  + ";display:" + ("table-cell")  + ";vertical-align:" + ("middle")  + ";}.GFVDQLFDEJ{margin-left:" + ("200px")  + ";margin-right:" + ("200px")  + ";color:" + ("#08c")  + ";border-top:") + (("1px"+ " " +"solid"+ " " +"#08c")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#08c")  + ";padding-top:" + ("20px")  + ";padding-bottom:" + ("20px")  + ";}"));
      }
      public java.lang.String greyBorderWithRoundCorners() {
        return "GFVDQLFDCJ";
      }
      public java.lang.String infoContainer() {
        return "GFVDQLFDDJ";
      }
      public java.lang.String infoContent() {
        return "GFVDQLFDEJ";
      }
    }
    ;
  }
  private static class cssInitializer {
    static {
      _instance0.cssInitializer();
    }
    static org.kie.workbench.common.widgets.client.resources.CommonsCss get() {
      return css;
    }
  }
  public org.kie.workbench.common.widgets.client.resources.CommonsCss css() {
    return cssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.kie.workbench.common.widgets.client.resources.CommonsCss css;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      css(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("css", css());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'css': return this.@org.kie.workbench.common.widgets.client.resources.CommonsResources::css()();
    }
    return null;
  }-*/;
}
