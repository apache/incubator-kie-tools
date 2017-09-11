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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC.Color;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DI.Shape;

@XStreamAlias("DMNShape")
public class DMNShape extends Shape {
    
    @XStreamAsAttribute
    private String dmnElementRef;
        
    @XStreamAlias("BgColor")
    private Color bgColor;
    
    @XStreamAlias("BorderColor")
    private Color borderColor;
    
    @XStreamAlias("BorderSize")
    private BorderSize borderSize;
    
    @XStreamAlias("DMNFontStyle")
    private DMNStyle fontStyle;
    
    public String getDmnElementRef() {
        return dmnElementRef;
    }
    
    public void setDmnElementRef(String dmnElementRef) {
        this.dmnElementRef = dmnElementRef;
    }

    public Color getBgColor() {
        return bgColor;
    }
    
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    
    public Color getBorderColor() {
        return borderColor;
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
    
    public BorderSize getBorderSize() {
        return borderSize;
    }
    
    public void setBorderSize(BorderSize borderSize) {
        this.borderSize = borderSize;
    }

    public DMNStyle getFontStyle() {
        return fontStyle;
    }
    
    public void setFontStyle(DMNStyle fontStyle) {
        this.fontStyle = fontStyle;
    }
}
