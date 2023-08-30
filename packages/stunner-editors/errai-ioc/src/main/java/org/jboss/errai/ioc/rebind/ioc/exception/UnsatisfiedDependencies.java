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


package org.jboss.errai.ioc.rebind.ioc.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class UnsatisfiedDependencies {
  private List<UnsatisfiedDependency> unsatisfiedDependencies;

  public void addUnsatisfiedDependency(UnsatisfiedDependency dependency) {
    if (unsatisfiedDependencies == null)
      unsatisfiedDependencies = new ArrayList<UnsatisfiedDependency>();

    unsatisfiedDependencies.add(dependency);
  }
  
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (UnsatisfiedDependency d : unsatisfiedDependencies) {
      buf.append(d.toString());
    }
    return buf.toString();
  }
  
  public List<UnsatisfiedDependency> get() {
    return unsatisfiedDependencies;
  }
}
