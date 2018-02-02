/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.client.validation.ClientValidationService;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DataSourceDefEditorHelper {

    private TranslationService translationService;

    private Caller<DataSourceDefEditorService> editorService;

    private Caller<DataSourceDefQueryService> queryService;

    private ClientValidationService validationService;

    private PopupsUtil popupsUtil;

    private DataSourceDef dataSourceDef;

    private DataSourceDefMainPanel mainPanel;

    private Map<String, DriverDefInfo> driverDefMap = new HashMap<>();

    private DataSourceDefMainPanelView.Handler handler;

    private Module module;

    private boolean nameValid = false;
    private boolean connectionURLValid = false;
    private boolean userValid = false;
    private boolean passwordValid = false;
    private boolean driverValid = false;

    @Inject
    public DataSourceDefEditorHelper(final TranslationService translationService,
                                     final Caller<DataSourceDefEditorService> editorService,
                                     final Caller<DataSourceDefQueryService> queryService,
                                     final ClientValidationService validationService,
                                     final PopupsUtil popupsUtil) {
        this.translationService = translationService;
        this.editorService = editorService;
        this.queryService = queryService;
        this.validationService = validationService;
        this.popupsUtil = popupsUtil;
    }

    public void init(final DataSourceDefMainPanel mainPanel) {
        this.mainPanel = mainPanel;

        mainPanel.setHandler(new DataSourceDefMainPanelView.Handler() {
            @Override
            public void onNameChange() {
                DataSourceDefEditorHelper.this.onNameChange();
            }

            @Override
            public void onConnectionURLChange() {
                DataSourceDefEditorHelper.this.onConnectionURLChange();
            }

            @Override
            public void onUserChange() {
                DataSourceDefEditorHelper.this.onUserChange();
            }

            @Override
            public void onPasswordChange() {
                DataSourceDefEditorHelper.this.onPasswordChange();
            }

            @Override
            public void onDriverChange() {
                DataSourceDefEditorHelper.this.onDriverChange();
            }

            @Override
            public void onTestConnection() {
                DataSourceDefEditorHelper.this.onTestConnection();
            }
        });
    }

    public void setDataSourceDef(final DataSourceDef dataSourceDef) {
        this.dataSourceDef = dataSourceDef;
        mainPanel.clear();
        mainPanel.setName(dataSourceDef.getName());
        mainPanel.setConnectionURL(dataSourceDef.getConnectionURL());
        mainPanel.setUser(dataSourceDef.getUser());
        mainPanel.setPassword(dataSourceDef.getPassword());
        mainPanel.setDriver(dataSourceDef.getDriverUuid());
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    public void setHandler(final DataSourceDefMainPanelView.Handler handler) {
        this.handler = handler;
    }

    public void loadDrivers(final Command onSuccessCommand,
                            final ParameterizedCommand<Throwable> onErrorCommand) {
        if (module == null) {
            queryService.call(
                    getLoadDriversSuccessCallback(onSuccessCommand),
                    getLoadDriversErrorCallback(onErrorCommand)).findGlobalDrivers();
        } else {
            queryService.call(
                    getLoadDriversSuccessCallback(onSuccessCommand),
                    getLoadDriversErrorCallback(onErrorCommand)).findModuleDrivers(module.getRootPath());
        }
    }

    private RemoteCallback<List<DriverDefInfo>> getLoadDriversSuccessCallback(final Command onSuccessCommand) {
        return new RemoteCallback<List<DriverDefInfo>>() {
            @Override
            public void callback(List<DriverDefInfo> response) {
                mainPanel.loadDriverOptions(buildDriverOptions(response),
                                            true);
                onSuccessCommand.execute();
            }
        };
    }

    private ErrorCallback<?> getLoadDriversErrorCallback(final ParameterizedCommand<Throwable> onErrorCommand) {
        return new ErrorCallback<Object>() {
            @Override
            public boolean error(Object o,
                                 Throwable throwable) {
                onErrorCommand.execute(throwable);
                return false;
            }
        };
    }

    private List<Pair<String, String>> buildDriverOptions(final List<DriverDefInfo> driverDefs) {
        List<Pair<String, String>> options = new ArrayList<>();
        driverDefMap.clear();
        for (DriverDefInfo driverDef : driverDefs) {
            options.add(new Pair<>(driverDef.getName(),
                                   driverDef.getUuid()));
            driverDefMap.put(driverDef.getUuid(),
                             driverDef);
        }
        return options;
    }

    public void onNameChange() {
        final String newValue = mainPanel.getName().trim();
        validationService.isValidDataSourceName(newValue,
                                                new ValidatorCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        onNameChange(newValue,
                                                                     true);
                                                    }

                                                    @Override
                                                    public void onFailure() {
                                                        onNameChange(newValue,
                                                                     false);
                                                    }
                                                });
    }

    private void onNameChange(String newValue,
                              boolean isValid) {
        dataSourceDef.setName(newValue);
        nameValid = isValid;
        if (!nameValid) {
            mainPanel.setNameErrorMessage(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_InvalidNameMessage));
        } else {
            mainPanel.clearNameErrorMessage();
        }
        if (handler != null) {
            handler.onNameChange();
        }
    }

    public void onConnectionURLChange() {
        final String newValue = mainPanel.getConnectionURL().trim();
        validationService.isValidConnectionURL(newValue,
                                               new ValidatorCallback() {
                                                   @Override
                                                   public void onSuccess() {
                                                       onConnectionURLChange(newValue,
                                                                             true);
                                                   }

                                                   @Override
                                                   public void onFailure() {
                                                       onConnectionURLChange(newValue,
                                                                             false);
                                                   }
                                               });
    }

    private void onConnectionURLChange(String newValue,
                                       boolean isValid) {
        dataSourceDef.setConnectionURL(newValue);
        connectionURLValid = isValid;
        if (!connectionURLValid) {
            mainPanel.setConnectionURLErrorMessage(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_InvalidConnectionURLMessage));
        } else {
            mainPanel.clearConnectionURLErrorMessage();
        }
        if (handler != null) {
            handler.onConnectionURLChange();
        }
    }

    public void onUserChange() {
        final String newValue = mainPanel.getUser().trim();
        validationService.isNotEmpty(newValue,
                                     new ValidatorCallback() {
                                         @Override
                                         public void onSuccess() {
                                             onUserChange(newValue,
                                                          true);
                                         }

                                         @Override
                                         public void onFailure() {
                                             onUserChange(newValue,
                                                          false);
                                         }
                                     });
    }

    private void onUserChange(String newValue,
                              boolean isValid) {
        dataSourceDef.setUser(newValue);
        userValid = isValid;
        if (!userValid) {
            mainPanel.setUserErrorMessage(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_InvalidUserMessage));
        } else {
            mainPanel.clearUserErrorMessage();
        }
        if (handler != null) {
            handler.onUserChange();
        }
    }

    public void onPasswordChange() {
        final String newValue = mainPanel.getPassword().trim();
        validationService.isNotEmpty(newValue,
                                     new ValidatorCallback() {
                                         @Override
                                         public void onSuccess() {
                                             onPasswordChange(newValue,
                                                              true);
                                         }

                                         @Override
                                         public void onFailure() {
                                             onPasswordChange(newValue,
                                                              false);
                                         }
                                     });
    }

    private void onPasswordChange(String newValue,
                                  boolean isValid) {
        dataSourceDef.setPassword(newValue);
        passwordValid = isValid;
        if (!passwordValid) {
            mainPanel.setPasswordErrorMessage(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_InvalidPasswordMessage));
        } else {
            mainPanel.clearPasswordErrorMessage();
        }
        if (handler != null) {
            handler.onPasswordChange();
        }
    }

    public void onDriverChange() {
        DriverDefInfo driverDef = driverDefMap.get(mainPanel.getDriver());
        driverValid = driverDef != null;
        if (!driverValid) {
            mainPanel.setDriverErrorMessage(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_DriverRequiredMessage));
            dataSourceDef.setDriverUuid(null);
        } else {
            mainPanel.clearDriverErrorMessage();
            dataSourceDef.setDriverUuid(driverDef.getUuid());
        }
        if (handler != null) {
            handler.onDriverChange();
        }
    }

    public void onTestConnection() {
        if (module != null) {
            editorService.call(
                    getTestConnectionSuccessCallback(),
                    getTestConnectionErrorCallback()).testConnection(dataSourceDef,
                                                                     module);
        } else {
            editorService.call(
                    getTestConnectionSuccessCallback(),
                    getTestConnectionErrorCallback()).testConnection(dataSourceDef);
        }
    }

    private RemoteCallback<TestResult> getTestConnectionSuccessCallback() {
        return new RemoteCallback<TestResult>() {
            @Override
            public void callback(TestResult response) {
                onTestConnectionSuccess(response);
            }
        };
    }

    public void onTestConnectionSuccess(TestResult response) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        if (response.isTestPassed()) {
            builder.appendEscapedLines(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_ConnectionTestSuccessfulMessage) + "\n");
        } else {
            builder.appendEscapedLines(
                    getMessage(DataSourceManagementConstants.DataSourceDefEditor_ConnectionTestFailedMessage) + "\n");
        }
        builder.appendEscapedLines(response.getMessage());
        popupsUtil.showInformationPopup(builder.toSafeHtml().asString());
    }

    private ErrorCallback<?> getTestConnectionErrorCallback() {
        return new ErrorCallback<Object>() {
            @Override
            public boolean error(Object message,
                                 Throwable throwable) {
                onTestConnectionError(message,
                                      throwable);
                return false;
            }
        };
    }

    public void onTestConnectionError(Object message,
                                      Throwable throwable) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendEscapedLines(
                getMessage(DataSourceManagementConstants.DataSourceDefEditor_ConnectionTestFailedMessage) + "\n");
        builder.appendEscapedLines(throwable.getMessage());
        popupsUtil.showErrorPopup(builder.toSafeHtml().asString());
    }

    public void setValid(boolean valid) {
        this.nameValid = valid;
        this.connectionURLValid = valid;
        this.userValid = valid;
        this.passwordValid = valid;
        this.driverValid = valid;
    }

    public boolean isDriverValid() {
        return driverValid;
    }

    public boolean isNameValid() {
        return nameValid;
    }

    public boolean isConnectionURLValid() {
        return connectionURLValid;
    }

    public boolean isUserValid() {
        return userValid;
    }

    public boolean isPasswordValid() {
        return passwordValid;
    }

    public String getMessage(String messageKey) {
        return translationService.getTranslation(messageKey);
    }

    public String getMessage(String messageKey,
                             Object... args) {
        return translationService.format(messageKey,
                                         args);
    }
}