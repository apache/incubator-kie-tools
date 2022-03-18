/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen.test.model;

import java.util.Collection;

/**
 * A class with methods returning and accepting parameters of various generic
 * types, for testing the MetaClass implementations.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class ClassWithGenericMethods {

  public Object methodReturningObject() {
    return null;
  }

  public Collection<?> methodReturningUnboundedWildcardCollection() {
    return null;
  }

  public Collection<? extends String> methodReturningUpperBoundedWildcardCollection() {
    return null;
  }

  public Collection<? super String> methodReturningLowerBoundedWildcardCollection() {
    return null;
  }
}
