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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KnownColor.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KnownColor"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="maroon"/&gt;
 *     &lt;enumeration value="red"/&gt;
 *     &lt;enumeration value="orange"/&gt;
 *     &lt;enumeration value="yellow"/&gt;
 *     &lt;enumeration value="olive"/&gt;
 *     &lt;enumeration value="purple"/&gt;
 *     &lt;enumeration value="fuchsia"/&gt;
 *     &lt;enumeration value="white"/&gt;
 *     &lt;enumeration value="lime"/&gt;
 *     &lt;enumeration value="green"/&gt;
 *     &lt;enumeration value="navy"/&gt;
 *     &lt;enumeration value="blue"/&gt;
 *     &lt;enumeration value="aqua"/&gt;
 *     &lt;enumeration value="teal"/&gt;
 *     &lt;enumeration value="black"/&gt;
 *     &lt;enumeration value="silver"/&gt;
 *     &lt;enumeration value="gray"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "KnownColor", namespace = "http://www.omg.org/spec/CMMN/20151109/DC")
@XmlEnum
public enum KnownColor {


    /**
     * a color with a value of #800000
     * 
     */
    @XmlEnumValue("maroon")
    MAROON("maroon"),

    /**
     * a color with a value of #FF0000
     * 
     */
    @XmlEnumValue("red")
    RED("red"),

    /**
     * a color with a value of #FFA500
     * 
     */
    @XmlEnumValue("orange")
    ORANGE("orange"),

    /**
     * a color with a value of #FFFF00
     * 
     */
    @XmlEnumValue("yellow")
    YELLOW("yellow"),

    /**
     * a color with a value of #808000
     * 
     */
    @XmlEnumValue("olive")
    OLIVE("olive"),

    /**
     * a color with a value of #800080
     * 
     */
    @XmlEnumValue("purple")
    PURPLE("purple"),

    /**
     * a color with a value of #FF00FF
     * 
     */
    @XmlEnumValue("fuchsia")
    FUCHSIA("fuchsia"),

    /**
     * a color with a value of #FFFFFF
     * 
     */
    @XmlEnumValue("white")
    WHITE("white"),

    /**
     * a color with a value of #00FF00
     * 
     */
    @XmlEnumValue("lime")
    LIME("lime"),

    /**
     * a color with a value of #008000
     * 
     */
    @XmlEnumValue("green")
    GREEN("green"),

    /**
     * a color with a value of #000080
     * 
     */
    @XmlEnumValue("navy")
    NAVY("navy"),

    /**
     * a color with a value of #0000FF
     * 
     */
    @XmlEnumValue("blue")
    BLUE("blue"),

    /**
     * a color with a value of #00FFFF
     * 
     */
    @XmlEnumValue("aqua")
    AQUA("aqua"),

    /**
     * a color with a value of #008080
     * 
     */
    @XmlEnumValue("teal")
    TEAL("teal"),

    /**
     * a color with a value of #000000
     * 
     */
    @XmlEnumValue("black")
    BLACK("black"),

    /**
     * a color with a value of #C0C0C0
     * 
     */
    @XmlEnumValue("silver")
    SILVER("silver"),

    /**
     * a color with a value of #808080
     * 
     */
    @XmlEnumValue("gray")
    GRAY("gray");
    private final String value;

    KnownColor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KnownColor fromValue(String v) {
        for (KnownColor c: KnownColor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
