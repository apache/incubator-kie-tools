/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.properties.editor.client.fields;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

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
    }, COLOR {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, ColorField.class );
        }
    }, CUSTOM {
        @Override public Widget widget( PropertyEditorFieldInfo property ) {
            return null;
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