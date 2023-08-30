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


package org.jboss.errai.ioc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.impl.gwt.GWTClass;
import org.jboss.errai.codegen.util.CDIAnnotationUtils;

/**
 * Contains utility methods for processing annotations that reference clinet-side classes that can't be deployed to the
 * server.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TranslatableAnnotationUtils {

  public static Iterable<MetaClass> getTranslatableQualifiers(final TypeOracle oracle) {
    final Set<Class<?>> typesAnnotatedWith = CDIAnnotationUtils.getQualifiersAsClasses();

    final Iterator<Class<?>> iter = typesAnnotatedWith.iterator();

    return () -> {
      return new Iterator<MetaClass>() {
        private MetaClass next;

        @Override
        public MetaClass next() {
          if (hasNext()) {
            final MetaClass retVal = next;
            next = null;

            return retVal;
          }
          else {
            throw new NoSuchElementException();
          }
        }

        @Override
        public boolean hasNext() {
          if (next == null) {
            while (iter.hasNext()) {
              final Class<?> aClass = iter.next();
              try {
                next = GWTClass.newInstance(oracle, oracle.getType(aClass.getName()));
                break;
              } catch (NotFoundException e) {
              }
            }
          }

          return next != null;
        }
      };
    };
  }

}
