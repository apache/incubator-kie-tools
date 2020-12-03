package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_es_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_es_properties {
  private static TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_es_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_es_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-experimental-client/7.47.0-SNAPSHOT/uberfire-experimental-client-7.47.0-SNAPSHOT.jar!/org/uberfire/experimental/client/resources/i18n/UberfireExperimentalConstants_es.properties
      public String getText() {
        return "ExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesTitle=Funcionalidades experimentales\nExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesWarning=Estas son funcionalidades experimentales que se están desarrollando; su uso en un entorno de producción puede causar inestabilidad. Úselas solo para fines de demostración.\n\nDisabledFeatureComponentViewImpl.header=Atención\nDisabledExperimentalFeature=No puede visualizar el contenido: <b>{0}</b> es una funcionalidad experimental que está actualmente deshabilitada. Intente ir al Editor de funcionalidades experimentales y habilítela.\nDisabledGlobalExperimentalFeature=No se puede visualizar el contenido: <b>{0}</b> es una funcionalidad experimental actualmente deshabilitada. Si desea verla, póngase en contacto con el administrador.\nDisabledFeatureTitle=Funcionalidad deshabilitada\n\nexperimentalFeatures.generalGroup=General\nexperimentalFeatures.globalGroup=Administración\n\nExperimentalFeaturesGroup.enableAll=Habilitar todo\n\nExperimentalFeaturesGroup.disableAll=Deshabilitar todo\n\nexperimentalFeatures.global=Funcionalidades experimentales globales\nexperimentalFeatures.globalHelp=Establece quién puede actualizar las funcionalidades experimentales globales en la perspectiva de la administración.\nexperimentalFeatures.globalEdit=Editar";
      }
      public String getName() {
        return "getContents";
      }
    }
    ;
  }
  private static class getContentsInitializer {
    static {
      _instance0.getContentsInitializer();
    }
    static com.google.gwt.resources.client.TextResource get() {
      return getContents;
    }
  }
  public com.google.gwt.resources.client.TextResource getContents() {
    return getContentsInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_es_properties::getContents()();
    }
    return null;
  }-*/;
}
