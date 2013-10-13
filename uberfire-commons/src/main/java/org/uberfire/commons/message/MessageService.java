package org.uberfire.commons.message;

import java.util.Map;

public interface MessageService {

    void broadcastAndWait( final MessageType type,
                           final Map<String, String> content,
                           final int timeOut );

    void broadcastAndWait( final MessageType type,
                           final Map<String, String> content,
                           final int timeOut,
                           final AsyncCallback callback );

    void broadcast( final MessageType type,
                    final Map<String, String> content );

    void broadcast( final MessageType type,
                    final Map<String, String> content,
                    final int timeOut,
                    final AsyncCallback callback );

    void sendTo( final String resourceId,
                 final MessageType type,
                 final Map<String, String> content );

}
