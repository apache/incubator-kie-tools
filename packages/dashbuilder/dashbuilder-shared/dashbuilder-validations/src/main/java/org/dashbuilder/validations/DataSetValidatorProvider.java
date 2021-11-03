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

package org.dashbuilder.validations;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.ioc.client.api.ManagedInstance;

@ApplicationScoped
public class DataSetValidatorProvider {

    private Map<String, DataSetDefValidator> validators = new HashMap<>();

    private ManagedInstance<DataSetDefValidator> validatorsInstances;

    @Inject
    public DataSetValidatorProvider( ManagedInstance<DataSetDefValidator> validatorsInstances ) {
        this.validatorsInstances = validatorsInstances;
    }

    @PostConstruct
    public void init() {
        for( DataSetDefValidator validator : validatorsInstances ) {
            registerValidator( validator );
        }
    }

    protected void registerValidator( DataSetDefValidator validator ) {
        validators.put( validator.getSupportedProvider().getName(), validator );
    }

    public DataSetDefValidator getValidator( DataSetDef dataSetDef ) {
        Assert.notNull("DataSetDef cannot be null", dataSetDef);

        DataSetDefValidator validator = validators.get( dataSetDef.getProvider().getName() );

        if ( validator == null ) {
            throw new IllegalArgumentException( "Cannot find validator for type '" + dataSetDef.getClass().getName() + "'" );
        }

        return validator;
    }

    public Iterable<ConstraintViolation<?>> validateAttributes( DataSetDef dataSetDef, Object... params ) {
        DataSetDefValidator validator = getValidator( dataSetDef );

        return validator.validateCustomAttributes( dataSetDef, params );
    }

    public Iterable<ConstraintViolation<?>> validate( DataSetDef dataSetDef,
                                               boolean isCacheEnabled,
                                               boolean isPushEnabled,
                                               boolean isRefreshEnabled,
                                               Object... params ) {
        DataSetDefValidator validator = getValidator( dataSetDef );

        return validator.validate( dataSetDef, isCacheEnabled, isPushEnabled, isRefreshEnabled, params );
    }

    public <T extends DataSetDef> Iterable<ConstraintViolation<?>> validateBasicAttributes( DataSetDef dataSetDef ) {
        DataSetDefValidator validator = getValidator( dataSetDef );

        return validator.validateBasicAttributes( dataSetDef );
    }

    public Iterable<ConstraintViolation<?>> validateProviderType( DataSetDef dataSetDef ) {
        DataSetDefValidator validator = getValidator( dataSetDef );

        return validator.validateProviderType( dataSetDef );
    }
}
