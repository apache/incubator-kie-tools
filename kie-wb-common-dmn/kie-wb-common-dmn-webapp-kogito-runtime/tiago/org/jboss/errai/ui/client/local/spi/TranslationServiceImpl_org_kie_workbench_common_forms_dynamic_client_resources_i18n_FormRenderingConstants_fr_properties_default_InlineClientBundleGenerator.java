package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_fr_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/forms/kie-wb-common-dynamic-forms-client/7.47.0-SNAPSHOT/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/forms/dynamic/client/resources/i18n/FormRenderingConstants_fr.properties
      public String getText() {
        return "FieldConfigErrorViewImpl.unableToDisplayField=Impossible d’afficher le champ :\n\nMultipleSubform.noColumns=Aucune colonne n’est définie.\nMultipleSubform.noCreationForm=Aucun formulaire de création n’est sélectionné.\nMultipleSubform.wrongCreationForm=Mauvaise configuration du formulaire de création.\nMultipleSubform.noEditionForm=Aucun formulaire d’édition n’est défini.\nMultipleSubform.wrongEditionForm=Mauvaise configuration du formulaire d’édition.\n\nSubForm.noForm=Aucun formulaire n’est sélectionné.\nSubForm.wrongForm=Format sélectionné incorrect.\n\nFieldProperties.label=Étiquette\nFieldProperties.readOnly=Lecture seule\nFieldProperties.required=Obligatoire\nFieldProperties.validateOnChange=Valider lors de la modification de la valeur du champ\nFieldProperties.maxLength=Max. Longueur\nFieldProperties.placeHolder=PlaceHolder\nFieldProperties.helpMessage=Message d’aide\nFieldProperties.helpMessage.helpMessage=Utilisez ce champ pour définir le message d’aide (prenant en charge le format HTML) qui apparaîtra en regard du libellé.\nFieldProperties.defaultValue=Valeur par défaut\nFieldProperties.rows=Lignes visibles\nFieldProperties.addEmptyOption=Ajouter une option vide par défaut\n\nFieldProperties.showTime=Afficher l’heure\n\nFieldProperties.picture.size=Taille de l’image\n\nFieldProperties.selector.options=Options\nFieldProperties.selector.options.value=Valeur\nFieldProperties.selector.options.text=Texte\n\nFieldProperties.radios.inline=Afficher les options en ligne\n\nFieldProperties.slider.min=Min. Valeur\nFieldProperties.slider.max=Max. Valeur\nFieldProperties.slider.step=Étape\nFieldProperties.slider.precision=Précision\n\nFieldProperties.nestedForm=Formulaire imbriqué\n\nFieldProperties.mask=Masque de valeur\n\nFieldProperties.multipleSubform.creationForm=Formulaire de création\nFieldProperties.multipleSubform.editionForm=Formulaire d’édition\nFieldProperties.multipleSubform.columns=Colonnes du tableau\nFieldProperties.multipleSubform.columns.label=Légende\nFieldProperties.multipleSubform.columns.property=Propriété\n\nListBoxFieldRenderer.emptyOptionText=-- Sélectionner une valeur --\n\nFieldProperties.maxDropdownElements=Éléments visibles dans le menu déroulant\nFieldProperties.maxElementsOnTitle=Éléments visibles sur le titre du menu déroulant\nFieldProperties.allowFilter=Afficher le filtre de recherche\nFieldProperties.allowClearSelection=Afficher l’action Effacer la sélection\n\nFieldProperties.listOfValues=Éléments du sélecteur\n\nFieldProperties.pageSize=Taille de la page\n\nLOVCreationComponentViewImpl.addButton=Ajouter\nLOVCreationComponentViewImpl.removeButton=Supprimer les éléments sélectionnés\nLOVCreationComponentViewImpl.moveUp=Déplacer les éléments sélectionnés vers le haut\nLOVCreationComponentViewImpl.moveDown=Déplacer les éléments sélectionnés vers le bas\nLOVCreationComponentViewImpl.noItems=Aucun élément\n\nEditableColumnGenerator.valueHeader=Valeur\n\nCharacterEditableColumnGenerator.validationError=Valeur incorrecte : la longueur des éléments doit être d’un seul caractère\n\nInvalidInteger=Valeur incorrecte : la valeur doit être un nombre entier.\nInvalidIntegerWithRange=Valeur incorrecte : la valeur doit être un nombre entier compris entre {0} et {1}.\nInvalidDecimal=Valeur incorrecte : la valeur doit être un nombre décimal.\nInvalidDecimalWithRange=Valeur incorrecte : la valeur doit être un nombre décimal compris entre {0} et {1}.\n\nDecimalEditableColumnGenerator.invalidNumber=Valeur incorrecte : la valeur doit être un nombre décimal\nBooleanEditableColumnGenerator.yes=Vrai\nBooleanEditableColumnGenerator.no=Faux\n\nDatePickerWrapperViewImpl.showDateTooltip=afficher\nDatePickerWrapperViewImpl.clearDateTooltip=effacer";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}
