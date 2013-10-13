package org.uberfire.commons.message;

import java.util.Map;

import org.uberfire.commons.data.Pair;

public interface MessageHandler {

    Pair<MessageType, Map<String, String>> handleMessage( final MessageType type,
                                                          final Map<String, String> content );

}
