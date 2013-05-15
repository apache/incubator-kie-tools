/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.services.backend.config;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.java.nio.file.attribute.FileTime;
import org.kie.guvnor.services.backend.config.attribute.ConfigAttributes;
import org.kie.workbench.services.shared.config.ResourceConfigService;

import static org.kie.commons.validation.Preconditions.*;
import static org.kie.guvnor.services.backend.config.attribute.ConfigAttributesUtil.*;

@Service
@ApplicationScoped
public class ResourceConfigServiceImpl implements ResourceConfigService {

    @Override
    public Map<String, Object> configAttrs( final Map<String, Object> attrs) {
        checkNotNull( "_attrs", attrs );

        attrs.putAll( toMap( new ConfigAttributes() {


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
        }, "*" ) );

        return attrs;
    }
}
