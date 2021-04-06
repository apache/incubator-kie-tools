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


import org.jboss.errai.common.client.api.annotations.MapsTo;

import java.util.List;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
//@Portable
public class EntityToTestA {
  private final String foo;
  private final String foobar;
  private final String bar;
  private final List<EntityToTestA> entityToTestAList;
  private final EntityToTestA[][] arraysOfEntities;

  private int cachedHashCode = -1;

  public EntityToTestA(@MapsTo("foo") String foo,
                       @MapsTo("bar") String bar,
                       @MapsTo("entityToTestAList") List<EntityToTestA> entityToTestAList,
                       @MapsTo("arraysOfEntities") EntityToTestA[][] arraysOfEntities
  ) {
    this.foo = foo;
    this.bar = bar;
    this.entityToTestAList = entityToTestAList;
    this.foobar = foo + bar;
    this.arraysOfEntities = arraysOfEntities;
  }

  public String getFoo() {
    return foo;
  }

  @Override
  public int hashCode() {
    if (cachedHashCode == -1) {
      cachedHashCode = foo.hashCode() + 37 * bar.hashCode();
    }
    return cachedHashCode;
  }
}
