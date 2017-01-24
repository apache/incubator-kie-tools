class ${builderClassName} {
        public FormDefinitionSettings getSettings() {
            FormDefinitionSettings settings = new FormDefinitionSettings( "${modelClass}" );
            settings.setI18nSettings( new I18nSettings( "${i18n_bundle}", "${i18n_keyPreffix}", "${i18n_separator}" ) );
            settings.setLayout( new LayoutDefinition( new LayoutColumnDefinition[] { <#list layout_columns as column><#if column_index != 0>, </#if>new LayoutColumnDefinition( ColSpan.${column} )</#list> } ) );
            List<FormElement> elements = new ArrayList<FormElement>();
        <#list elements as element>
            elements.add( ${element.methodName}() );
        </#list>
            settings.getFormElements().addAll( elements );
            return settings;
        }

<#list elements as element>
${element.method}
</#list>
    }
