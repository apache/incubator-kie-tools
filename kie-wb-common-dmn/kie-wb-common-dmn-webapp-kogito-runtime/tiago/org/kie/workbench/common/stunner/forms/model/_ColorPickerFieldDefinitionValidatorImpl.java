package org.kie.workbench.common.stunner.forms.model;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintViolation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.impl.metadata.ValidationGroupsMetadata;
import com.google.gwt.validation.client.impl.Group;
import com.google.gwt.validation.client.impl.GroupChain;
import com.google.gwt.validation.client.impl.PathImpl;
import javax.validation.Path.Node;
import com.google.gwt.validation.client.impl.GroupChainGenerator;
import com.google.gwt.validation.client.impl.GwtBeanDescriptor;
import com.google.gwt.validation.client.impl.metadata.BeanMetadata;
import com.google.gwt.validation.client.impl.GwtValidationContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.IllegalArgumentException;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.validation.ValidationException;

public class _ColorPickerFieldDefinitionValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtSpecificValidator<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition> implements _ColorPickerFieldDefinitionValidator {
  private static final java.util.List<String> ALL_PROPERTY_NAMES = 
      java.util.Collections.<String>unmodifiableList(
          java.util.Arrays.<String>asList("binding","class","defaultValue","fieldType","fieldTypeInfo","helpMessage","id","label","name","readOnly","required","standaloneClassName","validateOnChange"));
  private final BeanMetadata beanMetadata =
      new BeanMetadata(
          org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class,
          javax.validation.groups.Default.class);
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.Pattern> defaultValue_c0  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<javax.validation.constraints.Pattern> builder()
          .setAnnotation( 
              new javax.validation.constraints.Pattern(){
                  public Class<? extends Annotation> annotationType() {  return javax.validation.constraints.Pattern.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.String regexp() { return "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {};}
                  public javax.validation.constraints.Pattern.Flag[] flags() { return new javax.validation.constraints.Pattern.Flag[] {};}
                  public java.lang.String message() { return "Invalid color code";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("flags", new javax.validation.constraints.Pattern.Flag[] {})
            .put("regexp", "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .put("message", "Invalid color code")
            .put("payload", new java.lang.Class[] {})
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {org.hibernate.validator.constraints.impl.PatternValidator.class})
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(false)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl defaultValue_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "defaultValue",
          java.lang.String.class,
          false,beanMetadata,
          defaultValue_c0);
  private final com.google.gwt.validation.client.impl.GwtBeanDescriptor<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition> beanDescriptor = 
      com.google.gwt.validation.client.impl.GwtBeanDescriptorImpl.builder(org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition.class)
          .setConstrained(true)
          .put("defaultValue", defaultValue_pd)
          .setBeanMetadata(beanMetadata)
          .build();
  
  
  public <T> void validateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateAllNonInheritedProperties(context, object, violations, groups);
  }
  
  public <T> void expandDefaultAndValidateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      Set<ConstraintViolation<T>> violations,
      Group... groups) {
    ArrayList<Class<?>> justGroups = new ArrayList<Class<?>>();
    for (Group g : groups) {
      if (!g.isDefaultGroup() || !getBeanMetadata().defaultGroupSequenceIsRedefined()) {
        justGroups.add(g.getGroup());
      }
    }
    Class<?>[] justGroupsArray = justGroups.toArray(new Class<?>[justGroups.size()]);
    validateAllNonInheritedProperties(context, object, violations, justGroupsArray);
    if (getBeanMetadata().defaultGroupSequenceIsRedefined()) {
      for (Class<?> g : beanMetadata.getDefaultGroupSequence()) {
        int numberOfViolations = violations.size();
        validateAllNonInheritedProperties(context, object, violations, g);
        if (violations.size() > numberOfViolations) {
          break;
        }
      }
    }
    else {
    }
  }
  
  public <T> void expandDefaultAndValidatePropertyGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Group... groups) {
    ArrayList<Class<?>> justGroups = new ArrayList<Class<?>>();
    for (Group g : groups) {
      if (!g.isDefaultGroup() || !getBeanMetadata().defaultGroupSequenceIsRedefined()) {
        justGroups.add(g.getGroup());
      }
    }
    Class<?>[] justGroupsArray = justGroups.toArray(new Class<?>[justGroups.size()]);
    validatePropertyGroups(context, object, propertyName, violations, justGroupsArray);
    if (getBeanMetadata().defaultGroupSequenceIsRedefined()) {
      for (Class<?> g : beanMetadata.getDefaultGroupSequence()) {
        int numberOfViolations = violations.size();
        validatePropertyGroups(context, object, propertyName, violations, g);
        if (violations.size() > numberOfViolations) {
          break;
        }
      }
    }
  }
  
  public <T> void expandDefaultAndValidateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Group... groups) {
    ArrayList<Class<?>> justGroups = new ArrayList<Class<?>>();
    for (Group g : groups) {
      if (!g.isDefaultGroup() || !getBeanMetadata().defaultGroupSequenceIsRedefined()) {
        justGroups.add(g.getGroup());
      }
    }
    Class<?>[] justGroupsArray = justGroups.toArray(new Class<?>[justGroups.size()]);
    validateValueGroups(context, beanType, propertyName, value, violations, justGroupsArray);
    if (getBeanMetadata().defaultGroupSequenceIsRedefined()) {
      for (Class<?> g : beanMetadata.getDefaultGroupSequence()) {
        int numberOfViolations = violations.size();
        validateValueGroups(context, beanType, propertyName, value, violations, g);
        if (violations.size() > numberOfViolations) {
          break;
        }
      }
    }
  }
  
  public <T> void validatePropertyGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) throws ValidationException {
    if (propertyName.equals("defaultValue")) {
      validateProperty_getdefaultValue(context, violations, object, object.getDefaultValue(), false, groups);
      validateProperty_defaultValue(context, violations, object, _defaultValue(object), false, groups);
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition");
    }
  }
  
  public <T> void validateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    if (propertyName.equals("defaultValue")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_getdefaultValue(context, violations, null, (java.lang.String) value, false, groups);
      }
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_defaultValue(context, violations, null, (java.lang.String) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition");
    }
  }
  
  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
  
  public GwtBeanDescriptor<org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition> getConstraints(ValidationGroupsMetadata validationGroupsMetadata) {
    beanDescriptor.setValidationGroupsMetadata(validationGroupsMetadata);
    return beanDescriptor;
  }
  
  private final <T> void validateProperty_defaultValue(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("defaultValue");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      validate(myContext, violations, object, value, new org.hibernate.validator.constraints.impl.PatternValidator(), defaultValue_c0, groups);
    }
  }
  
  private final <T> void validateProperty_getdefaultValue(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  
  private <T> void validateAllNonInheritedProperties(
      GwtValidationContext<T> context,
      org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateProperty_getdefaultValue(context, violations, object, object.getDefaultValue(), true, groups);
    validateProperty_defaultValue(context, violations, object, _defaultValue(object), true, groups);
  }
  
  // Write the wrappers after we know which are needed
  private native java.lang.String _defaultValue(org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition object) /*-{
    return object.@org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition::defaultValue;
  }-*/;
  
  
}
