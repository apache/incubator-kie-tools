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


package org.jboss.errai.ui.rebind;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class DataFieldCodeDecoratorTest {

  @Mock
  private Decorable decorable;

  @Mock
  private FactoryController controller;

  @Mock
  private InjectionContext context;

  @Before
  public void setup() {
    when(decorable.getAnnotation()).thenReturn(new DataField() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return DataField.class;
      }

      @Override
      public String value() {
        return "";
      }

      @Override
      public AttributeRule[] attributeRules() {
        return new AttributeRule[0];
      }

      @Override
      public ConflictStrategy defaultStrategy() {
        return ConflictStrategy.USE_TEMPLATE;
      }

    });
    final Map<String, Object> attrs = new HashMap<>();
    when(context.getAttribute(any())).then(invocation -> attrs.get(invocation.getArguments()[0]));
    doAnswer(invocation -> attrs.put((String) invocation.getArguments()[0], invocation.getArguments()[1]))
      .when(context).setAttribute(anyString(), any());
    when(decorable.getInjectionContext()).thenReturn(context);
  }

  @Test
  public void dataFieldsNotMixedForTypesWithSameSimpleName() throws Exception {
    final MetaClass type1 = MetaClassFactory.get(org.jboss.errai.ui.rebind.res.p1.DifferentPackageSameName.class);
    final MetaClass type2 = MetaClassFactory.get(org.jboss.errai.ui.rebind.res.p2.DifferentPackageSameName.class);
    final DataFieldCodeDecorator decorator = new DataFieldCodeDecorator(DataField.class);

    // Setup
    when(decorable.getType()).thenReturn(MetaClassFactory.get(Div.class));

    when(decorable.getDecorableDeclaringType()).thenReturn(type1);
    when(decorable.getName()).thenReturn("d1");
    decorator.generateDecorator(decorable, controller);

    when(decorable.getDecorableDeclaringType()).thenReturn(type2);
    when(decorable.getName()).thenReturn("d2");
    decorator.generateDecorator(decorable, controller);
    // End setup

    final Map<String, Statement> dataFieldMap1 = DataFieldCodeDecorator.aggregateDataFieldMap(decorable, type1);
    final Map<String, Statement> dataFieldMap2 = DataFieldCodeDecorator.aggregateDataFieldMap(decorable, type2);

    final Map<String, MetaClass> dataFieldTypeMap1 = DataFieldCodeDecorator.aggregateDataFieldTypeMap(decorable, type1);
    final Map<String, MetaClass> dataFieldTypeMap2 = DataFieldCodeDecorator.aggregateDataFieldTypeMap(decorable, type2);

    assertEquals(singleton("d1"), dataFieldMap1.keySet());
    assertEquals(singleton("d2"), dataFieldMap2.keySet());
    assertEquals(singleton("d1"), dataFieldTypeMap1.keySet());
    assertEquals(singleton("d2"), dataFieldTypeMap2.keySet());
  }

}
