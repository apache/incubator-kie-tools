package org.uberfire.commons.message;

import java.util.Map;

public interface AsyncCallback {

    void onTimeOut();

    void onReply( final MessageType type,
                  final Map<String, String> content );

}
