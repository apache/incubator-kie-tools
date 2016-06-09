/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.validation;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;

/**
 * Package Name validation
 */
@ApplicationScoped
public class PackageNameValidator implements FileNameValidator {

    @Inject
    private KieProjectService projectService;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean accept( final String fileName ) {
        return false;
    }

    @Override
    public boolean accept( final Path path ) {
        final Package pkg = projectService.resolvePackage( path );
        if ( pkg == null ) {
            return false;
        }
        return pkg.getPackageMainSrcPath().equals( path ) ||
                pkg.getPackageMainResourcesPath().equals( path ) ||
                pkg.getPackageTestSrcPath().equals( path ) ||
                pkg.getPackageTestResourcesPath().equals( path );
    }

    @Override
    public boolean isValid( final String value ) {
        if ( value == null ) {
            return false;
        }
        final Map<String, Boolean> results = evaluateIdentifiers( value.split( "\\.",
                                                                               -1 ) );
        return !results.containsValue( Boolean.FALSE );
    }

    private Map<String, Boolean> evaluateIdentifiers( final String[] identifiers ) {
        final Map<String, Boolean> result = new HashMap<String, Boolean>( identifiers.length );
        if ( identifiers != null && identifiers.length > 0 ) {
            for ( String s : identifiers ) {
                result.put( s, ValidationUtils.isJavaIdentifier( s ) );
            }
        }
        return result;
    }
}
