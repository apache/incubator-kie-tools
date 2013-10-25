package org.uberfire.commons.message;

import java.util.Map;

public interface MessageService {

    void broadcastAndWait( final String serviceId,
                           final MessageType type,
                           final Map<String, String> content,
                           final int timeOut );

    void broadcastAndWait( final String serviceId,
                           final MessageType type,
                           final Map<String, String> content,
                           final int timeOut,
                           final AsyncCallback callback );

    void broadcast( final String serviceId,
                    final MessageType type,
                    final Map<String, String> content );

    void broadcast( final String serviceId,
                    final MessageType type,
                    final Map<String, String> content,
                    final int timeOut,
                    final AsyncCallback callback );

    void sendTo( final String resourceId,
                 final String serviceId,
                 final MessageType type,
                 final Map<String, String> content );

}
