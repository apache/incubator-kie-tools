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

import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class UnsatisfiedDependenciesException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private UnsatisfiedDependencies unsatisfiedDependencies;

  public static UnsatisfiedDependenciesException createWithSingleFieldFailure(MetaField field, MetaClass enclosingType,
                                                                              MetaClass injectedType, String message) {
    UnsatisfiedDependencies unsatisfiedDependencies1 = new UnsatisfiedDependencies();
    UnsatisfiedDependency dependency = new UnsatisfiedField(field, enclosingType, injectedType, message);
    unsatisfiedDependencies1.addUnsatisfiedDependency(dependency);
    return new UnsatisfiedDependenciesException(unsatisfiedDependencies1);
  }

  public static UnsatisfiedDependenciesException createWithSingleParameterFailure(MetaParameter parm, MetaClass enclosingType,
                                                                                MetaClass injectedType, String message) {
      UnsatisfiedDependencies unsatisfiedDependencies1 = new UnsatisfiedDependencies();
      UnsatisfiedDependency dependency = new UnsatisfiedParameter(parm, enclosingType, injectedType, message);
      unsatisfiedDependencies1.addUnsatisfiedDependency(dependency);
      return new UnsatisfiedDependenciesException(unsatisfiedDependencies1);
    }
  public static UnsatisfiedDependenciesException createWithSingleMethodFailure(MetaMethod method, MetaClass enclosingType,
                                                                                MetaClass injectedType, String message) {
      UnsatisfiedDependencies unsatisfiedDependencies1 = new UnsatisfiedDependencies();
      UnsatisfiedDependency dependency = new UnsatisfiedMethod(method, enclosingType, injectedType, message);
      unsatisfiedDependencies1.addUnsatisfiedDependency(dependency);
      return new UnsatisfiedDependenciesException(unsatisfiedDependencies1);
    }


  public UnsatisfiedDependenciesException(UnsatisfiedDependencies unsatisfiedDependencies) {
    super(unsatisfiedDependencies.toString());
    this.unsatisfiedDependencies = unsatisfiedDependencies;
  }

  public List<UnsatisfiedDependency> getUnsatisfiedDependencies() {
    return unsatisfiedDependencies.get();
  }
}
