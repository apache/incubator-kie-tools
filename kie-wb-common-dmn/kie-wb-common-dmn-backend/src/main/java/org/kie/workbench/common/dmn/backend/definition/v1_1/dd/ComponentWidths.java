/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;

@XStreamAlias("ComponentWidths")
public class ComponentWidths extends KieDMNModelInstrumentedBase {

    @XStreamAsAttribute
    private QName dmnElementRef;

    @XStreamImplicit
    private List<Double> widths;

    public ComponentWidths() {
        this.widths = new ArrayList<>();
    }

    public QName getDmnElementRef() {
        return dmnElementRef;
    }

    public void setDmnElementRef(final QName value) {
        this.dmnElementRef = value;
    }

    public List<Double> getWidths() {
        return widths;
    }

    public void setWidths(final List<Double> widths) {
        this.widths = widths;
    }
}
