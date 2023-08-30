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


package org.jboss.errai.codegen.test.model;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class Foo {
  public Bar bar;
  
  public static <T> T foo(T t) {
    return t;
  }
  
  public static <T> T bar(List<T> list) {
    return list.get(0);
  }

  public static <T, V> T bar(int n, List<List<Map<T, V>>> list) {
    return null;
  }

  public static <K, V> V bar(Map<K, V> map) {
    return map.get(null);
  }
  
  public static <T> T baz(Class<T> list) {
    return null;
  }
}
