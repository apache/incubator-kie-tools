/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class DMNElementReference extends DMNModelInstrumentedBase {

    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(final String value) {
        this.href = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DMNElementReference)) {
            return false;
        }

        final DMNElementReference that = (DMNElementReference) o;

        return href != null ? href.equals(that.href) : that.href == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(href != null ? href.hashCode() : 0);
    }
}
