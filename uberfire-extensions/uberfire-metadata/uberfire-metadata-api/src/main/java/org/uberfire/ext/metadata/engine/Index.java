package org.uberfire.ext.metadata.engine;

import org.uberfire.ext.metadata.model.KCluster;

public interface Index {

    KCluster getCluster();

    void dispose();

    boolean freshIndex();

    void commit();

    void delete();

}
