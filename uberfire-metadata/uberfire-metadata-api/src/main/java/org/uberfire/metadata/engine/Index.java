package org.uberfire.metadata.engine;

import org.uberfire.metadata.model.KCluster;

public interface Index {

    KCluster getCluster();

    void dispose();

    boolean freshIndex();

    void commit();

    void delete();

}
