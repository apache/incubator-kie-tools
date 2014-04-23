/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Label;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils;

public class DefaultJavaRoasterModelAnnotationDriver implements AnnotationDriver {

    @Override
    public Annotation buildAnnotation(AnnotationDefinition annotationDefinition, Object annotationToken) throws ModelDriverException {

        AnnotationSource javaAnnotationToken = (AnnotationSource)annotationToken;
        AnnotationImpl annotation = new AnnotationImpl(annotationDefinition);
        if (annotationDefinition.isMarker()) {
            return annotation;
        } else {
            //try to read annotation parameters
            /*if (javaAnnotationToken.getAnnotationValue() != null ) {
                for (AnnotationMemberDefinition annotationMember : annotationDefinition.getAnnotationMembers()) {
                    if ("value".equals(annotationMember.getName())) {
                        annotation.setValue( annotationMember.getName(), parseParamValue(annotationDefinition, annotationMember.getName(), javaAnnotationToken.getAnnotationValue().getStringValue() ));
                    }
                }
            } else */

            if (javaAnnotationToken.getValues() != null) {
                List<ValuePair> values = javaAnnotationToken.getValues();
                Map<String, String> valuePairValues = new HashMap<String, String>();

                for (ValuePair valuePair : values) {
                    valuePairValues.put( valuePair.getName(), valuePair.getStringValue() );
                }
                if (valuePairValues.size() > 0) {
                    for (AnnotationMemberDefinition annotationMember : annotationDefinition.getAnnotationMembers()) {
                        String value = valuePairValues.get( annotationMember.getName() );
                        if (value != null) {
                            annotation.setValue(annotationMember.getName(), parseParamValue( annotationDefinition, annotationMember.getName(), value ));
                        }
                    }
                }
            }
        }
        return annotation;
    }

    //TODO provide a better implementation for this method
    private String parseParamValue(AnnotationDefinition annotationDefinition, String param, String value)  {

        String result = value;

        if ( result != null &&
             ( Description.class.getName().equals( annotationDefinition.getClassName() ) ||
               Label.class.getName().equals( annotationDefinition.getClassName() ) ||
               Timestamp.class.getName().equals( annotationDefinition.getClassName() ) ||
               Duration.class.getName().equals( annotationDefinition.getClassName() ) ||
               Expires.class.getName().equals( annotationDefinition.getClassName() )
             ) ) {

            /*
            if (result.startsWith( "\"" )) {
                result = result.length() > 1 ? result.substring( 1, result.length() ) : "";
            }
            if (result.endsWith( "\"" )) {
                result = result.length() > 1 ? result.substring( 0, result.length()-1 ) : "";
            }*/

            result = StringEscapeUtils.unescapeJava( result );

        } else if ( Position.class.getName().equals( annotationDefinition.getClassName() ) ) {
            result = value;
        } else if ( Role.class.getName().equals( annotationDefinition.getClassName() )) {
            if (value != null && value.endsWith( "FACT" )) {
                result = "FACT";
            } else if (value != null && value.endsWith( "EVENT" )) {
                result = "EVENT";
            } else {
                result = value;
            }
        }
        return result;
    }
}
