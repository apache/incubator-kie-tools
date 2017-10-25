/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class TempFiles {

    private ArrayList<File> files = new ArrayList<File>();

    public File createTempFile(final String fullName) throws IOException {
        Iterator<String> iterator = Arrays.asList(fullName.split("/")).iterator();

        File previousFolder = null;
        while (iterator.hasNext()) {

            String next = iterator.next();
            if (iterator.hasNext()) {
                previousFolder = createFolder(previousFolder,
                                              next);
            } else {
                return createFile(previousFolder,
                                  next);
            }
        }

        return null;
    }

    private File createFolder(final File previousFolder,
                              final String next) throws IOException {
        if (previousFolder == null) {
            return createTempDirectory(next);
        } else {
            return createTempDirectory(next,
                                       previousFolder);
        }
    }

    private File createFile(final File previousFolder,
                            final String next) throws IOException {

        String suffix = getSuffix(next);
        String prefix = getPrefix(next);

        if (previousFolder == null) {
            File tempFile = File.createTempFile(prefix,
                                                suffix);
            files.add(tempFile);
            return tempFile;
        } else {
            File tempFile = File.createTempFile(prefix,
                                                suffix,
                                                previousFolder);
            files.add(tempFile);
            return tempFile;
        }
    }

    public File createTempDirectory(final String name) throws IOException {
        final File temp = File.createTempFile(name,
                                              Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        files.add(temp);

        return temp;
    }

    private File createTempDirectory(final String name,
                                     final File file) throws IOException {
        final File temp = File.createTempFile(name,
                                              Long.toString(System.nanoTime()),
                                              file);

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        files.add(temp);

        return temp;
    }

    private String getSuffix(final String next) {
        final int index = next.lastIndexOf('.');
        if (index >= 0) {
            return next.substring(0,
                                  index);
        } else {
            return next;
        }
    }

    private String getPrefix(final String next) {
        final int index = next.lastIndexOf('.');
        if (index >= 0) {
            return next.substring(index);
        } else {
            return next;
        }
    }

    public void deleteFiles() {
        for (final File tempFile : files) {
            FileUtils.deleteQuietly(tempFile);
        }
    }
}
