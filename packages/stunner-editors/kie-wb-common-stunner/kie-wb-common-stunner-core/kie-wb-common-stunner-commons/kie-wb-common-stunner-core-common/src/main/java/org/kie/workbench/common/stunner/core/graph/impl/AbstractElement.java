/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractElement<C>
        implements Element<C> {

    private final String uuid;
    private final Set<String> labels = new LinkedHashSet<>();
    private C content;

    public AbstractElement(final String uuid) {
        this.uuid = PortablePreconditions.checkNotNull("uuid",
                                                       uuid);
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public Set<String> getLabels() {
        return labels;
    }

    @Override
    public C getContent() {
        return content;
    }

    @Override
    public void setContent(final C content) {
        this.content = content;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractElement)) {
            return false;
        }
        AbstractElement that = (AbstractElement) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }

    @Override
    public String toString() {
        return "ElementImpl{" +
                "uuid=" + uuid +
                ", labels=" + labels +
                '}';
    }
}
