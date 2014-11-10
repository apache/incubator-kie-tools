/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.uberfire.metadata.engine;

import org.uberfire.metadata.model.KCluster;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

public interface MetaIndexEngine {

    public static final String FULL_TEXT_FIELD = "fullText";

    boolean freshIndex( final KCluster cluster );

    void startBatch( final KCluster cluster );

    void index( final KObject object );

    void index( final KObject... objects );

    void rename( final KObjectKey from,
                 final KObject to );

    void delete( final KCluster cluster );

    void delete( final KObjectKey objectKey );

    void delete( final KObjectKey... objectsKey );

    void commit( final KCluster cluster );

    void dispose();

    void beforeDispose( final Runnable callback );
}
