/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.driver.model;

import org.kie.workbench.common.services.datamodeller.core.ElementType;

public class AnnotationParseRequest extends DriverRequest {

    private String annotationClassName;

    private String valuePairName;

    private String valuePairLiteralValue;

    private ElementType target;

    public AnnotationParseRequest() {
    }

    public AnnotationParseRequest( String annotationClassName, ElementType target, String valuePairName, String valuePairLiteralValue ) {
        this.annotationClassName = annotationClassName;
        this.target = target;
        this.valuePairName = valuePairName;
        this.valuePairLiteralValue = valuePairLiteralValue;
    }

    public String getAnnotationClassName() {
        return annotationClassName;
    }

    public String getValuePairName() {
        return valuePairName;
    }

    public String getValuePairLiteralValue() {
        return valuePairLiteralValue;
    }

    public ElementType getTarget() {
        return target;
    }

}
