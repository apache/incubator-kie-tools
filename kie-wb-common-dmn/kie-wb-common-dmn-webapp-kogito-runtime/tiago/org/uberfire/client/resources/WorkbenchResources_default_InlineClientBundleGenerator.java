package org.uberfire.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class WorkbenchResources_default_InlineClientBundleGenerator implements org.uberfire.client.resources.WorkbenchResources {
  private static WorkbenchResources_default_InlineClientBundleGenerator _instance0 = new WorkbenchResources_default_InlineClientBundleGenerator();
  private void CSSInitializer() {
    CSS = new org.uberfire.client.resources.WorkbenchCss() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDAM{border:" + ("0")  + ";margin:" + ("0")  + ";padding:" + ("0")  + ";z-index:" + ("100000")  + ";}.GFVDQLFDHM{min-height:" + ("30px")  + ";margin-top:" + ("0")  + ";margin-bottom:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";padding-top:" + ("5px")  + ";background-color:" + ("#f5f5f5")  + ";border:") + (("1px"+ " " +"solid"+ " " +"#e3e3e3")  + ";-webkit-border-radius:" + ("4px")  + ";-moz-border-radius:" + ("4px")  + ";border-radius:" + ("4px")  + ";-webkit-box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";-moz-box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";}.GFVDQLFDFM{cursor:" + ("default")  + ";height:" + ("36px")  + ";width:" + ("100%")  + ";border-spacing:" + ("0") ) + (";background:" + ("#c0c0c0")  + ";padding-top:" + ("0")  + ";padding-bottom:" + ("0")  + ";padding-right:" + ("2px")  + ";padding-left:" + ("2px")  + ";}.GFVDQLFDGM{float:" + ("none")  + ";margin:" + ("-2px"+ " " +"10px"+ " " +"0"+ " " +"0")  + ";}.uf-listbar{-moz-box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";-webkit-box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";background:" + ("#f5f5f5"+ " " +"linear-gradient(" + "#fff"+ ","+ " " +"#e6e6e6" + ")"+ " " +"repeat-x")  + ";border-image-outset:") + (("0")  + ";border-image-repeat:" + ("stretch")  + ";border-image-slice:" + ("100%")  + ";border-image-source:" + ("none")  + ";border-image-width:" + ("1")  + ";border:" + ("1px"+ " " +"solid"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0980392" + ")")  + ";border-top-width:" + ("0")  + ";border-bottom-color:" + ("#b3b3b3")  + ";box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";box-sizing:" + ("border-box")  + ";color:" + ("#333") ) + (";padding-right:" + ("6px")  + ";padding-top:" + ("5px")  + ";text-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.74902" + ")"+ " " +"0"+ " " +"1px"+ " " +"1px")  + ";height:" + ("32px")  + ";}.uf-lock-hint{width:" + ("180px")  + ";height:" + ("90px")  + ";background-color:" + ("#fff")  + ";color:" + ("#848484")  + ";border:" + ("1px"+ " " +"solid")  + ";border-color:" + ("#848484")  + ";text-align:") + (("center")  + ";font-size:" + ("13px")  + ";-webkit-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-moz-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-ms-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-o-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";}.uf-scroll-panel{height:" + ("100%")  + ";width:" + ("100%")  + ";}.GFVDQLFDBM{margin-right:" + ("0")  + ";-webkit-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out") ) + (";-moz-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";-ms-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";-o-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";}.navbar-fixed-top,.navbar-fixed-bottom{position:" + ("static")  + " !important;}.uf-maximized-panel{background-color:" + ("#fff")  + ";}.uf-split-layout-panel-hdragger,.uf-split-layout-panel-vdragger{background-color:" + ("#ddd")  + ";}.uf-split-layout-panel-hdragger:hover{cursor:" + ("col-resize")  + ";}.uf-split-layout-panel-vdragger:hover{cursor:" + ("row-resize")  + ";}.uf-no-select{-webkit-user-select:" + ("none")  + ";-moz-user-select:") + (("none")  + ";-ms-user-select:" + ("none")  + ";user-select:" + ("none")  + ";}.uf-perspective-container,.uf-perspective-row-12{height:" + ("100%")  + ";}.uf-perspective-row-11{height:" + ("91.666664%")  + ";}.uf-perspective-row-10{height:" + ("83.333336%")  + ";}.uf-perspective-row-9{height:" + ("75%")  + ";}.uf-perspective-row-8{height:" + ("66.666664%")  + ";}.uf-perspective-row-7{height:" + ("58.333332%")  + ";}.uf-perspective-row-6{height:" + ("50%")  + ";}.uf-perspective-row-5{height:" + ("41.666668%") ) + (";}.uf-perspective-row-4{height:" + ("33.333332%")  + ";}.uf-perspective-row-3{height:" + ("25%")  + ";}.uf-perspective-row-2{height:" + ("16.666666%")  + ";}.uf-perspective-row-1{height:" + ("8.333333%")  + ";}.uf-perspective-col,.uf-perspective-component{height:" + ("100%")  + ";}.uf-perspective-rendered-col{padding-right:" + ("0")  + ";padding-left:" + ("0")  + ";}.uf-perspective-rendered-row{margin-right:" + ("0")  + ";margin-left:" + ("0")  + ";}.uf-perspective-rendered-container{width:" + ("100%")  + ";max-width:") + (("none")  + ";padding:" + ("0")  + ";}.uf-le-overflow{overflow:" + ("auto")  + ";}.js-screen-container{display:" + ("inline")  + ";}")) : ((".GFVDQLFDAM{border:" + ("0")  + ";margin:" + ("0")  + ";padding:" + ("0")  + ";z-index:" + ("100000")  + ";}.GFVDQLFDHM{min-height:" + ("30px")  + ";margin-top:" + ("0")  + ";margin-bottom:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";padding-top:" + ("5px")  + ";background-color:" + ("#f5f5f5")  + ";border:") + (("1px"+ " " +"solid"+ " " +"#e3e3e3")  + ";-webkit-border-radius:" + ("4px")  + ";-moz-border-radius:" + ("4px")  + ";border-radius:" + ("4px")  + ";-webkit-box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";-moz-box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";box-shadow:" + ("inset"+ " " +"0"+ " " +"1px"+ " " +"1px"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.05" + ")")  + ";}.GFVDQLFDFM{cursor:" + ("default")  + ";height:" + ("36px")  + ";width:" + ("100%")  + ";border-spacing:" + ("0") ) + (";background:" + ("#c0c0c0")  + ";padding-top:" + ("0")  + ";padding-bottom:" + ("0")  + ";padding-left:" + ("2px")  + ";padding-right:" + ("2px")  + ";}.GFVDQLFDGM{float:" + ("none")  + ";margin:" + ("-2px"+ " " +"0"+ " " +"0"+ " " +"10px")  + ";}.uf-listbar{-moz-box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";-webkit-box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";background:" + ("#f5f5f5"+ " " +"linear-gradient(" + "#fff"+ ","+ " " +"#e6e6e6" + ")"+ " " +"repeat-x")  + ";border-image-outset:") + (("0")  + ";border-image-repeat:" + ("stretch")  + ";border-image-slice:" + ("100%")  + ";border-image-source:" + ("none")  + ";border-image-width:" + ("1")  + ";border:" + ("1px"+ " " +"solid"+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0980392" + ")")  + ";border-top-width:" + ("0")  + ";border-bottom-color:" + ("#b3b3b3")  + ";box-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.2" + ")"+ " " +"0"+ " " +"1px"+ " " +"0"+ " " +"0"+ " " +"inset"+ ","+ " " +"rgba(" + "0"+ ","+ " " +"0"+ ","+ " " +"0"+ ","+ " " +"0.0470588" + ")"+ " " +"0"+ " " +"1px"+ " " +"2px"+ " " +"0")  + ";box-sizing:" + ("border-box")  + ";color:" + ("#333") ) + (";padding-left:" + ("6px")  + ";padding-top:" + ("5px")  + ";text-shadow:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.74902" + ")"+ " " +"0"+ " " +"1px"+ " " +"1px")  + ";height:" + ("32px")  + ";}.uf-lock-hint{width:" + ("180px")  + ";height:" + ("90px")  + ";background-color:" + ("#fff")  + ";color:" + ("#848484")  + ";border:" + ("1px"+ " " +"solid")  + ";border-color:" + ("#848484")  + ";text-align:") + (("center")  + ";font-size:" + ("13px")  + ";-webkit-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-moz-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-ms-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";-o-transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";transition:" + ("top"+ " " +"0.5s"+ " " +"ease-in")  + ";}.uf-scroll-panel{height:" + ("100%")  + ";width:" + ("100%")  + ";}.GFVDQLFDBM{margin-left:" + ("0")  + ";-webkit-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out") ) + (";-moz-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";-ms-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";-o-transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";transition:" + ("all"+ " " +"0.7s"+ " " +"ease-out")  + ";}.navbar-fixed-top,.navbar-fixed-bottom{position:" + ("static")  + " !important;}.uf-maximized-panel{background-color:" + ("#fff")  + ";}.uf-split-layout-panel-hdragger,.uf-split-layout-panel-vdragger{background-color:" + ("#ddd")  + ";}.uf-split-layout-panel-hdragger:hover{cursor:" + ("col-resize")  + ";}.uf-split-layout-panel-vdragger:hover{cursor:" + ("row-resize")  + ";}.uf-no-select{-webkit-user-select:" + ("none")  + ";-moz-user-select:") + (("none")  + ";-ms-user-select:" + ("none")  + ";user-select:" + ("none")  + ";}.uf-perspective-container,.uf-perspective-row-12{height:" + ("100%")  + ";}.uf-perspective-row-11{height:" + ("91.666664%")  + ";}.uf-perspective-row-10{height:" + ("83.333336%")  + ";}.uf-perspective-row-9{height:" + ("75%")  + ";}.uf-perspective-row-8{height:" + ("66.666664%")  + ";}.uf-perspective-row-7{height:" + ("58.333332%")  + ";}.uf-perspective-row-6{height:" + ("50%")  + ";}.uf-perspective-row-5{height:" + ("41.666668%") ) + (";}.uf-perspective-row-4{height:" + ("33.333332%")  + ";}.uf-perspective-row-3{height:" + ("25%")  + ";}.uf-perspective-row-2{height:" + ("16.666666%")  + ";}.uf-perspective-row-1{height:" + ("8.333333%")  + ";}.uf-perspective-col,.uf-perspective-component{height:" + ("100%")  + ";}.uf-perspective-rendered-col{padding-left:" + ("0")  + ";padding-right:" + ("0")  + ";}.uf-perspective-rendered-row{margin-left:" + ("0")  + ";margin-right:" + ("0")  + ";}.uf-perspective-rendered-container{width:" + ("100%")  + ";max-width:") + (("none")  + ";padding:" + ("0")  + ";}.uf-le-overflow{overflow:" + ("auto")  + ";}.js-screen-container{display:" + ("inline")  + ";}"));
      }
      public java.lang.String activeNavTabs() {
        return "uf-activeNavTabs";
      }
      public java.lang.String dropTargetCompass() {
        return "uf-drop-target-compass";
      }
      public java.lang.String dropTargetHighlight() {
        return "uf-drop-target-highlight";
      }
      public java.lang.String listbar() {
        return "uf-listbar";
      }
      public java.lang.String modal() {
        return "uf-modal";
      }
      public java.lang.String notification() {
        return "GFVDQLFDAM";
      }
      public java.lang.String showContext() {
        return "GFVDQLFDBM";
      }
      public java.lang.String splitLayoutPanel() {
        return "uf-split-layout-panel";
      }
      public java.lang.String splitLayoutPanelHDragger() {
        return "uf-split-layout-panel-hdragger";
      }
      public java.lang.String splitLayoutPanelVDragger() {
        return "uf-split-layout-panel-vdragger";
      }
      public java.lang.String statusBar() {
        return "GFVDQLFDFM";
      }
      public java.lang.String tabCloseButton() {
        return "GFVDQLFDGM";
      }
      public java.lang.String toolbar() {
        return "GFVDQLFDHM";
      }
    }
    ;
  }
  private static class CSSInitializer {
    static {
      _instance0.CSSInitializer();
    }
    static org.uberfire.client.resources.WorkbenchCss get() {
      return CSS;
    }
  }
  public org.uberfire.client.resources.WorkbenchCss CSS() {
    return CSSInitializer.get();
  }
  private void imagesInitializer() {
    images = com.google.gwt.core.client.GWT.create(org.uberfire.client.resources.WorkbenchImages.class);
  }
  private static class imagesInitializer {
    static {
      _instance0.imagesInitializer();
    }
    static org.uberfire.client.resources.WorkbenchImages get() {
      return images;
    }
  }
  public org.uberfire.client.resources.WorkbenchImages images() {
    return imagesInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.client.resources.WorkbenchCss CSS;
  private static org.uberfire.client.resources.WorkbenchImages images;
  
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
      case 'CSS': return this.@org.uberfire.client.resources.WorkbenchResources::CSS()();
    }
    return null;
  }-*/;
}
