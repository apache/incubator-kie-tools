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


package org.jboss.errai.ioc.rebind.ioc.graph.api;

import java.lang.annotation.Annotation;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Named;

/**
 * A single object for holding all the qualifier annotations of an injectable or
 * an injection point.
 *
 * @see QualifierFactory
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface Qualifier extends Iterable<Annotation> {

  /**
   * @param other Another qualifier to compare this to.
   * @return True if this qualifier is satsified by {@code other}.
   */
  boolean isSatisfiedBy(Qualifier other);

  boolean isDefaultQualifier();

  /**
   * @return A unique string that can be used as part of a Java identifier.
   */
  String getIdentifierSafeString();

  /**
   * @return The value of {@link Named} if it is present.
   */
  String getName();

  default Stream<Annotation> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
  }

}
