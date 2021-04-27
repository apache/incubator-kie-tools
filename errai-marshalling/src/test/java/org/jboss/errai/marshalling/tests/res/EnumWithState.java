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

package org.jboss.errai.marshalling.tests.res;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An enum with mutable state, for testing purposes.
 * <p>
 * <blockquote>
 * An Enum with state?<br>
 * What a sad sight to see!<br>
 * Not in my codebase<br>
 * Who'd allow it? Not me!<br>
 * <p>
 * But the compiler allows it<br>
 * Without even a peep<br>
 * So there's one in our testsuite<br>
 * And now we can sleep.
 * </blockquote>
 *
 * @author Jonathan Fuerth <jfuerth@gmail.com>
 */
@Portable
public enum EnumWithState {

  THING1, THING2;

  int integerState;
  long longState;
  Object objectReference;

  public int getIntegerState() {
    return integerState;
  }
  public long getLongState() {
    return longState;
  }
  public Object getObjectReference() {
    return objectReference;
  }
  public void setIntegerState(int integerState) {
    this.integerState = integerState;
  }
  public void setLongState(long longState) {
    this.longState = longState;
  }
  public void setObjectReference(Object objectReference) {
    this.objectReference = objectReference;
  }
}
