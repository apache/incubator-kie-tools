/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

/**
 * This interface enables the definition of a component that can react upon modifications on datasource or driver
 * definition files.
 */
public interface DefChangeHandler {

    void processResourceAdd( Path path, SessionInfo sessionInfo );

    void processResourceUpdate( Path path, SessionInfo sessionInfo );

    void processResourceRename( Path originalPath, Path targetPath, SessionInfo sessionInfo );

    void processResourceDelete( Path path, SessionInfo sessionInfo );

}