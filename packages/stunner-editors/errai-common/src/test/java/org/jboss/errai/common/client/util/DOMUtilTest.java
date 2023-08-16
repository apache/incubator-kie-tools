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


package org.jboss.errai.common.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.dom.client.Style;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class DOMUtilTest {

  @Mock
  private CSSStyleDeclaration style;

  @Mock
  private HTMLElement element;

  private DOMTokenList domTokenList = new DOMTokenList() {

    private List<String> tokens = new ArrayList<>();
    
    @Override
    public int getLength() {
        return tokens.size();
    }

    @Override
    public String item(final int index) {
        return tokens.get(index);
    }

    @Override
    public boolean contains(final String token) {
        return tokens.contains(token);
    }

    @Override
    public void add(final String token) {
        tokens.add(token);
    }

    @Override
    public void remove(final String token) {
        tokens.remove(token);
    }

    @Override
    public boolean toggle(final String token) {
        return false;
    }

    @Override
    public void replace(final String token,
                        final String newToken) {
        tokens.remove(token);
        tokens.add(newToken );
    }

    @Override
    public boolean supports(final String token) {
        return false;
    }

  };

  private enum TestCss implements Style.HasCssName {

    ONE("one"),
    TWO("two");

    private final String cssClass;

    TestCss(final String cssClass) {
      this.cssClass = cssClass;
    }

    @Override
    public String getCssName() {
      return cssClass;
    }

  }

  @Before
  public void setup() {
    when(element.getClassList()).thenReturn(domTokenList);
  }

  @Test
  public void emptyStreamForEmptyCssDeclaration() throws Exception {
    when(style.getCssText()).thenReturn("");
    final List<String> styleNames = DOMUtil
      .cssPropertyNameStream(style)
      .collect(Collectors.toList());

    assertEquals(Collections.emptyList(), styleNames);
  }

  @Test
  public void splitsNonEmptyCssDeclaration() throws Exception {
    when(style.getCssText()).thenReturn("  display:block;   height: 100px;  width:     200px;   ");
    final List<String> styleNames = DOMUtil
      .cssPropertyNameStream(style)
      .collect(Collectors.toList());

    assertEquals(Arrays.asList("display", "height", "width"), styleNames);
  }

  @Test
  public void addUniqueEnumStyleName() {
    domTokenList.add(TestCss.ONE.getCssName());
    domTokenList.add("another-class-name");
    DOMUtil.addUniqueEnumStyleName(element, TestCss.class, TestCss.TWO);

    assertEquals(2, domTokenList.getLength());
    assertTrue(domTokenList.contains("another-class-name"));
    assertTrue(domTokenList.contains(TestCss.TWO.getCssName()));
  }

  @Test
  public void removeEnumStyleNames() {
    domTokenList.add(TestCss.ONE.getCssName());
    domTokenList.add(TestCss.TWO.getCssName());
    domTokenList.add("another-class-name");
    DOMUtil.removeEnumStyleNames(element, TestCss.class);

    assertEquals(1, domTokenList.getLength());
    assertEquals("another-class-name", domTokenList.item(0));
  }

  @Test
  public void addEnumStyleName() {
    domTokenList.add("another-class-name");
    DOMUtil.addEnumStyleName(element, TestCss.ONE);

    assertEquals(2, domTokenList.getLength());
    assertTrue(domTokenList.contains("another-class-name"));
    assertTrue(domTokenList.contains(TestCss.ONE.getCssName()));
  }

  @Test
  public void removeEnumStyleName() {
    domTokenList.add(TestCss.ONE.getCssName());
    domTokenList.add("another-class-name");
    DOMUtil.removeEnumStyleName(element, TestCss.ONE);

    assertEquals(1, domTokenList.getLength());
    assertEquals("another-class-name", domTokenList.item(0));
  }

}
