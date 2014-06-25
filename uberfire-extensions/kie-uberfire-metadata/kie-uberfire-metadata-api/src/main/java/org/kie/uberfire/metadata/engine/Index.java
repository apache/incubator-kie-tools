package org.kie.uberfire.metadata.engine;

import org.kie.uberfire.metadata.model.KCluster;

public interface Index {

    KCluster getCluster();

    void dispose();

    boolean freshIndex();

    void commit();

    void delete();

}
