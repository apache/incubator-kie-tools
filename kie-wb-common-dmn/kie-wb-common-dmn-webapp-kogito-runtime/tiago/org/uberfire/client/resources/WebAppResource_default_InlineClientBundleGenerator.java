package org.uberfire.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class WebAppResource_default_InlineClientBundleGenerator implements org.uberfire.client.resources.WebAppResource {
  private static WebAppResource_default_InlineClientBundleGenerator _instance0 = new WebAppResource_default_InlineClientBundleGenerator();
  private void CSSInitializer() {
    CSS = new org.uberfire.client.resources.DocksCss() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDIL{margin-top:" + ("5px")  + ";margin-right:" + ("2px")  + ";margin-left:" + ("2px")  + ";}.GFVDQLFDJL,.GFVDQLFDJL i{height:" + ("100%")  + ";}.GFVDQLFDGL{display:" + ("none")  + ";}.GFVDQLFDFL{border:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";}.GFVDQLFDEL{border-left:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";border-right:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";text-align:" + ("center")  + ";}.GFVDQLFDJK{height:" + ("100%")  + ";width:") + (("100%")  + ";}.GFVDQLFDDL{height:" + ("36px")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";}.GFVDQLFDOK{height:" + ("100%")  + ";width:" + ("100%")  + ";}.GFVDQLFDKL{margin-right:" + ("5px")  + ";margin-top:" + ("5px")  + ";}.GFVDQLFDCL{float:" + ("right")  + ";margin-right:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0") ) + (";}.GFVDQLFDMK{float:" + ("left")  + ";margin-top:" + ("6px")  + ";margin-left:" + ("15px")  + ";}.GFVDQLFDAL{float:" + ("right")  + ";margin-right:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0")  + ";}.GFVDQLFDKK{float:" + ("left")  + ";margin-top:" + ("6px")  + ";margin-left:" + ("15px")  + ";}.GFVDQLFDBL{float:") + (("right")  + ";margin-right:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0")  + ";}.GFVDQLFDLK{float:" + ("left")  + ";margin-left:" + ("15px")  + ";margin-top:" + ("6px")  + ";}.GFVDQLFDIK{margin-right:" + ("3px")  + ";margin-left:" + ("3px")  + ";}.GFVDQLFDHL{background-color:" + ("#ddd")  + ";float:" + ("left") ) + (";}.GFVDQLFDNK{float:" + ("left")  + ";margin-top:" + ("6px")  + ";margin-left:" + ("5px")  + ";}")) : ((".GFVDQLFDIL{margin-top:" + ("5px")  + ";margin-left:" + ("2px")  + ";margin-right:" + ("2px")  + ";}.GFVDQLFDJL,.GFVDQLFDJL i{height:" + ("100%")  + ";}.GFVDQLFDGL{display:" + ("none")  + ";}.GFVDQLFDFL{border:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";}.GFVDQLFDEL{border-right:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";border-left:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";text-align:" + ("center")  + ";}.GFVDQLFDJK{height:" + ("100%")  + ";width:") + (("100%")  + ";}.GFVDQLFDDL{height:" + ("36px")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + ";}.GFVDQLFDOK{height:" + ("100%")  + ";width:" + ("100%")  + ";}.GFVDQLFDKL{margin-left:" + ("5px")  + ";margin-top:" + ("5px")  + ";}.GFVDQLFDCL{float:" + ("left")  + ";margin-left:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0") ) + (";}.GFVDQLFDMK{float:" + ("right")  + ";margin-top:" + ("6px")  + ";margin-right:" + ("15px")  + ";}.GFVDQLFDAL{float:" + ("left")  + ";margin-left:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0")  + ";}.GFVDQLFDKK{float:" + ("right")  + ";margin-top:" + ("6px")  + ";margin-right:" + ("15px")  + ";}.GFVDQLFDBL{float:") + (("left")  + ";margin-left:" + ("15px")  + ";margin-top:" + ("10px")  + ";margin-bottom:" + ("0")  + ";}.GFVDQLFDLK{float:" + ("right")  + ";margin-right:" + ("15px")  + ";margin-top:" + ("6px")  + ";}.GFVDQLFDIK{margin-left:" + ("3px")  + ";margin-right:" + ("3px")  + ";}.GFVDQLFDHL{background-color:" + ("#ddd")  + ";float:" + ("right") ) + (";}.GFVDQLFDNK{float:" + ("right")  + ";margin-top:" + ("6px")  + ";margin-right:" + ("5px")  + ";}"));
      }
      public java.lang.String buttonFocused() {
        return "GFVDQLFDIK";
      }
      public java.lang.String dockExpanded() {
        return "GFVDQLFDJK";
      }
      public java.lang.String dockExpandedButtonEast() {
        return "GFVDQLFDKK";
      }
      public java.lang.String dockExpandedButtonSouth() {
        return "GFVDQLFDLK";
      }
      public java.lang.String dockExpandedButtonWest() {
        return "GFVDQLFDMK";
      }
      public java.lang.String dockExpandedContentButton() {
        return "GFVDQLFDNK";
      }
      public java.lang.String dockExpandedContentPanel() {
        return "GFVDQLFDOK";
      }
      public java.lang.String dockExpandedContentPanelSouth() {
        return "GFVDQLFDPK";
      }
      public java.lang.String dockExpandedLabelEast() {
        return "GFVDQLFDAL";
      }
      public java.lang.String dockExpandedLabelSouth() {
        return "GFVDQLFDBL";
      }
      public java.lang.String dockExpandedLabelWest() {
        return "GFVDQLFDCL";
      }
      public java.lang.String dockExpandedTitlePanel() {
        return "GFVDQLFDDL";
      }
      public java.lang.String gradientBottomTop() {
        return "GFVDQLFDEL";
      }
      public java.lang.String gradientTopBottom() {
        return "GFVDQLFDFL";
      }
      public java.lang.String hideElement() {
        return "GFVDQLFDGL";
      }
      public java.lang.String resizableBar() {
        return "GFVDQLFDHL";
      }
      public java.lang.String sideDockItem() {
        return "GFVDQLFDIL";
      }
      public java.lang.String singleDockItem() {
        return "GFVDQLFDJL";
      }
      public java.lang.String southDockItem() {
        return "GFVDQLFDKL";
      }
    }
    ;
  }
  private static class CSSInitializer {
    static {
      _instance0.CSSInitializer();
    }
    static org.uberfire.client.resources.DocksCss get() {
      return CSS;
    }
  }
  public org.uberfire.client.resources.DocksCss CSS() {
    return CSSInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.client.resources.DocksCss CSS;
  
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
      case 'CSS': return this.@org.uberfire.client.resources.WebAppResource::CSS()();
    }
    return null;
  }-*/;
}
