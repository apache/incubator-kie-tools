/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.validations.dataset;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.validation.groups.DataSetDefBasicAttributesGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefCacheRowsValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefProviderTypeGroup;
import org.dashbuilder.dataset.validation.groups.DataSetDefPushSizeValidation;
import org.dashbuilder.dataset.validation.groups.DataSetDefRefreshIntervalValidation;
import org.dashbuilder.validations.DataSetDefValidator;

/**
 * <p>The base data set definition validator.</p>
 * @since 0.4.0
 */
public abstract class AbstractDataSetDefValidator<T extends DataSetDef> implements DataSetDefValidator<T> {

    protected Validator validator;

    public AbstractDataSetDefValidator( Validator validator ) {
        this.validator = validator;
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateProviderType( final DataSetDef dataSetDef ) {
        Set<ConstraintViolation<DataSetDef>> violations = validator.validate( dataSetDef, DataSetDefProviderTypeGroup.class );
        return (Iterable<ConstraintViolation<?>>) (Set<?>) violations;
    }

    @Override
    public Iterable<ConstraintViolation<?>> validateBasicAttributes( DataSetDef dataSetDef ) {
        Set<ConstraintViolation<DataSetDef>> violations = validator.validate( dataSetDef,
                                                                      DataSetDefBasicAttributesGroup.class );
        return (Iterable<ConstraintViolation<?>>) (Set<?>) violations;
    }

    protected Class[] getValidationGroups( final boolean isCacheEnabled,
                                           final boolean isPushEnabled,
                                           final boolean isRefreshEnabled,
                                           final Class... groups ) {
        List<Class> classes = new LinkedList<Class>();
        classes.add( DataSetDefBasicAttributesGroup.class );
        classes.add( DataSetDefProviderTypeGroup.class );
        if ( isCacheEnabled ) {
            classes.add( DataSetDefCacheRowsValidation.class );
        }
        if ( isPushEnabled ) {
            classes.add( DataSetDefPushSizeValidation.class );
        }
        if ( isRefreshEnabled ) {
            classes.add( DataSetDefRefreshIntervalValidation.class );
        }
        if ( groups != null ) {
            for ( final Class group : groups ) {
                classes.add( group );
            }
        }

        return classes.toArray( new Class[classes.size()] );
    }

    protected Iterable<ConstraintViolation<?>> toIterable( Set<ConstraintViolation<T>> violations ) {
        return (Iterable<ConstraintViolation<?>>) (Set<?>) violations;
    }
}
