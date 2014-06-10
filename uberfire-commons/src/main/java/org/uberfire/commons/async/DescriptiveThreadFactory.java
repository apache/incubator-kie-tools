package org.uberfire.commons.async;

import java.util.concurrent.ThreadFactory;

public class DescriptiveThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread( final Runnable r ) {
        if ( r instanceof DescriptiveRunnable ) {
            return new Thread( r, ( (DescriptiveRunnable) r ).getDescription() );
        }
        return new Thread( r );
    }
}
