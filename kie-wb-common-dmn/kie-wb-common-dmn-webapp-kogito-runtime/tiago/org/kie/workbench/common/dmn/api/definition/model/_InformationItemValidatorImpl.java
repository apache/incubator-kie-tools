package org.kie.workbench.common.dmn.api.definition.model;

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

public class _InformationItemValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtSpecificValidator<org.kie.workbench.common.dmn.api.definition.model.InformationItem> implements _InformationItemValidator {
  private static final java.util.List<String> ALL_PROPERTY_NAMES = 
      java.util.Collections.<String>unmodifiableList(
          java.util.Arrays.<String>asList("additionalAttributes","class","defaultNamespace","description","domainObjectNameTranslationKey","domainObjectUUID","extensionElements","hasTypeRefs","id","name","nameHolder","nsContext","parent","stunnerCategory","stunnerLabels","typeRef","typeRefHolder"));
  private final BeanMetadata beanMetadata =
      new BeanMetadata(
          org.kie.workbench.common.dmn.api.definition.model.InformationItem.class,
          javax.validation.groups.Default.class);
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl typeRefHolder_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "typeRefHolder",
          org.kie.workbench.common.dmn.api.property.dmn.QNameHolder.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl id_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "id",
          org.kie.workbench.common.dmn.api.property.dmn.Id.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl description_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "description",
          org.kie.workbench.common.dmn.api.property.dmn.Description.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl nameHolder_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "nameHolder",
          org.kie.workbench.common.dmn.api.property.dmn.NameHolder.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.GwtBeanDescriptor<org.kie.workbench.common.dmn.api.definition.model.InformationItem> beanDescriptor = 
      com.google.gwt.validation.client.impl.GwtBeanDescriptorImpl.builder(org.kie.workbench.common.dmn.api.definition.model.InformationItem.class)
          .setConstrained(false)
          .put("typeRefHolder", typeRefHolder_pd)
          .put("id", id_pd)
          .put("description", description_pd)
          .put("nameHolder", nameHolder_pd)
          .setBeanMetadata(beanMetadata)
          .build();
  
  
  public <T> void validateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateAllNonInheritedProperties(context, object, violations, groups);
  }
  
  public <T> void expandDefaultAndValidateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
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
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
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
      Class<org.kie.workbench.common.dmn.api.definition.model.InformationItem> beanType,
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
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) throws ValidationException {
    if (propertyName.equals("typeRefHolder")) {
      validateProperty_gettypeRefHolder(context, violations, object, object.getTypeRefHolder(), false, groups);
      validateProperty_typeRefHolder(context, violations, object, _typeRefHolder(object), false, groups);
    } else if (propertyName.equals("id")) {
    } else if (propertyName.equals("description")) {
    } else if (propertyName.equals("nameHolder")) {
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.dmn.api.definition.model.InformationItem");
    }
  }
  
  public <T> void validateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.dmn.api.definition.model.InformationItem> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    if (propertyName.equals("typeRefHolder")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) {
        valueTypeMatches = true;
        validateProperty_gettypeRefHolder(context, violations, null, (org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) value, false, groups);
      }
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) {
        valueTypeMatches = true;
        validateProperty_typeRefHolder(context, violations, null, (org.kie.workbench.common.dmn.api.property.dmn.QNameHolder) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else if (propertyName.equals("id")) {
      boolean valueTypeMatches = false;
    } else if (propertyName.equals("description")) {
      boolean valueTypeMatches = false;
    } else if (propertyName.equals("nameHolder")) {
      boolean valueTypeMatches = false;
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.dmn.api.definition.model.InformationItem");
    }
  }
  
  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
  
  public GwtBeanDescriptor<org.kie.workbench.common.dmn.api.definition.model.InformationItem> getConstraints(ValidationGroupsMetadata validationGroupsMetadata) {
    beanDescriptor.setValidationGroupsMetadata(validationGroupsMetadata);
    return beanDescriptor;
  }
  
  private final <T> void validateProperty_typeRefHolder(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
      final org.kie.workbench.common.dmn.api.property.dmn.QNameHolder value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("typeRefHolder");
    Node leafNode = myContext.getPath().getLeafNode();
    PathImpl path = myContext.getPath().getPathWithoutLeafNode();
    boolean isReachable;
    try {
      isReachable = myContext.getTraversableResolver().isReachable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
    } catch (Exception e) {
      throw new ValidationException("TraversableResolver isReachable caused an exception", e);
    }
    if (isReachable) {
      if (honorValid && value != null) {
        boolean isCascadable;
        try {
          isCascadable = myContext.getTraversableResolver().isCascadable(object, leafNode, myContext.getRootBeanClass(), path, java.lang.annotation.ElementType.FIELD);
        } catch (Exception e) {
          throw new ValidationException("TraversableResolver isCascadable caused an exception", e);
        }
        if (isCascadable) {
           if (!context.alreadyValidated(value)) {
            violations.addAll(myContext.getValidator().validate(myContext, value, groups));
          }
        }
      }
    }
  }
  
  private final <T> void validateProperty_gettypeRefHolder(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
      final org.kie.workbench.common.dmn.api.property.dmn.QNameHolder value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  
  private <T> void validateAllNonInheritedProperties(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.definition.model.InformationItem object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateProperty_gettypeRefHolder(context, violations, object, object.getTypeRefHolder(), true, groups);
    validateProperty_typeRefHolder(context, violations, object, _typeRefHolder(object), true, groups);
  }
  
  // Write the wrappers after we know which are needed
  private native org.kie.workbench.common.dmn.api.property.dmn.QNameHolder _typeRefHolder(org.kie.workbench.common.dmn.api.definition.model.InformationItem object) /*-{
    return object.@org.kie.workbench.common.dmn.api.definition.model.InformationItem::typeRefHolder;
  }-*/;
  
  
}
