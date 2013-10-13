/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.metadata.backend.lucene;

import java.io.IOException;

import org.uberfire.metadata.backend.lucene.setups.BaseLuceneSetup;
import org.uberfire.metadata.backend.lucene.setups.MMapLuceneSetup;

import static org.uberfire.metadata.backend.lucene.FileTestUtil.*;

/**
 *
 */
public class MMapLuceneSetupTest extends BaseLuceneSetupTest {

    private final MMapLuceneSetup luceneSetup;

    public MMapLuceneSetupTest() {
        try {
            this.luceneSetup = new MMapLuceneSetup( createTempDirectory() );
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected BaseLuceneSetup getLuceneSetup() {
        return luceneSetup;
    }

}
