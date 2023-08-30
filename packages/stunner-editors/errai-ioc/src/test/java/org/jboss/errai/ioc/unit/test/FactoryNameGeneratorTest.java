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


package org.jboss.errai.ioc.unit.test;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.api.Qualifier;
import org.jboss.errai.ioc.rebind.ioc.graph.api.QualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.DefaultQualifierFactory;
import org.jboss.errai.ioc.rebind.ioc.graph.impl.FactoryNameGenerator;
import org.jboss.errai.ioc.unit.res.CustomQualifier;
import org.jboss.errai.ioc.unit.res.SomeClass.SomeInnerClass;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType.Producer;
import static org.jboss.errai.ioc.rebind.ioc.graph.api.DependencyGraphBuilder.InjectableType.Type;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class FactoryNameGeneratorTest {

  private FactoryNameGenerator generator;

  private final QualifierFactory qualFactory = new DefaultQualifierFactory();
  private final MetaClass object = MetaClassFactory.get(Object.class);
  private final Qualifier defaultQualifier = qualFactory.forDefault();

  @Before
  public void setup() {
    generator = new FactoryNameGenerator();
  }

  @Test
  public void typeWithDefaultQualifiers() throws Exception {
    final String generated = generator.generateFor(object, defaultQualifier, Type);
    assertEquals("Type_factory__j_l_Object__quals__j_e_i_Any_j_e_i_Default", generated);
  }

  @Test
  public void producerWithDefaultQualifiers() throws Exception {
    final String generated = generator.generateFor(object, defaultQualifier, Producer);
    assertEquals("Producer_factory__j_l_Object__quals__j_e_i_Any_j_e_i_Default", generated);
  }

  @Test
  public void typeWithNamedQualifier() throws Exception {
    final Qualifier namedQualifier = qualFactory.forSource(() -> new Annotation[] { new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return "foo";
      }
    }});
    final String generated = generator.generateFor(object, namedQualifier, Type);
    assertEquals("Type_factory__j_l_Object__quals__j_e_i_Any_j_e_i_Default_j_i_Named", generated);
  }

  @Test
  public void typeWithCustomQualifier() throws Exception {
    final Qualifier customQualifier = qualFactory.forSource(() -> new Annotation[] { new CustomQualifier() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return CustomQualifier.class;
      }
    }});
    final String generated = generator.generateFor(object, customQualifier, Type);
    assertEquals("Type_factory__j_l_Object__quals__j_e_i_Any_o_j_e_i_u_r_CustomQualifier", generated);
  }

  @Test
  public void staticInnerTypeWithDefaultQualifiers() throws Exception {
    final String generated = generator.generateFor(MetaClassFactory.get(SomeInnerClass.class), defaultQualifier, Type);
    assertEquals("Type_factory__o_j_e_i_u_r_SomeClass_SomeInnerClass__quals__j_e_i_Any_j_e_i_Default", generated);
  }

  @Test
  public void multipleCallsProduceDifferentNames() throws Exception {
    final Set<String> names = new HashSet<>();
    final int CALLS = 100;
    for (int i = 0; i < 100; i++) {
      names.add(generator.generateFor(object, defaultQualifier, Type));
    }

    assertEquals(CALLS, names.size());
  }

}
