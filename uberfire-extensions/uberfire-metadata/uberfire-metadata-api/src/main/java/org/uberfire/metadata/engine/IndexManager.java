package org.uberfire.metadata.engine;

import org.uberfire.metadata.model.KCluster;
import org.uberfire.metadata.model.KObjectKey;

public interface IndexManager {

    boolean contains( final KCluster cluster );

    Index indexOf( final KObjectKey object );

    KCluster kcluster( final KObjectKey object );

    void delete( final KCluster cluster );

    void dispose();

    Index get( final KCluster cluster );

}
