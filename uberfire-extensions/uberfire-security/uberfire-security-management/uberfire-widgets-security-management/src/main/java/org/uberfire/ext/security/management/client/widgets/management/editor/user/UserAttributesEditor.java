/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.UpdateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Presenter class for user editor attributes widget.</p>
 */
@Dependent
public class UserAttributesEditor implements IsWidget, org.uberfire.ext.security.management.client.editor.user.UserAttributesEditor {

    public interface View extends UberView<UserAttributesEditor> {

        View initWidgets(NewUserAttributeEditor.View newUserAttributeEditorView);
        View setCanCreate(boolean isCreateAllowed);
        View setColumnSortHandler(ColumnSortEvent.ListHandler<Map.Entry<String, String>> sortHandler);
        View addColumn(com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> column,
                       String name);
        View removeColumn(int index);
        int getColumnCount();
        View showEmpty();
        View redraw();
    }

    ClientUserSystemManager userSystemManager;
    ConfirmBox confirmBox;
    Event<CreateUserAttributeEvent> createUserAttributeEventEvent;
    Event<UpdateUserAttributeEvent> updateUserAttributeEventEvent;
    Event<DeleteUserAttributeEvent> deleteUserAttributeEventEvent;
    Event<OnErrorEvent> errorEvent;
    NewUserAttributeEditor newUserAttributeEditor;
    public View view;

    /**
     * The provider that holds the list of containers.
     */
    final ListDataProvider<Entry<String, String>> userAttributesProvider = new ListDataProvider<Entry<String, String>>();
    Map<UserManager.UserAttribute, String> attributes;
    boolean isEditMode;
    
    @Inject
    public UserAttributesEditor(final ClientUserSystemManager userSystemManager, 
                                final ConfirmBox confirmBox,
                                final Event<CreateUserAttributeEvent> createUserAttributeEventEvent,
                                final Event<UpdateUserAttributeEvent> updateUserAttributeEventEvent,
                                final Event<DeleteUserAttributeEvent> deleteUserAttributeEventEvent,
                                final Event<OnErrorEvent> errorEvent,
                                final NewUserAttributeEditor newUserAttributeEditor,
                                final View view) {
        this.userSystemManager = userSystemManager;
        this.confirmBox = confirmBox;
        this.createUserAttributeEventEvent = createUserAttributeEventEvent;
        this.updateUserAttributeEventEvent = updateUserAttributeEventEvent;
        this.deleteUserAttributeEventEvent = deleteUserAttributeEventEvent;
        this.errorEvent = errorEvent;
        this.newUserAttributeEditor = newUserAttributeEditor;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(newUserAttributeEditor.view);
        buildViewColumns();
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */
    
    @Override
    public void show(final User user) {
        this.isEditMode = false;
        open(user);
    }

    @Override
    public void edit(final User user) {
        this.isEditMode = true;
        open(user);
    }

    @Override
    public void flush() {
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    @Override
    public Map<String, String> getValue() {
        final Map<String, String> result = new HashMap<String, String>(attributes.size());
        for (Map.Entry<UserManager.UserAttribute, String> entry : attributes.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

    @Override
    public void setViolations(Set<ConstraintViolation<User>> constraintViolations) {
        //  Currently no violations expected.
    }

    public void clear() {
        userAttributesProvider.getList().clear();
        attributes = null;
        isEditMode = false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */
    
    /**
     * View callback for getting the list.
     */
    void addDataDisplay(HasData<Entry<String, String>> display) {
        userAttributesProvider.addDataDisplay(display);
    }

    final ProvidesKey<Entry<String, String>> KEY_PROVIDER = new ProvidesKey<Map.Entry<String, String>>() {
        @Override
        public Object getKey(final Map.Entry<String, String> item) {
            return item == null ? null : item.getKey();
        }
    };



    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    protected void loadUserAttributes(final User user) {
        final Map<String, String> properties = user.getProperties();
        if (properties != null) {
            final Map<UserManager.UserAttribute, String> _properties = new HashMap<UserManager.UserAttribute, String>(properties.size());
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String name = entry.getKey();
                UserManager.UserAttribute attribute = getSupportedAttribute(name);
                final String value = entry.getValue();
                if (attribute != null) {
                    _properties.put(attribute, value);
                }

            }
            this.attributes = _properties;
        }
    }
    
    protected UserManager.UserAttribute getSupportedAttribute(final String name) {
        UserManager.UserAttribute attribute = userSystemManager.getUserSupportedAttribute(name);
        if (attribute == null) {
            attribute = userSystemManager.createUserAttribute(name, false, true, null);
        }
        return attribute;
    }
    
    protected void open(final User user) {

        // User attributes editor settings.
        newUserAttributeEditor.clear().showAddButton();
        view.setCanCreate(canManageAttributes());
        
        // User load.
        if (user != null) {
            loadUserAttributes(user);
            redraw();
        } 
    }
    
    protected void redraw() {
        userAttributesProvider.getList().clear();
        buildViewColumns();
        if (attributes != null) {
            for (final Map.Entry<UserManager.UserAttribute, String> entry : attributes.entrySet()) {
                final UserManager.UserAttribute attribute = entry.getKey();
                final String value = entry.getValue();
                final Map.Entry<String, String> attrEntry = createAttributeEntry(attribute.getName(), value);
                addAttributeEntry(attrEntry);
            }
            view.redraw();
        } else {
            view.showEmpty();
        }
    }

    protected void addAttributeEntry(final Map.Entry<String, String> entry) {
        List<Map.Entry<String, String>> contacts = userAttributesProvider.getList();
        contacts.remove(entry);
        contacts.add(entry);
    }
    
    boolean canManageAttributes() {
        if (!isEditMode) return false;
        final boolean canManageAttrs = userSystemManager.isUserCapabilityEnabled(Capability.CAN_MANAGE_ATTRIBUTES);
        return canManageAttrs;
    }
    
    protected void buildViewColumns() {
        int columnCount = view.getColumnCount();
        while (columnCount > 0) {
            view.removeColumn(0);
            columnCount = view.getColumnCount();
        }

        // Attach a column sort handler to the ListDataProvider to sort the list.
        ColumnSortEvent.ListHandler<Map.Entry<String, String>> sortHandler = new ColumnSortEvent.ListHandler<Map.Entry<String, String>>(userAttributesProvider.getList());
        view.setColumnSortHandler(sortHandler);

        // Attribute name.
        final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> keyColumn = createAttributeNameColumn(sortHandler);
        if (keyColumn != null) {
            view.addColumn(keyColumn, UsersManagementWidgetsConstants.INSTANCE.name());
        }

        // Attribute value.
        final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> valueColumn = createAttributeValueColumn(sortHandler);
        if (valueColumn != null) {
            view.addColumn(valueColumn, UsersManagementWidgetsConstants.INSTANCE.value());
        }

        // Create remove button column.
        final com.google.gwt.user.cellview.client.Column<Map.Entry<String, String>, String> removeColumn = createAttributeRemoveColumn();
        if (removeColumn != null) {
            view.addColumn(removeColumn, UsersManagementWidgetsConstants.INSTANCE.remove());
        }
    }

    protected com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> createAttributeNameColumn(ColumnSortEvent.ListHandler<Entry<String, String>> sortHandler) {
        // Attribute name.
        final Cell<String> nameCell = canManageAttributes() ? new EditTextCell() : new TextCell();
        final com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> keyColumn = new com.google.gwt.user.cellview.client.Column<Entry<String, String>, String>(
                nameCell) {
            @Override
            public String getValue(final Entry<String, String> object) {
                return object.getKey() != null ? object.getKey() : "";
            }
        };
        keyColumn.setSortable(true);
        sortHandler.setComparator(keyColumn, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        keyColumn.setFieldUpdater(canManageAttributes() ? keyModifiedEventHandler : null);

        return keyColumn;
    }

    protected com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> createAttributeValueColumn(ColumnSortEvent.ListHandler<Entry<String, String>> sortHandler) {
        // Attribute value.
        final Cell<String> valueCell = canManageAttributes() ? new EditTextCell() : new TextCell();
        final com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> valueColumn = new com.google.gwt.user.cellview.client.Column<Entry<String, String>, String>(
                valueCell) {
            @Override
            public String getValue(final Entry<String, String> object) {
                return object.getValue() != null ? object.getValue() : "";
            }
        };
        valueColumn.setSortable(true);
        sortHandler.setComparator(valueColumn, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        valueColumn.setFieldUpdater(canManageAttributes() ? valueModifiedEventHandler : null);

        return valueColumn;
    }

    protected com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> createAttributeRemoveColumn() {
        // On read mode, remove button not present.
        if (!canManageAttributes()) return null;

        // Create remove button column.
        final ButtonCell removeButtonCell = new ButtonCell(IconType.CLOSE, ButtonType.LINK, ButtonSize.SMALL);
        final com.google.gwt.user.cellview.client.Column<Entry<String, String>, String> removeColumn =
                new com.google.gwt.user.cellview.client.Column<Entry<String, String>, String>(removeButtonCell) {

                    @Override
                    public String getValue(final Entry<String, String> object) {
                        // if can be removed return empty string, if not, return null
                        if (object != null) {
                            final UserManager.UserAttribute attribute = getAttribute(object.getKey());
                            if (attribute != null && !attribute.isMandatory() && attribute.isEditable()) {
                                removeButtonCell.setEnabled(true);
                                return "";
                            }
                        }
                        removeButtonCell.setEnabled(false);
                        return null;
                    }
                };

        removeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        removeColumn.setFieldUpdater(canManageAttributes() ? removeButtonHandler : null);

        return removeColumn;
    }

    private UserManager.UserAttribute getAttribute(final String name) {
        if (!isEmpty(name) && attributes != null && !attributes.isEmpty()) {
            for (final Map.Entry<UserManager.UserAttribute, String> entry : attributes.entrySet()) {
                final UserManager.UserAttribute attribute = entry.getKey();
                if (name.equals(attribute.getName())) return attribute;
            }
        }
        return null;
    }
    
    private Map.Entry<String, String> createAttributeEntry(final String key, final String value) {
        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
    }
    
    
    private final FieldUpdater keyModifiedEventHandler = new FieldUpdater<Entry<String, String>, String>() {
        @Override
        public void update(final int index, final Entry<String, String> object, final String value) {
            final String k = object != null ? object.getKey() : null;
            if (hasValueChanged(k, value)) {
                updateUserAttribute(index, value, object != null ? object.getValue() : null);
            }
        }
    };

    private final FieldUpdater valueModifiedEventHandler = new FieldUpdater<Entry<String, String>, String>() {
        @Override
        public void update(final int index, final Entry<String, String> object, final String value) {
            final String k = object != null ? object.getValue() : null;
            if (hasValueChanged(k, value)) {
                updateUserAttribute(index, object != null ? object.getKey() : null, value);
            }
        }
    };

    private final FieldUpdater removeButtonHandler = new FieldUpdater<Entry<String, String>, String>() {
        @Override
        public void update(final int index, final Entry<String, String> object, final String value) {
            if (value == null) {
                // Attribute is mandatory and cannot be removed.
                errorEvent.fire(new OnErrorEvent(UserAttributesEditor.this, UsersManagementWidgetsConstants.INSTANCE.attributeIsMandatory()));
            } else {
                confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.ensureRemoveAttribute(),
                        new Command() {
                            @Override
                            public void execute() {
                                removeUserAttribute(index, object);
                            }
                        });
            }
        }
    };

    private boolean hasValueChanged(final String s1, final String s2) {
        if (s1 != null && !s1.equals(s2)) return true;
        if (s2 != null && !s2.equals(s1)) return true;
        return false;
    }

    private boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }

    void updateUserAttribute(final int index, final String key, final String value) {
        final Entry<String, String> attr = createAttributeEntry(key, value);
        UserManager.UserAttribute attribute = getAttribute(key);
        attributes.put(attribute, value);
        redraw();
        updateUserAttributeEventEvent.fire(new UpdateUserAttributeEvent(UserAttributesEditor.this, attr));
    }

    void removeUserAttribute(final int index, final Entry<String ,String> entry) {
        UserManager.UserAttribute attribute = getAttribute(entry.getKey());
        attributes.remove(attribute);
        redraw();
        deleteUserAttributeEventEvent.fire(new DeleteUserAttributeEvent(UserAttributesEditor.this, entry));
    }

    void onAttributeCreated(@Observes final CreateUserAttributeEvent createUserAttributeEvent) {
        final Entry<String, String> entry = createUserAttributeEvent.getAttribute();
        final String name = entry.getKey();
        final String value = entry.getValue();
        UserManager.UserAttribute attribute = getSupportedAttribute(name);
        attributes.put(attribute, value);
        redraw();
    }
    
}
