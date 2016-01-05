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

package org.uberfire.security.impl.authz;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.Resource;
import org.uberfire.security.annotations.All;
import org.uberfire.security.annotations.Authorized;
import org.uberfire.security.annotations.Deny;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.GroupsResource;
import org.uberfire.security.authz.ProfileDecisionManager;
import org.uberfire.security.authz.ProfilesResource;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.RolesResource;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.authz.VotingStrategy;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

public class RuntimeResourceDecisionManager implements ResourceDecisionManager {

    private static final UnanimousBasedVoter ALL_VOTER = new UnanimousBasedVoter();
    private static final AffirmativeBasedVoter DEFAULT_VOTER = new AffirmativeBasedVoter();

    private final RuntimeAuthzCache cache = new RuntimeAuthzCache();

    private final RuntimeResourceManager resourceManager;

    public RuntimeResourceDecisionManager( final RuntimeResourceManager resourceManager ) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean supports( final Resource resource ) {
        if ( resource == null ) {
            return false;
        }
        if ( resource instanceof RuntimeResource ) {
            return true;
        }
        return false;
    }

    @Override
    public AuthorizationResult decide( final Resource resource,
                                       final User user,
                                       final ProfileDecisionManager profileDecisionManager ) {
        checkNotNull( "profileDecisionManager", profileDecisionManager );
        if ( !( resource instanceof RuntimeResource ) ) {
            throw new IllegalArgumentException( "Parameter named 'resource' is not instance of clazz 'RuntimeResource'!" );
        }
        boolean refreshCache = false;
        if ( resource instanceof Cacheable ) {
            refreshCache = ( (Cacheable) resource ).requiresRefresh();
        }
        final RuntimeResource runtimeResource = (RuntimeResource) resource;

        if ( cache.notContains( user, runtimeResource ) || refreshCache ) {
            if ( !resourceManager.requiresAuthentication( runtimeResource ) ) {
                return ACCESS_ABSTAIN;
            }

            final RuntimeResourceManager.RuntimeRestriction restriction = resourceManager.getRestriction( runtimeResource );

            if ( restriction == null || restriction.isEmpty() ) {
                return ACCESS_ABSTAIN;
            }

            boolean invertResult = false;
            VotingStrategy votingStrategy = null;

            for ( final String trait : restriction.getTraits() ) {
                if ( trait.equals( All.class.getName() ) ) {
                    votingStrategy = ALL_VOTER;
                } else if ( trait.equals( Authorized.class.getName() ) ) {
                    if ( user != null ) {
                        return ACCESS_GRANTED;
                    }
                } else if ( trait.equals( Deny.class.getName() ) ) {
                    invertResult = true;
                }
            }

            if ( votingStrategy == null ) {
                votingStrategy = DEFAULT_VOTER;
            }

            ProfilesResource profilesResource = null;

            if ( restriction instanceof RuntimeResourceManager.FeatureRestriction ) {
                profilesResource = new RolesResource() {
                    @Override
                    public Collection<Role> getRoles() {
                        return ( (RuntimeResourceManager.FeatureRestriction) restriction ).getRoles();
                    }
                };
            } else if ( restriction instanceof RuntimeResourceManager.ContentRestriction ) {
                profilesResource = new GroupsResource() {
                    @Override
                    public Collection<Group> getGroups() {
                        return ( (RuntimeResourceManager.ContentRestriction) restriction ).getGroups();
                    }
                };
            }

            final AuthorizationResult result = votingStrategy.vote( profileDecisionManager.decide( profilesResource, user ) );

            if ( invertResult ) {
                cache.put( user, runtimeResource, result.invert() );
            } else {
                cache.put( user, runtimeResource, result );
            }
            if ( resource instanceof Cacheable ) {
                ( (Cacheable) resource ).markAsCached();
            }
        }

        return cache.get( user, runtimeResource );
    }

    class RuntimeAuthzCache {

        final Map<String, Map<String, AuthorizationResult>> internal = new HashMap<String, Map<String, AuthorizationResult>>();

        public boolean notContains( final User user,
                                    final RuntimeResource resource ) {

            final Map<String, AuthorizationResult> result = internal.get( resource.getSignatureId() );
            if ( result == null ) {
                return true;
            }

            return !result.containsKey( user.getIdentifier() );
        }

        public void put( final User user,
                         final RuntimeResource resource,
                         final AuthorizationResult authzResult ) {
            if ( !internal.containsKey( resource.getSignatureId() ) ) {
                internal.put( resource.getSignatureId(), new HashMap<String, AuthorizationResult>() );
            }
            final Map<String, AuthorizationResult> result = internal.get( resource.getSignatureId() );
            AuthorizationResult knowValue = result.get( user.getIdentifier() );
            if ( result.containsKey( user.getIdentifier() ) && knowValue.equals( authzResult ) ) {
                return;
            }
            result.put( user.getIdentifier(), authzResult );
        }

        public AuthorizationResult get( final User user,
                                        final RuntimeResource resource ) {
            final Map<String, AuthorizationResult> result = internal.get( resource.getSignatureId() );
            if ( result == null ) {
                return ACCESS_DENIED;
            }

            final AuthorizationResult decision = result.get( user.getIdentifier() );
            if ( decision == null ) {
                return ACCESS_DENIED;
            }

            return decision;
        }
    }
}
