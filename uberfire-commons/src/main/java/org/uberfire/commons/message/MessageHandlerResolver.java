package org.uberfire.commons.message;

public interface MessageHandlerResolver {

    public MessageHandler resolveHandler( final MessageType type );

}
