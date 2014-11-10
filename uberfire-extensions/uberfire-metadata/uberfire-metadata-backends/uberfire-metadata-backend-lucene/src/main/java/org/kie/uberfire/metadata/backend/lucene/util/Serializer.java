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

package org.uberfire.metadata.backend.lucene.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 */
public final class Serializer {

    private Serializer() {
    }

    public static byte[] toByteArray( final Object obj )
            throws IOException {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final ObjectOutputStream o = new ObjectOutputStream( b );
        o.writeObject( obj );
        return b.toByteArray();
    }

    public static Object fromByteArray( byte[] bytes )
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream b = new ByteArrayInputStream( bytes );
        final ObjectInputStream o = new ObjectInputStream( b );
        return o.readObject();

    }
}
