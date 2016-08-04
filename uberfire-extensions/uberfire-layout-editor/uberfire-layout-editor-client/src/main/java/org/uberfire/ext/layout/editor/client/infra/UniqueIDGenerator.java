package org.uberfire.ext.layout.editor.client.infra;

import com.google.gwt.core.client.GWT;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIDGenerator {

    private AtomicLong counter = new AtomicLong();

    public String createContainerID() {
        return "container: " + String.valueOf( counter.getAndIncrement() );
    }

    public String createRowID( String containerID ) {
        return containerID + "|row: " + String.valueOf( counter.getAndIncrement() );
    }

    public String createColumnID( String rowID ) {
        return rowID + "|column: " + String.valueOf( counter.getAndIncrement() );
    }

}

