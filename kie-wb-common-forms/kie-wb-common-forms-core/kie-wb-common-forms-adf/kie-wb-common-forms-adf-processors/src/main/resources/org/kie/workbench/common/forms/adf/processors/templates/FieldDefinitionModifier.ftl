class ${fieldModifierName} implements FieldStatusModifier<${modelClassName}> {
        @Override
        public void modifyFieldStatus( FieldDefinition field, ${modelClassName} model ) {
            if ( model != null ) {
<#if label??>
                field.setLabel( model.${label}() );
</#if>
<#if helpMessage??>
                field.setHelpMessage( model.${helpMessage}() );
</#if>
<#if readOnly??>
                field.setReadOnly( Boolean.TRUE.equals( model.${readOnly}() ) );
</#if>
<#if required??>
                field.setRequired( Boolean.TRUE.equals( model.${required}() ) );
</#if>
            }
        }
    }
