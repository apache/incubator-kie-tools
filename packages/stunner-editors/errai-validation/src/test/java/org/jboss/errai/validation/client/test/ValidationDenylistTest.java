/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.validation.client.test;

import javax.validation.Validation;
import javax.validation.Validator;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.validation.client.DenylistedWithConstraint;
import org.jboss.errai.validation.client.denylisted.ClassWithConstraintInDenylistedPackage;
import org.junit.Test;

public class ValidationDenylistTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.validation.ValidationTestModule";
  }

  @Test
  public void testDenylistedBeanNotFound() throws Exception {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    DenylistedWithConstraint bean = DataBinder.forModel(new DenylistedWithConstraint()).getModel();
    try {
      validator.validate(bean);
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail("There should not be a validator for the denylisted bean.");
  }

  @Test
  public void testBeanInDenylistedPackageNotFound() throws Exception {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    ClassWithConstraintInDenylistedPackage bean = DataBinder.forModel(new ClassWithConstraintInDenylistedPackage())
            .getModel();
    try {
      validator.validate(bean);
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail("There should not be a validator for beans in denylisted packages.");
  }

}
