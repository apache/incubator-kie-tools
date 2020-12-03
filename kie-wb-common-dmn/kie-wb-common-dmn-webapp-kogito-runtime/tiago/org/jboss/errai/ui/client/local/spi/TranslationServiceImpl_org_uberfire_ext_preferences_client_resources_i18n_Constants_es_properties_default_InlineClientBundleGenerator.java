package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_es_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_es_properties {
  private static TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_es_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_es_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-preferences-ui-client/7.47.0-SNAPSHOT/uberfire-preferences-ui-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/preferences/client/resources/i18n/Constants_es.properties
      public String getText() {
        return "#\n# Copyright 2016 Red Hat, Inc. and/or its affiliates.\n#\n# Licensed under the Apache License, Version 2.0 (the \"License\");\n# you may not use this file except in compliance with the License.\n# You may obtain a copy of the License at\n#\n#       http://www.apache.org/licenses/LICENSE-2.0\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\nPreferencesCentralActionsView.Save=Guardar\nPreferencesCentralActionsView.Cancel=Cancelar\nPreferencesCentralActionsView.ChangesUndone=Cambios anulados.\nTreeHierarchyStructureView.SaveSuccess=Los cambios se guardaron correctamente.\nAdminPagePresenter.NoScreenParameterError=En la solicitud de lugar, se espera una pantalla con el nombre del parámetro que contiene el nombre de la pantalla.\nAdminPagePresenter.NoScreenFoundError=No se agregó ninguna pantalla con el nombre {0} a la página de administración.\nAdminPagePerspective.GoBackToThePreviousPage=Ir a la página anterior\nPreferencesCentralPerspective.Preferences=Preferencias\nPropertyValidator.NotEmptyValidator.IsEmpty=No debe estar vacío.\nPropertyValidator.ConstrainedValuesValidator.NotAllowed=El valor no está permitido\nUnexpectedErrorWhileSaving=Se produjo un error inesperado al guardar: {0}\nAdmin=Admin\n\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_es_properties::getContents()();
    }
    return null;
  }-*/;
}
