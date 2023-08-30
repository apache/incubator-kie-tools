/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

public class FormalExpressionBodyHandler extends AbstractConverterHandler {

    private final FormalExpression formalExpression;

    public static FormalExpressionBodyHandler of(FormalExpression formalExpression) {
        return new FormalExpressionBodyHandler(formalExpression);
    }

    private FormalExpressionBodyHandler(FormalExpression formalExpression) {
        this.formalExpression = formalExpression;
    }

    public String getBody() {
        return get_3_6();
    }

    public void setBody(String newBody) {
        set(newBody);
    }

    public void setCDataBody(String newBody) {
        set(XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_CDATA(), newBody);
    }

    protected FeatureMap getMixed() {
        return formalExpression.getMixed();
    }
}
