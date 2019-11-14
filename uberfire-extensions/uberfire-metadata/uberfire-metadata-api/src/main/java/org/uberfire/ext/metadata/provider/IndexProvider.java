/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.provider;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;

public interface IndexProvider extends Disposable {

    boolean isFreshIndex(final KCluster cluster);

    void index(KObject object);

    void index(List<KObject> elements);

    boolean exists(String index,
                   String id);

    void delete(String index);

    void delete(String index,
                String id);

    List<KObject> findById(String index,
                           String id) throws IOException;

    void rename(String index,
                String id,
                KObject to);

    long getIndexSize(String index);

    List<KObject> findByQuery(List<String> indices,
                              Query query,
                              int limit);

    List<KObject> findByQuery(List<String> indices,
                              Query query,
                              Sort sort,
                              int limit);

    long findHitsByQuery(List<String> indices,
                         Query query);

    List<String> getIndices();

    void observerInitialization(Runnable runnable);

    boolean isAlive();
}
