package org.uberfire.ext.metadata.engine;

import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObjectKey;

public interface IndexManager extends Disposable {

    boolean contains( final KCluster cluster );

    Index indexOf( final KObjectKey object );

    KCluster kcluster( final KObjectKey object );

    void delete( final KCluster cluster );

    Index get( final KCluster cluster );

}
