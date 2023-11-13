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


package org.jboss.errai.codegen.test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.CharStreams;
import org.jboss.errai.codegen.test.model.BeanWithTypeParmedMeths;
import org.jboss.errai.codegen.test.model.FakeBean;
import org.jboss.errai.codegen.util.QuickDeps;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mike Brock
 */
public class ReachabilityTest {

  private static String getSource(Class clazz) throws Exception {
    final String pathSeparator = File.separator;
    final String name = clazz.getName().replace('.', pathSeparator.charAt(0)) + ".java";
    final InputStream inputStream = clazz.getClassLoader().getResourceAsStream(name);

    if (inputStream == null) {
      throw new RuntimeException("not found: " + name);
    }

    return CharStreams.toString(new InputStreamReader(inputStream));
  }

  @Test
  public void testBasicReachability() throws Exception {
    final String source = getSource(BeanWithTypeParmedMeths.class);

    final Set<String> quickTypeDependencyList
        = QuickDeps.getQuickTypeDependencyList(source, BeanWithTypeParmedMeths.class.getClassLoader());

    final Set<String> expected = new HashSet<String>(
        Arrays.asList(
            "org.jboss.errai.codegen.test.model.BeanWithTypeParmedMeths",
            "org.jboss.errai.codegen.test.model.Foo",
            "org.jboss.errai.codegen.test.model.Bar",
            "java.util.Map"
        )
    );

    Assert.assertEquals(expected, quickTypeDependencyList);
  }

  @Test
  public void testBasicReachability2() throws Exception {
    final String source = getSource(FakeBean.class);

    final Set<String> quickTypeDependencyList
        = QuickDeps.getQuickTypeDependencyList(source, FakeBean.class.getClassLoader());

    System.out.println(quickTypeDependencyList);

    final Set<String> expected = new HashSet<String>(
        Arrays.asList(
            "org.jboss.errai.codegen.test.model.FakeBean",
            "javax.enterprise.inject.Instance",
            "javax.inject.Inject",
            "org.jboss.errai.codegen.test.model.Bar",
            "org.jboss.errai.codegen.test.model.Bwah",
            BigDecimal.class.getName(),
            BigInteger.class.getName(),
            String.class.getName(),
            Class.class.getName()
        )
    );

    Assert.assertEquals(expected, quickTypeDependencyList);
  }
}
