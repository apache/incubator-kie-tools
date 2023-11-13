/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.validation.client;

import java.util.Collections;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.google.gwt.validation.client.impl.AbstractGwtValidator;
import com.google.gwt.validation.client.impl.GwtValidationContext;

/**
 * A {@link Validator} implementation that does nothing. We fall back to this
 * validator in case the Errai validation module is used but no type on the
 * classpath contains any {@link Constraint}s.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class NoopValidator extends AbstractGwtValidator {
  
  @Override
  public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
    return Collections.emptySet();
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
    return Collections.emptySet();
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value,
          Class<?>... groups) {
    return Collections.emptySet();
  }

  @Override
  public BeanDescriptor getConstraintsForClass(final Class<?> clazz) {
    return new BeanDescriptor () {

      @Override
      public boolean hasConstraints() {
        return false;
      }

      @Override
      public Class<?> getElementClass() {
        return clazz;
      }

      @Override
      public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return Collections.emptySet();
      }

      @Override
      public ConstraintFinder findConstraints() {
        return null;
      }

      @Override
      public boolean isBeanConstrained() {
        return false;
      }

      @Override
      public PropertyDescriptor getConstraintsForProperty(final String propertyName) {
       return null;
      }

      @Override
      public Set<PropertyDescriptor> getConstrainedProperties() {
        return Collections.emptySet();
      }
      
    };
  }

  @Override
  public <T> Set<ConstraintViolation<T>> validate(GwtValidationContext<T> context, Object object, Class<?>... groups)
          throws ValidationException {
    return Collections.emptySet();
  }

}
