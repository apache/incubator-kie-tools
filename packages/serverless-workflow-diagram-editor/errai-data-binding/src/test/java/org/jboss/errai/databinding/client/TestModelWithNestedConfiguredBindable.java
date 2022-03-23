/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class TestModelWithNestedConfiguredBindable {

  private TestModelWithoutBindableAnnotation nested;

  public TestModelWithoutBindableAnnotation getNested() {
    return nested;
  }

  public void setNested(TestModelWithoutBindableAnnotation nested) {
    this.nested = nested;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nested == null) ? 0 : nested.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TestModelWithNestedConfiguredBindable other = (TestModelWithNestedConfiguredBindable) obj;
    if (nested == null) {
      if (other.nested != null)
        return false;
    }
    else if (!nested.equals(other.nested))
      return false;
    return true;
  }

}
