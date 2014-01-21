/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.services.backend.validation.java;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.validation.java.IdentifierValidationService;

/**
 * Implementation of Validation Service for Java identifiers
 */
@Service
@ApplicationScoped
public class IdentifierValidationServiceImpl implements IdentifierValidationService {

    @Override
    public Map<String, Boolean> evaluateIdentifiers( String[] identifiers ) {
        Map<String, Boolean> result = new HashMap<String, Boolean>( identifiers.length );
        if ( identifiers != null && identifiers.length > 0 ) {
            for ( String s : identifiers ) {
                result.put( s, ValidationUtils.isJavaIdentifier( s ) );
            }
        }
        return result;
    }

    @Override
    public Map<String, Boolean> evaluateArtifactIdentifiers( String[] identifiers ) {
        Map<String, Boolean> result = new HashMap<String, Boolean>( identifiers.length );
        if ( identifiers != null && identifiers.length > 0 ) {
            for ( String s : identifiers ) {
                result.put( s, ValidationUtils.isArtifactIdentifier( s ) );
            }
        }
        return result;
    }
}
