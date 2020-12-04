package org.jboss.errai.databinding.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.ui.shared.api.Locale;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement.ExtensionElements;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.background.BgColour;
import org.kie.workbench.common.dmn.api.property.background.BorderColour;
import org.kie.workbench.common.dmn.api.property.background.BorderSize;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.ConstraintTypeProperty;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.font.FontColour;
import org.kie.workbench.common.dmn.api.property.font.FontFamily;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.api.property.font.FontSize;
import org.kie.workbench.common.dmn.client.property.dmn.DocumentationLinksFieldDefinition;
import org.kie.workbench.common.dmn.client.property.dmn.NameFieldDefinition;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.definition.PictureFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.definition.PictureSize;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.type.PictureFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.BooleanMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.CharacterMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DateMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DecimalMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.IntegerMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.BooleanMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.CharacterMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DateMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DecimalMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.IntegerMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.StringMultipleSelectorFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.CharacterListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.DecimalListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.EnumListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.IntegerListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.CharacterRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.DecimalRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.DoubleSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.IntegerSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldType;

public class BindableProxyLoaderImpl implements BindableProxyLoader { public void loadBindableProxies() {
    class org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy extends OutputClauseUnaryTests implements BindableProxy {
      private BindableProxyAgent<OutputClauseUnaryTests> agent;
      private OutputClauseUnaryTests target;
      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy() {
        this(new OutputClauseUnaryTests());
      }

      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy(OutputClauseUnaryTests targetVal) {
        agent = new BindableProxyAgent<OutputClauseUnaryTests>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("constraintType", new PropertyType(ConstraintType.class, false, false));
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(OutputClauseUnaryTests.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public OutputClauseUnaryTests unwrap() {
        return target;
      }

      public OutputClauseUnaryTests deepUnwrap() {
        final OutputClauseUnaryTests clone = new OutputClauseUnaryTests();
        final OutputClauseUnaryTests t = unwrap();
        clone.setParent(t.getParent());
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public ConstraintType getConstraintType() {
        return target.getConstraintType();
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Id getId() {
        return target.getId();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "constraintType": return getConstraintType();
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "nsContext": return getNsContext();
          case "id": return getId();
          case "text": return getText();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("OutputClauseUnaryTests", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (OutputClauseUnaryTests) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("OutputClauseUnaryTests", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public OutputClauseUnaryTests copy() {
        final OutputClauseUnaryTests returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public void setConstraintTypeField(ConstraintType a0) {
        target.setConstraintTypeField(a0);
        agent.updateWidgetsAndFireEvents();
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(OutputClauseUnaryTests.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy((OutputClauseUnaryTests) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseUnaryTestsProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy extends BorderSize implements BindableProxy {
      private BindableProxyAgent<BorderSize> agent;
      private BorderSize target;
      public org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy() {
        this(new BorderSize());
      }

      public org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy(BorderSize targetVal) {
        agent = new BindableProxyAgent<BorderSize>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(BorderSize.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BorderSize unwrap() {
        return target;
      }

      public BorderSize deepUnwrap() {
        final BorderSize clone = new BorderSize();
        final BorderSize t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("BorderSize", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (BorderSize) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BorderSize", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(BorderSize.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy((BorderSize) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_background_BorderSizeProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy extends BackgroundSet implements BindableProxy {
      private BindableProxyAgent<BackgroundSet> agent;
      private BackgroundSet target;
      public org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy() {
        this(new BackgroundSet());
      }

      public org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy(BackgroundSet targetVal) {
        agent = new BindableProxyAgent<BackgroundSet>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("borderColour", new PropertyType(BorderColour.class, true, false));
        p.put("borderSize", new PropertyType(BorderSize.class, true, false));
        p.put("bgColour", new PropertyType(BgColour.class, true, false));
        p.put("this", new PropertyType(BackgroundSet.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BackgroundSet unwrap() {
        return target;
      }

      public BackgroundSet deepUnwrap() {
        final BackgroundSet clone = new BackgroundSet();
        final BackgroundSet t = unwrap();
        if (t.getBorderColour() instanceof BindableProxy) {
          clone.setBorderColour((BorderColour) ((BindableProxy) getBorderColour()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBorderColour())) {
          clone.setBorderColour((BorderColour) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBorderColour())).deepUnwrap());
        } else {
          clone.setBorderColour(t.getBorderColour());
        }
        if (t.getBorderSize() instanceof BindableProxy) {
          clone.setBorderSize((BorderSize) ((BindableProxy) getBorderSize()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBorderSize())) {
          clone.setBorderSize((BorderSize) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBorderSize())).deepUnwrap());
        } else {
          clone.setBorderSize(t.getBorderSize());
        }
        if (t.getBgColour() instanceof BindableProxy) {
          clone.setBgColour((BgColour) ((BindableProxy) getBgColour()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBgColour())) {
          clone.setBgColour((BgColour) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBgColour())).deepUnwrap());
        } else {
          clone.setBgColour(t.getBgColour());
        }
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public BorderColour getBorderColour() {
        return target.getBorderColour();
      }

      public void setBorderColour(BorderColour borderColour) {
        if (agent.binders.containsKey("borderColour")) {
          borderColour = (BorderColour) agent.binders.get("borderColour").setModel(borderColour, StateSync.FROM_MODEL, true);
        }
        changeAndFire("borderColour", borderColour);
      }

      public BorderSize getBorderSize() {
        return target.getBorderSize();
      }

      public void setBorderSize(BorderSize borderSize) {
        if (agent.binders.containsKey("borderSize")) {
          borderSize = (BorderSize) agent.binders.get("borderSize").setModel(borderSize, StateSync.FROM_MODEL, true);
        }
        changeAndFire("borderSize", borderSize);
      }

      public BgColour getBgColour() {
        return target.getBgColour();
      }

      public void setBgColour(BgColour bgColour) {
        if (agent.binders.containsKey("bgColour")) {
          bgColour = (BgColour) agent.binders.get("bgColour").setModel(bgColour, StateSync.FROM_MODEL, true);
        }
        changeAndFire("bgColour", bgColour);
      }

      public Object get(String property) {
        switch (property) {
          case "borderColour": return getBorderColour();
          case "borderSize": return getBorderSize();
          case "bgColour": return getBgColour();
          case "this": return target;
          default: throw new NonExistingPropertyException("BackgroundSet", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "borderColour": target.setBorderColour((BorderColour) value);
          break;
          case "borderSize": target.setBorderSize((BorderSize) value);
          break;
          case "bgColour": target.setBgColour((BgColour) value);
          break;
          case "this": target = (BackgroundSet) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BackgroundSet", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(BackgroundSet.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy((BackgroundSet) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_background_BackgroundSetProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy extends CheckBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CheckBoxFieldDefinition> agent;
      private CheckBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy() {
        this(new CheckBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy(CheckBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CheckBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(CheckBoxFieldType.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(CheckBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CheckBoxFieldDefinition unwrap() {
        return target;
      }

      public CheckBoxFieldDefinition deepUnwrap() {
        final CheckBoxFieldDefinition clone = new CheckBoxFieldDefinition();
        final CheckBoxFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setName(t.getName());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setId(t.getId());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public CheckBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "name": return getName();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "id": return getId();
          case "label": return getLabel();
          case "fieldType": return getFieldType();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "this": return target;
          default: throw new NonExistingPropertyException("CheckBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "this": target = (CheckBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CheckBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CheckBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy((CheckBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_checkBox_definition_CheckBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy extends UnaryTests implements BindableProxy {
      private BindableProxyAgent<UnaryTests> agent;
      private UnaryTests target;
      public org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy() {
        this(new UnaryTests());
      }

      public org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy(UnaryTests targetVal) {
        agent = new BindableProxyAgent<UnaryTests>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("constraintType", new PropertyType(ConstraintType.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(UnaryTests.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public UnaryTests unwrap() {
        return target;
      }

      public UnaryTests deepUnwrap() {
        final UnaryTests clone = new UnaryTests();
        final UnaryTests t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        clone.setConstraintType(t.getConstraintType());
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ConstraintType getConstraintType() {
        return target.getConstraintType();
      }

      public void setConstraintType(ConstraintType constraintType) {
        changeAndFire("constraintType", constraintType);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "expressionLanguage": return getExpressionLanguage();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "constraintType": return getConstraintType();
          case "nsContext": return getNsContext();
          case "text": return getText();
          case "id": return getId();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("UnaryTests", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "constraintType": target.setConstraintType((ConstraintType) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (UnaryTests) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("UnaryTests", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public UnaryTests copy() {
        final UnaryTests returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(UnaryTests.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy((UnaryTests) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_UnaryTestsProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy extends DateMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DateMultipleInputFieldDefinition> agent;
      private DateMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy() {
        this(new DateMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy(DateMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DateMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(DateMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DateMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public DateMultipleInputFieldDefinition deepUnwrap() {
        final DateMultipleInputFieldDefinition clone = new DateMultipleInputFieldDefinition();
        final DateMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DateMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DateMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DateMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DateMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy((DateMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DateMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy extends OutputClauseLiteralExpression implements BindableProxy {
      private BindableProxyAgent<OutputClauseLiteralExpression> agent;
      private OutputClauseLiteralExpression target;
      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy() {
        this(new OutputClauseLiteralExpression());
      }

      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy(OutputClauseLiteralExpression targetVal) {
        agent = new BindableProxyAgent<OutputClauseLiteralExpression>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("importedValues", new PropertyType(ImportedValues.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(OutputClauseLiteralExpression.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public OutputClauseLiteralExpression unwrap() {
        return target;
      }

      public OutputClauseLiteralExpression deepUnwrap() {
        final OutputClauseLiteralExpression clone = new OutputClauseLiteralExpression();
        final OutputClauseLiteralExpression t = unwrap();
        clone.setParent(t.getParent());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getImportedValues() instanceof BindableProxy) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) getImportedValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getImportedValues())) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getImportedValues())).deepUnwrap());
        } else {
          clone.setImportedValues(t.getImportedValues());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ImportedValues getImportedValues() {
        return target.getImportedValues();
      }

      public void setImportedValues(ImportedValues importedValues) {
        if (agent.binders.containsKey("importedValues")) {
          importedValues = (ImportedValues) agent.binders.get("importedValues").setModel(importedValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("importedValues", importedValues);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Id getId() {
        return target.getId();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "importedValues": return getImportedValues();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "id": return getId();
          case "text": return getText();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("OutputClauseLiteralExpression", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "importedValues": target.setImportedValues((ImportedValues) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (OutputClauseLiteralExpression) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("OutputClauseLiteralExpression", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public OutputClauseLiteralExpression copy() {
        final OutputClauseLiteralExpression returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(OutputClauseLiteralExpression.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy((OutputClauseLiteralExpression) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseLiteralExpressionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy extends QNameHolder implements BindableProxy {
      private BindableProxyAgent<QNameHolder> agent;
      private QNameHolder target;
      public org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy() {
        this(new QNameHolder());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy(QNameHolder targetVal) {
        agent = new BindableProxyAgent<QNameHolder>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("type", new PropertyType(org.kie.workbench.common.stunner.core.definition.property.PropertyType.class, false, false));
        p.put("value", new PropertyType(QName.class, false, false));
        p.put("this", new PropertyType(QNameHolder.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public QNameHolder unwrap() {
        return target;
      }

      public QNameHolder deepUnwrap() {
        final QNameHolder clone = new QNameHolder();
        final QNameHolder t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public org.kie.workbench.common.stunner.core.definition.property.PropertyType getType() {
        return target.getType();
      }

      public QName getValue() {
        return target.getValue();
      }

      public void setValue(QName value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "type": return getType();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("QNameHolder", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((QName) value);
          break;
          case "this": target = (QNameHolder) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("QNameHolder", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public QNameHolder copy() {
        final QNameHolder returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(QNameHolder.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy((QNameHolder) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_QNameHolderProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy extends Width implements BindableProxy {
      private BindableProxyAgent<Width> agent;
      private Width target;
      public org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy() {
        this(new Width());
      }

      public org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy(Width targetVal) {
        agent = new BindableProxyAgent<Width>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(Width.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Width unwrap() {
        return target;
      }

      public Width deepUnwrap() {
        final Width clone = new Width();
        final Width t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Width", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (Width) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Width", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(Width.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy((Width) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dimensions_WidthProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_AssociationProxy extends Association implements BindableProxy {
      private BindableProxyAgent<Association> agent;
      private Association target;
      public org_kie_workbench_common_dmn_api_definition_model_AssociationProxy() {
        this(new Association());
      }

      public org_kie_workbench_common_dmn_api_definition_model_AssociationProxy(Association targetVal) {
        agent = new BindableProxyAgent<Association>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(Association.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Association unwrap() {
        return target;
      }

      public Association deepUnwrap() {
        final Association clone = new Association();
        final Association t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_AssociationProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_AssociationProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "nsContext": return getNsContext();
          case "extensionElements": return getExtensionElements();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "id": return getId();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("Association", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (Association) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Association", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Association.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_AssociationProxy((Association) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_AssociationProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy extends InputClauseUnaryTests implements BindableProxy {
      private BindableProxyAgent<InputClauseUnaryTests> agent;
      private InputClauseUnaryTests target;
      public org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy() {
        this(new InputClauseUnaryTests());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy(InputClauseUnaryTests targetVal) {
        agent = new BindableProxyAgent<InputClauseUnaryTests>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("constraintType", new PropertyType(ConstraintType.class, false, false));
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("constraintTypeProperty", new PropertyType(ConstraintTypeProperty.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InputClauseUnaryTests.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InputClauseUnaryTests unwrap() {
        return target;
      }

      public InputClauseUnaryTests deepUnwrap() {
        final InputClauseUnaryTests clone = new InputClauseUnaryTests();
        final InputClauseUnaryTests t = unwrap();
        clone.setParent(t.getParent());
        if (t.getConstraintTypeProperty() instanceof BindableProxy) {
          clone.setConstraintTypeProperty((ConstraintTypeProperty) ((BindableProxy) getConstraintTypeProperty()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getConstraintTypeProperty())) {
          clone.setConstraintTypeProperty((ConstraintTypeProperty) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getConstraintTypeProperty())).deepUnwrap());
        } else {
          clone.setConstraintTypeProperty(t.getConstraintTypeProperty());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public ConstraintType getConstraintType() {
        return target.getConstraintType();
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public ConstraintTypeProperty getConstraintTypeProperty() {
        return target.getConstraintTypeProperty();
      }

      public void setConstraintTypeProperty(ConstraintTypeProperty constraintTypeProperty) {
        if (agent.binders.containsKey("constraintTypeProperty")) {
          constraintTypeProperty = (ConstraintTypeProperty) agent.binders.get("constraintTypeProperty").setModel(constraintTypeProperty, StateSync.FROM_MODEL, true);
        }
        changeAndFire("constraintTypeProperty", constraintTypeProperty);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Id getId() {
        return target.getId();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "constraintType": return getConstraintType();
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "constraintTypeProperty": return getConstraintTypeProperty();
          case "nsContext": return getNsContext();
          case "id": return getId();
          case "text": return getText();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InputClauseUnaryTests", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "constraintTypeProperty": target.setConstraintTypeProperty((ConstraintTypeProperty) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InputClauseUnaryTests) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InputClauseUnaryTests", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public InputClauseUnaryTests copy() {
        final InputClauseUnaryTests returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InputClauseUnaryTests.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy((InputClauseUnaryTests) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseUnaryTestsProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_font_FontSizeProxy extends FontSize implements BindableProxy {
      private BindableProxyAgent<FontSize> agent;
      private FontSize target;
      public org_kie_workbench_common_dmn_api_property_font_FontSizeProxy() {
        this(new FontSize());
      }

      public org_kie_workbench_common_dmn_api_property_font_FontSizeProxy(FontSize targetVal) {
        agent = new BindableProxyAgent<FontSize>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(FontSize.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public FontSize unwrap() {
        return target;
      }

      public FontSize deepUnwrap() {
        final FontSize clone = new FontSize();
        final FontSize t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_font_FontSizeProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_font_FontSizeProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("FontSize", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (FontSize) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("FontSize", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(FontSize.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_font_FontSizeProxy((FontSize) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_font_FontSizeProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy extends DMNDefinitionSet implements BindableProxy {
      private BindableProxyAgent<DMNDefinitionSet> agent;
      private DMNDefinitionSet target;
      public org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy() {
        this(new DMNDefinitionSet());
      }

      public org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy(DMNDefinitionSet targetVal) {
        agent = new BindableProxyAgent<DMNDefinitionSet>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("description", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(DMNDefinitionSet.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DMNDefinitionSet unwrap() {
        return target;
      }

      public DMNDefinitionSet deepUnwrap() {
        final DMNDefinitionSet clone = new DMNDefinitionSet();
        final DMNDefinitionSet t = unwrap();
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy) {
          obj = ((org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getDescription() {
        return target.getDescription();
      }

      public Object get(String property) {
        switch (property) {
          case "description": return getDescription();
          case "this": return target;
          default: throw new NonExistingPropertyException("DMNDefinitionSet", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "this": target = (DMNDefinitionSet) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DMNDefinitionSet", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DMNDefinitionSet.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy((DMNDefinitionSet) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_DMNDefinitionSetProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy extends KnowledgeRequirement implements BindableProxy {
      private BindableProxyAgent<KnowledgeRequirement> agent;
      private KnowledgeRequirement target;
      public org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy() {
        this(new KnowledgeRequirement());
      }

      public org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy(KnowledgeRequirement targetVal) {
        agent = new BindableProxyAgent<KnowledgeRequirement>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(KnowledgeRequirement.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public KnowledgeRequirement unwrap() {
        return target;
      }

      public KnowledgeRequirement deepUnwrap() {
        final KnowledgeRequirement clone = new KnowledgeRequirement();
        final KnowledgeRequirement t = unwrap();
        clone.setParent(t.getParent());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "nsContext": return getNsContext();
          case "stunnerLabels": return getStunnerLabels();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("KnowledgeRequirement", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (KnowledgeRequirement) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("KnowledgeRequirement", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(KnowledgeRequirement.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy((KnowledgeRequirement) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_KnowledgeRequirementProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy extends CharacterBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CharacterBoxFieldDefinition> agent;
      private CharacterBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy() {
        this(new CharacterBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy(CharacterBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CharacterBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(TextBoxFieldType.class, false, false));
        p.put("maxLength", new PropertyType(Integer.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(CharacterBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterBoxFieldDefinition unwrap() {
        return target;
      }

      public CharacterBoxFieldDefinition deepUnwrap() {
        final CharacterBoxFieldDefinition clone = new CharacterBoxFieldDefinition();
        final CharacterBoxFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setMaxLength(t.getMaxLength());
        clone.setPlaceHolder(t.getPlaceHolder());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public TextBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Integer getMaxLength() {
        return target.getMaxLength();
      }

      public void setMaxLength(Integer maxLength) {
        changeAndFire("maxLength", maxLength);
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "maxLength": return getMaxLength();
          case "placeHolder": return getPlaceHolder();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "maxLength": target.setMaxLength((Integer) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "this": target = (CharacterBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy((CharacterBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_CharacterBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy extends DecisionService implements BindableProxy {
      private BindableProxyAgent<DecisionService> agent;
      private DecisionService target;
      public org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy() {
        this(new DecisionService());
      }

      public org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy(DecisionService targetVal) {
        agent = new BindableProxyAgent<DecisionService>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("inputDecision", new PropertyType(List.class, false, true));
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("outputDecision", new PropertyType(List.class, false, true));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("dimensionsSet", new PropertyType(DecisionServiceRectangleDimensionsSet.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("dividerLineY", new PropertyType(DecisionServiceDividerLineY.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("inputData", new PropertyType(List.class, false, true));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("variable", new PropertyType(InformationItemPrimary.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("encapsulatedDecision", new PropertyType(List.class, false, true));
        p.put("linksHolder", new PropertyType(DocumentationLinksHolder.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(DecisionService.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecisionService unwrap() {
        return target;
      }

      public DecisionService deepUnwrap() {
        final DecisionService clone = new DecisionService();
        final DecisionService t = unwrap();
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((DecisionServiceRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((DecisionServiceRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getDividerLineY() instanceof BindableProxy) {
          clone.setDividerLineY((DecisionServiceDividerLineY) ((BindableProxy) getDividerLineY()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDividerLineY())) {
          clone.setDividerLineY((DecisionServiceDividerLineY) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDividerLineY())).deepUnwrap());
        } else {
          clone.setDividerLineY(t.getDividerLineY());
        }
        clone.setValue(t.getValue());
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        if (t.getVariable() instanceof BindableProxy) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) getVariable()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getVariable())) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getVariable())).deepUnwrap());
        } else {
          clone.setVariable(t.getVariable());
        }
        clone.setName(t.getName());
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getLinksHolder() instanceof BindableProxy) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) getLinksHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLinksHolder())) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLinksHolder())).deepUnwrap());
        } else {
          clone.setLinksHolder(t.getLinksHolder());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public List getInputDecision() {
        return target.getInputDecision();
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public List getOutputDecision() {
        return target.getOutputDecision();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public DecisionServiceRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(DecisionServiceRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (DecisionServiceRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public DecisionServiceDividerLineY getDividerLineY() {
        return target.getDividerLineY();
      }

      public void setDividerLineY(DecisionServiceDividerLineY dividerLineY) {
        if (agent.binders.containsKey("dividerLineY")) {
          dividerLineY = (DecisionServiceDividerLineY) agent.binders.get("dividerLineY").setModel(dividerLineY, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dividerLineY", dividerLineY);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public List getInputData() {
        return target.getInputData();
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public InformationItemPrimary getVariable() {
        return target.getVariable();
      }

      public void setVariable(InformationItemPrimary variable) {
        if (agent.binders.containsKey("variable")) {
          variable = (InformationItemPrimary) agent.binders.get("variable").setModel(variable, StateSync.FROM_MODEL, true);
        }
        changeAndFire("variable", variable);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public List getEncapsulatedDecision() {
        return target.getEncapsulatedDecision();
      }

      public DocumentationLinksHolder getLinksHolder() {
        return target.getLinksHolder();
      }

      public void setLinksHolder(DocumentationLinksHolder linksHolder) {
        if (agent.binders.containsKey("linksHolder")) {
          linksHolder = (DocumentationLinksHolder) agent.binders.get("linksHolder").setModel(linksHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("linksHolder", linksHolder);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "inputDecision": return getInputDecision();
          case "parent": return getParent();
          case "outputDecision": return getOutputDecision();
          case "defaultNamespace": return getDefaultNamespace();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "nsContext": return getNsContext();
          case "dimensionsSet": return getDimensionsSet();
          case "id": return getId();
          case "dividerLineY": return getDividerLineY();
          case "value": return getValue();
          case "inputData": return getInputData();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "stunnerCategory": return getStunnerCategory();
          case "stunnerLabels": return getStunnerLabels();
          case "contentDefinitionId": return getContentDefinitionId();
          case "backgroundSet": return getBackgroundSet();
          case "variable": return getVariable();
          case "name": return getName();
          case "fontSet": return getFontSet();
          case "encapsulatedDecision": return getEncapsulatedDecision();
          case "linksHolder": return getLinksHolder();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecisionService", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((DecisionServiceRectangleDimensionsSet) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "dividerLineY": target.setDividerLineY((DecisionServiceDividerLineY) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "variable": target.setVariable((InformationItemPrimary) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "linksHolder": target.setLinksHolder((DocumentationLinksHolder) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (DecisionService) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecisionService", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(DecisionService.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy((DecisionService) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_DecisionServiceProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy extends DecimalRadioGroupFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DecimalRadioGroupFieldDefinition> agent;
      private DecimalRadioGroupFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy() {
        this(new DecimalRadioGroupFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy(DecimalRadioGroupFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DecimalRadioGroupFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Double.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("inline", new PropertyType(Boolean.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(RadioGroupFieldType.class, false, false));
        p.put("this", new PropertyType(DecimalRadioGroupFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalRadioGroupFieldDefinition unwrap() {
        return target;
      }

      public DecimalRadioGroupFieldDefinition deepUnwrap() {
        final DecimalRadioGroupFieldDefinition clone = new DecimalRadioGroupFieldDefinition();
        final DecimalRadioGroupFieldDefinition t = unwrap();
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setInline(t.getInline());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Double getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Double defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Boolean getInline() {
        return target.getInline();
      }

      public void setInline(Boolean inline) {
        changeAndFire("inline", inline);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<DecimalSelectorOption> options) {
        List<DecimalSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public RadioGroupFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "inline": return getInline();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalRadioGroupFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Double) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "inline": target.setInline((Boolean) value);
          break;
          case "options": target.setOptions((List<DecimalSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DecimalRadioGroupFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalRadioGroupFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalRadioGroupFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy((DecimalRadioGroupFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_DecimalRadioGroupFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy extends QNameFieldDefinition implements BindableProxy {
      private BindableProxyAgent<QNameFieldDefinition> agent;
      private QNameFieldDefinition target;
      public org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy() {
        this(new QNameFieldDefinition());
      }

      public org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy(QNameFieldDefinition targetVal) {
        agent = new BindableProxyAgent<QNameFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(QNameFieldType.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(QNameFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public QNameFieldDefinition unwrap() {
        return target;
      }

      public QNameFieldDefinition deepUnwrap() {
        final QNameFieldDefinition clone = new QNameFieldDefinition();
        final QNameFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setPlaceHolder(t.getPlaceHolder());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public QNameFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "placeHolder": return getPlaceHolder();
          case "this": return target;
          default: throw new NonExistingPropertyException("QNameFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "this": target = (QNameFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("QNameFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(QNameFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy((QNameFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_client_property_dmn_QNameFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy extends KnowledgeSourceType implements BindableProxy {
      private BindableProxyAgent<KnowledgeSourceType> agent;
      private KnowledgeSourceType target;
      public org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy() {
        this(new KnowledgeSourceType());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy(KnowledgeSourceType targetVal) {
        agent = new BindableProxyAgent<KnowledgeSourceType>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(KnowledgeSourceType.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public KnowledgeSourceType unwrap() {
        return target;
      }

      public KnowledgeSourceType deepUnwrap() {
        final KnowledgeSourceType clone = new KnowledgeSourceType();
        final KnowledgeSourceType t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("KnowledgeSourceType", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (KnowledgeSourceType) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("KnowledgeSourceType", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(KnowledgeSourceType.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy((KnowledgeSourceType) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_KnowledgeSourceTypeProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy extends CharacterRadioGroupFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CharacterRadioGroupFieldDefinition> agent;
      private CharacterRadioGroupFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy() {
        this(new CharacterRadioGroupFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy(CharacterRadioGroupFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CharacterRadioGroupFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Character.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("inline", new PropertyType(Boolean.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(RadioGroupFieldType.class, false, false));
        p.put("this", new PropertyType(CharacterRadioGroupFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterRadioGroupFieldDefinition unwrap() {
        return target;
      }

      public CharacterRadioGroupFieldDefinition deepUnwrap() {
        final CharacterRadioGroupFieldDefinition clone = new CharacterRadioGroupFieldDefinition();
        final CharacterRadioGroupFieldDefinition t = unwrap();
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setInline(t.getInline());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Character getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Character defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Boolean getInline() {
        return target.getInline();
      }

      public void setInline(Boolean inline) {
        changeAndFire("inline", inline);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<CharacterSelectorOption> options) {
        List<CharacterSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public RadioGroupFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "inline": return getInline();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterRadioGroupFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Character) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "inline": target.setInline((Boolean) value);
          break;
          case "options": target.setOptions((List<CharacterSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (CharacterRadioGroupFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterRadioGroupFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterRadioGroupFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy((CharacterRadioGroupFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_CharacterRadioGroupFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy extends DecimalMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DecimalMultipleInputFieldDefinition> agent;
      private DecimalMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy() {
        this(new DecimalMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy(DecimalMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DecimalMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(DecimalMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public DecimalMultipleInputFieldDefinition deepUnwrap() {
        final DecimalMultipleInputFieldDefinition clone = new DecimalMultipleInputFieldDefinition();
        final DecimalMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DecimalMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy((DecimalMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_DecimalMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy extends StringListBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<StringListBoxFieldDefinition> agent;
      private StringListBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy() {
        this(new StringListBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy(StringListBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<StringListBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("addEmptyOption", new PropertyType(Boolean.class, false, false));
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ListBoxFieldType.class, false, false));
        p.put("this", new PropertyType(StringListBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public StringListBoxFieldDefinition unwrap() {
        return target;
      }

      public StringListBoxFieldDefinition deepUnwrap() {
        final StringListBoxFieldDefinition clone = new StringListBoxFieldDefinition();
        final StringListBoxFieldDefinition t = unwrap();
        clone.setAddEmptyOption(t.getAddEmptyOption());
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getAddEmptyOption() {
        return target.getAddEmptyOption();
      }

      public void setAddEmptyOption(Boolean addEmptyOption) {
        changeAndFire("addEmptyOption", addEmptyOption);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(String defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<StringSelectorOption> options) {
        List<StringSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ListBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "addEmptyOption": return getAddEmptyOption();
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("StringListBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "addEmptyOption": target.setAddEmptyOption((Boolean) value);
          break;
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "options": target.setOptions((List<StringSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (StringListBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("StringListBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(StringListBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy((StringListBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_StringListBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy extends DatePickerFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DatePickerFieldDefinition> agent;
      private DatePickerFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy() {
        this(new DatePickerFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy(DatePickerFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DatePickerFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("showTime", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(DatePickerFieldType.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(DatePickerFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DatePickerFieldDefinition unwrap() {
        return target;
      }

      public DatePickerFieldDefinition deepUnwrap() {
        final DatePickerFieldDefinition clone = new DatePickerFieldDefinition();
        final DatePickerFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setShowTime(t.getShowTime());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setPlaceHolder(t.getPlaceHolder());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Boolean getShowTime() {
        return target.getShowTime();
      }

      public void setShowTime(Boolean showTime) {
        changeAndFire("showTime", showTime);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public DatePickerFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "showTime": return getShowTime();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "placeHolder": return getPlaceHolder();
          case "this": return target;
          default: throw new NonExistingPropertyException("DatePickerFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "showTime": target.setShowTime((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "this": target = (DatePickerFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DatePickerFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DatePickerFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy((DatePickerFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_datePicker_definition_DatePickerFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy extends OutputClause implements BindableProxy {
      private BindableProxyAgent<OutputClause> agent;
      private OutputClause target;
      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy() {
        this(new OutputClause());
      }

      public org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy(OutputClause targetVal) {
        agent = new BindableProxyAgent<OutputClause>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("outputValues", new PropertyType(OutputClauseUnaryTests.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("defaultOutputEntry", new PropertyType(OutputClauseLiteralExpression.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(OutputClause.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public OutputClause unwrap() {
        return target;
      }

      public OutputClause deepUnwrap() {
        final OutputClause clone = new OutputClause();
        final OutputClause t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getOutputValues() instanceof BindableProxy) {
          clone.setOutputValues((OutputClauseUnaryTests) ((BindableProxy) getOutputValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getOutputValues())) {
          clone.setOutputValues((OutputClauseUnaryTests) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getOutputValues())).deepUnwrap());
        } else {
          clone.setOutputValues(t.getOutputValues());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getDefaultOutputEntry() instanceof BindableProxy) {
          clone.setDefaultOutputEntry((OutputClauseLiteralExpression) ((BindableProxy) getDefaultOutputEntry()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDefaultOutputEntry())) {
          clone.setDefaultOutputEntry((OutputClauseLiteralExpression) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDefaultOutputEntry())).deepUnwrap());
        } else {
          clone.setDefaultOutputEntry(t.getDefaultOutputEntry());
        }
        clone.setName(t.getName());
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public OutputClauseUnaryTests getOutputValues() {
        return target.getOutputValues();
      }

      public void setOutputValues(OutputClauseUnaryTests outputValues) {
        if (agent.binders.containsKey("outputValues")) {
          outputValues = (OutputClauseUnaryTests) agent.binders.get("outputValues").setModel(outputValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("outputValues", outputValues);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public OutputClauseLiteralExpression getDefaultOutputEntry() {
        return target.getDefaultOutputEntry();
      }

      public void setDefaultOutputEntry(OutputClauseLiteralExpression defaultOutputEntry) {
        if (agent.binders.containsKey("defaultOutputEntry")) {
          defaultOutputEntry = (OutputClauseLiteralExpression) agent.binders.get("defaultOutputEntry").setModel(defaultOutputEntry, StateSync.FROM_MODEL, true);
        }
        changeAndFire("defaultOutputEntry", defaultOutputEntry);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "outputValues": return getOutputValues();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "defaultOutputEntry": return getDefaultOutputEntry();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "name": return getName();
          case "id": return getId();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("OutputClause", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "outputValues": target.setOutputValues((OutputClauseUnaryTests) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "defaultOutputEntry": target.setDefaultOutputEntry((OutputClauseLiteralExpression) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (OutputClause) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("OutputClause", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public OutputClause copy() {
        final OutputClause returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(OutputClause.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy((OutputClause) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_OutputClauseProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy extends IntegerMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerMultipleInputFieldDefinition> agent;
      private IntegerMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy() {
        this(new IntegerMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy(IntegerMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(IntegerMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public IntegerMultipleInputFieldDefinition deepUnwrap() {
        final IntegerMultipleInputFieldDefinition clone = new IntegerMultipleInputFieldDefinition();
        final IntegerMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (IntegerMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy((IntegerMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_IntegerMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy extends CharacterListBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CharacterListBoxFieldDefinition> agent;
      private CharacterListBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy() {
        this(new CharacterListBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy(CharacterListBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CharacterListBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("addEmptyOption", new PropertyType(Boolean.class, false, false));
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Character.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ListBoxFieldType.class, false, false));
        p.put("this", new PropertyType(CharacterListBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterListBoxFieldDefinition unwrap() {
        return target;
      }

      public CharacterListBoxFieldDefinition deepUnwrap() {
        final CharacterListBoxFieldDefinition clone = new CharacterListBoxFieldDefinition();
        final CharacterListBoxFieldDefinition t = unwrap();
        clone.setAddEmptyOption(t.getAddEmptyOption());
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getAddEmptyOption() {
        return target.getAddEmptyOption();
      }

      public void setAddEmptyOption(Boolean addEmptyOption) {
        changeAndFire("addEmptyOption", addEmptyOption);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Character getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Character defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<CharacterSelectorOption> options) {
        List<CharacterSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ListBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "addEmptyOption": return getAddEmptyOption();
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterListBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "addEmptyOption": target.setAddEmptyOption((Boolean) value);
          break;
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Character) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "options": target.setOptions((List<CharacterSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (CharacterListBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterListBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterListBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy((CharacterListBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_CharacterListBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy extends IntegerMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerMultipleSelectorFieldDefinition> agent;
      private IntegerMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy() {
        this(new IntegerMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy(IntegerMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(IntegerMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public IntegerMultipleSelectorFieldDefinition deepUnwrap() {
        final IntegerMultipleSelectorFieldDefinition clone = new IntegerMultipleSelectorFieldDefinition();
        final IntegerMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<Long> listOfValues) {
        List<Long> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<Long>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (IntegerMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy((IntegerMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_IntegerMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy extends InputClauseLiteralExpression implements BindableProxy {
      private BindableProxyAgent<InputClauseLiteralExpression> agent;
      private InputClauseLiteralExpression target;
      public org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy() {
        this(new InputClauseLiteralExpression());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy(InputClauseLiteralExpression targetVal) {
        agent = new BindableProxyAgent<InputClauseLiteralExpression>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("typeRefHolder", new PropertyType(QNameHolder.class, true, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("importedValues", new PropertyType(ImportedValues.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InputClauseLiteralExpression.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InputClauseLiteralExpression unwrap() {
        return target;
      }

      public InputClauseLiteralExpression deepUnwrap() {
        final InputClauseLiteralExpression clone = new InputClauseLiteralExpression();
        final InputClauseLiteralExpression t = unwrap();
        clone.setParent(t.getParent());
        if (t.getTypeRefHolder() instanceof BindableProxy) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) getTypeRefHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getTypeRefHolder())) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getTypeRefHolder())).deepUnwrap());
        } else {
          clone.setTypeRefHolder(t.getTypeRefHolder());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getImportedValues() instanceof BindableProxy) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) getImportedValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getImportedValues())) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getImportedValues())).deepUnwrap());
        } else {
          clone.setImportedValues(t.getImportedValues());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public QNameHolder getTypeRefHolder() {
        return target.getTypeRefHolder();
      }

      public void setTypeRefHolder(QNameHolder typeRefHolder) {
        if (agent.binders.containsKey("typeRefHolder")) {
          typeRefHolder = (QNameHolder) agent.binders.get("typeRefHolder").setModel(typeRefHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("typeRefHolder", typeRefHolder);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ImportedValues getImportedValues() {
        return target.getImportedValues();
      }

      public void setImportedValues(ImportedValues importedValues) {
        if (agent.binders.containsKey("importedValues")) {
          importedValues = (ImportedValues) agent.binders.get("importedValues").setModel(importedValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("importedValues", importedValues);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Id getId() {
        return target.getId();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "typeRefHolder": return getTypeRefHolder();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "importedValues": return getImportedValues();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "id": return getId();
          case "text": return getText();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InputClauseLiteralExpression", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "typeRefHolder": target.setTypeRefHolder((QNameHolder) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "importedValues": target.setImportedValues((ImportedValues) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InputClauseLiteralExpression) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InputClauseLiteralExpression", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public InputClauseLiteralExpression copy() {
        final InputClauseLiteralExpression returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InputClauseLiteralExpression.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy((InputClauseLiteralExpression) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseLiteralExpressionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy extends InputClause implements BindableProxy {
      private BindableProxyAgent<InputClause> agent;
      private InputClause target;
      public org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy() {
        this(new InputClause());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy(InputClause targetVal) {
        agent = new BindableProxyAgent<InputClause>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("inputExpression", new PropertyType(InputClauseLiteralExpression.class, true, false));
        p.put("inputValues", new PropertyType(InputClauseUnaryTests.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InputClause.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InputClause unwrap() {
        return target;
      }

      public InputClause deepUnwrap() {
        final InputClause clone = new InputClause();
        final InputClause t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getInputExpression() instanceof BindableProxy) {
          clone.setInputExpression((InputClauseLiteralExpression) ((BindableProxy) getInputExpression()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getInputExpression())) {
          clone.setInputExpression((InputClauseLiteralExpression) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getInputExpression())).deepUnwrap());
        } else {
          clone.setInputExpression(t.getInputExpression());
        }
        if (t.getInputValues() instanceof BindableProxy) {
          clone.setInputValues((InputClauseUnaryTests) ((BindableProxy) getInputValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getInputValues())) {
          clone.setInputValues((InputClauseUnaryTests) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getInputValues())).deepUnwrap());
        } else {
          clone.setInputValues(t.getInputValues());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public InputClauseLiteralExpression getInputExpression() {
        return target.getInputExpression();
      }

      public void setInputExpression(InputClauseLiteralExpression inputExpression) {
        if (agent.binders.containsKey("inputExpression")) {
          inputExpression = (InputClauseLiteralExpression) agent.binders.get("inputExpression").setModel(inputExpression, StateSync.FROM_MODEL, true);
        }
        changeAndFire("inputExpression", inputExpression);
      }

      public InputClauseUnaryTests getInputValues() {
        return target.getInputValues();
      }

      public void setInputValues(InputClauseUnaryTests inputValues) {
        if (agent.binders.containsKey("inputValues")) {
          inputValues = (InputClauseUnaryTests) agent.binders.get("inputValues").setModel(inputValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("inputValues", inputValues);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "id": return getId();
          case "inputExpression": return getInputExpression();
          case "inputValues": return getInputValues();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InputClause", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "inputExpression": target.setInputExpression((InputClauseLiteralExpression) value);
          break;
          case "inputValues": target.setInputValues((InputClauseUnaryTests) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InputClause) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InputClause", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public InputClause copy() {
        final InputClause returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InputClause.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy((InputClause) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InputClauseProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy extends CharacterMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CharacterMultipleInputFieldDefinition> agent;
      private CharacterMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy() {
        this(new CharacterMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy(CharacterMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CharacterMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(CharacterMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public CharacterMultipleInputFieldDefinition deepUnwrap() {
        final CharacterMultipleInputFieldDefinition clone = new CharacterMultipleInputFieldDefinition();
        final CharacterMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (CharacterMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy((CharacterMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_CharacterMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy extends TextAnnotation implements BindableProxy {
      private BindableProxyAgent<TextAnnotation> agent;
      private TextAnnotation target;
      public org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy() {
        this(new TextAnnotation());
      }

      public org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy(TextAnnotation targetVal) {
        agent = new BindableProxyAgent<TextAnnotation>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("textFormat", new PropertyType(TextFormat.class, true, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("dimensionsSet", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(TextAnnotation.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public TextAnnotation unwrap() {
        return target;
      }

      public TextAnnotation deepUnwrap() {
        final TextAnnotation clone = new TextAnnotation();
        final TextAnnotation t = unwrap();
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getTextFormat() instanceof BindableProxy) {
          clone.setTextFormat((TextFormat) ((BindableProxy) getTextFormat()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getTextFormat())) {
          clone.setTextFormat((TextFormat) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getTextFormat())).deepUnwrap());
        } else {
          clone.setTextFormat(t.getTextFormat());
        }
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public TextFormat getTextFormat() {
        return target.getTextFormat();
      }

      public void setTextFormat(TextFormat textFormat) {
        if (agent.binders.containsKey("textFormat")) {
          textFormat = (TextFormat) agent.binders.get("textFormat").setModel(textFormat, StateSync.FROM_MODEL, true);
        }
        changeAndFire("textFormat", textFormat);
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public GeneralRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(GeneralRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (GeneralRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "stunnerLabels": return getStunnerLabels();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "textFormat": return getTextFormat();
          case "contentDefinitionId": return getContentDefinitionId();
          case "nsContext": return getNsContext();
          case "backgroundSet": return getBackgroundSet();
          case "fontSet": return getFontSet();
          case "dimensionsSet": return getDimensionsSet();
          case "text": return getText();
          case "id": return getId();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("TextAnnotation", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "textFormat": target.setTextFormat((TextFormat) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((GeneralRectangleDimensionsSet) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (TextAnnotation) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("TextAnnotation", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(TextAnnotation.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy((TextAnnotation) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_TextAnnotationProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy extends DMNDiagram implements BindableProxy {
      private BindableProxyAgent<DMNDiagram> agent;
      private DMNDiagram target;
      public org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy() {
        this(new DMNDiagram());
      }

      public org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy(DMNDiagram targetVal) {
        agent = new BindableProxyAgent<DMNDiagram>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("definitions", new PropertyType(Definitions.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(DMNDiagram.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DMNDiagram unwrap() {
        return target;
      }

      public DMNDiagram deepUnwrap() {
        final DMNDiagram clone = new DMNDiagram();
        final DMNDiagram t = unwrap();
        clone.setParent(t.getParent());
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getDefinitions() instanceof BindableProxy) {
          clone.setDefinitions((Definitions) ((BindableProxy) getDefinitions()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDefinitions())) {
          clone.setDefinitions((Definitions) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDefinitions())).deepUnwrap());
        } else {
          clone.setDefinitions(t.getDefinitions());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Definitions getDefinitions() {
        return target.getDefinitions();
      }

      public void setDefinitions(Definitions definitions) {
        if (agent.binders.containsKey("definitions")) {
          definitions = (Definitions) agent.binders.get("definitions").setModel(definitions, StateSync.FROM_MODEL, true);
        }
        changeAndFire("definitions", definitions);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "nsContext": return getNsContext();
          case "stunnerLabels": return getStunnerLabels();
          case "id": return getId();
          case "definitions": return getDefinitions();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("DMNDiagram", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "definitions": target.setDefinitions((Definitions) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (DMNDiagram) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DMNDiagram", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(DMNDiagram.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy((DMNDiagram) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_DMNDiagramProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy extends LiteralExpressionPMMLDocumentModel implements BindableProxy {
      private BindableProxyAgent<LiteralExpressionPMMLDocumentModel> agent;
      private LiteralExpressionPMMLDocumentModel target;
      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy() {
        this(new LiteralExpressionPMMLDocumentModel());
      }

      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy(LiteralExpressionPMMLDocumentModel targetVal) {
        agent = new BindableProxyAgent<LiteralExpressionPMMLDocumentModel>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("importedValues", new PropertyType(ImportedValues.class, true, false));
        p.put("requiredComponentWidthCount", new PropertyType(Integer.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("componentWidths", new PropertyType(List.class, false, true));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(LiteralExpressionPMMLDocumentModel.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public LiteralExpressionPMMLDocumentModel unwrap() {
        return target;
      }

      public LiteralExpressionPMMLDocumentModel deepUnwrap() {
        final LiteralExpressionPMMLDocumentModel clone = new LiteralExpressionPMMLDocumentModel();
        final LiteralExpressionPMMLDocumentModel t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getImportedValues() instanceof BindableProxy) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) getImportedValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getImportedValues())) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getImportedValues())).deepUnwrap());
        } else {
          clone.setImportedValues(t.getImportedValues());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ImportedValues getImportedValues() {
        return target.getImportedValues();
      }

      public void setImportedValues(ImportedValues importedValues) {
        if (agent.binders.containsKey("importedValues")) {
          importedValues = (ImportedValues) agent.binders.get("importedValues").setModel(importedValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("importedValues", importedValues);
      }

      public int getRequiredComponentWidthCount() {
        return target.getRequiredComponentWidthCount();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getComponentWidths() {
        return target.getComponentWidths();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "expressionLanguage": return getExpressionLanguage();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "importedValues": return getImportedValues();
          case "requiredComponentWidthCount": return getRequiredComponentWidthCount();
          case "nsContext": return getNsContext();
          case "componentWidths": return getComponentWidths();
          case "hasTypeRefs": return getHasTypeRefs();
          case "text": return getText();
          case "id": return getId();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("LiteralExpressionPMMLDocumentModel", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "importedValues": target.setImportedValues((ImportedValues) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (LiteralExpressionPMMLDocumentModel) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("LiteralExpressionPMMLDocumentModel", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public LiteralExpression copy() {
        final LiteralExpression returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(LiteralExpressionPMMLDocumentModel.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy((LiteralExpressionPMMLDocumentModel) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentModelProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy extends MultipleSubFormFieldDefinition implements BindableProxy {
      private BindableProxyAgent<MultipleSubFormFieldDefinition> agent;
      private MultipleSubFormFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy() {
        this(new MultipleSubFormFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy(MultipleSubFormFieldDefinition targetVal) {
        agent = new BindableProxyAgent<MultipleSubFormFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("container", new PropertyType(Container.class, false, false));
        p.put("creationForm", new PropertyType(String.class, false, false));
        p.put("columnMetas", new PropertyType(List.class, false, true));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("editionForm", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(MultipleSubFormFieldType.class, false, false));
        p.put("this", new PropertyType(MultipleSubFormFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public MultipleSubFormFieldDefinition unwrap() {
        return target;
      }

      public MultipleSubFormFieldDefinition deepUnwrap() {
        final MultipleSubFormFieldDefinition clone = new MultipleSubFormFieldDefinition();
        final MultipleSubFormFieldDefinition t = unwrap();
        clone.setContainer(t.getContainer());
        clone.setCreationForm(t.getCreationForm());
        if (t.getColumnMetas() != null) {
          final List columnMetasClone = new ArrayList();
          for (Object columnMetasElem : t.getColumnMetas()) {
            if (columnMetasElem instanceof BindableProxy) {
              columnMetasClone.add(((BindableProxy) columnMetasElem).deepUnwrap());
            } else {
              columnMetasClone.add(columnMetasElem);
            }
          }
          clone.setColumnMetas(columnMetasClone);
        }
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setEditionForm(t.getEditionForm());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Container getContainer() {
        return target.getContainer();
      }

      public void setContainer(Container container) {
        changeAndFire("container", container);
      }

      public String getCreationForm() {
        return target.getCreationForm();
      }

      public void setCreationForm(String creationForm) {
        changeAndFire("creationForm", creationForm);
      }

      public List getColumnMetas() {
        return target.getColumnMetas();
      }

      public void setColumnMetas(List<TableColumnMeta> columnMetas) {
        List<TableColumnMeta> oldValue = target.getColumnMetas();
        columnMetas = agent.ensureBoundListIsProxied("columnMetas", columnMetas);
        target.setColumnMetas(columnMetas);
        agent.updateWidgetsAndFireEvent(true, "columnMetas", oldValue, columnMetas);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public String getEditionForm() {
        return target.getEditionForm();
      }

      public void setEditionForm(String editionForm) {
        changeAndFire("editionForm", editionForm);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public MultipleSubFormFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "container": return getContainer();
          case "creationForm": return getCreationForm();
          case "columnMetas": return getColumnMetas();
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "editionForm": return getEditionForm();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("MultipleSubFormFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "container": target.setContainer((Container) value);
          break;
          case "creationForm": target.setCreationForm((String) value);
          break;
          case "columnMetas": target.setColumnMetas((List<TableColumnMeta>) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "editionForm": target.setEditionForm((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (MultipleSubFormFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("MultipleSubFormFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(MultipleSubFormFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy((MultipleSubFormFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_multipleSubform_definition_MultipleSubFormFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy extends IntegerRadioGroupFieldDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerRadioGroupFieldDefinition> agent;
      private IntegerRadioGroupFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy() {
        this(new IntegerRadioGroupFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy(IntegerRadioGroupFieldDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerRadioGroupFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Long.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("inline", new PropertyType(Boolean.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(RadioGroupFieldType.class, false, false));
        p.put("this", new PropertyType(IntegerRadioGroupFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerRadioGroupFieldDefinition unwrap() {
        return target;
      }

      public IntegerRadioGroupFieldDefinition deepUnwrap() {
        final IntegerRadioGroupFieldDefinition clone = new IntegerRadioGroupFieldDefinition();
        final IntegerRadioGroupFieldDefinition t = unwrap();
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setInline(t.getInline());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Long getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Long defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Boolean getInline() {
        return target.getInline();
      }

      public void setInline(Boolean inline) {
        changeAndFire("inline", inline);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<IntegerSelectorOption> options) {
        List<IntegerSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public RadioGroupFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "inline": return getInline();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerRadioGroupFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Long) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "inline": target.setInline((Boolean) value);
          break;
          case "options": target.setOptions((List<IntegerSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (IntegerRadioGroupFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerRadioGroupFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerRadioGroupFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy((IntegerRadioGroupFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_IntegerRadioGroupFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy extends NameFieldDefinition implements BindableProxy {
      private BindableProxyAgent<NameFieldDefinition> agent;
      private NameFieldDefinition target;
      public org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy() {
        this(new NameFieldDefinition());
      }

      public org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy(NameFieldDefinition targetVal) {
        agent = new BindableProxyAgent<NameFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(NameFieldType.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(NameFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public NameFieldDefinition unwrap() {
        return target;
      }

      public NameFieldDefinition deepUnwrap() {
        final NameFieldDefinition clone = new NameFieldDefinition();
        final NameFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setName(t.getName());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setId(t.getId());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public NameFieldType getFieldType() {
        return target.getFieldType();
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "name": return getName();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "id": return getId();
          case "label": return getLabel();
          case "fieldType": return getFieldType();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "this": return target;
          default: throw new NonExistingPropertyException("NameFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "this": target = (NameFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("NameFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(NameFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy((NameFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_client_property_dmn_NameFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy extends StringMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<StringMultipleInputFieldDefinition> agent;
      private StringMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy() {
        this(new StringMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy(StringMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<StringMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(StringMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public StringMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public StringMultipleInputFieldDefinition deepUnwrap() {
        final StringMultipleInputFieldDefinition clone = new StringMultipleInputFieldDefinition();
        final StringMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("StringMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (StringMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("StringMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(StringMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy((StringMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_StringMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy extends FontFamily implements BindableProxy {
      private BindableProxyAgent<FontFamily> agent;
      private FontFamily target;
      public org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy() {
        this(new FontFamily());
      }

      public org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy(FontFamily targetVal) {
        agent = new BindableProxyAgent<FontFamily>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(FontFamily.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public FontFamily unwrap() {
        return target;
      }

      public FontFamily deepUnwrap() {
        final FontFamily clone = new FontFamily();
        final FontFamily t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("FontFamily", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (FontFamily) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("FontFamily", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(FontFamily.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy((FontFamily) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_font_FontFamilyProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_font_FontColourProxy extends FontColour implements BindableProxy {
      private BindableProxyAgent<FontColour> agent;
      private FontColour target;
      public org_kie_workbench_common_dmn_api_property_font_FontColourProxy() {
        this(new FontColour());
      }

      public org_kie_workbench_common_dmn_api_property_font_FontColourProxy(FontColour targetVal) {
        agent = new BindableProxyAgent<FontColour>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("type", new PropertyType(org.kie.workbench.common.stunner.core.definition.property.PropertyType.class, false, false));
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(FontColour.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public FontColour unwrap() {
        return target;
      }

      public FontColour deepUnwrap() {
        final FontColour clone = new FontColour();
        final FontColour t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_font_FontColourProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_font_FontColourProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public org.kie.workbench.common.stunner.core.definition.property.PropertyType getType() {
        return target.getType();
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "type": return getType();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("FontColour", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (FontColour) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("FontColour", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(FontColour.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_font_FontColourProxy((FontColour) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_font_FontColourProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy extends EnumSelectorOption implements BindableProxy {
      private BindableProxyAgent<EnumSelectorOption> agent;
      private EnumSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy() {
        this(new EnumSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy(EnumSelectorOption targetVal) {
        agent = new BindableProxyAgent<EnumSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(Enum.class, false, false));
        p.put("this", new PropertyType(EnumSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public EnumSelectorOption unwrap() {
        return target;
      }

      public EnumSelectorOption deepUnwrap() {
        final EnumSelectorOption clone = new EnumSelectorOption();
        final EnumSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public Enum getValue() {
        return target.getValue();
      }

      public void setValue(Enum value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("EnumSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue((Enum) value);
          break;
          case "this": target = (EnumSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("EnumSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(EnumSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy((EnumSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_EnumSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy extends ConstraintTypeProperty implements BindableProxy {
      private BindableProxyAgent<ConstraintTypeProperty> agent;
      private ConstraintTypeProperty target;
      public org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy() {
        this(new ConstraintTypeProperty());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy(ConstraintTypeProperty targetVal) {
        agent = new BindableProxyAgent<ConstraintTypeProperty>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(ConstraintTypeProperty.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ConstraintTypeProperty unwrap() {
        return target;
      }

      public ConstraintTypeProperty deepUnwrap() {
        final ConstraintTypeProperty clone = new ConstraintTypeProperty();
        final ConstraintTypeProperty t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("ConstraintTypeProperty", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (ConstraintTypeProperty) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("ConstraintTypeProperty", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(ConstraintTypeProperty.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy((ConstraintTypeProperty) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_ConstraintTypePropertyProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_background_BorderColourProxy extends BorderColour implements BindableProxy {
      private BindableProxyAgent<BorderColour> agent;
      private BorderColour target;
      public org_kie_workbench_common_dmn_api_property_background_BorderColourProxy() {
        this(new BorderColour());
      }

      public org_kie_workbench_common_dmn_api_property_background_BorderColourProxy(BorderColour targetVal) {
        agent = new BindableProxyAgent<BorderColour>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("type", new PropertyType(org.kie.workbench.common.stunner.core.definition.property.PropertyType.class, false, false));
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(BorderColour.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BorderColour unwrap() {
        return target;
      }

      public BorderColour deepUnwrap() {
        final BorderColour clone = new BorderColour();
        final BorderColour t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_background_BorderColourProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_background_BorderColourProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public org.kie.workbench.common.stunner.core.definition.property.PropertyType getType() {
        return target.getType();
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "type": return getType();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("BorderColour", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (BorderColour) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BorderColour", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(BorderColour.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_background_BorderColourProxy((BorderColour) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_background_BorderColourProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_font_FontSetProxy extends FontSet implements BindableProxy {
      private BindableProxyAgent<FontSet> agent;
      private FontSet target;
      public org_kie_workbench_common_dmn_api_property_font_FontSetProxy() {
        this(new FontSet());
      }

      public org_kie_workbench_common_dmn_api_property_font_FontSetProxy(FontSet targetVal) {
        agent = new BindableProxyAgent<FontSet>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("fontFamily", new PropertyType(FontFamily.class, true, false));
        p.put("fontSize", new PropertyType(FontSize.class, true, false));
        p.put("fontColour", new PropertyType(FontColour.class, true, false));
        p.put("this", new PropertyType(FontSet.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public FontSet unwrap() {
        return target;
      }

      public FontSet deepUnwrap() {
        final FontSet clone = new FontSet();
        final FontSet t = unwrap();
        if (t.getFontFamily() instanceof BindableProxy) {
          clone.setFontFamily((FontFamily) ((BindableProxy) getFontFamily()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontFamily())) {
          clone.setFontFamily((FontFamily) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontFamily())).deepUnwrap());
        } else {
          clone.setFontFamily(t.getFontFamily());
        }
        if (t.getFontSize() instanceof BindableProxy) {
          clone.setFontSize((FontSize) ((BindableProxy) getFontSize()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSize())) {
          clone.setFontSize((FontSize) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSize())).deepUnwrap());
        } else {
          clone.setFontSize(t.getFontSize());
        }
        if (t.getFontColour() instanceof BindableProxy) {
          clone.setFontColour((FontColour) ((BindableProxy) getFontColour()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontColour())) {
          clone.setFontColour((FontColour) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontColour())).deepUnwrap());
        } else {
          clone.setFontColour(t.getFontColour());
        }
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_font_FontSetProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_font_FontSetProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public FontFamily getFontFamily() {
        return target.getFontFamily();
      }

      public void setFontFamily(FontFamily fontFamily) {
        if (agent.binders.containsKey("fontFamily")) {
          fontFamily = (FontFamily) agent.binders.get("fontFamily").setModel(fontFamily, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontFamily", fontFamily);
      }

      public FontSize getFontSize() {
        return target.getFontSize();
      }

      public void setFontSize(FontSize fontSize) {
        if (agent.binders.containsKey("fontSize")) {
          fontSize = (FontSize) agent.binders.get("fontSize").setModel(fontSize, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSize", fontSize);
      }

      public FontColour getFontColour() {
        return target.getFontColour();
      }

      public void setFontColour(FontColour fontColour) {
        if (agent.binders.containsKey("fontColour")) {
          fontColour = (FontColour) agent.binders.get("fontColour").setModel(fontColour, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontColour", fontColour);
      }

      public Object get(String property) {
        switch (property) {
          case "fontFamily": return getFontFamily();
          case "fontSize": return getFontSize();
          case "fontColour": return getFontColour();
          case "this": return target;
          default: throw new NonExistingPropertyException("FontSet", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "fontFamily": target.setFontFamily((FontFamily) value);
          break;
          case "fontSize": target.setFontSize((FontSize) value);
          break;
          case "fontColour": target.setFontColour((FontColour) value);
          break;
          case "this": target = (FontSet) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("FontSet", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(FontSet.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_font_FontSetProxy((FontSet) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_font_FontSetProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy extends DecimalSelectorOption implements BindableProxy {
      private BindableProxyAgent<DecimalSelectorOption> agent;
      private DecimalSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy() {
        this(new DecimalSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy(DecimalSelectorOption targetVal) {
        agent = new BindableProxyAgent<DecimalSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(DecimalSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalSelectorOption unwrap() {
        return target;
      }

      public DecimalSelectorOption deepUnwrap() {
        final DecimalSelectorOption clone = new DecimalSelectorOption();
        final DecimalSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (DecimalSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy((DecimalSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DecimalSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_TextProxy extends Text implements BindableProxy {
      private BindableProxyAgent<Text> agent;
      private Text target;
      public org_kie_workbench_common_dmn_api_property_dmn_TextProxy() {
        this(new Text());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_TextProxy(Text targetVal) {
        agent = new BindableProxyAgent<Text>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(Text.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Text unwrap() {
        return target;
      }

      public Text deepUnwrap() {
        final Text clone = new Text();
        final Text t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_TextProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_TextProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Text", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (Text) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Text", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Text copy() {
        final Text returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Text.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_TextProxy((Text) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_TextProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy extends CharacterMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<CharacterMultipleSelectorFieldDefinition> agent;
      private CharacterMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy() {
        this(new CharacterMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy(CharacterMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<CharacterMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(CharacterMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public CharacterMultipleSelectorFieldDefinition deepUnwrap() {
        final CharacterMultipleSelectorFieldDefinition clone = new CharacterMultipleSelectorFieldDefinition();
        final CharacterMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<Character> listOfValues) {
        List<Character> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<Character>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (CharacterMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy((CharacterMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_CharacterMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy extends StringMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<StringMultipleSelectorFieldDefinition> agent;
      private StringMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy() {
        this(new StringMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy(StringMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<StringMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(StringMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public StringMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public StringMultipleSelectorFieldDefinition deepUnwrap() {
        final StringMultipleSelectorFieldDefinition clone = new StringMultipleSelectorFieldDefinition();
        final StringMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<String> listOfValues) {
        List<String> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("StringMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<String>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (StringMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("StringMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(StringMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy((StringMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_StringMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy extends ColorPickerFieldDefinition implements BindableProxy {
      private BindableProxyAgent<ColorPickerFieldDefinition> agent;
      private ColorPickerFieldDefinition target;
      public org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy() {
        this(new ColorPickerFieldDefinition());
      }

      public org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy(ColorPickerFieldDefinition targetVal) {
        agent = new BindableProxyAgent<ColorPickerFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ColorPickerFieldType.class, false, false));
        p.put("this", new PropertyType(ColorPickerFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ColorPickerFieldDefinition unwrap() {
        return target;
      }

      public ColorPickerFieldDefinition deepUnwrap() {
        final ColorPickerFieldDefinition clone = new ColorPickerFieldDefinition();
        final ColorPickerFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(String defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ColorPickerFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("ColorPickerFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (ColorPickerFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("ColorPickerFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(ColorPickerFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy((ColorPickerFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_stunner_forms_model_ColorPickerFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy extends StringSelectorOption implements BindableProxy {
      private BindableProxyAgent<StringSelectorOption> agent;
      private StringSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy() {
        this(new StringSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy(StringSelectorOption targetVal) {
        agent = new BindableProxyAgent<StringSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(StringSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public StringSelectorOption unwrap() {
        return target;
      }

      public StringSelectorOption deepUnwrap() {
        final StringSelectorOption clone = new StringSelectorOption();
        final StringSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("StringSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue((String) value);
          break;
          case "this": target = (StringSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("StringSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(StringSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy((StringSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_StringSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy extends PictureFieldDefinition implements BindableProxy {
      private BindableProxyAgent<PictureFieldDefinition> agent;
      private PictureFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy() {
        this(new PictureFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy(PictureFieldDefinition targetVal) {
        agent = new BindableProxyAgent<PictureFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("size", new PropertyType(PictureSize.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(PictureFieldType.class, false, false));
        p.put("this", new PropertyType(PictureFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public PictureFieldDefinition unwrap() {
        return target;
      }

      public PictureFieldDefinition deepUnwrap() {
        final PictureFieldDefinition clone = new PictureFieldDefinition();
        final PictureFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setSize(t.getSize());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public PictureSize getSize() {
        return target.getSize();
      }

      public void setSize(PictureSize size) {
        changeAndFire("size", size);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public PictureFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "size": return getSize();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("PictureFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "size": target.setSize((PictureSize) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (PictureFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("PictureFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(PictureFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy((PictureFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_image_definition_PictureFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy extends CharacterSelectorOption implements BindableProxy {
      private BindableProxyAgent<CharacterSelectorOption> agent;
      private CharacterSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy() {
        this(new CharacterSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy(CharacterSelectorOption targetVal) {
        agent = new BindableProxyAgent<CharacterSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(Character.class, false, false));
        p.put("this", new PropertyType(CharacterSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public CharacterSelectorOption unwrap() {
        return target;
      }

      public CharacterSelectorOption deepUnwrap() {
        final CharacterSelectorOption clone = new CharacterSelectorOption();
        final CharacterSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public Character getValue() {
        return target.getValue();
      }

      public void setValue(Character value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("CharacterSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue((Character) value);
          break;
          case "this": target = (CharacterSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("CharacterSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(CharacterSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy((CharacterSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_CharacterSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy extends IntegerSliderDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerSliderDefinition> agent;
      private IntegerSliderDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy() {
        this(new IntegerSliderDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy(IntegerSliderDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerSliderDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("max", new PropertyType(Integer.class, false, false));
        p.put("precision", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("min", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("step", new PropertyType(Integer.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(SliderFieldType.class, false, false));
        p.put("this", new PropertyType(IntegerSliderDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerSliderDefinition unwrap() {
        return target;
      }

      public IntegerSliderDefinition deepUnwrap() {
        final IntegerSliderDefinition clone = new IntegerSliderDefinition();
        final IntegerSliderDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setMax(t.getMax());
        clone.setPrecision(t.getPrecision());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMin(t.getMin());
        clone.setName(t.getName());
        clone.setStep(t.getStep());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getMax() {
        return target.getMax();
      }

      public void setMax(Integer max) {
        changeAndFire("max", max);
      }

      public Integer getPrecision() {
        return target.getPrecision();
      }

      public void setPrecision(Integer precision) {
        changeAndFire("precision", precision);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMin() {
        return target.getMin();
      }

      public void setMin(Integer min) {
        changeAndFire("min", min);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Integer getStep() {
        return target.getStep();
      }

      public void setStep(Integer step) {
        changeAndFire("step", step);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public SliderFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "max": return getMax();
          case "precision": return getPrecision();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "min": return getMin();
          case "name": return getName();
          case "step": return getStep();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerSliderDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "max": target.setMax((Integer) value);
          break;
          case "precision": target.setPrecision((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "min": target.setMin((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "step": target.setStep((Integer) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (IntegerSliderDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerSliderDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerSliderDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy((IntegerSliderDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_IntegerSliderDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy extends DecisionServiceRectangleDimensionsSet implements BindableProxy {
      private BindableProxyAgent<DecisionServiceRectangleDimensionsSet> agent;
      private DecisionServiceRectangleDimensionsSet target;
      public org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy() {
        this(new DecisionServiceRectangleDimensionsSet());
      }

      public org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy(DecisionServiceRectangleDimensionsSet targetVal) {
        agent = new BindableProxyAgent<DecisionServiceRectangleDimensionsSet>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("width", new PropertyType(Width.class, true, false));
        p.put("maximumWidth", new PropertyType(Double.class, false, false));
        p.put("minimumHeight", new PropertyType(Double.class, false, false));
        p.put("maximumHeight", new PropertyType(Double.class, false, false));
        p.put("minimumWidth", new PropertyType(Double.class, false, false));
        p.put("height", new PropertyType(Height.class, true, false));
        p.put("this", new PropertyType(DecisionServiceRectangleDimensionsSet.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecisionServiceRectangleDimensionsSet unwrap() {
        return target;
      }

      public DecisionServiceRectangleDimensionsSet deepUnwrap() {
        final DecisionServiceRectangleDimensionsSet clone = new DecisionServiceRectangleDimensionsSet();
        final DecisionServiceRectangleDimensionsSet t = unwrap();
        if (t.getWidth() instanceof BindableProxy) {
          clone.setWidth((Width) ((BindableProxy) getWidth()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getWidth())) {
          clone.setWidth((Width) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getWidth())).deepUnwrap());
        } else {
          clone.setWidth(t.getWidth());
        }
        if (t.getHeight() instanceof BindableProxy) {
          clone.setHeight((Height) ((BindableProxy) getHeight()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getHeight())) {
          clone.setHeight((Height) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getHeight())).deepUnwrap());
        } else {
          clone.setHeight(t.getHeight());
        }
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Width getWidth() {
        return target.getWidth();
      }

      public void setWidth(Width width) {
        if (agent.binders.containsKey("width")) {
          width = (Width) agent.binders.get("width").setModel(width, StateSync.FROM_MODEL, true);
        }
        changeAndFire("width", width);
      }

      public double getMaximumWidth() {
        return target.getMaximumWidth();
      }

      public double getMinimumHeight() {
        return target.getMinimumHeight();
      }

      public double getMaximumHeight() {
        return target.getMaximumHeight();
      }

      public double getMinimumWidth() {
        return target.getMinimumWidth();
      }

      public Height getHeight() {
        return target.getHeight();
      }

      public void setHeight(Height height) {
        if (agent.binders.containsKey("height")) {
          height = (Height) agent.binders.get("height").setModel(height, StateSync.FROM_MODEL, true);
        }
        changeAndFire("height", height);
      }

      public Object get(String property) {
        switch (property) {
          case "width": return getWidth();
          case "maximumWidth": return getMaximumWidth();
          case "minimumHeight": return getMinimumHeight();
          case "maximumHeight": return getMaximumHeight();
          case "minimumWidth": return getMinimumWidth();
          case "height": return getHeight();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecisionServiceRectangleDimensionsSet", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "width": target.setWidth((Width) value);
          break;
          case "height": target.setHeight((Height) value);
          break;
          case "this": target = (DecisionServiceRectangleDimensionsSet) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecisionServiceRectangleDimensionsSet", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DecisionServiceRectangleDimensionsSet.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy((DecisionServiceRectangleDimensionsSet) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dimensions_DecisionServiceRectangleDimensionsSetProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy extends IntegerBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerBoxFieldDefinition> agent;
      private IntegerBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy() {
        this(new IntegerBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy(IntegerBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(IntegerBoxFieldType.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("maxLength", new PropertyType(Integer.class, false, false));
        p.put("this", new PropertyType(IntegerBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerBoxFieldDefinition unwrap() {
        return target;
      }

      public IntegerBoxFieldDefinition deepUnwrap() {
        final IntegerBoxFieldDefinition clone = new IntegerBoxFieldDefinition();
        final IntegerBoxFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setPlaceHolder(t.getPlaceHolder());
        clone.setMaxLength(t.getMaxLength());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public IntegerBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Integer getMaxLength() {
        return target.getMaxLength();
      }

      public void setMaxLength(Integer maxLength) {
        changeAndFire("maxLength", maxLength);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "placeHolder": return getPlaceHolder();
          case "maxLength": return getMaxLength();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "maxLength": target.setMaxLength((Integer) value);
          break;
          case "this": target = (IntegerBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy((IntegerBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_integerBox_definition_IntegerBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy extends AllowedAnswers implements BindableProxy {
      private BindableProxyAgent<AllowedAnswers> agent;
      private AllowedAnswers target;
      public org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy() {
        this(new AllowedAnswers());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy(AllowedAnswers targetVal) {
        agent = new BindableProxyAgent<AllowedAnswers>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(AllowedAnswers.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public AllowedAnswers unwrap() {
        return target;
      }

      public AllowedAnswers deepUnwrap() {
        final AllowedAnswers clone = new AllowedAnswers();
        final AllowedAnswers t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("AllowedAnswers", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (AllowedAnswers) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("AllowedAnswers", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public AllowedAnswers copy() {
        final AllowedAnswers returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(AllowedAnswers.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy((AllowedAnswers) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_AllowedAnswersProxy();
      }
    });
    class org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy extends DocumentationLinksFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DocumentationLinksFieldDefinition> agent;
      private DocumentationLinksFieldDefinition target;
      public org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy() {
        this(new DocumentationLinksFieldDefinition());
      }

      public org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy(DocumentationLinksFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DocumentationLinksFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(DocumentationLinksFieldType.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(DocumentationLinksFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DocumentationLinksFieldDefinition unwrap() {
        return target;
      }

      public DocumentationLinksFieldDefinition deepUnwrap() {
        final DocumentationLinksFieldDefinition clone = new DocumentationLinksFieldDefinition();
        final DocumentationLinksFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setName(t.getName());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setId(t.getId());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public DocumentationLinksFieldType getFieldType() {
        return target.getFieldType();
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "name": return getName();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "id": return getId();
          case "label": return getLabel();
          case "fieldType": return getFieldType();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "this": return target;
          default: throw new NonExistingPropertyException("DocumentationLinksFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "this": target = (DocumentationLinksFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DocumentationLinksFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DocumentationLinksFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy((DocumentationLinksFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_client_property_dmn_DocumentationLinksFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy extends Height implements BindableProxy {
      private BindableProxyAgent<Height> agent;
      private Height target;
      public org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy() {
        this(new Height());
      }

      public org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy(Height targetVal) {
        agent = new BindableProxyAgent<Height>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(Height.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Height unwrap() {
        return target;
      }

      public Height deepUnwrap() {
        final Height clone = new Height();
        final Height t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Height", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (Height) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Height", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(Height.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy((Height) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dimensions_HeightProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_background_BgColourProxy extends BgColour implements BindableProxy {
      private BindableProxyAgent<BgColour> agent;
      private BgColour target;
      public org_kie_workbench_common_dmn_api_property_background_BgColourProxy() {
        this(new BgColour());
      }

      public org_kie_workbench_common_dmn_api_property_background_BgColourProxy(BgColour targetVal) {
        agent = new BindableProxyAgent<BgColour>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("type", new PropertyType(org.kie.workbench.common.stunner.core.definition.property.PropertyType.class, false, false));
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(BgColour.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BgColour unwrap() {
        return target;
      }

      public BgColour deepUnwrap() {
        final BgColour clone = new BgColour();
        final BgColour t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_background_BgColourProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_background_BgColourProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public org.kie.workbench.common.stunner.core.definition.property.PropertyType getType() {
        return target.getType();
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "type": return getType();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("BgColour", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (BgColour) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BgColour", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(BgColour.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_background_BgColourProxy((BgColour) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_background_BgColourProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy extends TableColumnMeta implements BindableProxy {
      private BindableProxyAgent<TableColumnMeta> agent;
      private TableColumnMeta target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy() {
        this(new TableColumnMeta());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy(TableColumnMeta targetVal) {
        agent = new BindableProxyAgent<TableColumnMeta>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("property", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(TableColumnMeta.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public TableColumnMeta unwrap() {
        return target;
      }

      public TableColumnMeta deepUnwrap() {
        final TableColumnMeta clone = new TableColumnMeta();
        final TableColumnMeta t = unwrap();
        clone.setProperty(t.getProperty());
        clone.setLabel(t.getLabel());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getProperty() {
        return target.getProperty();
      }

      public void setProperty(String property) {
        changeAndFire("property", property);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Object get(String property) {
        switch (property) {
          case "property": return getProperty();
          case "label": return getLabel();
          case "this": return target;
          default: throw new NonExistingPropertyException("TableColumnMeta", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "property": target.setProperty((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "this": target = (TableColumnMeta) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("TableColumnMeta", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(TableColumnMeta.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy((TableColumnMeta) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_TableColumnMetaProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy extends SubFormFieldDefinition implements BindableProxy {
      private BindableProxyAgent<SubFormFieldDefinition> agent;
      private SubFormFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy() {
        this(new SubFormFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy(SubFormFieldDefinition targetVal) {
        agent = new BindableProxyAgent<SubFormFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("container", new PropertyType(Container.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(SubFormFieldType.class, false, false));
        p.put("nestedForm", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(SubFormFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public SubFormFieldDefinition unwrap() {
        return target;
      }

      public SubFormFieldDefinition deepUnwrap() {
        final SubFormFieldDefinition clone = new SubFormFieldDefinition();
        final SubFormFieldDefinition t = unwrap();
        clone.setContainer(t.getContainer());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setNestedForm(t.getNestedForm());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Container getContainer() {
        return target.getContainer();
      }

      public void setContainer(Container container) {
        changeAndFire("container", container);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public SubFormFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getNestedForm() {
        return target.getNestedForm();
      }

      public void setNestedForm(String nestedForm) {
        changeAndFire("nestedForm", nestedForm);
      }

      public Object get(String property) {
        switch (property) {
          case "container": return getContainer();
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "nestedForm": return getNestedForm();
          case "this": return target;
          default: throw new NonExistingPropertyException("SubFormFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "container": target.setContainer((Container) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "nestedForm": target.setNestedForm((String) value);
          break;
          case "this": target = (SubFormFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("SubFormFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(SubFormFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy((SubFormFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_relations_subForm_definition_SubFormFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy extends LiteralExpression implements BindableProxy {
      private BindableProxyAgent<LiteralExpression> agent;
      private LiteralExpression target;
      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy() {
        this(new LiteralExpression());
      }

      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy(LiteralExpression targetVal) {
        agent = new BindableProxyAgent<LiteralExpression>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("importedValues", new PropertyType(ImportedValues.class, true, false));
        p.put("requiredComponentWidthCount", new PropertyType(Integer.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("componentWidths", new PropertyType(List.class, false, true));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(LiteralExpression.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public LiteralExpression unwrap() {
        return target;
      }

      public LiteralExpression deepUnwrap() {
        final LiteralExpression clone = new LiteralExpression();
        final LiteralExpression t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getImportedValues() instanceof BindableProxy) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) getImportedValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getImportedValues())) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getImportedValues())).deepUnwrap());
        } else {
          clone.setImportedValues(t.getImportedValues());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ImportedValues getImportedValues() {
        return target.getImportedValues();
      }

      public void setImportedValues(ImportedValues importedValues) {
        if (agent.binders.containsKey("importedValues")) {
          importedValues = (ImportedValues) agent.binders.get("importedValues").setModel(importedValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("importedValues", importedValues);
      }

      public int getRequiredComponentWidthCount() {
        return target.getRequiredComponentWidthCount();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getComponentWidths() {
        return target.getComponentWidths();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "expressionLanguage": return getExpressionLanguage();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "importedValues": return getImportedValues();
          case "requiredComponentWidthCount": return getRequiredComponentWidthCount();
          case "nsContext": return getNsContext();
          case "componentWidths": return getComponentWidths();
          case "hasTypeRefs": return getHasTypeRefs();
          case "text": return getText();
          case "id": return getId();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("LiteralExpression", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "importedValues": target.setImportedValues((ImportedValues) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (LiteralExpression) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("LiteralExpression", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public LiteralExpression copy() {
        final LiteralExpression returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(LiteralExpression.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy((LiteralExpression) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy extends ExpressionLanguage implements BindableProxy {
      private BindableProxyAgent<ExpressionLanguage> agent;
      private ExpressionLanguage target;
      public org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy() {
        this(new ExpressionLanguage());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy(ExpressionLanguage targetVal) {
        agent = new BindableProxyAgent<ExpressionLanguage>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(ExpressionLanguage.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ExpressionLanguage unwrap() {
        return target;
      }

      public ExpressionLanguage deepUnwrap() {
        final ExpressionLanguage clone = new ExpressionLanguage();
        final ExpressionLanguage t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("ExpressionLanguage", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (ExpressionLanguage) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("ExpressionLanguage", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public ExpressionLanguage copy() {
        final ExpressionLanguage returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(ExpressionLanguage.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy((ExpressionLanguage) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_ExpressionLanguageProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy extends BooleanMultipleInputFieldDefinition implements BindableProxy {
      private BindableProxyAgent<BooleanMultipleInputFieldDefinition> agent;
      private BooleanMultipleInputFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy() {
        this(new BooleanMultipleInputFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy(BooleanMultipleInputFieldDefinition targetVal) {
        agent = new BindableProxyAgent<BooleanMultipleInputFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("pageSize", new PropertyType(Integer.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(BooleanMultipleInputFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BooleanMultipleInputFieldDefinition unwrap() {
        return target;
      }

      public BooleanMultipleInputFieldDefinition deepUnwrap() {
        final BooleanMultipleInputFieldDefinition clone = new BooleanMultipleInputFieldDefinition();
        final BooleanMultipleInputFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setPageSize(t.getPageSize());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Integer getPageSize() {
        return target.getPageSize();
      }

      public void setPageSize(Integer pageSize) {
        changeAndFire("pageSize", pageSize);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "pageSize": return getPageSize();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("BooleanMultipleInputFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "pageSize": target.setPageSize((Integer) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (BooleanMultipleInputFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BooleanMultipleInputFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(BooleanMultipleInputFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy((BooleanMultipleInputFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_input_impl_BooleanMultipleInputFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy extends NOPDomainObject implements BindableProxy {
      private BindableProxyAgent<NOPDomainObject> agent;
      private NOPDomainObject target;
      public org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy() {
        this(new NOPDomainObject());
      }

      public org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy(NOPDomainObject targetVal) {
        agent = new BindableProxyAgent<NOPDomainObject>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("this", new PropertyType(NOPDomainObject.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public NOPDomainObject unwrap() {
        return target;
      }

      public NOPDomainObject deepUnwrap() {
        final NOPDomainObject clone = new NOPDomainObject();
        final NOPDomainObject t = unwrap();
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Object get(String property) {
        switch (property) {
          case "domainObjectUUID": return getDomainObjectUUID();
          case "stunnerCategory": return getStunnerCategory();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "this": return target;
          default: throw new NonExistingPropertyException("NOPDomainObject", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "this": target = (NOPDomainObject) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("NOPDomainObject", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(NOPDomainObject.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy((NOPDomainObject) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_NOPDomainObjectProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy extends KnowledgeSource implements BindableProxy {
      private BindableProxyAgent<KnowledgeSource> agent;
      private KnowledgeSource target;
      public org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy() {
        this(new KnowledgeSource());
      }

      public org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy(KnowledgeSource targetVal) {
        agent = new BindableProxyAgent<KnowledgeSource>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("type", new PropertyType(KnowledgeSourceType.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("dimensionsSet", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("locationURI", new PropertyType(LocationURI.class, true, false));
        p.put("linksHolder", new PropertyType(DocumentationLinksHolder.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(KnowledgeSource.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public KnowledgeSource unwrap() {
        return target;
      }

      public KnowledgeSource deepUnwrap() {
        final KnowledgeSource clone = new KnowledgeSource();
        final KnowledgeSource t = unwrap();
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        if (t.getType() instanceof BindableProxy) {
          clone.setType((KnowledgeSourceType) ((BindableProxy) getType()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getType())) {
          clone.setType((KnowledgeSourceType) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getType())).deepUnwrap());
        } else {
          clone.setType(t.getType());
        }
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        clone.setName(t.getName());
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getLocationURI() instanceof BindableProxy) {
          clone.setLocationURI((LocationURI) ((BindableProxy) getLocationURI()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLocationURI())) {
          clone.setLocationURI((LocationURI) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLocationURI())).deepUnwrap());
        } else {
          clone.setLocationURI(t.getLocationURI());
        }
        if (t.getLinksHolder() instanceof BindableProxy) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) getLinksHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLinksHolder())) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLinksHolder())).deepUnwrap());
        } else {
          clone.setLinksHolder(t.getLinksHolder());
        }
        clone.setValue(t.getValue());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public KnowledgeSourceType getType() {
        return target.getType();
      }

      public void setType(KnowledgeSourceType type) {
        if (agent.binders.containsKey("type")) {
          type = (KnowledgeSourceType) agent.binders.get("type").setModel(type, StateSync.FROM_MODEL, true);
        }
        changeAndFire("type", type);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public GeneralRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(GeneralRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (GeneralRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public LocationURI getLocationURI() {
        return target.getLocationURI();
      }

      public void setLocationURI(LocationURI locationURI) {
        if (agent.binders.containsKey("locationURI")) {
          locationURI = (LocationURI) agent.binders.get("locationURI").setModel(locationURI, StateSync.FROM_MODEL, true);
        }
        changeAndFire("locationURI", locationURI);
      }

      public DocumentationLinksHolder getLinksHolder() {
        return target.getLinksHolder();
      }

      public void setLinksHolder(DocumentationLinksHolder linksHolder) {
        if (agent.binders.containsKey("linksHolder")) {
          linksHolder = (DocumentationLinksHolder) agent.binders.get("linksHolder").setModel(linksHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("linksHolder", linksHolder);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "stunnerLabels": return getStunnerLabels();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "type": return getType();
          case "nsContext": return getNsContext();
          case "contentDefinitionId": return getContentDefinitionId();
          case "backgroundSet": return getBackgroundSet();
          case "name": return getName();
          case "fontSet": return getFontSet();
          case "dimensionsSet": return getDimensionsSet();
          case "id": return getId();
          case "locationURI": return getLocationURI();
          case "linksHolder": return getLinksHolder();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("KnowledgeSource", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "type": target.setType((KnowledgeSourceType) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((GeneralRectangleDimensionsSet) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "locationURI": target.setLocationURI((LocationURI) value);
          break;
          case "linksHolder": target.setLinksHolder((DocumentationLinksHolder) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (KnowledgeSource) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("KnowledgeSource", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(KnowledgeSource.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy((KnowledgeSource) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_KnowledgeSourceProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy extends IntegerSelectorOption implements BindableProxy {
      private BindableProxyAgent<IntegerSelectorOption> agent;
      private IntegerSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy() {
        this(new IntegerSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy(IntegerSelectorOption targetVal) {
        agent = new BindableProxyAgent<IntegerSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(Long.class, false, false));
        p.put("this", new PropertyType(IntegerSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerSelectorOption unwrap() {
        return target;
      }

      public IntegerSelectorOption deepUnwrap() {
        final IntegerSelectorOption clone = new IntegerSelectorOption();
        final IntegerSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public Long getValue() {
        return target.getValue();
      }

      public void setValue(Long value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue((Long) value);
          break;
          case "this": target = (IntegerSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy((IntegerSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_IntegerSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy extends TextBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<TextBoxFieldDefinition> agent;
      private TextBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy() {
        this(new TextBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy(TextBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<TextBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(TextBoxFieldType.class, false, false));
        p.put("maxLength", new PropertyType(Integer.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(TextBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public TextBoxFieldDefinition unwrap() {
        return target;
      }

      public TextBoxFieldDefinition deepUnwrap() {
        final TextBoxFieldDefinition clone = new TextBoxFieldDefinition();
        final TextBoxFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setMaxLength(t.getMaxLength());
        clone.setPlaceHolder(t.getPlaceHolder());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public TextBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Integer getMaxLength() {
        return target.getMaxLength();
      }

      public void setMaxLength(Integer maxLength) {
        changeAndFire("maxLength", maxLength);
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "maxLength": return getMaxLength();
          case "placeHolder": return getPlaceHolder();
          case "this": return target;
          default: throw new NonExistingPropertyException("TextBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "maxLength": target.setMaxLength((Integer) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "this": target = (TextBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("TextBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(TextBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy((TextBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textBox_definition_TextBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_IdProxy extends Id implements BindableProxy {
      private BindableProxyAgent<Id> agent;
      private Id target;
      public org_kie_workbench_common_dmn_api_property_dmn_IdProxy() {
        this(new Id());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_IdProxy(Id targetVal) {
        agent = new BindableProxyAgent<Id>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(Id.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Id unwrap() {
        return target;
      }

      public Id deepUnwrap() {
        final Id clone = new Id();
        final Id t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_IdProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_IdProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public boolean isReadOnly() {
        return target.isReadOnly();
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "readOnly": return isReadOnly();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Id", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (Id) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Id", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(Id.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_IdProxy((Id) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_IdProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy extends DecimalBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DecimalBoxFieldDefinition> agent;
      private DecimalBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy() {
        this(new DecimalBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy(DecimalBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DecimalBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(DecimalBoxFieldType.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("maxLength", new PropertyType(Integer.class, false, false));
        p.put("this", new PropertyType(DecimalBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalBoxFieldDefinition unwrap() {
        return target;
      }

      public DecimalBoxFieldDefinition deepUnwrap() {
        final DecimalBoxFieldDefinition clone = new DecimalBoxFieldDefinition();
        final DecimalBoxFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setPlaceHolder(t.getPlaceHolder());
        clone.setMaxLength(t.getMaxLength());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public DecimalBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Integer getMaxLength() {
        return target.getMaxLength();
      }

      public void setMaxLength(Integer maxLength) {
        changeAndFire("maxLength", maxLength);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "placeHolder": return getPlaceHolder();
          case "maxLength": return getMaxLength();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "maxLength": target.setMaxLength((Integer) value);
          break;
          case "this": target = (DecimalBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy((DecimalBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_decimalBox_definition_DecimalBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy extends AuthorityRequirement implements BindableProxy {
      private BindableProxyAgent<AuthorityRequirement> agent;
      private AuthorityRequirement target;
      public org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy() {
        this(new AuthorityRequirement());
      }

      public org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy(AuthorityRequirement targetVal) {
        agent = new BindableProxyAgent<AuthorityRequirement>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(AuthorityRequirement.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public AuthorityRequirement unwrap() {
        return target;
      }

      public AuthorityRequirement deepUnwrap() {
        final AuthorityRequirement clone = new AuthorityRequirement();
        final AuthorityRequirement t = unwrap();
        clone.setParent(t.getParent());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "nsContext": return getNsContext();
          case "stunnerLabels": return getStunnerLabels();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("AuthorityRequirement", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (AuthorityRequirement) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("AuthorityRequirement", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(AuthorityRequirement.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy((AuthorityRequirement) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_AuthorityRequirementProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy extends DefaultSelectorOption implements BindableProxy {
      private BindableProxyAgent<DefaultSelectorOption> agent;
      private DefaultSelectorOption target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy() {
        this(new DefaultSelectorOption());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy(DefaultSelectorOption targetVal) {
        agent = new BindableProxyAgent<DefaultSelectorOption>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("text", new PropertyType(String.class, false, false));
        p.put("value", new PropertyType(Object.class, false, false));
        p.put("this", new PropertyType(DefaultSelectorOption.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DefaultSelectorOption unwrap() {
        return target;
      }

      public DefaultSelectorOption deepUnwrap() {
        final DefaultSelectorOption clone = new DefaultSelectorOption();
        final DefaultSelectorOption t = unwrap();
        clone.setText(t.getText());
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getText() {
        return target.getText();
      }

      public void setText(String text) {
        changeAndFire("text", text);
      }

      public Object getValue() {
        return target.getValue();
      }

      public void setValue(Object value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "text": return getText();
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("DefaultSelectorOption", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "text": target.setText((String) value);
          break;
          case "value": target.setValue(value);
          break;
          case "this": target = (DefaultSelectorOption) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DefaultSelectorOption", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DefaultSelectorOption.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy((DefaultSelectorOption) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_DefaultSelectorOptionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy extends BusinessKnowledgeModel implements BindableProxy {
      private BindableProxyAgent<BusinessKnowledgeModel> agent;
      private BusinessKnowledgeModel target;
      public org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy() {
        this(new BusinessKnowledgeModel());
      }

      public org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy(BusinessKnowledgeModel targetVal) {
        agent = new BindableProxyAgent<BusinessKnowledgeModel>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("encapsulatedLogic", new PropertyType(FunctionDefinition.class, false, false));
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("variable", new PropertyType(InformationItemPrimary.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("dimensionsSet", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("linksHolder", new PropertyType(DocumentationLinksHolder.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(BusinessKnowledgeModel.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BusinessKnowledgeModel unwrap() {
        return target;
      }

      public BusinessKnowledgeModel deepUnwrap() {
        final BusinessKnowledgeModel clone = new BusinessKnowledgeModel();
        final BusinessKnowledgeModel t = unwrap();
        clone.setEncapsulatedLogic(t.getEncapsulatedLogic());
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        if (t.getVariable() instanceof BindableProxy) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) getVariable()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getVariable())) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getVariable())).deepUnwrap());
        } else {
          clone.setVariable(t.getVariable());
        }
        clone.setName(t.getName());
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getLinksHolder() instanceof BindableProxy) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) getLinksHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLinksHolder())) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLinksHolder())).deepUnwrap());
        } else {
          clone.setLinksHolder(t.getLinksHolder());
        }
        clone.setValue(t.getValue());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public FunctionDefinition getEncapsulatedLogic() {
        return target.getEncapsulatedLogic();
      }

      public void setEncapsulatedLogic(FunctionDefinition encapsulatedLogic) {
        changeAndFire("encapsulatedLogic", encapsulatedLogic);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public InformationItemPrimary getVariable() {
        return target.getVariable();
      }

      public void setVariable(InformationItemPrimary variable) {
        if (agent.binders.containsKey("variable")) {
          variable = (InformationItemPrimary) agent.binders.get("variable").setModel(variable, StateSync.FROM_MODEL, true);
        }
        changeAndFire("variable", variable);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public GeneralRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(GeneralRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (GeneralRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public DocumentationLinksHolder getLinksHolder() {
        return target.getLinksHolder();
      }

      public void setLinksHolder(DocumentationLinksHolder linksHolder) {
        if (agent.binders.containsKey("linksHolder")) {
          linksHolder = (DocumentationLinksHolder) agent.binders.get("linksHolder").setModel(linksHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("linksHolder", linksHolder);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "encapsulatedLogic": return getEncapsulatedLogic();
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "nsContext": return getNsContext();
          case "contentDefinitionId": return getContentDefinitionId();
          case "backgroundSet": return getBackgroundSet();
          case "variable": return getVariable();
          case "name": return getName();
          case "fontSet": return getFontSet();
          case "dimensionsSet": return getDimensionsSet();
          case "id": return getId();
          case "linksHolder": return getLinksHolder();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("BusinessKnowledgeModel", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "encapsulatedLogic": target.setEncapsulatedLogic((FunctionDefinition) value);
          break;
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "variable": target.setVariable((InformationItemPrimary) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((GeneralRectangleDimensionsSet) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "linksHolder": target.setLinksHolder((DocumentationLinksHolder) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (BusinessKnowledgeModel) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BusinessKnowledgeModel", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public HasExpression asHasExpression() {
        final HasExpression returnValue = target.asHasExpression();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(BusinessKnowledgeModel.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy((BusinessKnowledgeModel) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_BusinessKnowledgeModelProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy extends DocumentationLinksHolder implements BindableProxy {
      private BindableProxyAgent<DocumentationLinksHolder> agent;
      private DocumentationLinksHolder target;
      public org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy() {
        this(new DocumentationLinksHolder());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy(DocumentationLinksHolder targetVal) {
        agent = new BindableProxyAgent<DocumentationLinksHolder>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(DocumentationLinks.class, false, false));
        p.put("this", new PropertyType(DocumentationLinksHolder.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DocumentationLinksHolder unwrap() {
        return target;
      }

      public DocumentationLinksHolder deepUnwrap() {
        final DocumentationLinksHolder clone = new DocumentationLinksHolder();
        final DocumentationLinksHolder t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DocumentationLinks getValue() {
        return target.getValue();
      }

      public void setValue(DocumentationLinks value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("DocumentationLinksHolder", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((DocumentationLinks) value);
          break;
          case "this": target = (DocumentationLinksHolder) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DocumentationLinksHolder", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DocumentationLinksHolder.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy((DocumentationLinksHolder) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_DocumentationLinksHolderProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy extends TextFormat implements BindableProxy {
      private BindableProxyAgent<TextFormat> agent;
      private TextFormat target;
      public org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy() {
        this(new TextFormat());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy(TextFormat targetVal) {
        agent = new BindableProxyAgent<TextFormat>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(TextFormat.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public TextFormat unwrap() {
        return target;
      }

      public TextFormat deepUnwrap() {
        final TextFormat clone = new TextFormat();
        final TextFormat t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("TextFormat", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (TextFormat) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("TextFormat", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(TextFormat.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy((TextFormat) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_TextFormatProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy extends Definitions implements BindableProxy {
      private BindableProxyAgent<Definitions> agent;
      private Definitions target;
      public org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy() {
        this(new Definitions());
      }

      public org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy(Definitions targetVal) {
        agent = new BindableProxyAgent<Definitions>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("import", new PropertyType(List.class, false, true));
        p.put("exporterVersion", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("elementCollection", new PropertyType(List.class, false, true));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("itemDefinition", new PropertyType(List.class, false, true));
        p.put("artifact", new PropertyType(List.class, false, true));
        p.put("businessContextElement", new PropertyType(List.class, false, true));
        p.put("exporter", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("typeLanguage", new PropertyType(String.class, false, false));
        p.put("namespace", new PropertyType(Text.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("diagramElements", new PropertyType(List.class, false, true));
        p.put("drgElement", new PropertyType(List.class, false, true));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(Definitions.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Definitions unwrap() {
        return target;
      }

      public Definitions deepUnwrap() {
        final Definitions clone = new Definitions();
        final Definitions t = unwrap();
        clone.setParent(t.getParent());
        clone.setExporterVersion(t.getExporterVersion());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        clone.setExporter(t.getExporter());
        clone.setTypeLanguage(t.getTypeLanguage());
        if (t.getNamespace() instanceof BindableProxy) {
          clone.setNamespace((Text) ((BindableProxy) getNamespace()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNamespace())) {
          clone.setNamespace((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNamespace())).deepUnwrap());
        } else {
          clone.setNamespace(t.getNamespace());
        }
        clone.setName(t.getName());
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        clone.setValue(t.getValue());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public List getImport() {
        return target.getImport();
      }

      public String getExporterVersion() {
        return target.getExporterVersion();
      }

      public void setExporterVersion(String exporterVersion) {
        changeAndFire("exporterVersion", exporterVersion);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public List getElementCollection() {
        return target.getElementCollection();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public List getItemDefinition() {
        return target.getItemDefinition();
      }

      public List getArtifact() {
        return target.getArtifact();
      }

      public List getBusinessContextElement() {
        return target.getBusinessContextElement();
      }

      public String getExporter() {
        return target.getExporter();
      }

      public void setExporter(String exporter) {
        changeAndFire("exporter", exporter);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public String getTypeLanguage() {
        return target.getTypeLanguage();
      }

      public void setTypeLanguage(String typeLanguage) {
        changeAndFire("typeLanguage", typeLanguage);
      }

      public Text getNamespace() {
        return target.getNamespace();
      }

      public void setNamespace(Text namespace) {
        if (agent.binders.containsKey("namespace")) {
          namespace = (Text) agent.binders.get("namespace").setModel(namespace, StateSync.FROM_MODEL, true);
        }
        changeAndFire("namespace", namespace);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public List getDiagramElements() {
        return target.getDiagramElements();
      }

      public List getDrgElement() {
        return target.getDrgElement();
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "import": return getImport();
          case "exporterVersion": return getExporterVersion();
          case "extensionElements": return getExtensionElements();
          case "elementCollection": return getElementCollection();
          case "expressionLanguage": return getExpressionLanguage();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "itemDefinition": return getItemDefinition();
          case "artifact": return getArtifact();
          case "businessContextElement": return getBusinessContextElement();
          case "exporter": return getExporter();
          case "nsContext": return getNsContext();
          case "typeLanguage": return getTypeLanguage();
          case "namespace": return getNamespace();
          case "name": return getName();
          case "id": return getId();
          case "diagramElements": return getDiagramElements();
          case "drgElement": return getDrgElement();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("Definitions", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "exporterVersion": target.setExporterVersion((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "exporter": target.setExporter((String) value);
          break;
          case "typeLanguage": target.setTypeLanguage((String) value);
          break;
          case "namespace": target.setNamespace((Text) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (Definitions) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Definitions", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void setDmnDiagramElements(List a0) {
        target.setDmnDiagramElements(a0);
        agent.updateWidgetsAndFireEvents();
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Definitions.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy((Definitions) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_DefinitionsProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy extends DoubleSliderDefinition implements BindableProxy {
      private BindableProxyAgent<DoubleSliderDefinition> agent;
      private DoubleSliderDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy() {
        this(new DoubleSliderDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy(DoubleSliderDefinition targetVal) {
        agent = new BindableProxyAgent<DoubleSliderDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("max", new PropertyType(Double.class, false, false));
        p.put("precision", new PropertyType(Double.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("min", new PropertyType(Double.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("step", new PropertyType(Double.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(SliderFieldType.class, false, false));
        p.put("this", new PropertyType(DoubleSliderDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DoubleSliderDefinition unwrap() {
        return target;
      }

      public DoubleSliderDefinition deepUnwrap() {
        final DoubleSliderDefinition clone = new DoubleSliderDefinition();
        final DoubleSliderDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setMax(t.getMax());
        clone.setPrecision(t.getPrecision());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMin(t.getMin());
        clone.setName(t.getName());
        clone.setStep(t.getStep());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Double getMax() {
        return target.getMax();
      }

      public void setMax(Double max) {
        changeAndFire("max", max);
      }

      public Double getPrecision() {
        return target.getPrecision();
      }

      public void setPrecision(Double precision) {
        changeAndFire("precision", precision);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Double getMin() {
        return target.getMin();
      }

      public void setMin(Double min) {
        changeAndFire("min", min);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Double getStep() {
        return target.getStep();
      }

      public void setStep(Double step) {
        changeAndFire("step", step);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public SliderFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "max": return getMax();
          case "precision": return getPrecision();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "min": return getMin();
          case "name": return getName();
          case "step": return getStep();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DoubleSliderDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "max": target.setMax((Double) value);
          break;
          case "precision": target.setPrecision((Double) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "min": target.setMin((Double) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "step": target.setStep((Double) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DoubleSliderDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DoubleSliderDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DoubleSliderDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy((DoubleSliderDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_slider_definition_DoubleSliderDefinitionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy extends EnumListBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<EnumListBoxFieldDefinition> agent;
      private EnumListBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy() {
        this(new EnumListBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy(EnumListBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<EnumListBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("addEmptyOption", new PropertyType(Boolean.class, false, false));
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Enum.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ListBoxFieldType.class, false, false));
        p.put("this", new PropertyType(EnumListBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public EnumListBoxFieldDefinition unwrap() {
        return target;
      }

      public EnumListBoxFieldDefinition deepUnwrap() {
        final EnumListBoxFieldDefinition clone = new EnumListBoxFieldDefinition();
        final EnumListBoxFieldDefinition t = unwrap();
        clone.setAddEmptyOption(t.getAddEmptyOption());
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getAddEmptyOption() {
        return target.getAddEmptyOption();
      }

      public void setAddEmptyOption(Boolean addEmptyOption) {
        changeAndFire("addEmptyOption", addEmptyOption);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Enum getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Enum defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<EnumSelectorOption> options) {
        List<EnumSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ListBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "addEmptyOption": return getAddEmptyOption();
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("EnumListBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "addEmptyOption": target.setAddEmptyOption((Boolean) value);
          break;
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Enum) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "options": target.setOptions((List<EnumSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (EnumListBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("EnumListBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(EnumListBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy((EnumListBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_EnumListBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy extends InformationRequirement implements BindableProxy {
      private BindableProxyAgent<InformationRequirement> agent;
      private InformationRequirement target;
      public org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy() {
        this(new InformationRequirement());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy(InformationRequirement targetVal) {
        agent = new BindableProxyAgent<InformationRequirement>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InformationRequirement.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InformationRequirement unwrap() {
        return target;
      }

      public InformationRequirement deepUnwrap() {
        final InformationRequirement clone = new InformationRequirement();
        final InformationRequirement t = unwrap();
        clone.setParent(t.getParent());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "nsContext": return getNsContext();
          case "stunnerLabels": return getStunnerLabels();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InformationRequirement", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InformationRequirement) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InformationRequirement", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InformationRequirement.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy((InformationRequirement) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationRequirementProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy extends LocationURI implements BindableProxy {
      private BindableProxyAgent<LocationURI> agent;
      private LocationURI target;
      public org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy() {
        this(new LocationURI());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy(LocationURI targetVal) {
        agent = new BindableProxyAgent<LocationURI>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(LocationURI.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public LocationURI unwrap() {
        return target;
      }

      public LocationURI deepUnwrap() {
        final LocationURI clone = new LocationURI();
        final LocationURI t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("LocationURI", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (LocationURI) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("LocationURI", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public LocationURI copy() {
        final LocationURI returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(LocationURI.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy((LocationURI) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_LocationURIProxy();
      }
    });
    class org_jboss_errai_ui_shared_api_LocaleProxy extends Locale implements BindableProxy {
      private BindableProxyAgent<Locale> agent;
      private Locale target;
      public org_jboss_errai_ui_shared_api_LocaleProxy() {
        this(new Locale());
      }

      public org_jboss_errai_ui_shared_api_LocaleProxy(Locale targetVal) {
        agent = new BindableProxyAgent<Locale>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("label", new PropertyType(String.class, false, false));
        p.put("locale", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(Locale.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Locale unwrap() {
        return target;
      }

      public Locale deepUnwrap() {
        final Locale clone = new Locale();
        final Locale t = unwrap();
        clone.setLabel(t.getLabel());
        clone.setLocale(t.getLocale());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_jboss_errai_ui_shared_api_LocaleProxy) {
          obj = ((org_jboss_errai_ui_shared_api_LocaleProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public String getLocale() {
        return target.getLocale();
      }

      public void setLocale(String locale) {
        changeAndFire("locale", locale);
      }

      public Object get(String property) {
        switch (property) {
          case "label": return getLabel();
          case "locale": return getLocale();
          case "this": return target;
          default: throw new NonExistingPropertyException("Locale", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "label": target.setLabel((String) value);
          break;
          case "locale": target.setLocale((String) value);
          break;
          case "this": target = (Locale) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Locale", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(Locale.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_jboss_errai_ui_shared_api_LocaleProxy((Locale) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_jboss_errai_ui_shared_api_LocaleProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy extends DateMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DateMultipleSelectorFieldDefinition> agent;
      private DateMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy() {
        this(new DateMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy(DateMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DateMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(DateMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DateMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public DateMultipleSelectorFieldDefinition deepUnwrap() {
        final DateMultipleSelectorFieldDefinition clone = new DateMultipleSelectorFieldDefinition();
        final DateMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<Date> listOfValues) {
        List<Date> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DateMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<Date>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DateMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DateMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DateMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy((DateMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DateMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy extends Question implements BindableProxy {
      private BindableProxyAgent<Question> agent;
      private Question target;
      public org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy() {
        this(new Question());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy(Question targetVal) {
        agent = new BindableProxyAgent<Question>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(Question.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Question unwrap() {
        return target;
      }

      public Question deepUnwrap() {
        final Question clone = new Question();
        final Question t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Question", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (Question) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Question", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Question copy() {
        final Question returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Question.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy((Question) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_QuestionProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy extends IntegerListBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<IntegerListBoxFieldDefinition> agent;
      private IntegerListBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy() {
        this(new IntegerListBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy(IntegerListBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<IntegerListBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("addEmptyOption", new PropertyType(Boolean.class, false, false));
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Long.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ListBoxFieldType.class, false, false));
        p.put("this", new PropertyType(IntegerListBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public IntegerListBoxFieldDefinition unwrap() {
        return target;
      }

      public IntegerListBoxFieldDefinition deepUnwrap() {
        final IntegerListBoxFieldDefinition clone = new IntegerListBoxFieldDefinition();
        final IntegerListBoxFieldDefinition t = unwrap();
        clone.setAddEmptyOption(t.getAddEmptyOption());
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getAddEmptyOption() {
        return target.getAddEmptyOption();
      }

      public void setAddEmptyOption(Boolean addEmptyOption) {
        changeAndFire("addEmptyOption", addEmptyOption);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Long getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Long defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<IntegerSelectorOption> options) {
        List<IntegerSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ListBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "addEmptyOption": return getAddEmptyOption();
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("IntegerListBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "addEmptyOption": target.setAddEmptyOption((Boolean) value);
          break;
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Long) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "options": target.setOptions((List<IntegerSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (IntegerListBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("IntegerListBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(IntegerListBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy((IntegerListBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_IntegerListBoxFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy extends InformationItemPrimary implements BindableProxy {
      private BindableProxyAgent<InformationItemPrimary> agent;
      private InformationItemPrimary target;
      public org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy() {
        this(new InformationItemPrimary());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy(InformationItemPrimary targetVal) {
        agent = new BindableProxyAgent<InformationItemPrimary>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("typeRefHolder", new PropertyType(QNameHolder.class, true, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InformationItemPrimary.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InformationItemPrimary unwrap() {
        return target;
      }

      public InformationItemPrimary deepUnwrap() {
        final InformationItemPrimary clone = new InformationItemPrimary();
        final InformationItemPrimary t = unwrap();
        clone.setParent(t.getParent());
        if (t.getTypeRefHolder() instanceof BindableProxy) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) getTypeRefHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getTypeRefHolder())) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getTypeRefHolder())).deepUnwrap());
        } else {
          clone.setTypeRefHolder(t.getTypeRefHolder());
        }
        clone.setName(t.getName());
        clone.setValue(t.getValue());
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public QNameHolder getTypeRefHolder() {
        return target.getTypeRefHolder();
      }

      public void setTypeRefHolder(QNameHolder typeRefHolder) {
        if (agent.binders.containsKey("typeRefHolder")) {
          typeRefHolder = (QNameHolder) agent.binders.get("typeRefHolder").setModel(typeRefHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("typeRefHolder", typeRefHolder);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public Id getId() {
        return target.getId();
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "typeRefHolder": return getTypeRefHolder();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "name": return getName();
          case "id": return getId();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InformationItemPrimary", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "typeRefHolder": target.setTypeRefHolder((QNameHolder) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InformationItemPrimary) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InformationItemPrimary", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InformationItemPrimary.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy((InformationItemPrimary) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationItemPrimaryProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy extends TextAreaFieldDefinition implements BindableProxy {
      private BindableProxyAgent<TextAreaFieldDefinition> agent;
      private TextAreaFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy() {
        this(new TextAreaFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy(TextAreaFieldDefinition targetVal) {
        agent = new BindableProxyAgent<TextAreaFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("rows", new PropertyType(Integer.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(TextAreaFieldType.class, false, false));
        p.put("placeHolder", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(TextAreaFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public TextAreaFieldDefinition unwrap() {
        return target;
      }

      public TextAreaFieldDefinition deepUnwrap() {
        final TextAreaFieldDefinition clone = new TextAreaFieldDefinition();
        final TextAreaFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRows(t.getRows());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setName(t.getName());
        clone.setId(t.getId());
        clone.setPlaceHolder(t.getPlaceHolder());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Integer getRows() {
        return target.getRows();
      }

      public void setRows(Integer rows) {
        changeAndFire("rows", rows);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public TextAreaFieldType getFieldType() {
        return target.getFieldType();
      }

      public String getPlaceHolder() {
        return target.getPlaceHolder();
      }

      public void setPlaceHolder(String placeHolder) {
        changeAndFire("placeHolder", placeHolder);
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "rows": return getRows();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "name": return getName();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "placeHolder": return getPlaceHolder();
          case "this": return target;
          default: throw new NonExistingPropertyException("TextAreaFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "rows": target.setRows((Integer) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "placeHolder": target.setPlaceHolder((String) value);
          break;
          case "this": target = (TextAreaFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("TextAreaFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(TextAreaFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy((TextAreaFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_textArea_definition_TextAreaFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_DecisionProxy extends Decision implements BindableProxy {
      private BindableProxyAgent<Decision> agent;
      private Decision target;
      public org_kie_workbench_common_dmn_api_definition_model_DecisionProxy() {
        this(new Decision());
      }

      public org_kie_workbench_common_dmn_api_definition_model_DecisionProxy(Decision targetVal) {
        agent = new BindableProxyAgent<Decision>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("allowedAnswers", new PropertyType(AllowedAnswers.class, true, false));
        p.put("dimensionsSet", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("expression", new PropertyType(Expression.class, false, false));
        p.put("question", new PropertyType(Question.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("variable", new PropertyType(InformationItemPrimary.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("linksHolder", new PropertyType(DocumentationLinksHolder.class, true, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(Decision.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Decision unwrap() {
        return target;
      }

      public Decision deepUnwrap() {
        final Decision clone = new Decision();
        final Decision t = unwrap();
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        if (t.getAllowedAnswers() instanceof BindableProxy) {
          clone.setAllowedAnswers((AllowedAnswers) ((BindableProxy) getAllowedAnswers()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getAllowedAnswers())) {
          clone.setAllowedAnswers((AllowedAnswers) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getAllowedAnswers())).deepUnwrap());
        } else {
          clone.setAllowedAnswers(t.getAllowedAnswers());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        clone.setValue(t.getValue());
        clone.setExpression(t.getExpression());
        if (t.getQuestion() instanceof BindableProxy) {
          clone.setQuestion((Question) ((BindableProxy) getQuestion()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getQuestion())) {
          clone.setQuestion((Question) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getQuestion())).deepUnwrap());
        } else {
          clone.setQuestion(t.getQuestion());
        }
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        if (t.getVariable() instanceof BindableProxy) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) getVariable()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getVariable())) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getVariable())).deepUnwrap());
        } else {
          clone.setVariable(t.getVariable());
        }
        clone.setName(t.getName());
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getLinksHolder() instanceof BindableProxy) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) getLinksHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLinksHolder())) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLinksHolder())).deepUnwrap());
        } else {
          clone.setLinksHolder(t.getLinksHolder());
        }
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_DecisionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_DecisionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public AllowedAnswers getAllowedAnswers() {
        return target.getAllowedAnswers();
      }

      public void setAllowedAnswers(AllowedAnswers allowedAnswers) {
        if (agent.binders.containsKey("allowedAnswers")) {
          allowedAnswers = (AllowedAnswers) agent.binders.get("allowedAnswers").setModel(allowedAnswers, StateSync.FROM_MODEL, true);
        }
        changeAndFire("allowedAnswers", allowedAnswers);
      }

      public GeneralRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(GeneralRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (GeneralRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public Expression getExpression() {
        return target.getExpression();
      }

      public void setExpression(Expression expression) {
        changeAndFire("expression", expression);
      }

      public Question getQuestion() {
        return target.getQuestion();
      }

      public void setQuestion(Question question) {
        if (agent.binders.containsKey("question")) {
          question = (Question) agent.binders.get("question").setModel(question, StateSync.FROM_MODEL, true);
        }
        changeAndFire("question", question);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public InformationItemPrimary getVariable() {
        return target.getVariable();
      }

      public void setVariable(InformationItemPrimary variable) {
        if (agent.binders.containsKey("variable")) {
          variable = (InformationItemPrimary) agent.binders.get("variable").setModel(variable, StateSync.FROM_MODEL, true);
        }
        changeAndFire("variable", variable);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public DocumentationLinksHolder getLinksHolder() {
        return target.getLinksHolder();
      }

      public void setLinksHolder(DocumentationLinksHolder linksHolder) {
        if (agent.binders.containsKey("linksHolder")) {
          linksHolder = (DocumentationLinksHolder) agent.binders.get("linksHolder").setModel(linksHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("linksHolder", linksHolder);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "nsContext": return getNsContext();
          case "allowedAnswers": return getAllowedAnswers();
          case "dimensionsSet": return getDimensionsSet();
          case "id": return getId();
          case "value": return getValue();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "stunnerCategory": return getStunnerCategory();
          case "expression": return getExpression();
          case "question": return getQuestion();
          case "stunnerLabels": return getStunnerLabels();
          case "contentDefinitionId": return getContentDefinitionId();
          case "backgroundSet": return getBackgroundSet();
          case "variable": return getVariable();
          case "name": return getName();
          case "fontSet": return getFontSet();
          case "linksHolder": return getLinksHolder();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("Decision", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "allowedAnswers": target.setAllowedAnswers((AllowedAnswers) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((GeneralRectangleDimensionsSet) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "expression": target.setExpression((Expression) value);
          break;
          case "question": target.setQuestion((Question) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "variable": target.setVariable((InformationItemPrimary) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "linksHolder": target.setLinksHolder((DocumentationLinksHolder) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (Decision) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Decision", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Decision.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_DecisionProxy((Decision) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_DecisionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy extends NameHolder implements BindableProxy {
      private BindableProxyAgent<NameHolder> agent;
      private NameHolder target;
      public org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy() {
        this(new NameHolder());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy(NameHolder targetVal) {
        agent = new BindableProxyAgent<NameHolder>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("this", new PropertyType(NameHolder.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public NameHolder unwrap() {
        return target;
      }

      public NameHolder deepUnwrap() {
        final NameHolder clone = new NameHolder();
        final NameHolder t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("NameHolder", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Name) value);
          break;
          case "this": target = (NameHolder) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("NameHolder", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public NameHolder copy() {
        final NameHolder returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(NameHolder.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy((NameHolder) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_NameHolderProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy extends DecimalMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DecimalMultipleSelectorFieldDefinition> agent;
      private DecimalMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy() {
        this(new DecimalMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy(DecimalMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DecimalMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(DecimalMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public DecimalMultipleSelectorFieldDefinition deepUnwrap() {
        final DecimalMultipleSelectorFieldDefinition clone = new DecimalMultipleSelectorFieldDefinition();
        final DecimalMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<Double> listOfValues) {
        List<Double> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<Double>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DecimalMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy((DecimalMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_DecimalMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy extends GeneralRectangleDimensionsSet implements BindableProxy {
      private BindableProxyAgent<GeneralRectangleDimensionsSet> agent;
      private GeneralRectangleDimensionsSet target;
      public org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy() {
        this(new GeneralRectangleDimensionsSet());
      }

      public org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy(GeneralRectangleDimensionsSet targetVal) {
        agent = new BindableProxyAgent<GeneralRectangleDimensionsSet>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("width", new PropertyType(Width.class, true, false));
        p.put("maximumWidth", new PropertyType(Double.class, false, false));
        p.put("minimumHeight", new PropertyType(Double.class, false, false));
        p.put("maximumHeight", new PropertyType(Double.class, false, false));
        p.put("minimumWidth", new PropertyType(Double.class, false, false));
        p.put("height", new PropertyType(Height.class, true, false));
        p.put("this", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public GeneralRectangleDimensionsSet unwrap() {
        return target;
      }

      public GeneralRectangleDimensionsSet deepUnwrap() {
        final GeneralRectangleDimensionsSet clone = new GeneralRectangleDimensionsSet();
        final GeneralRectangleDimensionsSet t = unwrap();
        if (t.getWidth() instanceof BindableProxy) {
          clone.setWidth((Width) ((BindableProxy) getWidth()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getWidth())) {
          clone.setWidth((Width) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getWidth())).deepUnwrap());
        } else {
          clone.setWidth(t.getWidth());
        }
        if (t.getHeight() instanceof BindableProxy) {
          clone.setHeight((Height) ((BindableProxy) getHeight()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getHeight())) {
          clone.setHeight((Height) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getHeight())).deepUnwrap());
        } else {
          clone.setHeight(t.getHeight());
        }
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Width getWidth() {
        return target.getWidth();
      }

      public void setWidth(Width width) {
        if (agent.binders.containsKey("width")) {
          width = (Width) agent.binders.get("width").setModel(width, StateSync.FROM_MODEL, true);
        }
        changeAndFire("width", width);
      }

      public double getMaximumWidth() {
        return target.getMaximumWidth();
      }

      public double getMinimumHeight() {
        return target.getMinimumHeight();
      }

      public double getMaximumHeight() {
        return target.getMaximumHeight();
      }

      public double getMinimumWidth() {
        return target.getMinimumWidth();
      }

      public Height getHeight() {
        return target.getHeight();
      }

      public void setHeight(Height height) {
        if (agent.binders.containsKey("height")) {
          height = (Height) agent.binders.get("height").setModel(height, StateSync.FROM_MODEL, true);
        }
        changeAndFire("height", height);
      }

      public Object get(String property) {
        switch (property) {
          case "width": return getWidth();
          case "maximumWidth": return getMaximumWidth();
          case "minimumHeight": return getMinimumHeight();
          case "maximumHeight": return getMaximumHeight();
          case "minimumWidth": return getMinimumWidth();
          case "height": return getHeight();
          case "this": return target;
          default: throw new NonExistingPropertyException("GeneralRectangleDimensionsSet", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "width": target.setWidth((Width) value);
          break;
          case "height": target.setHeight((Height) value);
          break;
          case "this": target = (GeneralRectangleDimensionsSet) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("GeneralRectangleDimensionsSet", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(GeneralRectangleDimensionsSet.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy((GeneralRectangleDimensionsSet) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dimensions_GeneralRectangleDimensionsSetProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy extends StringRadioGroupFieldDefinition implements BindableProxy {
      private BindableProxyAgent<StringRadioGroupFieldDefinition> agent;
      private StringRadioGroupFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy() {
        this(new StringRadioGroupFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy(StringRadioGroupFieldDefinition targetVal) {
        agent = new BindableProxyAgent<StringRadioGroupFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(String.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("inline", new PropertyType(Boolean.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(RadioGroupFieldType.class, false, false));
        p.put("this", new PropertyType(StringRadioGroupFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public StringRadioGroupFieldDefinition unwrap() {
        return target;
      }

      public StringRadioGroupFieldDefinition deepUnwrap() {
        final StringRadioGroupFieldDefinition clone = new StringRadioGroupFieldDefinition();
        final StringRadioGroupFieldDefinition t = unwrap();
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setInline(t.getInline());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(String defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Boolean getInline() {
        return target.getInline();
      }

      public void setInline(Boolean inline) {
        changeAndFire("inline", inline);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<StringSelectorOption> options) {
        List<StringSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public RadioGroupFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "inline": return getInline();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("StringRadioGroupFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((String) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "inline": target.setInline((Boolean) value);
          break;
          case "options": target.setOptions((List<StringSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (StringRadioGroupFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("StringRadioGroupFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(StringRadioGroupFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy((StringRadioGroupFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_radioGroup_definition_StringRadioGroupFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InputDataProxy extends InputData implements BindableProxy {
      private BindableProxyAgent<InputData> agent;
      private InputData target;
      public org_kie_workbench_common_dmn_api_definition_model_InputDataProxy() {
        this(new InputData());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InputDataProxy(InputData targetVal) {
        agent = new BindableProxyAgent<InputData>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("diagramId", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("allowOnlyVisualChange", new PropertyType(Boolean.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("contentDefinitionId", new PropertyType(String.class, false, false));
        p.put("backgroundSet", new PropertyType(BackgroundSet.class, true, false));
        p.put("variable", new PropertyType(InformationItemPrimary.class, true, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("fontSet", new PropertyType(FontSet.class, true, false));
        p.put("dimensionsSet", new PropertyType(GeneralRectangleDimensionsSet.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("linksHolder", new PropertyType(DocumentationLinksHolder.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InputData.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InputData unwrap() {
        return target;
      }

      public InputData deepUnwrap() {
        final InputData clone = new InputData();
        final InputData t = unwrap();
        clone.setParent(t.getParent());
        clone.setDiagramId(t.getDiagramId());
        clone.setExtensionElements(t.getExtensionElements());
        clone.setAllowOnlyVisualChange(t.isAllowOnlyVisualChange());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        clone.setContentDefinitionId(t.getContentDefinitionId());
        if (t.getBackgroundSet() instanceof BindableProxy) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) getBackgroundSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getBackgroundSet())) {
          clone.setBackgroundSet((BackgroundSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getBackgroundSet())).deepUnwrap());
        } else {
          clone.setBackgroundSet(t.getBackgroundSet());
        }
        if (t.getVariable() instanceof BindableProxy) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) getVariable()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getVariable())) {
          clone.setVariable((InformationItemPrimary) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getVariable())).deepUnwrap());
        } else {
          clone.setVariable(t.getVariable());
        }
        clone.setName(t.getName());
        if (t.getFontSet() instanceof BindableProxy) {
          clone.setFontSet((FontSet) ((BindableProxy) getFontSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getFontSet())) {
          clone.setFontSet((FontSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getFontSet())).deepUnwrap());
        } else {
          clone.setFontSet(t.getFontSet());
        }
        if (t.getDimensionsSet() instanceof BindableProxy) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) getDimensionsSet()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDimensionsSet())) {
          clone.setDimensionsSet((GeneralRectangleDimensionsSet) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDimensionsSet())).deepUnwrap());
        } else {
          clone.setDimensionsSet(t.getDimensionsSet());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getLinksHolder() instanceof BindableProxy) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) getLinksHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLinksHolder())) {
          clone.setLinksHolder((DocumentationLinksHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLinksHolder())).deepUnwrap());
        } else {
          clone.setLinksHolder(t.getLinksHolder());
        }
        clone.setValue(t.getValue());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InputDataProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InputDataProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public String getDiagramId() {
        return target.getDiagramId();
      }

      public void setDiagramId(String diagramId) {
        changeAndFire("diagramId", diagramId);
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public boolean isAllowOnlyVisualChange() {
        return target.isAllowOnlyVisualChange();
      }

      public void setAllowOnlyVisualChange(boolean allowOnlyVisualChange) {
        changeAndFire("allowOnlyVisualChange", allowOnlyVisualChange);
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public String getContentDefinitionId() {
        return target.getContentDefinitionId();
      }

      public void setContentDefinitionId(String contentDefinitionId) {
        changeAndFire("contentDefinitionId", contentDefinitionId);
      }

      public BackgroundSet getBackgroundSet() {
        return target.getBackgroundSet();
      }

      public void setBackgroundSet(BackgroundSet backgroundSet) {
        if (agent.binders.containsKey("backgroundSet")) {
          backgroundSet = (BackgroundSet) agent.binders.get("backgroundSet").setModel(backgroundSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("backgroundSet", backgroundSet);
      }

      public InformationItemPrimary getVariable() {
        return target.getVariable();
      }

      public void setVariable(InformationItemPrimary variable) {
        if (agent.binders.containsKey("variable")) {
          variable = (InformationItemPrimary) agent.binders.get("variable").setModel(variable, StateSync.FROM_MODEL, true);
        }
        changeAndFire("variable", variable);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public FontSet getFontSet() {
        return target.getFontSet();
      }

      public void setFontSet(FontSet fontSet) {
        if (agent.binders.containsKey("fontSet")) {
          fontSet = (FontSet) agent.binders.get("fontSet").setModel(fontSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("fontSet", fontSet);
      }

      public GeneralRectangleDimensionsSet getDimensionsSet() {
        return target.getDimensionsSet();
      }

      public void setDimensionsSet(GeneralRectangleDimensionsSet dimensionsSet) {
        if (agent.binders.containsKey("dimensionsSet")) {
          dimensionsSet = (GeneralRectangleDimensionsSet) agent.binders.get("dimensionsSet").setModel(dimensionsSet, StateSync.FROM_MODEL, true);
        }
        changeAndFire("dimensionsSet", dimensionsSet);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public DocumentationLinksHolder getLinksHolder() {
        return target.getLinksHolder();
      }

      public void setLinksHolder(DocumentationLinksHolder linksHolder) {
        if (agent.binders.containsKey("linksHolder")) {
          linksHolder = (DocumentationLinksHolder) agent.binders.get("linksHolder").setModel(linksHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("linksHolder", linksHolder);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "diagramId": return getDiagramId();
          case "extensionElements": return getExtensionElements();
          case "stunnerLabels": return getStunnerLabels();
          case "allowOnlyVisualChange": return isAllowOnlyVisualChange();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "nsContext": return getNsContext();
          case "contentDefinitionId": return getContentDefinitionId();
          case "backgroundSet": return getBackgroundSet();
          case "variable": return getVariable();
          case "name": return getName();
          case "fontSet": return getFontSet();
          case "dimensionsSet": return getDimensionsSet();
          case "id": return getId();
          case "linksHolder": return getLinksHolder();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InputData", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "diagramId": target.setDiagramId((String) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "allowOnlyVisualChange": target.setAllowOnlyVisualChange((Boolean) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "contentDefinitionId": target.setContentDefinitionId((String) value);
          break;
          case "backgroundSet": target.setBackgroundSet((BackgroundSet) value);
          break;
          case "variable": target.setVariable((InformationItemPrimary) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "fontSet": target.setFontSet((FontSet) value);
          break;
          case "dimensionsSet": target.setDimensionsSet((GeneralRectangleDimensionsSet) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "linksHolder": target.setLinksHolder((DocumentationLinksHolder) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InputData) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InputData", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public ReadOnly getReadOnly(String a0) {
        final ReadOnly returnValue = target.getReadOnly(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InputData.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InputDataProxy((InputData) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InputDataProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy extends DecisionServiceDividerLineY implements BindableProxy {
      private BindableProxyAgent<DecisionServiceDividerLineY> agent;
      private DecisionServiceDividerLineY target;
      public org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy() {
        this(new DecisionServiceDividerLineY());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy(DecisionServiceDividerLineY targetVal) {
        agent = new BindableProxyAgent<DecisionServiceDividerLineY>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(Double.class, false, false));
        p.put("this", new PropertyType(DecisionServiceDividerLineY.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecisionServiceDividerLineY unwrap() {
        return target;
      }

      public DecisionServiceDividerLineY deepUnwrap() {
        final DecisionServiceDividerLineY clone = new DecisionServiceDividerLineY();
        final DecisionServiceDividerLineY t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Double getValue() {
        return target.getValue();
      }

      public void setValue(Double value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecisionServiceDividerLineY", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((Double) value);
          break;
          case "this": target = (DecisionServiceDividerLineY) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecisionServiceDividerLineY", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }
    }
    BindableProxyFactory.addBindableProxy(DecisionServiceDividerLineY.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy((DecisionServiceDividerLineY) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_DecisionServiceDividerLineYProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy extends BooleanMultipleSelectorFieldDefinition implements BindableProxy {
      private BindableProxyAgent<BooleanMultipleSelectorFieldDefinition> agent;
      private BooleanMultipleSelectorFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy() {
        this(new BooleanMultipleSelectorFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy(BooleanMultipleSelectorFieldDefinition targetVal) {
        agent = new BindableProxyAgent<BooleanMultipleSelectorFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("maxDropdownElements", new PropertyType(Integer.class, false, false));
        p.put("listOfValues", new PropertyType(List.class, false, true));
        p.put("maxElementsOnTitle", new PropertyType(Integer.class, false, false));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("allowFilter", new PropertyType(Boolean.class, false, false));
        p.put("allowClearSelection", new PropertyType(Boolean.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(FieldType.class, false, false));
        p.put("this", new PropertyType(BooleanMultipleSelectorFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public BooleanMultipleSelectorFieldDefinition unwrap() {
        return target;
      }

      public BooleanMultipleSelectorFieldDefinition deepUnwrap() {
        final BooleanMultipleSelectorFieldDefinition clone = new BooleanMultipleSelectorFieldDefinition();
        final BooleanMultipleSelectorFieldDefinition t = unwrap();
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        clone.setMaxDropdownElements(t.getMaxDropdownElements());
        if (t.getListOfValues() != null) {
          final List listOfValuesClone = new ArrayList();
          for (Object listOfValuesElem : t.getListOfValues()) {
            if (listOfValuesElem instanceof BindableProxy) {
              listOfValuesClone.add(((BindableProxy) listOfValuesElem).deepUnwrap());
            } else {
              listOfValuesClone.add(listOfValuesElem);
            }
          }
          clone.setListOfValues(listOfValuesClone);
        }
        clone.setMaxElementsOnTitle(t.getMaxElementsOnTitle());
        clone.setName(t.getName());
        clone.setAllowFilter(t.getAllowFilter());
        clone.setAllowClearSelection(t.getAllowClearSelection());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public Integer getMaxDropdownElements() {
        return target.getMaxDropdownElements();
      }

      public void setMaxDropdownElements(Integer maxDropdownElements) {
        changeAndFire("maxDropdownElements", maxDropdownElements);
      }

      public List getListOfValues() {
        return target.getListOfValues();
      }

      public void setListOfValues(List<Boolean> listOfValues) {
        List<Boolean> oldValue = target.getListOfValues();
        listOfValues = agent.ensureBoundListIsProxied("listOfValues", listOfValues);
        target.setListOfValues(listOfValues);
        agent.updateWidgetsAndFireEvent(true, "listOfValues", oldValue, listOfValues);
      }

      public Integer getMaxElementsOnTitle() {
        return target.getMaxElementsOnTitle();
      }

      public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        changeAndFire("maxElementsOnTitle", maxElementsOnTitle);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public Boolean getAllowFilter() {
        return target.getAllowFilter();
      }

      public void setAllowFilter(Boolean allowFilter) {
        changeAndFire("allowFilter", allowFilter);
      }

      public Boolean getAllowClearSelection() {
        return target.getAllowClearSelection();
      }

      public void setAllowClearSelection(Boolean allowClearSelection) {
        changeAndFire("allowClearSelection", allowClearSelection);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public FieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "validateOnChange": return getValidateOnChange();
          case "binding": return getBinding();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "maxDropdownElements": return getMaxDropdownElements();
          case "listOfValues": return getListOfValues();
          case "maxElementsOnTitle": return getMaxElementsOnTitle();
          case "name": return getName();
          case "allowFilter": return getAllowFilter();
          case "allowClearSelection": return getAllowClearSelection();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("BooleanMultipleSelectorFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "maxDropdownElements": target.setMaxDropdownElements((Integer) value);
          break;
          case "listOfValues": target.setListOfValues((List<Boolean>) value);
          break;
          case "maxElementsOnTitle": target.setMaxElementsOnTitle((Integer) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "allowFilter": target.setAllowFilter((Boolean) value);
          break;
          case "allowClearSelection": target.setAllowClearSelection((Boolean) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (BooleanMultipleSelectorFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("BooleanMultipleSelectorFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(BooleanMultipleSelectorFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy((BooleanMultipleSelectorFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_lists_selector_impl_BooleanMultipleSelectorFieldDefinitionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy extends ImportedValues implements BindableProxy {
      private BindableProxyAgent<ImportedValues> agent;
      private ImportedValues target;
      public org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy() {
        this(new ImportedValues());
      }

      public org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy(ImportedValues targetVal) {
        agent = new BindableProxyAgent<ImportedValues>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("importedElement", new PropertyType(String.class, false, false));
        p.put("importType", new PropertyType(String.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("namespace", new PropertyType(String.class, false, false));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("locationURI", new PropertyType(LocationURI.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(ImportedValues.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ImportedValues unwrap() {
        return target;
      }

      public ImportedValues deepUnwrap() {
        final ImportedValues clone = new ImportedValues();
        final ImportedValues t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        clone.setImportedElement(t.getImportedElement());
        clone.setImportType(t.getImportType());
        clone.setNamespace(t.getNamespace());
        clone.setName(t.getName());
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getLocationURI() instanceof BindableProxy) {
          clone.setLocationURI((LocationURI) ((BindableProxy) getLocationURI()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getLocationURI())) {
          clone.setLocationURI((LocationURI) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getLocationURI())).deepUnwrap());
        } else {
          clone.setLocationURI(t.getLocationURI());
        }
        clone.setValue(t.getValue());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public String getImportedElement() {
        return target.getImportedElement();
      }

      public void setImportedElement(String importedElement) {
        changeAndFire("importedElement", importedElement);
      }

      public String getImportType() {
        return target.getImportType();
      }

      public void setImportType(String importType) {
        changeAndFire("importType", importType);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public String getNamespace() {
        return target.getNamespace();
      }

      public void setNamespace(String namespace) {
        changeAndFire("namespace", namespace);
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public LocationURI getLocationURI() {
        return target.getLocationURI();
      }

      public void setLocationURI(LocationURI locationURI) {
        if (agent.binders.containsKey("locationURI")) {
          locationURI = (LocationURI) agent.binders.get("locationURI").setModel(locationURI, StateSync.FROM_MODEL, true);
        }
        changeAndFire("locationURI", locationURI);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "expressionLanguage": return getExpressionLanguage();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "importedElement": return getImportedElement();
          case "importType": return getImportType();
          case "nsContext": return getNsContext();
          case "namespace": return getNamespace();
          case "name": return getName();
          case "id": return getId();
          case "locationURI": return getLocationURI();
          case "value": return getValue();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("ImportedValues", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "importedElement": target.setImportedElement((String) value);
          break;
          case "importType": target.setImportType((String) value);
          break;
          case "namespace": target.setNamespace((String) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "locationURI": target.setLocationURI((LocationURI) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (ImportedValues) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("ImportedValues", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public ImportedValues copy() {
        final ImportedValues returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(ImportedValues.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy((ImportedValues) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_ImportedValuesProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy extends Description implements BindableProxy {
      private BindableProxyAgent<Description> agent;
      private Description target;
      public org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy() {
        this(new Description());
      }

      public org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy(Description targetVal) {
        agent = new BindableProxyAgent<Description>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("value", new PropertyType(String.class, false, false));
        p.put("this", new PropertyType(Description.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Description unwrap() {
        return target;
      }

      public Description deepUnwrap() {
        final Description clone = new Description();
        final Description t = unwrap();
        clone.setValue(t.getValue());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy) {
          obj = ((org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public String getValue() {
        return target.getValue();
      }

      public void setValue(String value) {
        changeAndFire("value", value);
      }

      public Object get(String property) {
        switch (property) {
          case "value": return getValue();
          case "this": return target;
          default: throw new NonExistingPropertyException("Description", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "value": target.setValue((String) value);
          break;
          case "this": target = (Description) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("Description", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public Description copy() {
        final Description returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(Description.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy((Description) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_property_dmn_DescriptionProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy extends InformationItem implements BindableProxy {
      private BindableProxyAgent<InformationItem> agent;
      private InformationItem target;
      public org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy() {
        this(new InformationItem());
      }

      public org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy(InformationItem targetVal) {
        agent = new BindableProxyAgent<InformationItem>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("typeRefHolder", new PropertyType(QNameHolder.class, true, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("nameHolder", new PropertyType(NameHolder.class, true, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(Name.class, false, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Name.class, false, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(InformationItem.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public InformationItem unwrap() {
        return target;
      }

      public InformationItem deepUnwrap() {
        final InformationItem clone = new InformationItem();
        final InformationItem t = unwrap();
        clone.setParent(t.getParent());
        if (t.getTypeRefHolder() instanceof BindableProxy) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) getTypeRefHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getTypeRefHolder())) {
          clone.setTypeRefHolder((QNameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getTypeRefHolder())).deepUnwrap());
        } else {
          clone.setTypeRefHolder(t.getTypeRefHolder());
        }
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getNameHolder() instanceof BindableProxy) {
          clone.setNameHolder((NameHolder) ((BindableProxy) getNameHolder()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getNameHolder())) {
          clone.setNameHolder((NameHolder) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getNameHolder())).deepUnwrap());
        } else {
          clone.setNameHolder(t.getNameHolder());
        }
        clone.setName(t.getName());
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        clone.setValue(t.getValue());
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public QNameHolder getTypeRefHolder() {
        return target.getTypeRefHolder();
      }

      public void setTypeRefHolder(QNameHolder typeRefHolder) {
        if (agent.binders.containsKey("typeRefHolder")) {
          typeRefHolder = (QNameHolder) agent.binders.get("typeRefHolder").setModel(typeRefHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("typeRefHolder", typeRefHolder);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public NameHolder getNameHolder() {
        return target.getNameHolder();
      }

      public void setNameHolder(NameHolder nameHolder) {
        if (agent.binders.containsKey("nameHolder")) {
          nameHolder = (NameHolder) agent.binders.get("nameHolder").setModel(nameHolder, StateSync.FROM_MODEL, true);
        }
        changeAndFire("nameHolder", nameHolder);
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Name getName() {
        return target.getName();
      }

      public void setName(Name name) {
        changeAndFire("name", name);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Name getValue() {
        return target.getValue();
      }

      public void setValue(Name value) {
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "typeRefHolder": return getTypeRefHolder();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "nameHolder": return getNameHolder();
          case "nsContext": return getNsContext();
          case "hasTypeRefs": return getHasTypeRefs();
          case "name": return getName();
          case "id": return getId();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("InformationItem", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "typeRefHolder": target.setTypeRefHolder((QNameHolder) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "nameHolder": target.setNameHolder((NameHolder) value);
          break;
          case "name": target.setName((Name) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Name) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (InformationItem) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("InformationItem", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public InformationItem copy() {
        final InformationItem returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(InformationItem.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy((InformationItem) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_InformationItemProxy();
      }
    });
    class org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy extends LiteralExpressionPMMLDocument implements BindableProxy {
      private BindableProxyAgent<LiteralExpressionPMMLDocument> agent;
      private LiteralExpressionPMMLDocument target;
      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy() {
        this(new LiteralExpressionPMMLDocument());
      }

      public org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy(LiteralExpressionPMMLDocument targetVal) {
        agent = new BindableProxyAgent<LiteralExpressionPMMLDocument>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("parent", new PropertyType(DMNModelInstrumentedBase.class, false, false));
        p.put("domainObjectUUID", new PropertyType(String.class, false, false));
        p.put("defaultNamespace", new PropertyType(String.class, false, false));
        p.put("stunnerCategory", new PropertyType(String.class, false, false));
        p.put("extensionElements", new PropertyType(ExtensionElements.class, false, false));
        p.put("domainObjectNameTranslationKey", new PropertyType(String.class, false, false));
        p.put("expressionLanguage", new PropertyType(ExpressionLanguage.class, true, false));
        p.put("stunnerLabels", new PropertyType(Set.class, false, false));
        p.put("description", new PropertyType(Description.class, true, false));
        p.put("importedValues", new PropertyType(ImportedValues.class, true, false));
        p.put("requiredComponentWidthCount", new PropertyType(Integer.class, false, false));
        p.put("nsContext", new PropertyType(Map.class, false, false));
        p.put("componentWidths", new PropertyType(List.class, false, true));
        p.put("hasTypeRefs", new PropertyType(List.class, false, true));
        p.put("text", new PropertyType(Text.class, true, false));
        p.put("id", new PropertyType(Id.class, true, false));
        p.put("value", new PropertyType(Text.class, true, false));
        p.put("typeRef", new PropertyType(QName.class, false, false));
        p.put("additionalAttributes", new PropertyType(Map.class, false, false));
        p.put("this", new PropertyType(LiteralExpressionPMMLDocument.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public LiteralExpressionPMMLDocument unwrap() {
        return target;
      }

      public LiteralExpressionPMMLDocument deepUnwrap() {
        final LiteralExpressionPMMLDocument clone = new LiteralExpressionPMMLDocument();
        final LiteralExpressionPMMLDocument t = unwrap();
        clone.setParent(t.getParent());
        clone.setExtensionElements(t.getExtensionElements());
        if (t.getExpressionLanguage() instanceof BindableProxy) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) getExpressionLanguage()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getExpressionLanguage())) {
          clone.setExpressionLanguage((ExpressionLanguage) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getExpressionLanguage())).deepUnwrap());
        } else {
          clone.setExpressionLanguage(t.getExpressionLanguage());
        }
        if (t.getDescription() instanceof BindableProxy) {
          clone.setDescription((Description) ((BindableProxy) getDescription()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getDescription())) {
          clone.setDescription((Description) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getDescription())).deepUnwrap());
        } else {
          clone.setDescription(t.getDescription());
        }
        if (t.getImportedValues() instanceof BindableProxy) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) getImportedValues()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getImportedValues())) {
          clone.setImportedValues((ImportedValues) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getImportedValues())).deepUnwrap());
        } else {
          clone.setImportedValues(t.getImportedValues());
        }
        if (t.getText() instanceof BindableProxy) {
          clone.setText((Text) ((BindableProxy) getText()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getText())) {
          clone.setText((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getText())).deepUnwrap());
        } else {
          clone.setText(t.getText());
        }
        if (t.getId() instanceof BindableProxy) {
          clone.setId((Id) ((BindableProxy) getId()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getId())) {
          clone.setId((Id) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getId())).deepUnwrap());
        } else {
          clone.setId(t.getId());
        }
        if (t.getValue() instanceof BindableProxy) {
          clone.setValue((Text) ((BindableProxy) getValue()).deepUnwrap());
        } else if (BindableProxyFactory.isBindableType(t.getValue())) {
          clone.setValue((Text) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.getValue())).deepUnwrap());
        } else {
          clone.setValue(t.getValue());
        }
        clone.setTypeRef(t.getTypeRef());
        clone.setAdditionalAttributes(t.getAdditionalAttributes());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy) {
          obj = ((org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public DMNModelInstrumentedBase getParent() {
        return target.getParent();
      }

      public void setParent(DMNModelInstrumentedBase parent) {
        changeAndFire("parent", parent);
      }

      public String getDomainObjectUUID() {
        return target.getDomainObjectUUID();
      }

      public String getDefaultNamespace() {
        return target.getDefaultNamespace();
      }

      public String getStunnerCategory() {
        return target.getStunnerCategory();
      }

      public ExtensionElements getExtensionElements() {
        return target.getExtensionElements();
      }

      public void setExtensionElements(ExtensionElements extensionElements) {
        changeAndFire("extensionElements", extensionElements);
      }

      public String getDomainObjectNameTranslationKey() {
        return target.getDomainObjectNameTranslationKey();
      }

      public ExpressionLanguage getExpressionLanguage() {
        return target.getExpressionLanguage();
      }

      public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        if (agent.binders.containsKey("expressionLanguage")) {
          expressionLanguage = (ExpressionLanguage) agent.binders.get("expressionLanguage").setModel(expressionLanguage, StateSync.FROM_MODEL, true);
        }
        changeAndFire("expressionLanguage", expressionLanguage);
      }

      public Set getStunnerLabels() {
        return target.getStunnerLabels();
      }

      public Description getDescription() {
        return target.getDescription();
      }

      public void setDescription(Description description) {
        if (agent.binders.containsKey("description")) {
          description = (Description) agent.binders.get("description").setModel(description, StateSync.FROM_MODEL, true);
        }
        changeAndFire("description", description);
      }

      public ImportedValues getImportedValues() {
        return target.getImportedValues();
      }

      public void setImportedValues(ImportedValues importedValues) {
        if (agent.binders.containsKey("importedValues")) {
          importedValues = (ImportedValues) agent.binders.get("importedValues").setModel(importedValues, StateSync.FROM_MODEL, true);
        }
        changeAndFire("importedValues", importedValues);
      }

      public int getRequiredComponentWidthCount() {
        return target.getRequiredComponentWidthCount();
      }

      public Map getNsContext() {
        return target.getNsContext();
      }

      public List getComponentWidths() {
        return target.getComponentWidths();
      }

      public List getHasTypeRefs() {
        return target.getHasTypeRefs();
      }

      public Text getText() {
        return target.getText();
      }

      public void setText(Text text) {
        if (agent.binders.containsKey("text")) {
          text = (Text) agent.binders.get("text").setModel(text, StateSync.FROM_MODEL, true);
        }
        changeAndFire("text", text);
      }

      public Id getId() {
        return target.getId();
      }

      public void setId(Id id) {
        if (agent.binders.containsKey("id")) {
          id = (Id) agent.binders.get("id").setModel(id, StateSync.FROM_MODEL, true);
        }
        changeAndFire("id", id);
      }

      public Text getValue() {
        return target.getValue();
      }

      public void setValue(Text value) {
        if (agent.binders.containsKey("value")) {
          value = (Text) agent.binders.get("value").setModel(value, StateSync.FROM_MODEL, true);
        }
        changeAndFire("value", value);
      }

      public QName getTypeRef() {
        return target.getTypeRef();
      }

      public void setTypeRef(QName typeRef) {
        changeAndFire("typeRef", typeRef);
      }

      public Map getAdditionalAttributes() {
        return target.getAdditionalAttributes();
      }

      public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        changeAndFire("additionalAttributes", additionalAttributes);
      }

      public Object get(String property) {
        switch (property) {
          case "parent": return getParent();
          case "domainObjectUUID": return getDomainObjectUUID();
          case "defaultNamespace": return getDefaultNamespace();
          case "stunnerCategory": return getStunnerCategory();
          case "extensionElements": return getExtensionElements();
          case "domainObjectNameTranslationKey": return getDomainObjectNameTranslationKey();
          case "expressionLanguage": return getExpressionLanguage();
          case "stunnerLabels": return getStunnerLabels();
          case "description": return getDescription();
          case "importedValues": return getImportedValues();
          case "requiredComponentWidthCount": return getRequiredComponentWidthCount();
          case "nsContext": return getNsContext();
          case "componentWidths": return getComponentWidths();
          case "hasTypeRefs": return getHasTypeRefs();
          case "text": return getText();
          case "id": return getId();
          case "value": return getValue();
          case "typeRef": return getTypeRef();
          case "additionalAttributes": return getAdditionalAttributes();
          case "this": return target;
          default: throw new NonExistingPropertyException("LiteralExpressionPMMLDocument", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "parent": target.setParent((DMNModelInstrumentedBase) value);
          break;
          case "extensionElements": target.setExtensionElements((ExtensionElements) value);
          break;
          case "expressionLanguage": target.setExpressionLanguage((ExpressionLanguage) value);
          break;
          case "description": target.setDescription((Description) value);
          break;
          case "importedValues": target.setImportedValues((ImportedValues) value);
          break;
          case "text": target.setText((Text) value);
          break;
          case "id": target.setId((Id) value);
          break;
          case "value": target.setValue((Text) value);
          break;
          case "typeRef": target.setTypeRef((QName) value);
          break;
          case "additionalAttributes": target.setAdditionalAttributes((Map<QName, String>) value);
          break;
          case "this": target = (LiteralExpressionPMMLDocument) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("LiteralExpressionPMMLDocument", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public LiteralExpression copy() {
        final LiteralExpression returnValue = target.copy();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        final DMNModelInstrumentedBase returnValue = target.asDMNModelInstrumentedBase();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }

      public Optional getPrefixForNamespaceURI(String a0) {
        final Optional returnValue = target.getPrefixForNamespaceURI(a0);
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(LiteralExpressionPMMLDocument.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy((LiteralExpressionPMMLDocument) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_dmn_api_definition_model_LiteralExpressionPMMLDocumentProxy();
      }
    });
    class org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy extends DecimalListBoxFieldDefinition implements BindableProxy {
      private BindableProxyAgent<DecimalListBoxFieldDefinition> agent;
      private DecimalListBoxFieldDefinition target;
      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy() {
        this(new DecimalListBoxFieldDefinition());
      }

      public org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy(DecimalListBoxFieldDefinition targetVal) {
        agent = new BindableProxyAgent<DecimalListBoxFieldDefinition>(this, targetVal);
        target = targetVal;
        final Map<String, PropertyType> p = agent.propertyTypes;
        p.put("addEmptyOption", new PropertyType(Boolean.class, false, false));
        p.put("relatedField", new PropertyType(String.class, false, false));
        p.put("validateOnChange", new PropertyType(Boolean.class, false, false));
        p.put("defaultValue", new PropertyType(Double.class, false, false));
        p.put("binding", new PropertyType(String.class, false, false));
        p.put("readOnly", new PropertyType(Boolean.class, false, false));
        p.put("standaloneClassName", new PropertyType(String.class, false, false));
        p.put("fieldTypeInfo", new PropertyType(TypeInfo.class, false, false));
        p.put("label", new PropertyType(String.class, false, false));
        p.put("required", new PropertyType(Boolean.class, false, false));
        p.put("helpMessage", new PropertyType(String.class, false, false));
        p.put("options", new PropertyType(List.class, false, true));
        p.put("name", new PropertyType(String.class, false, false));
        p.put("dataProvider", new PropertyType(String.class, false, false));
        p.put("id", new PropertyType(String.class, false, false));
        p.put("fieldType", new PropertyType(ListBoxFieldType.class, false, false));
        p.put("this", new PropertyType(DecimalListBoxFieldDefinition.class, true, false));
        agent.copyValues();
      }

      public BindableProxyAgent getBindableProxyAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public DecimalListBoxFieldDefinition unwrap() {
        return target;
      }

      public DecimalListBoxFieldDefinition deepUnwrap() {
        final DecimalListBoxFieldDefinition clone = new DecimalListBoxFieldDefinition();
        final DecimalListBoxFieldDefinition t = unwrap();
        clone.setAddEmptyOption(t.getAddEmptyOption());
        clone.setRelatedField(t.getRelatedField());
        clone.setValidateOnChange(t.getValidateOnChange());
        clone.setDefaultValue(t.getDefaultValue());
        clone.setBinding(t.getBinding());
        clone.setReadOnly(t.getReadOnly());
        clone.setStandaloneClassName(t.getStandaloneClassName());
        clone.setLabel(t.getLabel());
        clone.setRequired(t.getRequired());
        clone.setHelpMessage(t.getHelpMessage());
        if (t.getOptions() != null) {
          final List optionsClone = new ArrayList();
          for (Object optionsElem : t.getOptions()) {
            if (optionsElem instanceof BindableProxy) {
              optionsClone.add(((BindableProxy) optionsElem).deepUnwrap());
            } else {
              optionsClone.add(optionsElem);
            }
          }
          clone.setOptions(optionsClone);
        }
        clone.setName(t.getName());
        clone.setDataProvider(t.getDataProvider());
        clone.setId(t.getId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy) {
          obj = ((org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy) obj).unwrap();
        }
        return target.equals(obj);
      }

      public int hashCode() {
        return target.hashCode();
      }

      public String toString() {
        return target.toString();
      }

      private void changeAndFire(String property, Object value) {
        final Object oldValue = get(property);
        set(property, value);
        agent.updateWidgetsAndFireEvent(false, property, oldValue, value);
      }

      public Boolean getAddEmptyOption() {
        return target.getAddEmptyOption();
      }

      public void setAddEmptyOption(Boolean addEmptyOption) {
        changeAndFire("addEmptyOption", addEmptyOption);
      }

      public String getRelatedField() {
        return target.getRelatedField();
      }

      public void setRelatedField(String relatedField) {
        changeAndFire("relatedField", relatedField);
      }

      public Boolean getValidateOnChange() {
        return target.getValidateOnChange();
      }

      public void setValidateOnChange(Boolean validateOnChange) {
        changeAndFire("validateOnChange", validateOnChange);
      }

      public Double getDefaultValue() {
        return target.getDefaultValue();
      }

      public void setDefaultValue(Double defaultValue) {
        changeAndFire("defaultValue", defaultValue);
      }

      public String getBinding() {
        return target.getBinding();
      }

      public void setBinding(String binding) {
        changeAndFire("binding", binding);
      }

      public Boolean getReadOnly() {
        return target.getReadOnly();
      }

      public void setReadOnly(Boolean readOnly) {
        changeAndFire("readOnly", readOnly);
      }

      public String getStandaloneClassName() {
        return target.getStandaloneClassName();
      }

      public void setStandaloneClassName(String standaloneClassName) {
        changeAndFire("standaloneClassName", standaloneClassName);
      }

      public TypeInfo getFieldTypeInfo() {
        return target.getFieldTypeInfo();
      }

      public String getLabel() {
        return target.getLabel();
      }

      public void setLabel(String label) {
        changeAndFire("label", label);
      }

      public Boolean getRequired() {
        return target.getRequired();
      }

      public void setRequired(Boolean required) {
        changeAndFire("required", required);
      }

      public String getHelpMessage() {
        return target.getHelpMessage();
      }

      public void setHelpMessage(String helpMessage) {
        changeAndFire("helpMessage", helpMessage);
      }

      public List getOptions() {
        return target.getOptions();
      }

      public void setOptions(List<DecimalSelectorOption> options) {
        List<DecimalSelectorOption> oldValue = target.getOptions();
        options = agent.ensureBoundListIsProxied("options", options);
        target.setOptions(options);
        agent.updateWidgetsAndFireEvent(true, "options", oldValue, options);
      }

      public String getName() {
        return target.getName();
      }

      public void setName(String name) {
        changeAndFire("name", name);
      }

      public String getDataProvider() {
        return target.getDataProvider();
      }

      public void setDataProvider(String dataProvider) {
        changeAndFire("dataProvider", dataProvider);
      }

      public String getId() {
        return target.getId();
      }

      public void setId(String id) {
        changeAndFire("id", id);
      }

      public ListBoxFieldType getFieldType() {
        return target.getFieldType();
      }

      public Object get(String property) {
        switch (property) {
          case "addEmptyOption": return getAddEmptyOption();
          case "relatedField": return getRelatedField();
          case "validateOnChange": return getValidateOnChange();
          case "defaultValue": return getDefaultValue();
          case "binding": return getBinding();
          case "readOnly": return getReadOnly();
          case "standaloneClassName": return getStandaloneClassName();
          case "fieldTypeInfo": return getFieldTypeInfo();
          case "label": return getLabel();
          case "required": return getRequired();
          case "helpMessage": return getHelpMessage();
          case "options": return getOptions();
          case "name": return getName();
          case "dataProvider": return getDataProvider();
          case "id": return getId();
          case "fieldType": return getFieldType();
          case "this": return target;
          default: throw new NonExistingPropertyException("DecimalListBoxFieldDefinition", property);
        }
      }

      public void set(String property, Object value) {
        switch (property) {
          case "addEmptyOption": target.setAddEmptyOption((Boolean) value);
          break;
          case "relatedField": target.setRelatedField((String) value);
          break;
          case "validateOnChange": target.setValidateOnChange((Boolean) value);
          break;
          case "defaultValue": target.setDefaultValue((Double) value);
          break;
          case "binding": target.setBinding((String) value);
          break;
          case "readOnly": target.setReadOnly((Boolean) value);
          break;
          case "standaloneClassName": target.setStandaloneClassName((String) value);
          break;
          case "label": target.setLabel((String) value);
          break;
          case "required": target.setRequired((Boolean) value);
          break;
          case "helpMessage": target.setHelpMessage((String) value);
          break;
          case "options": target.setOptions((List<DecimalSelectorOption>) value);
          break;
          case "name": target.setName((String) value);
          break;
          case "dataProvider": target.setDataProvider((String) value);
          break;
          case "id": target.setId((String) value);
          break;
          case "this": target = (DecimalListBoxFieldDefinition) value;
          agent.target = target;
          break;
          default: throw new NonExistingPropertyException("DecimalListBoxFieldDefinition", property);
        }
      }

      public Map getBeanProperties() {
        final Map props = new HashMap(agent.propertyTypes);
        props.remove("this");
        return Collections.unmodifiableMap(props);
      }

      public void copyFrom(FieldDefinition a0) {
        target.copyFrom(a0);
        agent.updateWidgetsAndFireEvents();
      }
    }
    BindableProxyFactory.addBindableProxy(DecimalListBoxFieldDefinition.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model) {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy((DecimalListBoxFieldDefinition) model);
      }
      public BindableProxy getBindableProxy() {
        return new org_kie_workbench_common_forms_fields_shared_fieldTypes_basic_selectors_listBox_definition_DecimalListBoxFieldDefinitionProxy();
      }
    });
  }
}