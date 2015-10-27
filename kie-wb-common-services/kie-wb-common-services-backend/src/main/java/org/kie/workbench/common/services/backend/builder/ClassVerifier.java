/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.services.backend.builder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.kie.scanner.KieModuleMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.services.backend.builder.BuildMessageBuilder.*;

public class ClassVerifier {

    private static final Logger logger = LoggerFactory.getLogger( ClassVerifier.class );

    private final static String ERROR_EXTERNAL_CLASS_VERIFICATION = "Verification of class {0} failed and will not be available for authoring.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private final TypeSourceResolver typeSourceResolver;
    private final KieModuleMetaData kieModuleMetaData;

    private final List<BuildMessage> buildMessages = new ArrayList<BuildMessage>();

    public ClassVerifier( final KieModuleMetaData kieModuleMetaData,
                          final TypeSourceResolver typeSourceResolver ) {
        this.kieModuleMetaData = kieModuleMetaData;
        this.typeSourceResolver = typeSourceResolver;
    }

    public List<BuildMessage> verify( Set<String> whiteList ) {

        for (final String packageName : kieModuleMetaData.getPackages()) {
            if ( whiteList.contains( packageName ) ) {
                for (final String className : kieModuleMetaData.getClasses( packageName )) {
                    verifyClass( packageName,
                                 className );
                }
            }
        }
        return buildMessages;
    }

    private void verifyClass( final String packageName,
                              final String className ) {

        try {
            final Class clazz = kieModuleMetaData.getClass( packageName,
                                                            className );
            if ( clazz != null ) {
                if ( TypeSource.JAVA_DEPENDENCY == typeSourceResolver.getTypeSource( clazz ) ) {
                    verifyExternalClass( clazz );
                }
            } else {
                logger.warn( MessageFormat.format( ERROR_EXTERNAL_CLASS_VERIFICATION,
                                                   toFQCN( packageName, className ) ) );
            }

        } catch (Throwable e) {
            final String msg = MessageFormat.format( ERROR_EXTERNAL_CLASS_VERIFICATION,
                                                     toFQCN( packageName, className ) );
            logger.warn( msg );
            buildMessages.add( makeWarningMessage( msg ) );
        }
    }

    private String toFQCN( final String packageName,
                           final String className ) {
        return packageName + "." + className;
    }

    private void verifyExternalClass( final Class clazz ) {
        //don't recommended to instantiate the class doing clazz.newInstance().
        clazz.getDeclaredConstructors();
        clazz.getDeclaredFields();
        clazz.getDeclaredMethods();
        clazz.getDeclaredClasses();
        clazz.getDeclaredAnnotations();
    }
}
