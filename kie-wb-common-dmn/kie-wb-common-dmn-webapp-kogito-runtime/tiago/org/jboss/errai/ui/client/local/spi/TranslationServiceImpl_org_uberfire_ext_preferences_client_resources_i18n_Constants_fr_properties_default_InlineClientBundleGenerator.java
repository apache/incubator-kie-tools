package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_fr_properties {
  private static TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-preferences-ui-client/7.47.0-SNAPSHOT/uberfire-preferences-ui-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/preferences/client/resources/i18n/Constants_fr.properties
      public String getText() {
        return "#\n# Copyright 2016 Red Hat, Inc. and/or its affiliates.\n#\n# Licensed under the Apache License, Version 2.0 (the \"License\");\n# you may not use this file except in compliance with the License.\n# You may obtain a copy of the License at\n#\n#       http://www.apache.org/licenses/LICENSE-2.0\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\nPreferencesCentralActionsView.Save=Enregistrer\nPreferencesCentralActionsView.Cancel=Annuler\nPreferencesCentralActionsView.ChangesUndone=Modifications annulées.\nTreeHierarchyStructureView.SaveSuccess=Les modifications ont été enregistrées.\nAdminPagePresenter.NoScreenParameterError=Un écran nommé avec un paramètre contenant son nom est attendu dans la demande d’emplacement.\nAdminPagePresenter.NoScreenFoundError=Aucun écran nommé {0} n’a été ajouté à la page d’administration.\nAdminPagePerspective.GoBackToThePreviousPage=Revenir à la page précédente\nPreferencesCentralPerspective.Preferences=Préférences\nPropertyValidator.NotEmptyValidator.IsEmpty=Ne doit pas être vide.\nPropertyValidator.ConstrainedValuesValidator.NotAllowed=La valeur n''est pas autorisée\nUnexpectedErrorWhileSaving=Erreur inattendue lors de l’enregistrement : {0}\nAdmin=Administrateur\n\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}
