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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.validation.client.ModuleWithInjectedValidator;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;

/**
 * Integration tests for {@link DynamicValidator DynamicValidators}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DynamicValidationIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.validation.ValidationTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
  }

  public void testLookupDynamicValidatorWithIoc() throws Exception {
    final SyncBeanDef<DynamicValidator> beanDef = IOC.getBeanManager().lookupBean(DynamicValidator.class);
    assertEquals(Singleton.class, beanDef.getScope());
    final DynamicValidator validator = beanDef.getInstance();
    assertNotNull(validator);
    assertSame(validator, beanDef.getInstance());
  }

  public void testInjectionOfDynamicValidator() throws Exception {
    final ModuleWithInjectedValidator module = IOC.getBeanManager().lookupBean(ModuleWithInjectedValidator.class).getInstance();
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    assertNotNull(module.getDynamicValidator());
    assertNotNull(validator);
    assertSame(validator, module.getDynamicValidator());
  }

  public void testValidateMaxStringValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<String>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100L), "99");
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<String>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100L), "101");
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidateMaxStringValidatorWithMessage() throws Exception {
    final Map<String, Object> parameters = new HashMap<>();
    parameters.put("value", 100l);
    parameters.put("message", "{javax.validation.constraints.Max.message}");
    
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<String>> invalidResult = validator.validate(Max.class, parameters, "101");
    assertEquals(1, invalidResult.size());
    assertNotNull(invalidResult.iterator().next().getMessage());
    // i.e. must be less than or equal to 100 (not verifying the rest of the text as it's dependent on the locale)
    assertTrue(invalidResult.iterator().next().getMessage().contains("100"));
  }
  
  public void testValidateMaxLongValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<Long>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100L), 99l);
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<Long>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100L), 101l);
    assertEquals(1, invalidResult.size());
  }

  public void testValidateMaxIntegerValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<Integer>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), 99);
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<Integer>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), 101);
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidateMaxDoubleValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<Double>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), 99d);
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<Double>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), 101d);
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidateDecimalMaxFloatValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<Float>> validResult = validator.validate(DecimalMax.class,
            Collections.singletonMap("value", "100.0"), 99.9f);
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<Float>> invalidResult = validator.validate(DecimalMax.class,
            Collections.singletonMap("value", "100.0"), 100.1f);
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidateMaxBigDecimalValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<BigDecimal>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), BigDecimal.valueOf(99l));
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<BigDecimal>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), BigDecimal.valueOf(101l));
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidateMaxBigIntegerValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<BigInteger>> validResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), BigInteger.valueOf(99l));
    assertTrue(validResult.toString(), validResult.isEmpty());
    final Set<ConstraintViolation<BigInteger>> invalidResult = validator.validate(Max.class,
            Collections.singletonMap("value", 100l), BigInteger.valueOf(101l));
    assertEquals(1, invalidResult.size());
  }
  
  public void testValidationWithMultipleParams() throws Exception {
    final Map<String, Object> params = new HashMap<>();
    params.put("integer", 3);
    params.put("fraction", 3);
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Set<ConstraintViolation<String>> validResult = validator.validate(Digits.class, params, "123.123");
    assertTrue(validResult.isEmpty());
    final Set<ConstraintViolation<String>> invalidResult = validator.validate(Digits.class, params, "1234.123");
    assertEquals(1, invalidResult.size());
  }

  public void testValidateSeveralTypesForSameConstraintAndParams() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();
    final Map<String, Object> params = new HashMap<>();
    params.put("min", 1);
    params.put("max", 1);

    assertFalse(validator.validate(Size.class, params, Collections.emptyList()).isEmpty());
    assertTrue(validator.validate(Size.class, params, Collections.singleton(new Object())).isEmpty());

    assertFalse(validator.validate(Size.class, params, "").isEmpty());
    assertTrue(validator.validate(Size.class, params, "a").isEmpty());

    assertFalse(validator.validate(Size.class, params, new int[0]).isEmpty());
    assertTrue(validator.validate(Size.class, params, new int[] { 1 }).isEmpty());

    assertFalse(validator.validate(Size.class, params, new Integer[0]).isEmpty());
    assertTrue(validator.validate(Size.class, params, new Integer[] { 1 }).isEmpty());
  }

  public void testNotNullValidator() throws Exception {
    final DynamicValidator validator = IOC.getBeanManager().lookupBean(DynamicValidator.class).getInstance();

    assertFalse(validator.validate(NotNull.class, Collections.emptyMap(), null).isEmpty());
    assertTrue(validator.validate(NotNull.class, Collections.emptyMap(), new Object()).isEmpty());
    assertTrue(validator.validate(NotNull.class, Collections.emptyMap(), "foo").isEmpty());
  }
}
