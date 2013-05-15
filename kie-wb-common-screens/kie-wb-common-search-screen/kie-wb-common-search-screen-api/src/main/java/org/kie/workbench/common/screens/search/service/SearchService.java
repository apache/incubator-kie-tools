package org.kie.workbench.common.screens.search.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.search.model.SearchTermPageRequest;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.common.screens.search.model.SearchPageRow;
import org.uberfire.client.tables.PageResponse;

@Remote
public interface SearchService {

    PageResponse<SearchPageRow> fullTextSearch( final SearchTermPageRequest searchTerm );

    PageResponse<SearchPageRow> queryMetadata( final QueryMetadataPageRequest queryRequest );
}
