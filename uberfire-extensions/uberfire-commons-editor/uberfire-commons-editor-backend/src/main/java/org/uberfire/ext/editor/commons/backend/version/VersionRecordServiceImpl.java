/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.backend.version;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;

public class VersionRecordServiceImpl
        implements VersionRecordService {

    private IOService   ioService;
    private VersionUtil util;

    public VersionRecordServiceImpl() {
    }

    @Inject
    public VersionRecordServiceImpl(@Named("ioStrategy") IOService ioService,
                                    VersionUtil util) {
        this.ioService = ioService;
        this.util = util;
    }

    @Override
    public List<VersionRecord> load(Path path) {

        final List<VersionRecord> records = loadVersionRecords(path);

        final List<VersionRecord> result = loadVersionRecords(util.getDotFilePath(path));

        for (final VersionRecord record : records) {
            if (doesNotContainID(record.id(), result)) {
                result.add(record);
            }
        }

        Collections.sort(
                result,
                new VersionRecordComparator());

        return result;
    }

    @Override
    public List<VersionRecord> loadVersionRecords(Path path) {
        if (ioService.exists(path)) {
            ArrayList<VersionRecord> portableRecords = new ArrayList<VersionRecord>();
            for (VersionRecord versionRecord : ioService.getFileAttributeView(path,
                                                                              VersionAttributeView.class).readAttributes().history().records()) {
                portableRecords.add(makePortable(versionRecord));
            }
            return portableRecords;
        } else {
            return new ArrayList<VersionRecord>();
        }
    }

    private PortableVersionRecord makePortable(VersionRecord record) {
        return new PortableVersionRecord(record.id(),
                                         record.author(),
                                         record.email(),
                                         record.comment(),
                                         record.date(),
                                         record.uri());
    }

    private boolean doesNotContainID(String id, List<VersionRecord> records) {
        for (VersionRecord record : records) {
            if (record.id().equals(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public VersionRecord loadRecord(Path path) throws URISyntaxException {

        for (VersionRecord record : loadVersionRecords(util.getPath(path, "master"))) {
            String version = util.getVersion(path);
            if ("master".equals(version)) {
                // Return first record when looking for master
                return record;
            } else if (record.id().equals(version)) {
                return record;
            }
        }

        return null;
    }

    private class VersionRecordComparator
            implements Comparator<VersionRecord> {

        @Override
        public int compare(VersionRecord left, VersionRecord right) {
            int compareTo = left.date().compareTo(right.date());

            if (compareTo == 0) {
                return compareUri(right.uri(), left.uri());
            } else {
                return compareTo;
            }
        }

        private int compareUri(String rightUri, String leftUri) {
            Iterator<Character> right = getReversedIterator(rightUri);
            Iterator<Character> left = getReversedIterator(leftUri);

            while (left.hasNext() && right.hasNext()) {
                Character l = left.next();
                Character r = right.next();
                if (l.equals('.') && !r.equals('.')) {
                    return 1;
                } else if (!l.equals('.') && r.equals('.')) {
                    return -1;
                }
            }

            return 0;
        }

        private Iterator<Character> getReversedIterator(String uri) {
            List<Character> chars = new ArrayList<Character>();
            for (char c : uri.toCharArray()) {
                chars.add(c);
            }
            Collections.reverse(chars);
            return chars.iterator();
        }
    }
}

