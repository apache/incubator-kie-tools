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

package org.uberfire.java.nio.fs.base;

import java.util.concurrent.TimeUnit;

import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.commons.util.Preconditions.*;

public class FileTimeImpl implements FileTime {

    private final long lastModified;

    public FileTimeImpl(final long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public long to(final TimeUnit unit) {
        checkNotNull("unit", unit);
        return unit.convert(lastModified, TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMillis() {
        return lastModified;
    }

    @Override
    public int compareTo(final FileTime o) {
        checkNotNull("o", o);
        long thisVal = this.toMillis();
        long anotherVal = o.toMillis();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

}
