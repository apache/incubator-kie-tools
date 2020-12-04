package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

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

public class _DefaultSelectorOptionValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtSpecificValidator<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption> implements _DefaultSelectorOptionValidator {
  private static final java.util.List<String> ALL_PROPERTY_NAMES = 
      java.util.Collections.<String>unmodifiableList(
          java.util.Arrays.<String>asList("class","text","value"));
  private final BeanMetadata beanMetadata =
      new BeanMetadata(
          org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class,
          javax.validation.groups.Default.class);
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> value_c0  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<javax.validation.constraints.NotNull> builder()
          .setAnnotation( 
              new javax.validation.constraints.NotNull(){
                  public Class<? extends Annotation> annotationType() {  return javax.validation.constraints.NotNull.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {};}
                  public java.lang.String message() { return "{javax.validation.constraints.NotNull.message}";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("message", "{javax.validation.constraints.NotNull.message}")
            .put("payload", new java.lang.Class[] {})
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {org.hibernate.validator.constraints.impl.NotNullValidator.class})
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(false)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl value_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "value",
          java.lang.Object.class,
          false,beanMetadata,
          value_c0);
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> text_c0  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<javax.validation.constraints.NotNull> builder()
          .setAnnotation( 
              new javax.validation.constraints.NotNull(){
                  public Class<? extends Annotation> annotationType() {  return javax.validation.constraints.NotNull.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {};}
                  public java.lang.String message() { return "{javax.validation.constraints.NotNull.message}";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("message", "{javax.validation.constraints.NotNull.message}")
            .put("payload", new java.lang.Class[] {})
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {org.hibernate.validator.constraints.impl.NotNullValidator.class})
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(false)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> text_c1_0  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<javax.validation.constraints.NotNull> builder()
          .setAnnotation( 
              new javax.validation.constraints.NotNull(){
                  public Class<? extends Annotation> annotationType() {  return javax.validation.constraints.NotNull.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {javax.validation.groups.Default.class};}
                  public java.lang.String message() { return "{javax.validation.constraints.NotNull.message}";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("message", "{javax.validation.constraints.NotNull.message}")
            .put("payload", new java.lang.Class[] {})
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {org.hibernate.validator.constraints.impl.NotNullValidator.class})
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(false)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.Size> text_c1_1  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<javax.validation.constraints.Size> builder()
          .setAnnotation( 
              new javax.validation.constraints.Size(){
                  public Class<? extends Annotation> annotationType() {  return javax.validation.constraints.Size.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {javax.validation.groups.Default.class};}
                  public int min() { return 1;}
                  public int max() { return 2147483647;}
                  public java.lang.String message() { return "{javax.validation.constraints.Size.message}";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .put("min", 1)
            .put("message", "{javax.validation.constraints.Size.message}")
            .put("payload", new java.lang.Class[] {})
            .put("max", 2147483647)
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {org.hibernate.validator.constraints.impl.SizeValidatorForString.class,org.hibernate.validator.constraints.impl.SizeValidatorForCollection.class,org.hibernate.validator.constraints.impl.SizeValidatorForArray.class,org.hibernate.validator.constraints.impl.SizeValidatorForMap.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfBoolean.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfByte.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfChar.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfDouble.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfFloat.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfInt.class,org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfLong.class})
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(false)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<org.hibernate.validator.constraints.NotEmpty> text_c1  = 
      com.google.gwt.validation.client.impl.ConstraintDescriptorImpl.<org.hibernate.validator.constraints.NotEmpty> builder()
          .setAnnotation( 
              new org.hibernate.validator.constraints.NotEmpty(){
                  public Class<? extends Annotation> annotationType() {  return org.hibernate.validator.constraints.NotEmpty.class; }
                  public java.lang.Class[] payload() { return new java.lang.Class[] {};}
                  public java.lang.Class[] groups() { return new java.lang.Class[] {};}
                  public java.lang.String message() { return "{org.hibernate.validator.constraints.NotEmpty.message}";}
              }
              )
          .setAttributes(attributeBuilder()
            .put("message", "{org.hibernate.validator.constraints.NotEmpty.message}")
            .put("payload", new java.lang.Class[] {})
            .put("groups", new java.lang.Class[] {javax.validation.groups.Default.class})
            .build())
          .setConstraintValidatorClasses(new java.lang.Class[] {})
          .addComposingConstraint(text_c1_0)
          .addComposingConstraint(text_c1_1)
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(true)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl text_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "text",
          java.lang.String.class,
          false,beanMetadata,
          text_c0,
          text_c1);
  private final com.google.gwt.validation.client.impl.GwtBeanDescriptor<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption> beanDescriptor = 
      com.google.gwt.validation.client.impl.GwtBeanDescriptorImpl.builder(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption.class)
          .setConstrained(true)
          .put("value", value_pd)
          .put("text", text_pd)
          .setBeanMetadata(beanMetadata)
          .build();
  
  
  public <T> void validateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateAllNonInheritedProperties(context, object, violations, groups);
  }
  
  public <T> void expandDefaultAndValidateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
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
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
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
      Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption> beanType,
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
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) throws ValidationException {
    if (propertyName.equals("value")) {
      validateProperty_getvalue(context, violations, object, object.getValue(), false, groups);
      validateProperty_value(context, violations, object, _value(object), false, groups);
    } else if (propertyName.equals("text")) {
      validateProperty_gettext(context, violations, object, object.getText(), false, groups);
      validateProperty_text(context, violations, object, _text(object), false, groups);
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption");
    }
  }
  
  public <T> void validateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    if (propertyName.equals("value")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof java.lang.Object) {
        valueTypeMatches = true;
        validateProperty_getvalue(context, violations, null, (java.lang.Object) value, false, groups);
      }
      if ( value == null || value instanceof java.lang.Object) {
        valueTypeMatches = true;
        validateProperty_value(context, violations, null, (java.lang.Object) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else if (propertyName.equals("text")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_gettext(context, violations, null, (java.lang.String) value, false, groups);
      }
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_text(context, violations, null, (java.lang.String) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption");
    }
  }
  
  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
  
  public GwtBeanDescriptor<org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption> getConstraints(ValidationGroupsMetadata validationGroupsMetadata) {
    beanDescriptor.setValidationGroupsMetadata(validationGroupsMetadata);
    return beanDescriptor;
  }
  
  private final <T> void validateProperty_value(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      final java.lang.Object value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("value");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      validate(myContext, violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), value_c0, groups);
    }
  }
  
  private final <T> void validateProperty_getvalue(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      final java.lang.Object value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  private final <T> void validateProperty_text(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("text");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      validate(myContext, violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), text_c0, groups);
      // Report org.hibernate.validator.constraints.NotEmpty as Single Violation
      Set<ConstraintViolation<T>> text_c1_violations = 
          new HashSet<ConstraintViolation<T>>();
      if (validate(myContext, text_c1_violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), text_c1_0, groups) ||
          validate(myContext, text_c1_violations, object, value, new org.hibernate.validator.constraints.impl.SizeValidatorForString(), text_c1_1, groups) ||
          false ) {
        addSingleViolation(myContext, violations, object, value, text_c1);
      }
    }
  }
  
  private final <T> void validateProperty_gettext(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  
  private <T> void validateAllNonInheritedProperties(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateProperty_getvalue(context, violations, object, object.getValue(), true, groups);
    validateProperty_value(context, violations, object, _value(object), true, groups);
    validateProperty_gettext(context, violations, object, object.getText(), true, groups);
    validateProperty_text(context, violations, object, _text(object), true, groups);
  }
  
  // Write the wrappers after we know which are needed
  private native java.lang.Object _value(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object) /*-{
    return object.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption::value;
  }-*/;
  
  private native java.lang.String _text(org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption object) /*-{
    return object.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DefaultSelectorOption::text;
  }-*/;
  
  
}
