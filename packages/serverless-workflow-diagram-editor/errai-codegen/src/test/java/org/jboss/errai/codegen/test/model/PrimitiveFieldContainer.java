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

/**
 * A class that has a field of every primitive type. This is relied upon by the
 * mock type oracle in the GWT codegen tests, but you can use it for other tests
 * if that's convenient.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class PrimitiveFieldContainer {

  // DO NOT RENAME OR REMOVE ANY OF THE FOLLOWING FIELDS

  byte byteField;
  char charField;
  short shortField;
  int intField;
  long longField;
  float floatField;
  double doubleField;

  void voidMethod() {}
}
