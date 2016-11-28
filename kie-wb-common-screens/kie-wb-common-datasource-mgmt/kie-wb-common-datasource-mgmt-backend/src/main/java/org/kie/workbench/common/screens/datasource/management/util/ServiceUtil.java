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

package org.kie.workbench.common.screens.datasource.management.util;

import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger( ServiceUtil.class );

    public static String getManagedProperty( Properties properties, String propertyName ) {
        return getManagedProperty( properties, propertyName, null );
    }

    public static String getManagedProperty( Properties properties, String propertyName, String defaultValue ) {
        String propertyValue = System.getProperty( propertyName );
        if ( isEmpty( propertyValue ) ) {
            propertyValue = properties.getProperty( propertyName );
        }
        return propertyValue != null ? propertyValue.trim() : defaultValue;
    }

    public static boolean isEmpty( final String value ) {
        return value == null || value.trim().length() == 0;
    }

    public static Object getManagedBean( BeanManager beanManager, String beanName ) {

        // Obtain the beans for the concrete impl to use.
        Set<Bean<?>> beans = beanManager.getBeans( beanName );
        if ( beans == null || beans.isEmpty() ) {
            logger.warn( "Managed bean: " + beanName + " was not found." );
            return null;
        }

        // Instantiate the service impl.
        logger.info( "Getting reference to managed bean: " + beanName );
        Bean bean = ( Bean ) beans.iterator().next();
        if ( beans.size() > 1 ) {
            logger.warn( "Multiple beans were found for beanName: " + beanName +
                    "Using the first one found in the classpath with fully classified classname '" + bean.getBeanClass() );
        }
        CreationalContext context = beanManager.createCreationalContext( bean );
        return beanManager.getReference( bean, bean.getBeanClass(), context );
    }
}