/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionAttributes;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionAttributesUtil;
import org.guvnor.common.services.backend.metadata.attribute.DiscussionView;
import org.guvnor.common.services.backend.metadata.attribute.GeneratedAttributesUtil;
import org.guvnor.common.services.backend.metadata.attribute.GeneratedAttributesView;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaAttributes;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaAttributesUtil;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreAttributes;
import org.uberfire.io.attribute.DublinCoreAttributesUtil;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.BasicFileAttributesUtil;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.rpc.SessionInfo;

import static java.util.Collections.emptyList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class MetadataServiceImpl
        implements MetadataServerSideService {

    private IOService ioService;
    private IOService configIOService;
    private CommentedOptionFactory commentedOptionFactory;
    private SessionInfo sessionInfo;

    public MetadataServiceImpl() {
    }

    @Inject
    public MetadataServiceImpl(@Named("ioStrategy") IOService ioService,
                               @Named("configIO") IOService configIOService,
                               CommentedOptionFactory commentedOptionFactory,
                               SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.configIOService = configIOService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public Path saveMetadata(final Path path,
                             final Metadata metadata,
                             final String comment) {

        try (final InputStream inputStream = ioService.newInputStream(Paths.convert(path), StandardOpenOption.READ)) {

            return Paths.convert(ioService.write(Paths.convert(path),
                                                 IOUtils.toByteArray(inputStream),
                                                 setUpAttributes(path,
                                                                 metadata),
                                                 commentedOptionFactory.makeCommentedOption(comment)));
        } catch (IOException e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Metadata getMetadata(final Path pathToResource) {
        return getMetadata(Paths.convert(pathToResource));
    }

    @Override
    public Metadata getMetadata(org.uberfire.java.nio.file.Path path) {

        try {
            return new MetadataCreator(path,
                                       configIOService,
                                       sessionInfo,
                                       ioService.getFileAttributeView(path,
                                                                      DublinCoreView.class),
                                       ioService.getFileAttributeView(path,
                                                                      DiscussionView.class),
                                       ioService.getFileAttributeView(path,
                                                                      OtherMetaView.class),
                                       ioService.getFileAttributeView(path,
                                                                      VersionAttributeView.class),
                                       ioService.getFileAttributeView(path,
                                                                      GeneratedAttributesView.class)).create();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public List<String> getTags(final Path resource) {
        checkNotNull("MetadataServiceImpl.resource",
                     resource);
        return getTags(Paths.convert(resource));
    }

    @Override
    public List<String> getTags(final org.uberfire.java.nio.file.Path resource) {
        checkNotNull("MetadataServiceImpl.resource",
                     resource);
        final OtherMetaView otherMetaView = ioService.getFileAttributeView(resource,
                                                                           OtherMetaView.class);
        if (otherMetaView != null) {
            return otherMetaView.readAttributes().tags();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> configAttrs(final Map<String, Object> _attrs,
                                           final Metadata metadata) {
        try {
            checkNotNull("_attrs",
                         _attrs);
            checkNotNull("metadata",
                         metadata);

            Map<String, Object> attrs = BasicFileAttributesUtil.cleanup(_attrs);
            attrs = DublinCoreAttributesUtil.cleanup(attrs);
            attrs = DiscussionAttributesUtil.cleanup(attrs);
            attrs = OtherMetaAttributesUtil.cleanup(attrs);
            attrs = GeneratedAttributesUtil.cleanup(attrs);

            attrs.putAll(DiscussionAttributesUtil.toMap(
                    new DiscussionAttributes() {
                        @Override
                        public List<DiscussionRecord> discussion() {
                            return metadata.getDiscussion();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    },
                    "*"));

            attrs.putAll(OtherMetaAttributesUtil.toMap(
                    new OtherMetaAttributes() {
                        @Override
                        public List<String> tags() {
                            return metadata.getTags();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    },
                    "*"));

            attrs.putAll(DublinCoreAttributesUtil.toMap(
                    new DublinCoreAttributes() {

                        @Override
                        public List<String> titles() {
                            return emptyList();
                        }

                        @Override
                        public List<String> creators() {
                            return emptyList();
                        }

                        @Override
                        public List<String> subjects() {
                            return new ArrayList<String>(1) {{
                                add(metadata.getSubject());
                            }};
                        }

                        @Override
                        public List<String> descriptions() {
                            return new ArrayList<String>(1) {{
                                add(metadata.getDescription());
                            }};
                        }

                        @Override
                        public List<String> publishers() {
                            return emptyList();
                        }

                        @Override
                        public List<String> contributors() {
                            return emptyList();
                        }

                        @Override
                        public List<String> types() {
                            return new ArrayList<String>(1) {{
                                add(metadata.getType());
                            }};
                        }

                        @Override
                        public List<String> formats() {
                            return emptyList();
                        }

                        @Override
                        public List<String> identifiers() {
                            return emptyList();
                        }

                        @Override
                        public List<String> sources() {
                            return new ArrayList<String>(1) {{
                                add(metadata.getExternalSource());
                            }};
                        }

                        @Override
                        public List<String> languages() {
                            return emptyList();
                        }

                        @Override
                        public List<String> relations() {
                            return new ArrayList<String>(1) {{
                                add(metadata.getExternalRelation());
                            }};
                        }

                        @Override
                        public List<String> coverages() {
                            return emptyList();
                        }

                        @Override
                        public List<String> rights() {
                            return emptyList();
                        }

                        @Override
                        public FileTime lastModifiedTime() {
                            return null;
                        }

                        @Override
                        public FileTime lastAccessTime() {
                            return null;
                        }

                        @Override
                        public FileTime creationTime() {
                            return null;
                        }

                        @Override
                        public boolean isRegularFile() {
                            return false;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public boolean isSymbolicLink() {
                            return false;
                        }

                        @Override
                        public boolean isOther() {
                            return false;
                        }

                        @Override
                        public long size() {
                            return 0;
                        }

                        @Override
                        public Object fileKey() {
                            return null;
                        }
                    },
                    "*"));

            if (metadata.isGenerated()) {
                attrs.put(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME,
                          true);
            }

            return attrs;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Map<String, Object> setUpAttributes(final Path path,
                                               final Metadata metadata) {
        try {
            Map<String, Object> attributes;
            try {
                attributes = ioService.readAttributes(Paths.convert(path));
            } catch (final NoSuchFileException ex) {
                attributes = new HashMap<String, Object>();
            }
            if (metadata != null) {
                attributes = configAttrs(attributes,
                                         metadata);
            }

            return BasicFileAttributesUtil.cleanup(attributes);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}
