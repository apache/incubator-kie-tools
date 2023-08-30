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

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.ProxyMaker;
import org.jboss.errai.codegen.test.model.ToProxyBean;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link ProxyMaker}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ProxyMakerTest extends AbstractCodegenTest {

  /**
   * Because JDK7 doesn't return reflection members in the order they were
   * declared in source, we have to make a list of all the expected chunks in
   * the generated sources, then verify that they were all present while
   * ignoring the order of the fields and methods.
   */
  private static final List<String> expectedChunks = Arrays.asList(
      "public class ToProxy_Proxy extends org.jboss.errai.codegen.test.model.ToProxyBean {\n" +
          "  private org.jboss.errai.codegen.test.model.ToProxyBean $$_proxy_$$;\n",
          "  private boolean $$_init_$$;\n",

      "  @Override public String getName() {\n" +
          "    if ($$_init_$$) {\n" +
          "      return $$_proxy_$$.getName();\n" +
          "    } else {\n" +
          "      return null;\n" +
          "    }\n" +
          "  }\n",

      "  @Override public org.jboss.errai.codegen.test.model.Integer getBlah() {\n" +
          "    if ($$_init_$$) {\n" +
          "      return $$_proxy_$$.getBlah();\n" +
          "    } else {\n" +
          "      return null;\n" +
          "    }\n" +
          "  }\n",

      "  @Override public void methodWithTypeArgs(Class a0, com.google.common.collect.Multimap a1) {\n" +
          "    if ($$_init_$$) {\n" +
          "      $$_proxy_$$.methodWithTypeArgs(a0, a1);\n" +
          "    }\n" +
          "  }\n",

      "  @Override public String toString() {\n" +
          "    if ($$_init_$$) {\n" +
          "      return $$_proxy_$$.toString();\n" +
          "    } else {\n" +
          "      return null;\n" +
          "    }\n" +
          "  }\n",

      "  @Override public int hashCode() {\n" +
          "    if ($$_proxy_$$ == null) {\n" +
          "      throw new IllegalStateException(\"call to hashCode() on an unclosed proxy.\");\n" +
          "    } else {\n" +
          "      return $$_proxy_$$.hashCode();\n" +
          "    }\n" +
          "  }\n",

      "  @Override public boolean equals(Object o) {\n" +
          "    if ($$_proxy_$$ == null) {\n" +
          "      throw new IllegalStateException(\"call to equals() on an unclosed proxy.\");\n" +
          "    } else {\n" +
          "      return $$_proxy_$$.equals(o);\n" +
          "    }\n" +
          "  }\n",

      "  public void __$setProxiedInstance$(org.jboss.errai.codegen.test.model.ToProxyBean proxy) {\n" +
          "    $$_proxy_$$ = proxy;\n" +
          "    $$_init_$$ = true;\n" +
          "  }\n",
      "}\n");

  @Test
  public void testProxyGeneration() {
    String proxy = new InnerClass(ProxyMaker.makeProxy("ToProxy_Proxy", ToProxyBean.class)).generate(Context.create());

    int totalChars = 0;
    for (String expectedChunk : expectedChunks) {
      assertTrue("Generated code was missing a chunk:\n" + expectedChunk + "\n----- Actual generated proxy was:\n" + proxy,
          proxy.contains(expectedChunk));
      totalChars += countNonWhitespace(expectedChunk);
    }

    // finally, a length check will ensure there were no extra chunks we weren't expecting
    Assert.assertEquals(
        "Found wrong number of non-whitespace chars in generated proxy:\n" + proxy,
        totalChars, countNonWhitespace(proxy));
  }

  private static int countNonWhitespace(String s) {
    return s.replaceAll("\\s", "").length();
  }
}
