/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.vfs;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.guvnor.ala.marshalling.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

/**
 * Helper class for implementing the different VFS based registries that are backed by using VFSRegistryEntry elements.
 */
@ApplicationScoped
public class VFSRegistryHelper {

    protected static final String PROVISIONING_BRANCH = "master";

    protected static final String PROVISIONING_PATH = "provisioning";

    private static final Logger logger = LoggerFactory.getLogger(VFSRegistryHelper.class);

    private VFSMarshallerRegistry marshallerRegistry;

    private IOService ioService;

    private FileSystem fileSystem;

    private Path provisioningRootPath;

    private VFSRegistryEntryMarshaller entryMarshaller;

    public VFSRegistryHelper() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public VFSRegistryHelper(final VFSMarshallerRegistry marshallerRegistry,
                             final @Named("ioStrategy") IOService ioService,
                             final @Named("systemFS") FileSystem fileSystem) {
        this.marshallerRegistry = marshallerRegistry;
        this.ioService = ioService;
        this.fileSystem = fileSystem;
    }

    @PostConstruct
    protected void init() {
        try {
            provisioningRootPath = fileSystem.getPath(PROVISIONING_BRANCH,
                                                      PROVISIONING_PATH);
            logger.debug("provisioningRootPath: " + provisioningRootPath.toUri());
        } catch (Exception e) {
            //uncommon error
            logger.error("An error was produced during VFS registries directory initialization.",
                         e);
        }
        entryMarshaller = (VFSRegistryEntryMarshaller) marshallerRegistry.get(VFSRegistryEntry.class);
    }

    /**
     * Ensure that an expected directory under the VFS registries host directory exists.
     * @param directory a directory name.
     * @return the path to the directory.
     */
    public Path ensureDirectory(final String directory) {
        Path directoryPath = provisioningRootPath.resolve(directory);
        if (!ioService.exists(directoryPath)) {
            directoryPath = ioService.createDirectory(directoryPath);
        }
        return directoryPath;
    }

    /**
     * Helper method calculating the MD5 digest for a String.
     * @param content a String value for calculating the digest.
     * @return returns the digest as a 32 character hex string or the empty string "" if content == null.
     */
    public String md5Hex(String content) {
        if (content == null) {
            return "";
        }
        return DigestUtils.md5Hex(content);
    }

    /**
     * Stores an Object marshalled value as a VFSRegistryEntry in the target path.
     * @param path a path for storing the generated VFSRegistryEntry.
     * @param value an object value to marshall and store.
     * @throws Exception exceptions might be thrown in cases of filesystem or marshalling errors.
     */
    public void storeEntry(final Path path,
                           final Object value) throws Exception {
        final Marshaller marshaller = marshallerRegistry.get(value.getClass());
        if (marshaller == null) {
            throw new Exception("No marshaller was found for class: " + value.getClass());
        }
        @SuppressWarnings("unchecked")
        final String marshalledValue = marshaller.marshal(value);
        final VFSRegistryEntry entry = new VFSRegistryEntry(value.getClass().getName(),
                                                            marshalledValue);
        final String content = entryMarshaller.marshal(entry);
        writeBatch(path,
                   content);
    }

    /**
     * Reads an Object previously marshalled and stored as a VFSRegistryEntry in a given path.
     * @param path the path where the VFSRegistryEntry is stored.
     * @return the unmarshalled object backed by the VFSRegistryEntry.
     * @throws Exception exceptions might be thrown in cases of filesystem or marshalling errors.
     */
    public Object readEntry(final Path path) throws Exception {
        final String entryContent = ioService.readAllString(path);
        final VFSRegistryEntry entry = entryMarshaller.unmarshal(entryContent);
        final Marshaller marshaller = marshallerRegistry.get(Class.forName(entry.getContentType()));
        if (marshaller == null) {
            throw new Exception("No marshaller was found for class: " + entry.getContentType());
        }
        return marshaller.unmarshal(entry.getContent());
    }

    /**
     * Reads a list of entries from a path by filtering the files by a given filter.
     * @param rootPath a path for looking of the VFSRegistryEntry files.
     * @param filter a filter for selecting the files.
     * @return a list with the unmarshalled objects backed by the filtered files.
     */
    public List<Object> readEntries(final Path rootPath,
                                    final DirectoryStream.Filter<Path> filter) throws Exception {
        final List<Object> entries = new ArrayList<>();
        for (Path path : ioService.newDirectoryStream(rootPath,
                                                      filter)) {
            try {
                entries.add(readEntry(path));
            } catch (Exception e) {
                logger.error("An error was produced while processing entry for path: " + path,
                             e);
                throw e;
            }
        }
        return entries;
    }

    /**
     * Writes a content on a target path by performing a batch delete operation.
     * @param path a path to write.
     * @param content a content to write.
     */
    public void writeBatch(final Path path,
                           final String content) {
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.write(path,
                            content);
        } finally {
            ioService.endBatch();
        }
    }

    /**
     * Deletes a path by performing a batch delete operation.
     * @param path a path to delete.
     */
    public void deleteBatch(final Path path) {
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.deleteIfExists(path);
        } finally {
            ioService.endBatch();
        }
    }

    /**
     * Helper class for filtering files by a suffix. Files that ends with the expected suffix verifies the filter.
     */
    public static class BySuffixFilter
            implements DirectoryStream.Filter<Path> {

        private String suffix;

        private BySuffixFilter(final String suffix) {
            this.suffix = suffix;
        }

        public static BySuffixFilter newFilter(final String suffix) {
            return new BySuffixFilter(suffix);
        }

        @Override
        public boolean accept(Path path) throws IOException {
            return path.getFileName().toString().endsWith(suffix);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BySuffixFilter that = (BySuffixFilter) o;

            return suffix != null ? suffix.equals(that.suffix) : that.suffix == null;
        }

        @Override
        public int hashCode() {
            return suffix != null ? suffix.hashCode() : 0;
        }
    }
}
