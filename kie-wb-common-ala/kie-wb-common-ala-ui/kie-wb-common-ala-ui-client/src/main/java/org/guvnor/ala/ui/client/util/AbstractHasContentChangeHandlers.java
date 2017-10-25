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

package org.guvnor.ala.ui.client.util;

import java.util.HashSet;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractHasContentChangeHandlers
        implements HasContentChangeHandlers {

    protected final HashSet<ContentChangeHandler> changeHandlers = new HashSet<>();

    @Override
    public void addContentChangeHandler(final ContentChangeHandler changeHandler) {
        changeHandlers.add(checkNotNull("changeHandler",
                                        changeHandler));
    }

    @Override
    public boolean removeContentChangeHandler(ContentChangeHandler changeHandler) {
        return changeHandlers.remove(checkNotNull("changeHandler",
                                                  changeHandler));
    }

    public void fireChangeHandlers() {
        changeHandlers.forEach(ContentChangeHandler::onContentChange);
    }
}
