package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_es_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_es_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_es_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_es_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-forms/kie-wb-common-dynamic-forms/kie-wb-common-dynamic-forms-client/target/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/forms/dynamic/client/resources/i18n/FormRenderingConstants_es.properties
      public String getText() {
        return "FieldConfigErrorViewImpl.unableToDisplayField=No puede mostrarse el campo:\n\nMultipleSubform.noColumns=No hay columnas definidas.\nMultipleSubform.noCreationForm=No se seleccionó ningún formulario de creación.\nMultipleSubform.wrongCreationForm=Configuración incorrecta del formulario de creación.\nMultipleSubform.noEditionForm=No hay ningún formulario de edición definido.\nMultipleSubform.wrongEditionForm=Configuración incorrecta del formulario de edición.\n\nSubForm.noForm=No se seleccionó ningún formulario.\nSubForm.wrongForm=Se seleccionó el formulario incorrecto.\n\nFieldProperties.label=Etiqueta\nFieldProperties.readOnly=Solo lectura\nFieldProperties.required=Obligatorio\nFieldProperties.validateOnChange=Validar al cambiar el valor del campo\nFieldProperties.maxLength=Máx. Longitud\nFieldProperties.placeHolder=Marcador de posición\nFieldProperties.helpMessage=Mensaje de ayuda\nFieldProperties.helpMessage.helpMessage=Use este campo para configurar el mensaje de ayuda (admite el formato HTML) que aparecerá junto a la etiqueta del campo.\nFieldProperties.defaultValue=Valor predeterminado\nFieldProperties.rows=Filas visibles\nFieldProperties.addEmptyOption=Agregar la opción vacía predeterminada\n\nFieldProperties.showTime=Mostrar la hora\n\nFieldProperties.picture.size=Tamaño de la imagen\n\nFieldProperties.selector.options=Opciones\nFieldProperties.selector.options.value=Valor\nFieldProperties.selector.options.text=Texto\n\nFieldProperties.radios.inline=Mostrar las opciones en línea\n\nFieldProperties.slider.min=Mín. Valor\nFieldProperties.slider.max=Máx. Valor\nFieldProperties.slider.step=Paso\nFieldProperties.slider.precision=Precisión\n\nFieldProperties.nestedForm=Formulario anidado\n\nFieldProperties.mask=Máscara de valor\n\nFieldProperties.multipleSubform.creationForm=Formulario de creación\nFieldProperties.multipleSubform.editionForm=Formulario de edición\nFieldProperties.multipleSubform.columns=Columnas de la tabla\nFieldProperties.multipleSubform.columns.label=Leyenda\nFieldProperties.multipleSubform.columns.property=Propiedad\n\nListBoxFieldRenderer.emptyOptionText=-- Seleccionar un valor --\n\nFieldProperties.maxDropdownElements=Elementos visibles en el menú desplegable\nFieldProperties.maxElementsOnTitle=Elementos visibles en el título desplegable\nFieldProperties.allowFilter=Mostrar filtro de búsqueda\nFieldProperties.allowClearSelection=Mostrar la acción de anular selección\n\nFieldProperties.listOfValues=Ítems del selector\n\nFieldProperties.pageSize=Tamaño de la página\n\nLOVCreationComponentViewImpl.addButton=Agregar nuevo\nLOVCreationComponentViewImpl.removeButton=Eliminar los ítems seleccionados\nLOVCreationComponentViewImpl.moveUp=Mover hacia arriba los ítems seleccionados\nLOVCreationComponentViewImpl.moveDown=Mover hacia abajo los ítems seleccionados\nLOVCreationComponentViewImpl.noItems=No hay ítems\n\nEditableColumnGenerator.valueHeader=Valor\n\nCharacterEditableColumnGenerator.validationError=Valor incorrecto: la longitud de los elementos debe ser de un carácter.\n\nInvalidInteger=Valor incorrecto: el valor debe ser un número entero.\nInvalidIntegerWithRange=Valor incorrecto: el valor debe ser un número entero entre {0} y {1}.\nInvalidDecimal=Valor incorrecto: el valor debe ser un número decimal.\nInvalidDecimalWithRange=Valor incorrecto: el valor debe ser un número decimal entre {0} y {1}.\n\nDecimalEditableColumnGenerator.invalidNumber=Valor incorrecto: el valor debe ser un número decimal.\nBooleanEditableColumnGenerator.yes=Verdadero\nBooleanEditableColumnGenerator.no=Falso\n\nDatePickerWrapperViewImpl.showDateTooltip=mostrar\nDatePickerWrapperViewImpl.clearDateTooltip=borrar";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_es_properties::getContents()();
    }
    return null;
  }-*/;
}
