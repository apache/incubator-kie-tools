        private FormElement ${methodName}() {
            FieldElement field = new FieldElement( "${fieldName}", "${binding}", new TypeInfoImpl( TypeKind.${type}, "${className}", ${isList}) );
            field.setPreferredType( ${preferredType}.class );
            field.setLabelKey( "${labelKey}" );
            field.setHelpMessageKey("${helpMessageKey}");
            field.setRequired( ${required} );
            field.setReadOnly( ${readOnly} );
        <#list params?keys as param>
            field.getParams().put( "${param}", "${params[param]}" );
        </#list>
            field.getLayoutSettings().setAfterElement( "${afterElement}" );
            field.getLayoutSettings().setHorizontalSpan( ${horizontalSpan} );
            field.getLayoutSettings().setVerticalSpan( ${verticalSpan} );
            field.getLayoutSettings().setWrap( ${wrap} );
        <#if fieldModifier != "">
            fieldStatusModifiersReferences.put( "${formModel}.${fieldName}", "${fieldModifier}" );
        </#if>
            return field;
        }
