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
package org.drools.guvnor.annotations.processors;

import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * 
 */
public abstract class AbstractGenerator {

    protected Configuration config;

    public AbstractGenerator() {
        config = new Configuration();
        config.setClassForTemplateLoading( getClass(),
                                           "templates" );
        config.setObjectWrapper( new DefaultObjectWrapper() );
    }

    public abstract void generate(final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final TypeElement classElement,
                                  final ProcessingEnvironment processingEnvironment,
                                  final Writer w);

}
