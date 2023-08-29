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


package org.jboss.errai.ui.rebind.ioc.element;

import java.lang.annotation.Annotation;

import javax.inject.Named;

import org.jboss.errai.codegen.meta.HasAnnotations;
import org.jboss.errai.codegen.util.CDIAnnotationUtils;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
class HasNamedAnnotation implements HasAnnotations {

  private final Named named;

  HasNamedAnnotation(final String tagName) {
    this.named = new Named() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Named.class;
      }

      @Override
      public String value() {
        return tagName;
      }

      @Override
      public int hashCode() {
        return CDIAnnotationUtils.hashCode(this);
      }

      @Override
      public String toString() {
        return CDIAnnotationUtils.toString(this);
      }

      @Override
      public boolean equals(final Object obj) {
        return obj instanceof Named && CDIAnnotationUtils.equals(this, (Annotation) obj);
      }
    };
  }

  @Override
  public boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
    return Named.class.equals(annotation);
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[] { named };
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
    if (isAnnotationPresent(annotation)) {
      return (A) named;
    } else {
      return null;
    }
  }
}
