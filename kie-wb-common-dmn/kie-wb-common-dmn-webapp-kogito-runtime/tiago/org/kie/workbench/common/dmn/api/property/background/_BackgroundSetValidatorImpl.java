package org.kie.workbench.common.dmn.api.property.background;

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

public class _BackgroundSetValidatorImpl extends com.google.gwt.validation.client.impl.AbstractGwtSpecificValidator<org.kie.workbench.common.dmn.api.property.background.BackgroundSet> implements _BackgroundSetValidator {
  private static final java.util.List<String> ALL_PROPERTY_NAMES = 
      java.util.Collections.<String>unmodifiableList(
          java.util.Arrays.<String>asList("bgColour","borderColour","borderSize","class"));
  private final BeanMetadata beanMetadata =
      new BeanMetadata(
          org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class,
          javax.validation.groups.Default.class);
  
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl bgColour_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "bgColour",
          org.kie.workbench.common.dmn.api.property.background.BgColour.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.PropertyDescriptorImpl borderColour_pd =
      new com.google.gwt.validation.client.impl.PropertyDescriptorImpl(
          "borderColour",
          org.kie.workbench.common.dmn.api.property.background.BorderColour.class,
          true,beanMetadata);
  private final com.google.gwt.validation.client.impl.GwtBeanDescriptor<org.kie.workbench.common.dmn.api.property.background.BackgroundSet> beanDescriptor = 
      com.google.gwt.validation.client.impl.GwtBeanDescriptorImpl.builder(org.kie.workbench.common.dmn.api.property.background.BackgroundSet.class)
          .setConstrained(false)
          .put("bgColour", bgColour_pd)
          .put("borderColour", borderColour_pd)
          .setBeanMetadata(beanMetadata)
          .build();
  
  
  public <T> void validateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateAllNonInheritedProperties(context, object, violations, groups);
  }
  
  public <T> void expandDefaultAndValidateClassGroups(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
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
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
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
      Class<org.kie.workbench.common.dmn.api.property.background.BackgroundSet> beanType,
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
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      String propertyName,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) throws ValidationException {
    if (propertyName.equals("bgColour")) {
      validateProperty_getbgColour(context, violations, object, object.getBgColour(), false, groups);
      validateProperty_bgColour(context, violations, object, _bgColour(object), false, groups);
    } else if (propertyName.equals("borderColour")) {
      validateProperty_getborderColour(context, violations, object, object.getBorderColour(), false, groups);
      validateProperty_borderColour(context, violations, object, _borderColour(object), false, groups);
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.dmn.api.property.background.BackgroundSet");
    }
  }
  
  public <T> void validateValueGroups(
      GwtValidationContext<T> context,
      Class<org.kie.workbench.common.dmn.api.property.background.BackgroundSet> beanType,
      String propertyName,
      Object value,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    if (propertyName.equals("bgColour")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.background.BgColour) {
        valueTypeMatches = true;
        validateProperty_getbgColour(context, violations, null, (org.kie.workbench.common.dmn.api.property.background.BgColour) value, false, groups);
      }
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.background.BgColour) {
        valueTypeMatches = true;
        validateProperty_bgColour(context, violations, null, (org.kie.workbench.common.dmn.api.property.background.BgColour) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else if (propertyName.equals("borderColour")) {
      boolean valueTypeMatches = false;
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.background.BorderColour) {
        valueTypeMatches = true;
        validateProperty_getborderColour(context, violations, null, (org.kie.workbench.common.dmn.api.property.background.BorderColour) value, false, groups);
      }
      if ( value == null || value instanceof org.kie.workbench.common.dmn.api.property.background.BorderColour) {
        valueTypeMatches = true;
        validateProperty_borderColour(context, violations, null, (org.kie.workbench.common.dmn.api.property.background.BorderColour) value, false, groups);
      }
      if(!valueTypeMatches)  {
        throw new ValidationException(value.getClass() +" is not a valid type for "+ propertyName);
      }
    } else  if (!ALL_PROPERTY_NAMES.contains(propertyName)) {
      throw new java.lang.IllegalArgumentException( propertyName +" is not a valid property of org.kie.workbench.common.dmn.api.property.background.BackgroundSet");
    }
  }
  
  public BeanMetadata getBeanMetadata() {
    return beanMetadata;
  }
  
  public GwtBeanDescriptor<org.kie.workbench.common.dmn.api.property.background.BackgroundSet> getConstraints(ValidationGroupsMetadata validationGroupsMetadata) {
    beanDescriptor.setValidationGroupsMetadata(validationGroupsMetadata);
    return beanDescriptor;
  }
  
  private final <T> void validateProperty_bgColour(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      final org.kie.workbench.common.dmn.api.property.background.BgColour value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("bgColour");
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
  
  private final <T> void validateProperty_getbgColour(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      final org.kie.workbench.common.dmn.api.property.background.BgColour value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  private final <T> void validateProperty_borderColour(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      final org.kie.workbench.common.dmn.api.property.background.BorderColour value,
      boolean honorValid,
      Class<?>... groups) {
    final GwtValidationContext<T> myContext = context.append("borderColour");
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
  
  private final <T> void validateProperty_getborderColour(
      final GwtValidationContext<T> context,
      final Set<ConstraintViolation<T>> violations,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      final org.kie.workbench.common.dmn.api.property.background.BorderColour value,
      boolean honorValid,
      Class<?>... groups) {
  }
  
  
  private <T> void validateAllNonInheritedProperties(
      GwtValidationContext<T> context,
      org.kie.workbench.common.dmn.api.property.background.BackgroundSet object,
      Set<ConstraintViolation<T>> violations,
      Class<?>... groups) {
    validateProperty_getbgColour(context, violations, object, object.getBgColour(), true, groups);
    validateProperty_bgColour(context, violations, object, _bgColour(object), true, groups);
    validateProperty_getborderColour(context, violations, object, object.getBorderColour(), true, groups);
    validateProperty_borderColour(context, violations, object, _borderColour(object), true, groups);
  }
  
  // Write the wrappers after we know which are needed
  private native org.kie.workbench.common.dmn.api.property.background.BgColour _bgColour(org.kie.workbench.common.dmn.api.property.background.BackgroundSet object) /*-{
    return object.@org.kie.workbench.common.dmn.api.property.background.BackgroundSet::bgColour;
  }-*/;
  
  private native org.kie.workbench.common.dmn.api.property.background.BorderColour _borderColour(org.kie.workbench.common.dmn.api.property.background.BackgroundSet object) /*-{
    return object.@org.kie.workbench.common.dmn.api.property.background.BackgroundSet::borderColour;
  }-*/;
  
  
}
