/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.config;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.backend.config.attribute.ConfigAttributes;
import org.guvnor.common.services.backend.config.attribute.ConfigAttributesUtil;
import org.guvnor.common.services.shared.config.ResourceConfigService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.kie.soup.commons.validation.Preconditions.checkNotNull;

@Service
@ApplicationScoped
public class ResourceConfigServiceImpl implements ResourceConfigService {

    @Override
    public Map<String, Object> configAttrs(final Map<String, Object> attrs) {
        checkNotNull("_attrs",
                     attrs);

        attrs.putAll(ConfigAttributesUtil.toMap(new ConfigAttributes() {

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

        return attrs;
    }
}
