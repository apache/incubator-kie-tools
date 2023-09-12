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

import java.lang.annotation.Annotation;

import com.google.common.collect.Multimap;

/**
 * @author Mike Brock
 */
public class ToProxyBean {
  protected String name;
  protected Integer blah;

  public ToProxyBean() {
  }

  public ToProxyBean(String name, Integer blah) {
    this.name = name;
    this.blah = blah;
  }

  public String getName() {
    return name;
  }

  public Integer getBlah() {
    return blah;
  }

  public <W extends Annotation> void methodWithTypeArgs(Class<W> annotation, Multimap<String, String> map) {
  }

}
