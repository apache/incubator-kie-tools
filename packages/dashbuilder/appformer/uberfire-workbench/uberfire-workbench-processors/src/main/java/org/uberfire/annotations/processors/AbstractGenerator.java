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
package org.uberfire.annotations.processors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Version;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A class capable of generating source code using FreeMarker templates
 */
public abstract class AbstractGenerator {

    private static Version FREE_MARKER_VERSION = new Version(Version.intValueFor(2,
                                                                                 3,
                                                                                 25));

    static boolean FAIL_FOR_TESTING = false;

    private static ExceptionInInitializerError INITIALIZER_EXCEPTION = null;

    private static MultiTemplateLoader templateLoader = new MultiTemplateLoader();

    protected static Configuration config;

    //per-instance static initializer block
    {
        if (FAIL_FOR_TESTING) {
            throw new NoClassDefFoundError("Failing for testing purposes");
        }
        try {
            synchronized (AbstractGenerator.class) {
                final ClassTemplateLoader loader = new ClassTemplateLoader(getClass(),
                                                                           "templates");
                templateLoader.addTemplateLoader(loader);
            }
        } catch (NoClassDefFoundError ex) {
            if (ex.getCause() == null) {
                ex.initCause(INITIALIZER_EXCEPTION);
            }
            throw ex;
        } catch (ExceptionInInitializerError ex) {
            INITIALIZER_EXCEPTION = ex;
            throw ex;
        }
    }

    //Class's static initializer block
    static {
        config = new Configuration(FREE_MARKER_VERSION);
        config.setTemplateLoader(templateLoader);
        config.setObjectWrapper(new DefaultObjectWrapperBuilder(FREE_MARKER_VERSION).build());
    }

    public abstract StringBuffer generate(final String packageName,
                                          final PackageElement packageElement,
                                          final String className,
                                          final Element element,
                                          final ProcessingEnvironment processingEnvironment) throws GenerationException;
}
