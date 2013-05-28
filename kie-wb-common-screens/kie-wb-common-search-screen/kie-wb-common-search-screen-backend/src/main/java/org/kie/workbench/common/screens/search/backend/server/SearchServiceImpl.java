package org.kie.workbench.common.screens.search.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOSearchService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.commons.java.nio.file.Path;
import org.kie.kieora.search.DateRange;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.common.screens.search.model.SearchPageRow;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.screens.search.service.SearchService;
import org.kie.workbench.common.services.backend.metadata.attribute.OtherMetaView;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Service

@ApplicationScoped
public class SearchServiceImpl implements SearchService {

    @Inject
    @Named("ioSearchStrategy")
    private IOSearchService ioSearchService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    private PageResponse<SearchPageRow> emptyResponse = null;

    @Inject
    @Any
    private Instance<ResourceTypeDefinition> typeRegister;

    private Map<String, ResourceTypeDefinition> types = new HashMap<String, ResourceTypeDefinition>();

    @PostConstruct
    private void init() {
        for ( ResourceTypeDefinition actviveType : typeRegister ) {
            types.put( actviveType.getShortName().toLowerCase(), actviveType );
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
        final int hits = ioSearchService.fullTextSearchHits( pageRequest.getTerm(), roots() );
        if ( hits > 0 ) {
            final List<Path> pathResult = ioSearchService.fullTextSearch( pageRequest.getTerm(), pageRequest.getPageSize(), pageRequest.getStartRowIndex(), roots() );
            return buildResponse( pathResult, hits, pageRequest.getPageSize(), pageRequest.getStartRowIndex() );
        }
        return emptyResponse;
    }

    @Override
    public PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest pageRequest ) {

        final Map<String, Object> attrs = new HashMap<String, Object>( pageRequest.getMetadata() );

        if ( pageRequest.getCreatedAfter() != null || pageRequest.getCreatedBefore() != null ) {
            attrs.put( "creationTime", toDateRange( pageRequest.getCreatedBefore(), pageRequest.getCreatedAfter() ) );
        }
        if ( pageRequest.getLastModifiedAfter() != null || pageRequest.getLastModifiedBefore() != null ) {
            attrs.put( "lastModifiedTime", toDateRange( pageRequest.getLastModifiedBefore(), pageRequest.getLastModifiedAfter() ) );
        }

        final int hits = ioSearchService.searchByAttrsHits( attrs, roots() );
        if ( hits > 0 ) {
            final List<Path> pathResult = ioSearchService.searchByAttrs( attrs, pageRequest.getPageSize(), pageRequest.getStartRowIndex(), roots() );
            return buildResponse( pathResult, hits, pageRequest.getPageSize(), pageRequest.getStartRowIndex() );
        }
        return emptyResponse;
    }

    private PageResponse<SearchPageRow> buildResponse( final List<Path> pathResult,
                                                       final int hits,
                                                       final int pageSize,
                                                       final int startRow ) {
        final List<SearchPageRow> result = new ArrayList<SearchPageRow>( pathResult.size() );
        for ( final Path path : pathResult ) {
            final SearchPageRow row = new SearchPageRow( paths.convert( path, false ) );

            final DublinCoreView dcoreView = ioService.getFileAttributeView( path, DublinCoreView.class );
            final OtherMetaView otherMetaView = ioService.getFileAttributeView( path, OtherMetaView.class );
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( path, VersionAttributeView.class );

            row.setCreator( versionAttributeView.readAttributes().history().records().size() > 0 ? versionAttributeView.readAttributes().history().records().get( 0 ).author() : "" );
            row.setLastContributor( versionAttributeView.readAttributes().history().records().size() > 0 ? versionAttributeView.readAttributes().history().records().get( versionAttributeView.readAttributes().history().records().size() - 1 ).author() : "" );
            row.setLastModified( new Date( versionAttributeView.readAttributes().lastModifiedTime().toMillis() ) );
            row.setCreatedDate( new Date( versionAttributeView.readAttributes().creationTime().toMillis() ) );
            row.setDescription( dcoreView.readAttributes().descriptions().size() > 0 ? dcoreView.readAttributes().descriptions().get( 0 ) : "" );
            result.add( row );
        }

        final PageResponse<SearchPageRow> response = new PageResponse<SearchPageRow>();
        response.setTotalRowSize( hits );
        response.setPageRowList( result );
        response.setTotalRowSizeExact( true );
        response.setStartRowIndex( startRow );
        response.setLastPage( ( pageSize * startRow + 2 ) >= hits );

        return response;
    }

    private Path[] roots() {
        final Collection<Repository> repos = repositoryService.getRepositories();
        final Path[] roots = new Path[ repos.size() ];
        int i = 0;
        for ( final Repository repo : repos ) {
            roots[ i ] = paths.convert( repo.getRoot() );
            i++;
        }
        return roots;
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

}
