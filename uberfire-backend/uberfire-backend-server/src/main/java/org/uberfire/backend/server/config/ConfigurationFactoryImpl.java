package org.uberfire.backend.server.config;

import javax.inject.Inject;

public class ConfigurationFactoryImpl implements ConfigurationFactory {

    @Inject
    private PasswordService secureService;

    @Override
    public ConfigGroup newConfigGroup( final ConfigType type,
                                       final String name,
                                       final String description ) {
        final ConfigGroup configGroup = new ConfigGroup();
        configGroup.setDescription( description );
        configGroup.setName( name );
        configGroup.setType( type );
        configGroup.setEnabled( true );
        return configGroup;
    }

    @Override
    public ConfigItem<String> newConfigItem( final String name,
                                             final String valueType ) {
        final ConfigItem<String> stringConfigItem = new ConfigItem<String>();
        stringConfigItem.setName( name );
        stringConfigItem.setValue( valueType );
        return stringConfigItem;
    }

    @Override
    public ConfigItem<Boolean> newConfigItem( final String name,
                                              final boolean valueType ) {
        final ConfigItem<Boolean> stringConfigItem = new ConfigItem<Boolean>();
        stringConfigItem.setName( name );
        stringConfigItem.setValue( valueType );
        return stringConfigItem;
    }

    @Override
    public ConfigItem<String> newSecuredConfigItem( final String name,
                                                    final String valueType ) {
        final ConfigItem<String> stringConfigItem = new ConfigItem<String>();
        stringConfigItem.setName( name );
        stringConfigItem.setValue( secureService.encrypt( valueType ) );
        return stringConfigItem;
    }
}
