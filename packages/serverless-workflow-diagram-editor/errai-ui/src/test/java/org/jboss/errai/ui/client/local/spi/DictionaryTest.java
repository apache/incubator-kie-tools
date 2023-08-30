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


package org.jboss.errai.ui.client.local.spi;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author edewit@redhat.com
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DictionaryTest {

  public static final String WELCOME_KEY = "welcome";
  public static final String NL = "nl";
  public static final String CH = "ch";

  @Test
  public void shouldBeAbleToInsertAndRetrieveValuesFromDictionary() {
    // given
    Dictionary dictionary = new Dictionary();

    // when
    dictionary.put(NL, WELCOME_KEY, "Goedendag");
    dictionary.put(CH, WELCOME_KEY, "Grüezi wohl");


    // then
    assertEquals("Goedendag", dictionary.get(NL).get(WELCOME_KEY));
    assertEquals("Grüezi wohl", dictionary.get(CH).get(WELCOME_KEY));

    assertNotNull(dictionary.get("ru"));
    assertTrue(dictionary.get("ru").isEmpty());

    final Set<String> treeSet = new HashSet<String>();
    treeSet.add(NL);
    treeSet.add(CH);
    assertEquals(treeSet, dictionary.getSupportedLocals());
  }
  
  /**
   * It was crucial for performance reasons to not return a
   * defensive copy from {@link Dictionary#get(String)}. Some
   * applications started up 5 times faster after removing the
   * copying. 
   */
  @Test
  public void shouldNotReturnDefensiveCopy() {
    // given
    Dictionary dictionary = new Dictionary();
    dictionary.put(NL, WELCOME_KEY, "Goedendag");

    // when
    dictionary.get(NL).put("try to", "break");

    // then
    assertEquals(2, dictionary.get(NL).size());
  }
}
