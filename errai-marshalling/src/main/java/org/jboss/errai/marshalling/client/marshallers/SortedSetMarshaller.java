/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.client.marshallers;

import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ImplementationAliases;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJArray;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@ClientMarshaller(SortedSet.class)
@ServerMarshaller(SortedSet.class)
@ImplementationAliases({TreeSet.class})
public class SortedSetMarshaller extends AbstractCollectionMarshaller<SortedSet> {
  @Override
  public SortedSet[] getEmptyArray() {
    return new TreeSet[0];
  }
  
  @Override
  public SortedSet doDemarshall(final EJArray o, final MarshallingSession ctx) {
    return marshallToCollection(new TreeSet(), o, ctx);
  }
}
