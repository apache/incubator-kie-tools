/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver.impl.annotations;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Label;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.TypeSafe;
import org.kie.api.remote.Remotable;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;

public class CommonAnnotations {

    private static List<AnnotationDefinition> commonAnnotations = new ArrayList<AnnotationDefinition>( );
    static {

        //Drools & JBPM domain annotations.
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Description.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Key.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Label.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Role.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Position.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( PropertyReactive.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( ClassReactive.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Duration.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Expires.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Timestamp.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( TypeSafe.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Remotable.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( XmlRootElement.class ) );

        //JPA domain annotations
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Entity.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Id.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( GeneratedValue.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( SequenceGenerator.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Table.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Column.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( OneToOne.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( OneToMany.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( ManyToOne.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( ManyToMany.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( ElementCollection.class ) );
        commonAnnotations.add( DriverUtils.buildAnnotationDefinition( Audited.class ) );

    }

    public static List<AnnotationDefinition> getCommonAnnotations() {
        return commonAnnotations;
    }

}
