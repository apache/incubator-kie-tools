package org.uberfire.backend.server.config;

import java.util.List;

public interface ConfigurationFactory {

    ConfigGroup newConfigGroup( ConfigType type,
                                String name,
                                String description );

    ConfigItem<String> newConfigItem( String name,
                                      String valueType );

    ConfigItem<Boolean> newConfigItem( String name,
                                       boolean valueType );

    SecureConfigItem newSecuredConfigItem( String name,
                                           String valueType );

    ConfigItem<List> newConfigItem( String name,
                                    List valueType );

    ConfigItem<Object> newConfigItem( String name,
                                      Object valueType );
}
