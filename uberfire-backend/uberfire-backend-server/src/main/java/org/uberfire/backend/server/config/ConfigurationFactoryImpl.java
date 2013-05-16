package org.uberfire.backend.server.config;

import java.util.List;
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
        final ConfigItem<Boolean> booleanConfigItem = new ConfigItem<Boolean>();
        booleanConfigItem.setName( name );
        booleanConfigItem.setValue( valueType );
        return booleanConfigItem;
    }

    @Override
    public SecureConfigItem newSecuredConfigItem( final String name,
                                                  final String valueType ) {
        final SecureConfigItem stringConfigItem = new SecureConfigItem();
        if ( name.startsWith( "crypt:" ) ) {
            stringConfigItem.setName( name.substring( "crypt:".length() ) );
        } else {
            stringConfigItem.setName( name );
        }
        stringConfigItem.setValue( secureService.encrypt( valueType ) );
        return stringConfigItem;
    }

    @Override
    public ConfigItem<List> newConfigItem( String name,
                                           List valueType ) {
        final ConfigItem<List> listConfigItem = new ConfigItem<List>();
        listConfigItem.setName( name );
        listConfigItem.setValue( valueType );
        return listConfigItem;
    }

    @Override
    public ConfigItem<Object> newConfigItem( String name,
                                             Object valueType ) {
        final ConfigItem<Object> listConfigItem = new ConfigItem<Object>();
        listConfigItem.setName( name );
        listConfigItem.setValue( valueType );
        return listConfigItem;
    }
}
