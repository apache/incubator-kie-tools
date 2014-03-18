/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.annotations.processors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

import org.uberfire.annotations.processors.exceptions.GenerationException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * A class capable of generating source code using FreeMarker templates
 */
public abstract class AbstractGenerator {

    static boolean FAIL_FOR_TESTING = false;

    protected static Configuration config;

    {
        if (FAIL_FOR_TESTING) {
            throw new NoClassDefFoundError( "Failing for testing purposes" );
        }
        config = new Configuration();
        config.setClassForTemplateLoading( getClass(),
                                           "templates" );
        config.setObjectWrapper( new DefaultObjectWrapper() );
    }

    public abstract StringBuffer generate(final String packageName,
                                          final PackageElement packageElement,
                                          final String className,
                                          final Element element,
                                          final ProcessingEnvironment processingEnvironment) throws GenerationException;

}
