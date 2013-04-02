package org.uberfire.backend.server.config;

import com.thoughtworks.xstream.XStream;

/**
 * Marshall a ConfigGroup to and from XML
 */
public class ConfigGroupMarshaller {

    private final XStream xstream = new XStream();

    public ConfigGroupMarshaller() {
        xstream.alias( "group",
                       ConfigGroup.class );
        xstream.alias( "item",
                       ConfigItem.class );
        xstream.alias( "type",
                       ConfigType.class );
    }

    public String marshall( final ConfigGroup configGroup ) {
        return xstream.toXML( configGroup );
    }

    public ConfigGroup unmarshall( final String xml ) {
        return (ConfigGroup) xstream.fromXML( xml );
    }

}
