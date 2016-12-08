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

package org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.command;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.command.AbstractDataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelChangeNotifier;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.command.ValuePair;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public class AdjustFieldDefaultRelationsCommand extends AbstractDataModelCommand {

    protected ObjectProperty field;

    protected AbstractDataModelCommand previousCommand;

    DataModelCommandBuilder commandBuilder;

    public AdjustFieldDefaultRelationsCommand( AbstractDataModelCommand previousCommand, String source, ObjectProperty field,
            DataModelChangeNotifier notifier,
            DataModelCommandBuilder commandBuilder ) {
        super ( previousCommand.getContext(), source, previousCommand.getDataObject(), notifier );
        this.previousCommand = previousCommand;
        this.field = field;
        this.commandBuilder = commandBuilder;
    }

    public ObjectProperty getField() {
        return field;
    }

    public void setField( ObjectProperty field ) {
        this.field = field;
    }

    public AbstractDataModelCommand getPreviousCommand() {
        return previousCommand;
    }

    public void setPreviousCommand( AbstractDataModelCommand previousCommand ) {
        this.previousCommand = previousCommand;
    }

    @Override
    public void execute() {
        //this is not optimized just to read clearly what's defaulted in each case.
        //user added annotations that do not belong to the JPA by default managed annotations set remains untouched

        List<String> defaultCascadeType = new ArrayList<String>( 1 );
        defaultCascadeType.add( CascadeType.ALL.name() );
        String defaultFetchMode = FetchMode.EAGER.name();

        if ( getContext().getHelper().isPrimitiveType( field.getClassName() ) ) {

            //when a primitive type is set, all by default annotations managed by the JPA domain should be removed.
            removeOneToOne();
            removeOneTMany();
            removeManyToOne();
            removeElementCollection();

        } else if ( getContext().getHelper().isBaseType( field.getClassName() ) ||
                getContext().getDataModel().isEnum( field.getClassName() ) ) {

            //when a java lang type is set relation annotations should be removed.
            removeOneToOne();
            removeOneTMany();
            removeManyToOne();

            if ( field.isMultiple() ) {
                if ( field.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ELEMENT_COLLECTION ) == null ) {
                    List<ValuePair> values = new ArrayList<ValuePair>(  );
                    values.add( new ValuePair( RelationshipAnnotationValueHandler.FETCH, defaultFetchMode ) );
                    commandBuilder.buildFieldAnnotationAddCommand( getContext(), source, getDataObject(),
                            field, JPADomainAnnotations.JAVAX_PERSISTENCE_ELEMENT_COLLECTION, values ).execute();
                }
            } else {
                removeElementCollection();
            }

        } else {
            //last case, it's some other java class distinct than a basic java type
            removeOneToOne();
            removeElementCollection();

            if ( field.isMultiple() ) {
                removeManyToOne();
                if ( field.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY ) == null ) {
                    commandBuilder.buildFieldAnnotationAddCommand( getContext(), source, getDataObject(), getField(),
                            JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY )
                            .withValuePair( RelationshipAnnotationValueHandler.CASCADE, defaultCascadeType )
                            .withValuePair( RelationshipAnnotationValueHandler.FETCH, defaultFetchMode )
                            .execute();
                }
            } else {
                removeOneTMany();
                if ( field.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE ) == null ) {
                    commandBuilder.buildFieldAnnotationAddCommand( getContext(), source, getDataObject(), getField(),
                            JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE )
                            .withValuePair( RelationshipAnnotationValueHandler.CASCADE, defaultCascadeType )
                            .withValuePair( RelationshipAnnotationValueHandler.FETCH, defaultFetchMode )
                            .execute();
                }
            }
        }
    }

    private void removeManyToOne() {
        commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), source,
                getDataObject(), getField(),  JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE ).execute();
    }

    private void removeOneTMany() {
        commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), source,
                getDataObject(), getField(),  JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY ).execute();
    }

    private void removeElementCollection() {
        commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), source,
                getDataObject(), getField(),  JPADomainAnnotations.JAVAX_PERSISTENCE_ELEMENT_COLLECTION ).execute();
    }

    private void removeOneToOne() {
        commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), source,
                getDataObject(), getField(),  JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE ).execute();
    }
}
