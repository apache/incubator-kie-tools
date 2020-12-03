package org.uberfire.ext.widgets.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TreeNavigatorResources_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources {
  private static TreeNavigatorResources_default_InlineClientBundleGenerator _instance0 = new TreeNavigatorResources_default_InlineClientBundleGenerator();
  private void cssInitializer() {
    css = new org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources.NavigatorStyle() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDNO{color:" + ("#484848")  + ";}.GFVDQLFDNO a{color:" + ("inherit")  + ";text-decoration:" + ("none")  + ";}.GFVDQLFDNO .GFVDQLFDOO{width:" + ("100%")  + ";min-height:" + ("20px")  + ";margin-top:" + ("1px")  + ";cursor:" + ("pointer")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP{position:" + ("relative")  + ";display:" + ("inline-flex")  + ";height:" + ("20px")  + ";width:") + (("inherit")  + ";white-space:" + ("nowrap")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP i{position:" + ("relative")  + ";top:" + ("5px")  + ";right:" + ("5px")  + ";float:" + ("right")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP .GFVDQLFDBP{padding-right:" + ("12px")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis") ) + (";white-space:" + ("nowrap")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDPO{margin-right:" + ("23px")  + ";}.GFVDQLFDNO .GFVDQLFDCP{display:" + ("inline-flex")  + ";position:" + ("relative")  + ";width:" + ("100%")  + ";height:" + ("20px")  + ";margin:" + ("1px")  + ";cursor:" + ("pointer")  + ";white-space:" + ("nowrap")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";border-top:") + (("1px"+ " " +"solid"+ " " +"transparent")  + ";}.GFVDQLFDNO .GFVDQLFDCP:hover,.GFVDQLFDNO .GFVDQLFDAP:hover{background-color:" + ("#d4edfa")  + ";border-color:" + ("#b3d3e7")  + ";}.GFVDQLFDNO .GFVDQLFDCP .GFVDQLFDDP{position:" + ("relative")  + ";right:" + ("10px")  + ";z-index:" + ("1")  + " !important;display:" + ("inline-flex")  + ";}.GFVDQLFDNO .GFVDQLFDCP i{position:" + ("relative")  + ";padding-top:" + ("2px")  + ";padding-right:" + ("5px")  + ";}.GFVDQLFDNO .GFVDQLFDEP,.GFVDQLFDNO .GFVDQLFDEP:hover,.GFVDQLFDNO .GFVDQLFDEP a{background-color:" + ("#0099d3") ) + (" !important;border-color:" + ("#0076b7")  + " !important;color:" + ("#fff")  + " !important;}.GFVDQLFDNO .GFVDQLFDCP canvas{margin-top:" + ("2px")  + ";margin-right:" + ("2px")  + ";}")) : ((".GFVDQLFDNO{color:" + ("#484848")  + ";}.GFVDQLFDNO a{color:" + ("inherit")  + ";text-decoration:" + ("none")  + ";}.GFVDQLFDNO .GFVDQLFDOO{width:" + ("100%")  + ";min-height:" + ("20px")  + ";margin-top:" + ("1px")  + ";cursor:" + ("pointer")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP{position:" + ("relative")  + ";display:" + ("inline-flex")  + ";height:" + ("20px")  + ";width:") + (("inherit")  + ";white-space:" + ("nowrap")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP i{position:" + ("relative")  + ";top:" + ("5px")  + ";left:" + ("5px")  + ";float:" + ("left")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDAP .GFVDQLFDBP{padding-left:" + ("12px")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis") ) + (";white-space:" + ("nowrap")  + ";}.GFVDQLFDNO .GFVDQLFDOO .GFVDQLFDPO{margin-left:" + ("23px")  + ";}.GFVDQLFDNO .GFVDQLFDCP{display:" + ("inline-flex")  + ";position:" + ("relative")  + ";width:" + ("100%")  + ";height:" + ("20px")  + ";margin:" + ("1px")  + ";cursor:" + ("pointer")  + ";white-space:" + ("nowrap")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"transparent")  + ";border-top:") + (("1px"+ " " +"solid"+ " " +"transparent")  + ";}.GFVDQLFDNO .GFVDQLFDCP:hover,.GFVDQLFDNO .GFVDQLFDAP:hover{background-color:" + ("#d4edfa")  + ";border-color:" + ("#b3d3e7")  + ";}.GFVDQLFDNO .GFVDQLFDCP .GFVDQLFDDP{position:" + ("relative")  + ";left:" + ("10px")  + ";z-index:" + ("1")  + " !important;display:" + ("inline-flex")  + ";}.GFVDQLFDNO .GFVDQLFDCP i{position:" + ("relative")  + ";padding-top:" + ("2px")  + ";padding-left:" + ("5px")  + ";}.GFVDQLFDNO .GFVDQLFDEP,.GFVDQLFDNO .GFVDQLFDEP:hover,.GFVDQLFDNO .GFVDQLFDEP a{background-color:" + ("#0099d3") ) + (" !important;border-color:" + ("#0076b7")  + " !important;color:" + ("#fff")  + " !important;}.GFVDQLFDNO .GFVDQLFDCP canvas{margin-top:" + ("2px")  + ";margin-left:" + ("2px")  + ";}"));
      }
      public java.lang.String tree() {
        return "GFVDQLFDNO";
      }
      public java.lang.String treeFolder() {
        return "GFVDQLFDOO";
      }
      public java.lang.String treeFolderContent() {
        return "GFVDQLFDPO";
      }
      public java.lang.String treeFolderHeader() {
        return "GFVDQLFDAP";
      }
      public java.lang.String treeFolderName() {
        return "GFVDQLFDBP";
      }
      public java.lang.String treeItem() {
        return "GFVDQLFDCP";
      }
      public java.lang.String treeItemName() {
        return "GFVDQLFDDP";
      }
      public java.lang.String treeSelected() {
        return "GFVDQLFDEP";
      }
    }
    ;
  }
  private static class cssInitializer {
    static {
      _instance0.cssInitializer();
    }
    static org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources.NavigatorStyle get() {
      return css;
    }
  }
  public org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources.NavigatorStyle css() {
    return cssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources.NavigatorStyle css;
  
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
      case 'css': return this.@org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources::css()();
    }
    return null;
  }-*/;
}
