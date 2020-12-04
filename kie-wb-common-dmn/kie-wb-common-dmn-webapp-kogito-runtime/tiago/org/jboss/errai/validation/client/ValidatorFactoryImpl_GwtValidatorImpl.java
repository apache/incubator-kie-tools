package org.jboss.errai.validation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.impl.GwtBeanDescriptor;
import com.google.gwt.validation.client.impl.GwtSpecificValidator;
import com.google.gwt.validation.client.impl.GwtValidationContext;
import com.google.gwt.validation.client.impl.metadata.ValidationGroupsMetadata;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import javax.validation.groups.Default;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.BeanDescriptor;

public class ValidatorFactoryImpl_GwtValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtValidator implements org.jboss.errai.validation.client.ValidatorFactoryImpl.GwtValidator {
  public ValidatorFactoryImpl_GwtValidatorImpl() {
    super(createValidationGroupsMetadata());
  }
  
  private static ValidationGroupsMetadata createValidationGroupsMetadata() {
    return ValidationGroupsMetadata.builder()
        .addGroup(javax.validation.groups.Default.class)
        .build();
  }
  
  public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
    checkNotNull(object, "object");
    checkNotNull(groups, "groups");
    checkGroups(groups);
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItem) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItem.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItem) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Description) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Description.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Description) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Decision) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Decision.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Decision) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BgColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BgColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BgColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BorderColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BorderColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BorderColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Association) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Association.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Association) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNDiagram.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Id) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Id.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Id) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontFamily) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontFamily.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontFamily) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BackgroundSet) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BackgroundSet) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSet) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSet.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontSet) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DecisionService) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DecisionService.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DecisionService) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) object, groups);
    }
    if (object instanceof org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class, 
          object, 
          org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputData) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputData.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InputData) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.NameHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.NameHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSize) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSize.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontSize) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Definitions) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Definitions.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Definitions) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.TextAnnotation.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Question) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Question.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Question) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DRGElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DRGElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DRGElement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.NamedElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.NamedElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.NamedElement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DMNElement) object, groups);
    }
    throw new IllegalArgumentException("ValidatorFactoryImpl.GwtValidator can not  validate "+ object.getClass().getName()+ ". "
        + "Valid types are [org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption, org.kie.workbench.common.dmn.api.definition.model.InformationItem, org.kie.workbench.common.dmn.api.property.dmn.Description, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.Decision, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption, org.kie.workbench.common.dmn.api.property.background.BgColour, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption, org.kie.workbench.common.dmn.api.property.dmn.QNameHolder, org.kie.workbench.common.dmn.api.property.background.BorderColour, org.kie.workbench.common.dmn.api.definition.model.Association, org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression, org.kie.workbench.common.dmn.api.definition.model.DMNDiagram, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.Id, org.kie.workbench.common.dmn.api.property.font.FontFamily, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption, org.kie.workbench.common.dmn.api.property.background.BackgroundSet, org.kie.workbench.common.dmn.api.property.font.FontSet, org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta, org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder, org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary, org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement, org.kie.workbench.common.dmn.api.definition.model.InformationRequirement, org.kie.workbench.common.dmn.api.definition.model.DecisionService, org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel, org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition, org.kie.workbench.common.dmn.api.property.font.FontColour, org.kie.workbench.common.dmn.api.definition.model.InputData, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.NameHolder, org.kie.workbench.common.dmn.api.property.font.FontSize, org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement, org.kie.workbench.common.dmn.api.definition.model.Definitions, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation, org.kie.workbench.common.dmn.api.property.dmn.Question, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.DRGElement, org.kie.workbench.common.dmn.api.definition.model.NamedElement, org.kie.workbench.common.dmn.api.definition.model.DMNElement]");
  }
  
  public <T> Set<ConstraintViolation<T>> validateProperty(T object,String propertyName, Class<?>... groups) {
    checkNotNull(object, "object");
    checkNotNull(propertyName, "propertyName");
    checkNotNull(groups, "groups");
    checkGroups(groups);
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItem) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItem.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItem) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Description) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Description.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.Description) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Decision) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Decision.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.Decision) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BgColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BgColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.background.BgColour) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BorderColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BorderColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.background.BorderColour) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Association) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Association.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.Association) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNDiagram.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Id) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Id.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.Id) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontFamily) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontFamily.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.font.FontFamily) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BackgroundSet) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.background.BackgroundSet) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSet) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSet.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.font.FontSet) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DecisionService) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DecisionService.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.DecisionService) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class, 
          object, 
          org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontColour) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontColour.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.font.FontColour) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputData) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputData.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.InputData) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.NameHolder) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.NameHolder) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSize) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSize.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.font.FontSize) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Definitions) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Definitions.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.Definitions) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.TextAnnotation.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Question) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Question.class, 
          object, 
          org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.property.dmn.Question) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition.class, 
          object, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DRGElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DRGElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.DRGElement) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.NamedElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.NamedElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.NamedElement) object, propertyName, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNElement) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNElement.class, 
          object, 
          org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE
          .validateProperty(context, (org.kie.workbench.common.dmn.api.definition.model.DMNElement) object, propertyName, groups);
    }
    throw new IllegalArgumentException("ValidatorFactoryImpl.GwtValidator can not  validate "+ object.getClass().getName()+ ". "
        + "Valid types are [org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption, org.kie.workbench.common.dmn.api.definition.model.InformationItem, org.kie.workbench.common.dmn.api.property.dmn.Description, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.Decision, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption, org.kie.workbench.common.dmn.api.property.background.BgColour, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption, org.kie.workbench.common.dmn.api.property.dmn.QNameHolder, org.kie.workbench.common.dmn.api.property.background.BorderColour, org.kie.workbench.common.dmn.api.definition.model.Association, org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression, org.kie.workbench.common.dmn.api.definition.model.DMNDiagram, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.Id, org.kie.workbench.common.dmn.api.property.font.FontFamily, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption, org.kie.workbench.common.dmn.api.property.background.BackgroundSet, org.kie.workbench.common.dmn.api.property.font.FontSet, org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta, org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder, org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary, org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement, org.kie.workbench.common.dmn.api.definition.model.InformationRequirement, org.kie.workbench.common.dmn.api.definition.model.DecisionService, org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel, org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition, org.kie.workbench.common.dmn.api.property.font.FontColour, org.kie.workbench.common.dmn.api.definition.model.InputData, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.NameHolder, org.kie.workbench.common.dmn.api.property.font.FontSize, org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement, org.kie.workbench.common.dmn.api.definition.model.Definitions, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation, org.kie.workbench.common.dmn.api.property.dmn.Question, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.DRGElement, org.kie.workbench.common.dmn.api.definition.model.NamedElement, org.kie.workbench.common.dmn.api.definition.model.DMNElement]");
  }
  
  public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
    checkNotNull(beanType, "beanType");
    checkNotNull(propertyName, "propertyName");
    checkNotNull(groups, "groups");
    checkGroups(groups);
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.InformationItem.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItem.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.InformationItem>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.Description.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Description.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.Description>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.Decision.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Decision.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.Decision>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.background.BgColour.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BgColour.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.background.BgColour>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.QNameHolder>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.background.BorderColour.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BorderColour.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.background.BorderColour>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.Association.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Association.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.Association>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.DMNDiagram.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNDiagram.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.DMNDiagram>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.Id.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Id.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.Id>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.font.FontFamily.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontFamily.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.font.FontFamily>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.background.BackgroundSet>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.font.FontSet.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSet.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.font.FontSet>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.InformationRequirement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InformationRequirement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.InformationRequirement>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.DecisionService.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DecisionService.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.DecisionService>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class, 
          null, 
          org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.font.FontColour.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontColour.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.font.FontColour>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.InputData.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.InputData.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.InputData>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.NameHolder>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.font.FontSize.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.font.FontSize.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.font.FontSize>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.Definitions.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.Definitions.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.Definitions>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.TextAnnotation.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.TextAnnotation.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.TextAnnotation>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.property.dmn.Question.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.property.dmn.Question.class, 
          null, 
          org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.property.dmn.Question>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition.class, 
          null, 
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.DRGElement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DRGElement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.DRGElement>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.NamedElement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.NamedElement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.NamedElement>)beanType, propertyName, value, groups);
    }
    if (beanType.equals(org.kie.workbench.common.dmn.api.definition.model.DMNElement.class)) {
      GwtValidationContext<T> context = new GwtValidationContext<T>(
          (Class<T>) org.kie.workbench.common.dmn.api.definition.model.DMNElement.class, 
          null, 
          org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata()), 
          getMessageInterpolator(), 
          getTraversableResolver(), 
          this);
      return org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE
          .validateValue(context, (Class<org.kie.workbench.common.dmn.api.definition.model.DMNElement>)beanType, propertyName, value, groups);
    }
    throw new IllegalArgumentException("ValidatorFactoryImpl.GwtValidator can not  validate "+ beanType.getName()+ ". "
        + "Valid types are [org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption, org.kie.workbench.common.dmn.api.definition.model.InformationItem, org.kie.workbench.common.dmn.api.property.dmn.Description, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.Decision, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption, org.kie.workbench.common.dmn.api.property.background.BgColour, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption, org.kie.workbench.common.dmn.api.property.dmn.QNameHolder, org.kie.workbench.common.dmn.api.property.background.BorderColour, org.kie.workbench.common.dmn.api.definition.model.Association, org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression, org.kie.workbench.common.dmn.api.definition.model.DMNDiagram, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.Id, org.kie.workbench.common.dmn.api.property.font.FontFamily, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption, org.kie.workbench.common.dmn.api.property.background.BackgroundSet, org.kie.workbench.common.dmn.api.property.font.FontSet, org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta, org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder, org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary, org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement, org.kie.workbench.common.dmn.api.definition.model.InformationRequirement, org.kie.workbench.common.dmn.api.definition.model.DecisionService, org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel, org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition, org.kie.workbench.common.dmn.api.property.font.FontColour, org.kie.workbench.common.dmn.api.definition.model.InputData, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.NameHolder, org.kie.workbench.common.dmn.api.property.font.FontSize, org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement, org.kie.workbench.common.dmn.api.definition.model.Definitions, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation, org.kie.workbench.common.dmn.api.property.dmn.Question, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.DRGElement, org.kie.workbench.common.dmn.api.definition.model.NamedElement, org.kie.workbench.common.dmn.api.definition.model.DMNElement]");
  }
  
  public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
    checkNotNull(clazz, "clazz");
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.InformationItem.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.Description.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.Decision.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.background.BgColour.class)) {
      return org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.background.BorderColour.class)) {
      return org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.Association.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.DMNDiagram.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.Id.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.font.FontFamily.class)) {
      return org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class)) {
      return org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.font.FontSet.class)) {
      return org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.InformationRequirement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.DecisionService.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class)) {
      return org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.font.FontColour.class)) {
      return org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.InputData.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.font.FontSize.class)) {
      return org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.Definitions.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.TextAnnotation.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.property.dmn.Question.class)) {
      return org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition.class)) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.DRGElement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.NamedElement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    if (clazz.equals(org.kie.workbench.common.dmn.api.definition.model.DMNElement.class)) {
      return org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE.getConstraints(getValidationGroupsMetadata());
    }
    throw new IllegalArgumentException("ValidatorFactoryImpl.GwtValidator can not  validate "+ clazz.getName()+ ". "
        + "Valid types are [org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption, org.kie.workbench.common.dmn.api.definition.model.InformationItem, org.kie.workbench.common.dmn.api.property.dmn.Description, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.Decision, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption, org.kie.workbench.common.dmn.api.property.background.BgColour, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption, org.kie.workbench.common.dmn.api.property.dmn.QNameHolder, org.kie.workbench.common.dmn.api.property.background.BorderColour, org.kie.workbench.common.dmn.api.definition.model.Association, org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression, org.kie.workbench.common.dmn.api.definition.model.DMNDiagram, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.Id, org.kie.workbench.common.dmn.api.property.font.FontFamily, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption, org.kie.workbench.common.dmn.api.property.background.BackgroundSet, org.kie.workbench.common.dmn.api.property.font.FontSet, org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta, org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder, org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary, org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement, org.kie.workbench.common.dmn.api.definition.model.InformationRequirement, org.kie.workbench.common.dmn.api.definition.model.DecisionService, org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel, org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition, org.kie.workbench.common.dmn.api.property.font.FontColour, org.kie.workbench.common.dmn.api.definition.model.InputData, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.NameHolder, org.kie.workbench.common.dmn.api.property.font.FontSize, org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement, org.kie.workbench.common.dmn.api.definition.model.Definitions, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation, org.kie.workbench.common.dmn.api.property.dmn.Question, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.DRGElement, org.kie.workbench.common.dmn.api.definition.model.NamedElement, org.kie.workbench.common.dmn.api.definition.model.DMNElement]");
  }
  
  public <T> Set<ConstraintViolation<T>> validate(GwtValidationContext<T> context,
      Object object, Class<?>... groups) {
    checkNotNull(context, "context");
    checkNotNull(object, "object");
    checkNotNull(groups, "groups");
    checkGroups(groups);
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BgColour) {
      return org.kie.workbench.common.dmn.api.property.background._BgColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BgColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSet) {
      return org.kie.workbench.common.dmn.api.property.font._FontSetValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontSet) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Id) {
      return org.kie.workbench.common.dmn.api.property.dmn._IdValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Id) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition._DecimalBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Description) {
      return org.kie.workbench.common.dmn.api.property.dmn._DescriptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Description) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) {
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeSourceValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) {
      return org.kie.workbench.common.dmn.api.definition.model._InputClauseLiteralExpressionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition._TextAreaFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Association) {
      return org.kie.workbench.common.dmn.api.definition.model._AssociationValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Association) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) {
      return org.kie.workbench.common.dmn.api.property.dmn._QNameHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DefaultSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition._IntegerBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontSize) {
      return org.kie.workbench.common.dmn.api.property.font._FontSizeValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontSize) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItem) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItem) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._DecimalSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) {
      return org.kie.workbench.common.dmn.api.definition.model._DMNDiagramValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DMNDiagram) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationItemPrimaryValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.Question) {
      return org.kie.workbench.common.dmn.api.property.dmn._QuestionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.Question) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._CharacterBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._IntegerSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._CharacterSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontFamily) {
      return org.kie.workbench.common.dmn.api.property.font._FontFamilyValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontFamily) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) {
      return org.kie.workbench.common.dmn.api.property.dmn._DocumentationLinksHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DecisionService) {
      return org.kie.workbench.common.dmn.api.definition.model._DecisionServiceValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DecisionService) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) {
      return org.kie.workbench.common.dmn.api.definition.model._BusinessKnowledgeModelValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Decision) {
      return org.kie.workbench.common.dmn.api.definition.model._DecisionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Decision) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) {
      return org.kie.workbench.common.dmn.api.definition.model._InformationRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InformationRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) {
      return org.kie.workbench.common.dmn.api.definition.model._AuthorityRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.NameHolder) {
      return org.kie.workbench.common.dmn.api.property.dmn._NameHolderValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.NameHolder) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) {
      return org.kie.workbench.common.dmn.api.definition.model._KnowledgeRequirementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._EnumSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) {
      return org.kie.workbench.common.dmn.api.property.dmn._AllowedAnswersValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BackgroundSet) {
      return org.kie.workbench.common.dmn.api.property.background._BackgroundSetValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BackgroundSet) object, groups);
    }
    if (object instanceof org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) {
      return org.kie.workbench.common.stunner.forms.model._ColorPickerFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.Definitions) {
      return org.kie.workbench.common.dmn.api.definition.model._DefinitionsValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.Definitions) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.relations._TableColumnMetaValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.font.FontColour) {
      return org.kie.workbench.common.dmn.api.property.font._FontColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.font.FontColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.property.background.BorderColour) {
      return org.kie.workbench.common.dmn.api.property.background._BorderColourValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.property.background.BorderColour) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.InputData) {
      return org.kie.workbench.common.dmn.api.definition.model._InputDataValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.InputData) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DRGElement) {
      return org.kie.workbench.common.dmn.api.definition.model._DRGElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DRGElement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) {
      return org.kie.workbench.common.dmn.api.definition.model._TextAnnotationValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.TextAnnotation) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition._TextBoxFieldDefinitionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition) object, groups);
    }
    if (object instanceof org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) {
      return org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors._StringSelectorOptionValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.NamedElement) {
      return org.kie.workbench.common.dmn.api.definition.model._NamedElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.NamedElement) object, groups);
    }
    if (object instanceof org.kie.workbench.common.dmn.api.definition.model.DMNElement) {
      return org.kie.workbench.common.dmn.api.definition.model._DMNElementValidator.INSTANCE
          .validate(context, (org.kie.workbench.common.dmn.api.definition.model.DMNElement) object, groups);
    }
    throw new IllegalArgumentException("ValidatorFactoryImpl.GwtValidator can not  validate "+ object.getClass().getName()+ ". "
        + "Valid types are [org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption, org.kie.workbench.common.dmn.api.definition.model.InformationItem, org.kie.workbench.common.dmn.api.property.dmn.Description, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.Decision, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption, org.kie.workbench.common.dmn.api.property.background.BgColour, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption, org.kie.workbench.common.dmn.api.property.dmn.QNameHolder, org.kie.workbench.common.dmn.api.property.background.BorderColour, org.kie.workbench.common.dmn.api.definition.model.Association, org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression, org.kie.workbench.common.dmn.api.definition.model.DMNDiagram, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.Id, org.kie.workbench.common.dmn.api.property.font.FontFamily, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption, org.kie.workbench.common.dmn.api.property.background.BackgroundSet, org.kie.workbench.common.dmn.api.property.font.FontSet, org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta, org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder, org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary, org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement, org.kie.workbench.common.dmn.api.definition.model.InformationRequirement, org.kie.workbench.common.dmn.api.definition.model.DecisionService, org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel, org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition, org.kie.workbench.common.dmn.api.property.font.FontColour, org.kie.workbench.common.dmn.api.definition.model.InputData, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition, org.kie.workbench.common.dmn.api.property.dmn.NameHolder, org.kie.workbench.common.dmn.api.property.font.FontSize, org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement, org.kie.workbench.common.dmn.api.definition.model.Definitions, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation, org.kie.workbench.common.dmn.api.property.dmn.Question, org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition, org.kie.workbench.common.dmn.api.definition.model.DRGElement, org.kie.workbench.common.dmn.api.definition.model.NamedElement, org.kie.workbench.common.dmn.api.definition.model.DMNElement]");
  }
}
