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


package org.jboss.errai.validation.client.test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.validation.client.BeanValidator;
import org.jboss.errai.validation.client.ClassLevelConstraintBean;
import org.jboss.errai.validation.client.ModuleWithInjectedValidator;
import org.jboss.errai.validation.client.TestGroup;
import org.jboss.errai.validation.client.TestModel;
import org.jboss.errai.validation.client.TestModelWithoutConstraints;

/**
 * Integration test for injected {@link BeanValidator}s.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Johannes Barop <jb@barop.de>
 */
public class ValidationIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.validation.ValidationTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
  }

  public void testValidatorInjection() {
    ModuleWithInjectedValidator module =
      IOC.getBeanManager().lookupBean(ModuleWithInjectedValidator.class).getInstance();
    Validator validator = module.getValidator();
    assertNotNull("Validator was not injected", validator);
    
    Set<ConstraintViolation<TestModel>> violations = validator.validate(new TestModel());
    assertEquals("Expected two constraint violations", 2, violations.size());
  }
  
  public void testValidationOfBindableType() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    TestModel model = DataBinder.forModel(new TestModel()).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
    
    model.setStringVal("valid");
    violations = validator.validate(model);
    assertEquals("Expected one constraint violations", 1, violations.size());
    
    model.setNumVal(101);
    violations = validator.validate(model);
    assertEquals("Expected no constraint violations", 0, violations.size());
  }
  
  public void testValidationOfBindableTypeWithInjectedValidator() {
    ModuleWithInjectedValidator module =
      IOC.getBeanManager().lookupBean(ModuleWithInjectedValidator.class).getInstance();
    Validator validator = module.getValidator();
    
    TestModel model = DataBinder.forModel(new TestModel()).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
    
    model.setStringVal("valid");
    violations = validator.validate(model);
    assertEquals("Expected one constraint violations", 1, violations.size());
    
    model.setNumVal(101);
    violations = validator.validate(model);
    assertEquals("Expected no constraint violations", 0, violations.size());
  }
  
  public void testValidationOfNestedBindableType() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    TestModel model = DataBinder.forModel(new TestModel()).bind(new TextBox(), "child.stringVal").getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    // model and child model should get validated (see @Valid on child field which caused validation when not null)
    assertEquals("Expected four constraint violations", 4, violations.size());
    
    model.setNumVal(101);
    violations = validator.validate(model);
    assertEquals("Expected three constraint violations", 3, violations.size());
    
    model.getChild().setStringVal("valid");
    violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
  }
  
  public void testValidationOfListOfBindableTypes() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    TestModel model = new TestModel();
    model.setNumVal(101);
    model.setStringVal("val");
    model = DataBinder.forModel(model).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
    
    model.addToList(new TestModel());
    // the list now contains one invalid element
    violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
    
    model.getList().get(0).setNumVal(101);
    model.getList().get(0).setStringVal("val");
    // the list now contains one valid element
    violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
  }

  public void testValidationOfListOfProxiedBindableTypes() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    TestModel model = new TestModel();
    model.setNumVal(101);
    model.setStringVal("val");
    model = DataBinder.forModel(model).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
    
    model.addToList(DataBinder.forType(TestModel.class).getModel());
    // the list now contains one invalid element
    violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
    
    model.getList().get(0).setNumVal(101);
    model.getList().get(0).setStringVal("val");
    // the list now contains one valid element
    violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
  }
  
  public void testValidationOfSetOfProxiedBindableTypes() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    TestModel model = new TestModel();
    model.setNumVal(101);
    model.setStringVal("val");
    model = DataBinder.forModel(model).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
    
    model.addToSet(DataBinder.forType(TestModel.class).getModel());
    // the set now contains one invalid element
    violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());
    
    model.getSet().iterator().next().setNumVal(101);
    model.getSet().iterator().next().setStringVal("val");
    // the set now contains one valid element
    violations = validator.validate(model);
    assertEquals("Expected zero constraint violations", 0, violations.size());
  }
  
  public void testValidationWithGroup() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    TestModel model = DataBinder.forModel(new TestModel()).getModel();
    Set<ConstraintViolation<TestModel>> violations = validator.validate(model, TestGroup.class);
    assertEquals("Expected one constraint violations", 1, violations.size());

    violations = validator.validate(model, Default.class, TestGroup.class);
    assertEquals("Expected three constraint violations", 3, violations.size());
  }
  
  public void testValidationOfTypeWithoutConstraints() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    TestModelWithoutConstraints model = new TestModelWithoutConstraints(new TestModel());
    Set<ConstraintViolation<TestModelWithoutConstraints>> violations = validator.validate(model);
    assertEquals("Expected two constraint violations", 2, violations.size());

    TestModel validModel = new TestModel();
    validModel.setNumVal(101);
    validModel.setStringVal("valid");
    model.setModel(validModel);
    violations = validator.validate(model);
    assertEquals("Expected no constraint violations", 0, violations.size());
  }

  public void testValidationOfTypeWithClassLevelValidationAnnotation() {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    ClassLevelConstraintBean type = new ClassLevelConstraintBean();
    Set<ConstraintViolation<ClassLevelConstraintBean>> violations = validator.validate(type);
    assertEquals("Expected one constraint violation", 1, violations.size());
    type.setId(5);
    violations = validator.validate(type);
    assertEquals("Expected no constraint violations", 0, violations.size());
  }
}
