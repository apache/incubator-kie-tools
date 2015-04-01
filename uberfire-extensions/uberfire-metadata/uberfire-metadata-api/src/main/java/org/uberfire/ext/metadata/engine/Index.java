package org.uberfire.ext.metadata.engine;

import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.ext.metadata.model.KCluster;

public interface Index extends Disposable {

    KCluster getCluster();

    boolean freshIndex();

    void commit();

    void delete();

}
