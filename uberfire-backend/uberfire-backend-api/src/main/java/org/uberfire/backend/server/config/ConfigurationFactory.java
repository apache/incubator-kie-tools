package org.uberfire.backend.server.config;

public interface ConfigurationFactory {

    ConfigGroup newConfigGroup( ConfigType type,
                                String name,
                                String description );

    ConfigItem<String> newConfigItem( String name,
                                      String valueType );

    ConfigItem<Boolean> newConfigItem( String name,
                                       boolean valueType );

    ConfigItem<String> newSecuredConfigItem( String name,
                                             String valueType );
}
