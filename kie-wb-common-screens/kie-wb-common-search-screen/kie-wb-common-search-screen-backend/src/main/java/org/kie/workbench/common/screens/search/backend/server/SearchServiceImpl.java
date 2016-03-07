/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.search.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.common.screens.search.model.SearchPageRow;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.screens.search.service.SearchService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.search.DateRange;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Service
@ApplicationScoped
public class SearchServiceImpl implements SearchService {

    private IOSearchService ioSearchService;

    private IOService ioService;

    private OrganizationalUnitService organizationalUnitService;

    private KieProjectService projectService;

    protected User identity;

    private AuthorizationManager authorizationManager;

    private Instance<ResourceTypeDefinition> typeRegister;

    private Map<String, ResourceTypeDefinition> types = new HashMap<String, ResourceTypeDefinition>();

    private PageResponse<SearchPageRow> emptyResponse = null;

    public SearchServiceImpl() {
        //Needed for CDI proxies
    }

    @Inject
    public SearchServiceImpl( @Named("ioSearchStrategy") final IOSearchService ioSearchService,
                              @Named("ioStrategy") final IOService ioService,
                              final OrganizationalUnitService organizationalUnitService,
                              final KieProjectService projectService,
                              final User identity,
                              final AuthorizationManager authorizationManager,
                              @Any final Instance<ResourceTypeDefinition> typeRegister ) {
        this.ioSearchService = PortablePreconditions.checkNotNull( "ioSearchService",
                                                                   ioSearchService );
        this.ioService = PortablePreconditions.checkNotNull( "ioService",
                                                             ioService );
        this.organizationalUnitService = PortablePreconditions.checkNotNull( "organizationalUnitService",
                                                                             organizationalUnitService );
        this.projectService = PortablePreconditions.checkNotNull( "projectService",
                                                                  projectService );
        this.identity = PortablePreconditions.checkNotNull( "identity",
                                                            identity );
        this.authorizationManager = PortablePreconditions.checkNotNull( "authorizationManager",
                                                                        authorizationManager );
        this.typeRegister = PortablePreconditions.checkNotNull( "typeRegister",
                                                                typeRegister );
    }

    @PostConstruct
    void init() {
        for ( ResourceTypeDefinition activeType : typeRegister ) {
            types.put( activeType.getShortName().toLowerCase(), activeType );
        }
        emptyResponse = new PageResponse<SearchPageRow>();
        emptyResponse.setPageRowList( Collections.<SearchPageRow>emptyList() );
        emptyResponse.setStartRowIndex( 0 );
        emptyResponse.setTotalRowSize( 0 );
        emptyResponse.setLastPage( true );
        emptyResponse.setTotalRowSizeExact( true );
    }

    @Override
    public PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest pageRequest ) {
        try {
            //hits is an approximation at this stage, since we've not filtered by Authorised Project
            final int totalNumHitsEstimate = ioSearchService.fullTextSearchHits( pageRequest.getTerm(),
                                                                                 getAuthorizedRepositoryRoots() );
            if ( totalNumHitsEstimate > 0 ) {
                final PagedCountingFilter filter = new PagedCountingFilter( pageRequest.getStartRowIndex(),
                                                                            pageRequest.getPageSize() );
                final List<Path> pathResult = ioSearchService.fullTextSearch( pageRequest.getTerm(),
                                                                              filter,
                                                                              getAuthorizedRepositoryRoots() );
                return buildResponse( pathResult,
                                      pageRequest.getPageSize(),
                                      pageRequest.getStartRowIndex(),
                                      filter.getHitsTotalCount() );
            }
            return emptyResponse;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest pageRequest ) {
        try {
            final Map<String, Object> attrs = new HashMap<String, Object>( pageRequest.getMetadata() );

            if ( pageRequest.getCreatedAfter() != null || pageRequest.getCreatedBefore() != null ) {
                attrs.put( "creationTime", toDateRange( pageRequest.getCreatedBefore(),
                                                        pageRequest.getCreatedAfter() ) );
            }
            if ( pageRequest.getLastModifiedAfter() != null || pageRequest.getLastModifiedBefore() != null ) {
                attrs.put( "lastModifiedTime", toDateRange( pageRequest.getLastModifiedBefore(),
                                                            pageRequest.getLastModifiedAfter() ) );
            }

            //hits is an approximation at this stage, since we've not filtered by Authorised Project
            final int totalNumHitsEstimate = ioSearchService.searchByAttrsHits( attrs,
                                                                                getAuthorizedRepositoryRoots() );
            if ( totalNumHitsEstimate > 0 ) {
                final PagedCountingFilter filter = new PagedCountingFilter( pageRequest.getStartRowIndex(),
                                                                            pageRequest.getPageSize() );
                final List<Path> pathResult = ioSearchService.searchByAttrs( attrs,
                                                                             filter,
                                                                             getAuthorizedRepositoryRoots() );
                return buildResponse( pathResult,
                                      pageRequest.getPageSize(),
                                      pageRequest.getStartRowIndex(),
                                      filter.getHitsTotalCount() );
            }
            return emptyResponse;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private PageResponse<SearchPageRow> buildResponse( final List<Path> pathResult,
                                                       final int pageSize,
                                                       final int startRow,
                                                       final int hitsTotalCount ) {
        final List<SearchPageRow> result = new ArrayList<SearchPageRow>( pathResult.size() );
        for ( final Path path : pathResult ) {
            final DublinCoreView dcoreView = ioService.getFileAttributeView( path,
                                                                             DublinCoreView.class );
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( path,
                                                                                              VersionAttributeView.class );

            final String creator = extractCreator( versionAttributeView );
            final Date createdDate = extractCreatedDate( versionAttributeView );
            final String lastContributor = extractLastContributor( versionAttributeView );
            final Date lastModifiedDate = extractLastModifiedDate( versionAttributeView );
            final String description = extractDescription( dcoreView );

            final SearchPageRow row = new SearchPageRow( Paths.convert( path ),
                                                         creator,
                                                         createdDate,
                                                         lastContributor,
                                                         lastModifiedDate,
                                                         description );
            result.add( row );
        }

        final PageResponse<SearchPageRow> response = new PageResponse<SearchPageRow>();
        response.setTotalRowSize( hitsTotalCount );
        response.setPageRowList( result );
        response.setTotalRowSizeExact( true );
        response.setStartRowIndex( startRow );
        response.setLastPage( startRow > hitsTotalCount - pageSize );

        return response;
    }

    private String extractCreator( final VersionAttributeView versionAttributeView ) {
        if ( versionAttributeView.readAttributes().history().records().size() > 0 ) {
            return versionAttributeView.readAttributes().history().records().get( 0 ).author();
        }
        return "";
    }

    private Date extractCreatedDate( final VersionAttributeView versionAttributeView ) {
        return new Date( versionAttributeView.readAttributes().creationTime().toMillis() );
    }

    private String extractLastContributor( final VersionAttributeView versionAttributeView ) {
        if ( versionAttributeView.readAttributes().history().records().size() > 0 ) {
            final int lastIndex = versionAttributeView.readAttributes().history().records().size() - 1;
            return versionAttributeView.readAttributes().history().records().get( lastIndex ).author();
        }
        return "";
    }

    private Date extractLastModifiedDate( final VersionAttributeView versionAttributeView ) {
        return new Date( versionAttributeView.readAttributes().lastModifiedTime().toMillis() );
    }

    private String extractDescription( final DublinCoreView dcoreView ) {
        if ( dcoreView.readAttributes().descriptions().size() > 0 ) {
            return dcoreView.readAttributes().descriptions().get( 0 );
        }
        return "";
    }

    //Only search the Repositories for which the User has permission to access
    Path[] getAuthorizedRepositoryRoots() {
        //First get a collection of OU's to which the User has access
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Collection<OrganizationalUnit> authorizedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        for ( OrganizationalUnit ou : organizationalUnits ) {
            if ( authorizationManager.authorize( ou,
                                                 identity ) ) {
                authorizedOrganizationalUnits.add( ou );
            }
        }

        //Then check whether User has access to related Repositories
        final Set<Path> authorizedRoots = new HashSet<Path>();
        for ( OrganizationalUnit ou : authorizedOrganizationalUnits ) {
            final Collection<Repository> repositories = ou.getRepositories();
            for ( final Repository repository : repositories ) {
                if ( authorizationManager.authorize( repository,
                                                     identity ) ) {
                    authorizedRoots.add( Paths.convert( repository.getRoot() ) );
                }
            }
        }

        return authorizedRoots.toArray( new Path[ authorizedRoots.size() ] );
    }

    private DateRange toDateRange( final Date before,
                                   final Date after ) {
        return new DateRange() {
            @Override
            public Date before() {
                if ( before == null ) {
                    return new Date();
                }
                return before;
            }

            @Override
            public Date after() {
                if ( after == null ) {
                    return new Date( 0 );
                }
                return after;
            }
        };
    }

    class PagedCountingFilter implements IOSearchService.Filter {

        private int hitsStartIndex = -1;
        private int hitsPageCount = 0;
        private int hitsTotalCount = 0;

        private final int startRow;
        private final int pageSize;

        PagedCountingFilter( final int startRow,
                             final int pageSize ) {
            this.startRow = startRow;
            this.pageSize = pageSize;
        }

        @Override
        public boolean accept( final KObject kObject ) {
            final Path path = ioService.get( URI.create( kObject.getKey() ) );
            final org.uberfire.backend.vfs.Path vfsPath = Paths.convert( path );
            final KieProject project = projectService.resolveProject( vfsPath );

            //All Users are granted access to Resources outside the Project structure
            boolean authorized = true;
            if ( project != null ) {
                authorized = authorizationManager.authorize( project,
                                                             identity );
            }

            if ( authorized ) {
                hitsTotalCount++;
                hitsStartIndex++;
                if ( hitsStartIndex >= startRow && hitsPageCount < pageSize ) {
                    hitsPageCount++;
                    return true;
                }
            }
            return false;
        }

        public int getHitsTotalCount() {
            return hitsTotalCount;
        }

    }

}
