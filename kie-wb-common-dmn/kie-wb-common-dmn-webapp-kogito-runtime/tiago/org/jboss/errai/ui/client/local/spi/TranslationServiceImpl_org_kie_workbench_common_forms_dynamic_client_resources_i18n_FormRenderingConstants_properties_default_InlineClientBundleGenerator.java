package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/forms/kie-wb-common-dynamic-forms-client/7.47.0-SNAPSHOT/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/forms/dynamic/client/resources/i18n/FormRenderingConstants_default.properties
      public String getText() {
        return "FieldConfigErrorViewImpl.unableToDisplayField=Unable to display field:\n\nMultipleSubform.noColumns=There are no columns defined.\nMultipleSubform.noCreationForm=There's no creation form selected.\nMultipleSubform.wrongCreationForm=Wrong creation form configuration.\nMultipleSubform.noEditionForm=There's no edition form defined.\nMultipleSubform.wrongEditionForm=Wrong edition form configuration.\n\nSubForm.noForm=There's no selected form.\nSubForm.wrongForm=Wrong form selected.\n\nFieldProperties.label=Label\nFieldProperties.readOnly=Read Only\nFieldProperties.required=Required\nFieldProperties.validateOnChange=Validate on change field value\nFieldProperties.maxLength=Max. Length\nFieldProperties.placeHolder=PlaceHolder\nFieldProperties.helpMessage=Help Message\nFieldProperties.helpMessage.helpMessage=Use this field to set the help message (supports HTML format) that will appear next to the field label.\nFieldProperties.defaultValue=Default Value\nFieldProperties.rows=Visible rows\nFieldProperties.addEmptyOption=Add default empty option\n\nFieldProperties.showTime=Show Time\n\nFieldProperties.picture.size=Picture Size\n\nFieldProperties.selector.options=Options\nFieldProperties.selector.options.value=Value\nFieldProperties.selector.options.text=Text\n\nFieldProperties.radios.inline=Show options inline\n\nFieldProperties.slider.min=Min. Value\nFieldProperties.slider.max=Max. Value\nFieldProperties.slider.step=Step\nFieldProperties.slider.precision=Precision\n\nFieldProperties.nestedForm=Nested Form\n\nFieldProperties.mask=Value Mask\n\nFieldProperties.multipleSubform.creationForm=Creation Form\nFieldProperties.multipleSubform.editionForm=Edition Form\nFieldProperties.multipleSubform.columns=Table Columns\nFieldProperties.multipleSubform.columns.label=Caption\nFieldProperties.multipleSubform.columns.property=Property\n\nListBoxFieldRenderer.emptyOptionText=-- Select a value --\n\nFieldProperties.maxDropdownElements=Elements visible on the dropdown\nFieldProperties.maxElementsOnTitle=Elements visible on dropdown title\nFieldProperties.allowFilter=Show search filter\nFieldProperties.allowClearSelection=Show clear selection action\n\nFieldProperties.listOfValues=Selector items\n\nFieldProperties.pageSize=Page size\n\nLOVCreationComponentViewImpl.addButton=Add new\nLOVCreationComponentViewImpl.removeButton=Remove selected items\nLOVCreationComponentViewImpl.moveUp=Move up selected items\nLOVCreationComponentViewImpl.moveDown=Move down selected items\nLOVCreationComponentViewImpl.noItems=No Items\n\nEditableColumnGenerator.valueHeader=Value\n\nCharacterEditableColumnGenerator.validationError=Wrong value: elements length should be one character\n\nInvalidInteger=Wrong value: the value should be an integer number.\nInvalidIntegerWithRange=Wrong value: the value should be an integer number between {0} and {1}.\nInvalidDecimal=Wrong value: the value should be a decimal number.\nInvalidDecimalWithRange=Wrong value: the value should be a decimal number between {0} and {1}.\n\nDecimalEditableColumnGenerator.invalidNumber=Wrong value: the value should be a decimal number\nBooleanEditableColumnGenerator.yes=True\nBooleanEditableColumnGenerator.no=False\n\nDatePickerWrapperViewImpl.showDateTooltip=show\nDatePickerWrapperViewImpl.clearDateTooltip=clear";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_properties::getContents()();
    }
    return null;
  }-*/;
}
