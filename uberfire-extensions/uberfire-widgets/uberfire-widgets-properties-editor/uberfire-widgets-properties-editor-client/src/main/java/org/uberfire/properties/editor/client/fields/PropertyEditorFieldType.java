package org.uberfire.properties.editor.client.fields;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.properties.editor.model.PropertyEditorFieldInfo;

public enum PropertyEditorFieldType {

    TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, TextField.class );
        }

    }, BOOLEAN {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, BooleanField.class );
        }

    }, NATURAL_NUMBER {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return TEXT.widget( property );
        }

    }, COMBO {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, ComboField.class );
        }

    }, SECRET_TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, SecretTextField.class );
        }
    };

    private static Widget getWidget( PropertyEditorFieldInfo property,
                                     Class fieldType ) {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef iocBeanDef = beanManager.lookupBean( fieldType );
        AbstractField field = (AbstractField) iocBeanDef.getInstance();
        return field.widget( property );
    }

    public abstract Widget widget( PropertyEditorFieldInfo property );

    public static PropertyEditorFieldType getFieldTypeFrom( PropertyEditorFieldInfo fieldInfo ) {
        return PropertyEditorFieldType.valueOf( fieldInfo.getType().name() );
    }
}
