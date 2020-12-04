package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations;

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

public class _TableColumnMetaValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtSpecificValidator<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta> implements _TableColumnMetaValidator {
  private static final java.util.List<String> ALL_PROPERTY_NAMES = 
      java.util.Collections.<String>unmodifiableList(
          java.util.Arrays.<String>asList("class","label","property"));
  private final BeanMetadata beanMetadata =
      new BeanMetadata(
          org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class,
          javax.validation.groups.Default.class);
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> label_c0  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.Size> label_c1_0  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> label_c1_1  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<org.hibernate.validator.constraints.NotEmpty> label_c1  = 
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
          .addComposingConstraint(label_c1_0)
          .addComposingConstraint(label_c1_1)
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(true)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl label_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "label",
          java.lang.String.class,
          false,beanMetadata,
          label_c0,
          label_c1);
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> property_c0  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.NotNull> property_c1_0  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<javax.validation.constraints.Size> property_c1_1  = 
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
  
  private final com.google.gwt.validation.client.impl.ConstraintDescriptorImpl<org.hibernate.validator.constraints.NotEmpty> property_c1  = 
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
          .addComposingConstraint(property_c1_0)
          .addComposingConstraint(property_c1_1)
          .setGroups(new java.lang.Class[] {javax.validation.groups.Default.class})
          .setPayload(new java.lang.Class[] {})
          .setReportAsSingleViolation(true)
          .setElementType(java.lang.annotation.ElementType.FIELD)
          .setDefinedOn(com.google.gwt.validation.client.impl.ConstraintOrigin.DEFINED_LOCALLY)
          .build();
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl property_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "property",
          java.lang.String.class,
          false,beanMetadata,
          property_c0,
          property_c1);
  private final com.google.gwt.validation.client.impl.GwtBeanDescriptor<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta> beanDescriptor = 
      com.google.gwt.validation.client.impl.GwtBeanDescriptorImpl.builder(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta.class)
          .setConstrained(true)
          .put("label", label_pd)
          .put("property", property_pd)
          .setBeanMetadata(beanMetadata)
          .build();
  
  
  public <T> void validateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateAllNonInheritedProperties(context, object, violations, groups);
  }
  
  public <T> void expandDefaultAndValidateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
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
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
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
      Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta> beanType,
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
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) throws ValidationException {
    if (propertyName.equals("label")) {
      validateProperty_getlabel(context, violations, object, object.getLabel(), false, groups);
      validateProperty_label(context, violations, object, _label(object), false, groups);
    } else if (propertyName.equals("property")) {
      validateProperty_getproperty(context, violations, object, object.getProperty(), false, groups);
      validateProperty_property(context, violations, object, _property(object), false, groups);
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta");
    }
  }
  
  public <T> void validateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    if (propertyName.equals("label")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_getlabel(context, violations, null, (java.lang.String) value, false, groups);
      }
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_label(context, violations, null, (java.lang.String) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else if (propertyName.equals("property")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_getproperty(context, violations, null, (java.lang.String) value, false, groups);
      }
      if ( value == null || value instanceof java.lang.String) {
        valueTypeMatches = true;
        validateProperty_property(context, violations, null, (java.lang.String) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta");
    }
  }
  
  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
  
  public GwtBeanDescriptor<org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta> getConstraints(ValidationGroupsMetadata validationGroupsMetadata) {
    beanDescriptor.setValidationGroupsMetadata(validationGroupsMetadata);
    return beanDescriptor;
  }
  
  private final <T> void validateProperty_label(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("label");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      validate(myContext, violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), label_c0, groups);
      // Report org.hibernate.validator.constraints.NotEmpty as Single Violation
      Set<ConstraintViolation<T>> label_c1_violations = 
          new HashSet<ConstraintViolation<T>>();
      if (validate(myContext, label_c1_violations, object, value, new org.hibernate.validator.constraints.impl.SizeValidatorForString(), label_c1_0, groups) ||
          validate(myContext, label_c1_violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), label_c1_1, groups) ||
          false ) {
        addSingleViolation(myContext, violations, object, value, label_c1);
      }
    }
  }
  
  private final <T> void validateProperty_getlabel(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  private final <T> void validateProperty_property(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("property");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      validate(myContext, violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), property_c0, groups);
      // Report org.hibernate.validator.constraints.NotEmpty as Single Violation
      Set<ConstraintViolation<T>> property_c1_violations = 
          new HashSet<ConstraintViolation<T>>();
      if (validate(myContext, property_c1_violations, object, value, new org.hibernate.validator.constraints.impl.NotNullValidator(), property_c1_0, groups) ||
          validate(myContext, property_c1_violations, object, value, new org.hibernate.validator.constraints.impl.SizeValidatorForString(), property_c1_1, groups) ||
          false ) {
        addSingleViolation(myContext, violations, object, value, property_c1);
      }
    }
  }
  
  private final <T> void validateProperty_getproperty(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      final java.lang.String value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  
  private <T> void validateAllNonInheritedProperties(
      GwtValidationContext<T> context,
      org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateProperty_getlabel(context, violations, object, object.getLabel(), true, groups);
    validateProperty_label(context, violations, object, _label(object), true, groups);
    validateProperty_getproperty(context, violations, object, object.getProperty(), true, groups);
    validateProperty_property(context, violations, object, _property(object), true, groups);
  }
  
  // Write the wrappers after we know which are needed
  private native java.lang.String _property(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object) /*-{
    return object.@org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta::property;
  }-*/;
  
  private native java.lang.String _label(org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta object) /*-{
    return object.@org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta::label;
  }-*/;
  
  
}
