/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.screens.workbench.backend;

import org.kie.workbench.screens.workbench.backend.impl.DefaultApplicationScopedProducer;

/**
 * Interface that should be implemented to register all Application scoped
 * producers used in the web application. There is a default implementation
 * ({@link DefaultApplicationScopedProducer}),
 * that can be replaced by using CDI alternatives.
 */
public interface ApplicationScopedProducer {

}
