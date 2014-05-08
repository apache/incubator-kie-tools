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
package org.kie.workbench.common.services.shared.validation;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

/**
 * Validation Service
 */
@Remote
public interface ValidationService {

    public boolean isProjectNameValid( final String projectName );

    public boolean isPackageNameValid( final String packageName );

    public boolean isFileNameValid( final Path path,
                                    final String fileName );

    public boolean isFileNameValid( final String fileName );

    public Map<String, Boolean> evaluateIdentifiers( final String[] identifiers );

    public Map<String, Boolean> evaluateArtifactIdentifiers( final String[] identifiers );

    public boolean isTimerIntervalValid( final String timerInterval );

}
