package org.uberfire.io.impl;

import org.uberfire.io.lock.BatchLockControl;

public interface IOServiceLockable extends IOServiceIdentifiable {

    BatchLockControl getLockControl();

}
