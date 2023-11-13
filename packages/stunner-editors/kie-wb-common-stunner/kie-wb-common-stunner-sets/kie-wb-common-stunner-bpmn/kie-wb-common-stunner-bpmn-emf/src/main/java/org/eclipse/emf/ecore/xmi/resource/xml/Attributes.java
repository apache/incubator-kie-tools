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


package org.eclipse.emf.ecore.xmi.resource.xml;

public interface Attributes {

    int getLength();

    String getURI(int var1);

    String getLocalName(int var1);

    String getQName(int var1);

    String getType(int var1);

    String getValue(int var1);

    int getIndex(String var1, String var2);

    int getIndex(String var1);

    String getType(String var1, String var2);

    String getType(String var1);

    String getValue(String var1, String var2);

    String getValue(String var1);
}
