package org.kie.workbench.screens.search.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.screens.search.model.SearchPageRow;
import org.kie.workbench.screens.search.model.SearchTermPageRequest;
import org.uberfire.client.tables.PageResponse;

@Remote
public interface SearchService {

    PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest searchTerm );

    PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest queryRequest );
}
