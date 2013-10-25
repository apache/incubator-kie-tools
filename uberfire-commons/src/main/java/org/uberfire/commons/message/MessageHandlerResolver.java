package org.uberfire.commons.message;

public interface MessageHandlerResolver {

    String getServiceId();

    public MessageHandler resolveHandler( final String serviceId,
                                          final MessageType type );

}
