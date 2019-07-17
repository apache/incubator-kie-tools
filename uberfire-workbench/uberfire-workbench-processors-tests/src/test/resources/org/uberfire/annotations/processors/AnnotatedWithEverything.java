/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.experimental.definition.annotations.ExperimentalFeature;

/**
 * A non-functional class that exists only to support the UF-44 regression test.
 */
@WorkbenchPerspective(identifier = "sample")
@WorkbenchEditor(identifier = "sample")
@WorkbenchContext(identifier = "sample")
@WorkbenchPopup(identifier = "sample")
@WorkbenchScreen(identifier = "sample")
@WorkbenchSplashScreen(identifier = "sample")
@ExperimentalFeature
@WorkbenchClientEditor(identifier = "sample")
public class AnnotatedWithEverything {

}
