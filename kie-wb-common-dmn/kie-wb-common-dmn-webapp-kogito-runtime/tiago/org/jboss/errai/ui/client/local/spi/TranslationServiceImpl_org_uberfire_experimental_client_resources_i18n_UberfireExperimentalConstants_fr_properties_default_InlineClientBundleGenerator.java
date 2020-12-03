package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_fr_properties {
  private static TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-experimental-client/7.47.0-SNAPSHOT/uberfire-experimental-client-7.47.0-SNAPSHOT.jar!/org/uberfire/experimental/client/resources/i18n/UberfireExperimentalConstants_fr.properties
      public String getText() {
        return "ExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesTitle=Fonctionnalités expérimentales\nExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesWarning=Il s’agit de fonctionnalités expérimentales en cours de développement. Les utiliser dans un environnement de production peut entraîner une instabilité. Veuillez les utiliser uniquement à des fins de démonstration.\n\nDisabledFeatureComponentViewImpl.header=Attention\nDisabledExperimentalFeature=Impossible d’afficher le contenu : <b>{0}</b> est une fonctionnalité expérimentale qui est actuellement désactivée. Essayez d’accéder à l’éditeur de fonctionnalités expérimentales et activez-le.\nDisabledGlobalExperimentalFeature=Impossible d’afficher le contenu : <b>{0}</b> est une fonctionnalité expérimentale qui est actuellement désactivée. Si vous souhaitez l’afficher, veuillez contacter votre administrateur.\nDisabledFeatureTitle=Fonctionnalité désactivée\n\nexperimentalFeatures.generalGroup=Général\nexperimentalFeatures.globalGroup=Administration\n\nExperimentalFeaturesGroup.enableAll=Activer tout\n\nExperimentalFeaturesGroup.disableAll=Désactiver tout\n\nexperimentalFeatures.global=Fonctionnalités expérimentales globales\nexperimentalFeatures.globalHelp=Définit les personnes autorisées à mettre à jour les fonctionnalités expérimentales globales au niveau de la perspective d’administration.\nexperimentalFeatures.globalEdit=Modifier";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}
