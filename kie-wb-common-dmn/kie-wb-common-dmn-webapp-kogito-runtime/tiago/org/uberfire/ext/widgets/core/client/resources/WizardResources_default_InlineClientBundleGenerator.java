package org.uberfire.ext.widgets.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class WizardResources_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.core.client.resources.WizardResources {
  private static WizardResources_default_InlineClientBundleGenerator _instance0 = new WizardResources_default_InlineClientBundleGenerator();
  private void cssInitializer() {
    css = new org.uberfire.ext.widgets.core.client.resources.WizardResources.WizardStyle() {
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDDQ{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("2px"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GFVDQLFDCQ{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GFVDQLFDNP{margin-top:" + ("100px")  + ";}.GFVDQLFDNP td{margin-bottom:" + ("10px")  + ";width:" + ("48px") ) + (";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GFVDQLFDBQ{margin-right:" + ("3px")  + ";margin-left:" + ("3px")  + ";}.GFVDQLFDFQ{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GFVDQLFDEQ{margin-right:" + ("10px")  + ";margin-top:" + ("6px")  + ";}.GFVDQLFDHQ{color:") + (("#b94a48")  + ";}.GFVDQLFDMP{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GFVDQLFDIQ{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px") ) + (";padding-right:" + ("2px")  + ";}.GFVDQLFDIQ:HOVER{background-color:" + ("#cdcdcd")  + ";cursor:" + ("pointer")  + ";}.GFVDQLFDJQ{width:" + ("16px")  + ";margin-left:" + ("5px")  + ";}.GFVDQLFDKQ{width:" + ("180px")  + ";}.GFVDQLFDAQ{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GFVDQLFDPP{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:") + (("#900")  + ";}.GFVDQLFDGQ{margin:" + ("10px")  + ";}.GFVDQLFDOP{margin-right:" + ("10px")  + ";margin-left:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GFVDQLFDLP{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px") ) + (";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}")) : ((".GFVDQLFDDQ{border-color:" + ("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("2px"+ " " +"10px"+ " " +"10px"+ " " +"10px")  + ";padding:" + ("5px")  + ";}.GFVDQLFDCQ{font-weight:" + ("bold")  + ";background-color:" + ("#dcdcdc")  + ";border-color:") + (("#c8c8c8")  + ";border-top-style:" + ("solid")  + ";border-left-style:" + ("solid")  + ";border-right-style:" + ("solid")  + ";border-bottom-style:" + ("solid")  + ";border-width:" + ("1px")  + ";margin:" + ("10px"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";padding:" + ("2px"+ " " +"5px"+ " " +"2px"+ " " +"5px")  + ";}.GFVDQLFDNP{margin-top:" + ("100px")  + ";}.GFVDQLFDNP td{margin-bottom:" + ("10px")  + ";width:" + ("48px") ) + (";height:" + ("32px")  + ";text-align:" + ("center")  + ";}.GFVDQLFDBQ{margin-left:" + ("3px")  + ";margin-right:" + ("3px")  + ";}.GFVDQLFDFQ{background-color:" + ("red")  + ";margin-top:" + ("10px")  + ";padding:" + ("5px")  + ";width:" + ("100%")  + ";}.GFVDQLFDEQ{margin-left:" + ("10px")  + ";margin-top:" + ("6px")  + ";}.GFVDQLFDHQ{color:") + (("#b94a48")  + ";}.GFVDQLFDMP{width:" + ("100%")  + ";background-color:" + ("#dcdcdc")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#c8c8c8")  + ";padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";}.GFVDQLFDIQ{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";padding-top:" + ("2px")  + ";padding-bottom:" + ("2px") ) + (";padding-left:" + ("2px")  + ";}.GFVDQLFDIQ:HOVER{background-color:" + ("#cdcdcd")  + ";cursor:" + ("pointer")  + ";}.GFVDQLFDJQ{width:" + ("16px")  + ";margin-right:" + ("5px")  + ";}.GFVDQLFDKQ{width:" + ("180px")  + ";}.GFVDQLFDAQ{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";}.GFVDQLFDPP{padding:" + ("2px")  + ";margin-bottom:" + ("3px")  + ";background-color:") + (("#900")  + ";}.GFVDQLFDGQ{margin:" + ("10px")  + ";}.GFVDQLFDOP{margin-left:" + ("10px")  + ";margin-right:" + ("10px")  + ";margin-bottom:" + ("10px")  + ";margin-top:" + ("10px")  + ";}.GFVDQLFDLP{padding-top:" + ("5px")  + ";padding-bottom:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border-top-style:" + ("solid")  + ";border-top-width:" + ("1px") ) + (";border-top-color:" + ("#d0d0d0")  + ";border-bottom-style:" + ("solid")  + ";border-bottom-width:" + ("1px")  + ";border-bottom-color:" + ("#d0d0d0")  + ";}"));
      }
      public java.lang.String scrollPanel() {
        return "GFVDQLFDLP";
      }
      public java.lang.String wizardButtonbar() {
        return "GFVDQLFDMP";
      }
      public java.lang.String wizardDTableButtons() {
        return "GFVDQLFDNP";
      }
      public java.lang.String wizardDTableCaption() {
        return "GFVDQLFDOP";
      }
      public java.lang.String wizardDTableFieldContainerInvalid() {
        return "GFVDQLFDPP";
      }
      public java.lang.String wizardDTableFieldContainerValid() {
        return "GFVDQLFDAQ";
      }
      public java.lang.String wizardDTableFields() {
        return "GFVDQLFDBQ";
      }
      public java.lang.String wizardDTableHeader() {
        return "GFVDQLFDCQ";
      }
      public java.lang.String wizardDTableList() {
        return "GFVDQLFDDQ";
      }
      public java.lang.String wizardDTableMessage() {
        return "GFVDQLFDEQ";
      }
      public java.lang.String wizardDTableMessageContainer() {
        return "GFVDQLFDFQ";
      }
      public java.lang.String wizardDTableSummaryContainer() {
        return "GFVDQLFDGQ";
      }
      public java.lang.String wizardDTableValidationError() {
        return "GFVDQLFDHQ";
      }
      public java.lang.String wizardPageTitleContainer() {
        return "GFVDQLFDIQ";
      }
      public java.lang.String wizardPageTitleImageContainer() {
        return "GFVDQLFDJQ";
      }
      public java.lang.String wizardPageTitleLabelContainer() {
        return "GFVDQLFDKQ";
      }
    }
    ;
  }
  private static class cssInitializer {
    static {
      _instance0.cssInitializer();
    }
    static org.uberfire.ext.widgets.core.client.resources.WizardResources.WizardStyle get() {
      return css;
    }
  }
  public org.uberfire.ext.widgets.core.client.resources.WizardResources.WizardStyle css() {
    return cssInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.core.client.resources.WizardResources.WizardStyle css;
  
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
      case 'css': return this.@org.uberfire.ext.widgets.core.client.resources.WizardResources::css()();
    }
    return null;
  }-*/;
}
