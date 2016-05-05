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

package org.uberfire.ext.security.management.client.widgets.management;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.form.error.BasicEditorError;
import org.gwtbootstrap3.client.ui.form.validator.Validator;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.ChangePasswordEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>Presenter class for change user's password widget.</p>
 */
@Dependent
public class ChangePassword implements IsWidget {

    public interface ChangePasswordCallback {
        void onPasswordUpdated();
        void onError(final Throwable throwable);
    }
    
    public interface View extends UberView<ChangePassword> {

        View configure(final Validator<String> newPasswordBoxValidator,
                       final Validator<String> repeatNewPasswordBoxValidator);
        View show(final String username);
        View hide();
        View clear();

    }

    public View view;
    ClientUserSystemManager userSystemManager;
    Event<NotificationEvent> workbenchNotification;
    Event<OnErrorEvent> errorEvent;
    Event<ChangePasswordEvent> changePasswordEvent;
    
    String username = null;
    ChangePasswordCallback callback;
    
    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API 
     ****************************************************************************************************** */

    @Inject
    public ChangePassword(final ClientUserSystemManager userSystemManager,
                          final Event<NotificationEvent> workbenchNotification, 
                          final Event<OnErrorEvent> errorEvent,
                          final Event<ChangePasswordEvent> changePasswordEvent,
                          final View view) {
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.workbenchNotification = workbenchNotification;
        this.changePasswordEvent = changePasswordEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        // Configure the view using the default not empty password validator.
        view.configure(passwordValidator, passwordValidator);
    }
    
    public void show(final String username) {
        // Clear current view.
        clear();
        this.username = username;
        this.callback = null;
        view.show(UsersManagementWidgetsConstants.INSTANCE.changePasswordFor() + " " + username);
    }
    
    public void show(final String username, final ChangePasswordCallback callback) {
        // Clear current view.
        clear();
        this.username = username;
        this.callback = callback;
        view.show(UsersManagementWidgetsConstants.INSTANCE.changePasswordFor() + " " + username);
    }

    public void clear() {
        username = null;
        callback = null;
        view.clear();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

     /*  ******************************************************************************************************
                                 PACKAGE PROTECTED METHODS FOR USING AS CALLBACKS FOR THE VIEW 
     ****************************************************************************************************** */

    boolean validatePasswordsMatch(final String p1, final String p2) {
        final boolean valid = p1 != null && p1.equals(p2);
        if (!valid) {
            showError(UsersManagementWidgetsConstants.INSTANCE.passwordsNotMatch());
        }
        return valid;
    } 
    
    void onUpdatePassword(final String newPassword, final Command callback) {
        // Call backend service.
        userSystemManager.users(new RemoteCallback<Void>() {
            @Override
            public void callback(final Void group) {
                // Fire event as password changed succesfully on backend side.
                changePasswordEvent.fire(new ChangePasswordEvent(this));

                // Show a notification.
                workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.passwordUpdatedSuccessfully(), SUCCESS));
                
                // Run the callback when backend request completed.
                if (callback != null) {
                    callback.execute();
                }
                if (ChangePassword.this.callback != null) {
                    ChangePassword.this.callback.onPasswordUpdated();
                }
                
                // Hide the view.
                view.hide();
                        
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                if (throwable != null) showError("[ERROR] ChangePassword - Throwable: " + throwable.getMessage());
                else showError("[ERROR] ChangePassword - Message: " + message.getSubject());
                
                // Run the callback when backend request completed.
                if (callback != null) {
                    callback.execute();
                }
                if (ChangePassword.this.callback != null) {
                    ChangePassword.this.callback.onError(throwable);
                }
                return false;
            }
        }).changePassword(username, newPassword);
        
    }
    
    
     /*  ******************************************************************************************************
                                     PRIVATE METHODS FOR INTERNAL PRESENTER LOGIC 
         ****************************************************************************************************** */

    /**
     * <p>A default not empty password validator.</p>
     */
    final Validator<String> passwordValidator = new Validator<String>() {
        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public List<EditorError> validate(Editor<String> editor, String value) {
            List<EditorError> result = new ArrayList<EditorError>();
            if (value == null || value.trim().length() == 0) {
                result.add(new BasicEditorError(editor, value, UsersManagementWidgetsConstants.INSTANCE.passwordCannotBeEmpty()));
            }
            return result;
        }
    };
;
    protected void showError(final String message) {
        errorEvent.fire(new OnErrorEvent(ChangePassword.this, message));
    }
    
}
