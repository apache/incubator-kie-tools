/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

@Portable
public class RuleAnnotationClauseText extends DMNElement implements HasText {

    private Text text;

    public RuleAnnotationClauseText() {
        this.text = new Text();
    }

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public void setText(final Text text) {
        this.text = text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RuleAnnotationClauseText that = (RuleAnnotationClauseText) o;
        if (!Objects.equals(text, that.text)) {
            return false;
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}