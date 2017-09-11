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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * Color is a data type that represents a color value in the RGB format.
 * 
 * <p>Java class for Color complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Color"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="red" use="required" type="{http://www.omg.org/spec/CMMN/20151109/DC}rgb" /&gt;
 *       &lt;attribute name="green" use="required" type="{http://www.omg.org/spec/CMMN/20151109/DC}rgb" /&gt;
 *       &lt;attribute name="blue" use="required" type="{http://www.omg.org/spec/CMMN/20151109/DC}rgb" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Color", namespace = "http://www.omg.org/spec/CMMN/20151109/DC")
public class Color {

    @XmlAttribute(name = "red", required = true)
    @XStreamAsAttribute
    protected int red;
    
    @XmlAttribute(name = "green", required = true)
    @XStreamAsAttribute
    protected int green;
    
    @XmlAttribute(name = "blue", required = true)
    @XStreamAsAttribute
    protected int blue;

    /**
     * Gets the value of the red property.
     * 
     */
    public int getRed() {
        return red;
    }

    /**
     * Sets the value of the red property.
     * 
     */
    public void setRed(int value) {
        this.red = value;
    }

    /**
     * Gets the value of the green property.
     * 
     */
    public int getGreen() {
        return green;
    }

    /**
     * Sets the value of the green property.
     * 
     */
    public void setGreen(int value) {
        this.green = value;
    }

    /**
     * Gets the value of the blue property.
     * 
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Sets the value of the blue property.
     * 
     */
    public void setBlue(int value) {
        this.blue = value;
    }

}
