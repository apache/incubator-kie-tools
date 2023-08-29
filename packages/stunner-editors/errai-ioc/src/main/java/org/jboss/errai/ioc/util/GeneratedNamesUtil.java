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

import org.jboss.errai.codegen.meta.MetaClass;

/**
 * A collection of methods for generating identifier-safe names for resources dervied from Java classes.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class GeneratedNamesUtil {

  public static String qualifiedClassNameToIdentifier(final MetaClass type) {
    return qualifiedClassNameToIdentifier(type.getFullyQualifiedName());
  }

  public static String qualifiedClassNameToIdentifier(final Class<?> type) {
    return qualifiedClassNameToIdentifier(type.getName());
  }

  public static String qualifiedClassNameToIdentifier(final String fullyQualifiedName) {
    return fullyQualifiedName.replace('.', '_').replace('$', '_');
  }

  public static String qualifiedClassNameToShortenedIdentifier(final String fullyQualifiedName) {
    return shortenGeneratedIdentifier(qualifiedClassNameToIdentifier(fullyQualifiedName));
  }

  public static String qualifiedClassNameToShortenedIdentifier(final MetaClass type) {
    return shortenGeneratedIdentifier(qualifiedClassNameToIdentifier(type));
  }

  public static String qualifiedClassNameToShortenedIdentifier(final Class<?> type) {
    return shortenGeneratedIdentifier(qualifiedClassNameToIdentifier(type));
  }

  public static String shortenGeneratedIdentifier(final String name) {
    return shortenGeneratedIdentifier(name, "_");
  }

  public static String shortenGeneratedIdentifier(final String name, final String separator) {
    final String[] parts = name.split(separator);
    final StringBuilder builder = new StringBuilder();
    boolean haveSeenUpperCase = false;
    for (final String part : parts) {
      if (haveSeenUpperCase || Character.isUpperCase(part.charAt(0))) {
        builder.append(part);
        haveSeenUpperCase = true;
      }
      else {
        builder.append(part.charAt(0));
      }
      builder.append(separator);
    }
    builder.delete(builder.length() - 1, builder.length());

    return builder.toString();
  }

}
