/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DI.Shape;

@XStreamAlias("DMNDiagram")
public class DMNDiagram extends Shape {
    public static final String DMNV11_DD = "java://" + DMNDiagram.class.getPackage().getName();
    public static final String DMNV11_DC = "http://www.omg.org/spec/CMMN/20151109/DC";
    public static final String DMNV11_DI = "http://www.omg.org/spec/CMMN/20151109/DI";
    
    @XStreamImplicit
    protected List<DMNShape> any;

    public List<DMNShape> getAny() {
        if ( any == null ) {
            any = new ArrayList<>();
        }
        return this.any;
    }
    
}
