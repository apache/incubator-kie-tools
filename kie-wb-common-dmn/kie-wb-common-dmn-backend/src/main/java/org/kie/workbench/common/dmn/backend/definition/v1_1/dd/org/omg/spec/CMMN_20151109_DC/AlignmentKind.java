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
 * <p>Java class for AlignmentKind.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AlignmentKind"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="start"/&gt;
 *     &lt;enumeration value="end"/&gt;
 *     &lt;enumeration value="center"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AlignmentKind", namespace = "http://www.omg.org/spec/CMMN/20151109/DC")
@XmlEnum
public enum AlignmentKind {

    @XmlEnumValue("start")
    START("start"),
    @XmlEnumValue("end")
    END("end"),
    @XmlEnumValue("center")
    CENTER("center");
    private final String value;

    AlignmentKind(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlignmentKind fromValue(String v) {
        for (AlignmentKind c: AlignmentKind.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
