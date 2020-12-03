package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.Composite;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationHandler;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferences;

public class Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<LanguageConfigurationHandler> { private class Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LanguageConfigurationHandler implements Proxy<LanguageConfigurationHandler> {
    private final ProxyHelper<LanguageConfigurationHandler> proxyHelper = new ProxyHelperImpl<LanguageConfigurationHandler>("Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LanguageConfigurationHandler instance) {

    }

    public LanguageConfigurationHandler asBeanType() {
      return this;
    }

    public void setInstance(final LanguageConfigurationHandler instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public String getDescription() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription();
        return retVal;
      } else {
        return super.getDescription();
      }
    }

    @Override public void configurationSetting(boolean isInit) {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.configurationSetting(isInit);
      } else {
        super.configurationSetting(isInit);
      }
    }

    @Override protected void setDefaultConfigurationValues(UserWorkbenchPreferences response) {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        LanguageConfigurationHandler_setDefaultConfigurationValues_UserWorkbenchPreferences(proxiedInstance, response);
      } else {
        super.setDefaultConfigurationValues(response);
      }
    }

    @Override protected void initHandler() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        LanguageConfigurationHandler_initHandler(proxiedInstance);
      } else {
        super.initHandler();
      }
    }

    @Override protected UserWorkbenchPreferences getSelectedUserWorkbenchPreferences() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final UserWorkbenchPreferences retVal = LanguageConfigurationHandler_getSelectedUserWorkbenchPreferences(proxiedInstance);
        return retVal;
      } else {
        return super.getSelectedUserWorkbenchPreferences();
      }
    }

    @Override public List getExtensions() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getExtensions();
        return retVal;
      } else {
        return super.getExtensions();
      }
    }

    @Override public void loadUserWorkbenchPreferences() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadUserWorkbenchPreferences();
      } else {
        super.loadUserWorkbenchPreferences();
      }
    }

    @Override public void saveUserWorkbenchPreferences() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.saveUserWorkbenchPreferences();
      } else {
        super.saveUserWorkbenchPreferences();
      }
    }

    @Override public Composite getWidgetByName(String name) {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Composite retVal = proxiedInstance.getWidgetByName(name);
        return retVal;
      } else {
        return super.getWidgetByName(name);
      }
    }

    @Override public UserWorkbenchPreferences getPreference() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final UserWorkbenchPreferences retVal = proxiedInstance.getPreference();
        return retVal;
      } else {
        return super.getPreference();
      }
    }

    @Override public void setPreference(UserWorkbenchPreferences response) {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPreference(response);
      } else {
        super.setPreference(response);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LanguageConfigurationHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LanguageConfigurationHandler.class, "Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LanguageConfigurationHandler.class, WorkbenchConfigurationHandler.class, Object.class });
  }

  public LanguageConfigurationHandler createInstance(final ContextManager contextManager) {
    final LanguageConfigurationHandler instance = new LanguageConfigurationHandler();
    setIncompleteInstance(instance);
    final Caller WorkbenchConfigurationHandler_preferencesService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { UserPreferencesService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, WorkbenchConfigurationHandler_preferencesService);
    WorkbenchConfigurationHandler_Caller_preferencesService(instance, WorkbenchConfigurationHandler_preferencesService);
    final ConfigurationComboBoxItemWidget LanguageConfigurationHandler_languageItem = (ConfigurationComboBoxItemWidget) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_w_c_ConfigurationComboBoxItemWidget__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LanguageConfigurationHandler_languageItem);
    LanguageConfigurationHandler_ConfigurationComboBoxItemWidget_languageItem(instance, LanguageConfigurationHandler_languageItem);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LanguageConfigurationHandler> proxyImpl = new Type_factory__o_k_w_c_w_c_h_w_c_LanguageConfigurationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Caller WorkbenchConfigurationHandler_Caller_preferencesService(WorkbenchConfigurationHandler instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationHandler::preferencesService;
  }-*/;

  native static void WorkbenchConfigurationHandler_Caller_preferencesService(WorkbenchConfigurationHandler instance, Caller<UserPreferencesService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationHandler::preferencesService = value;
  }-*/;

  native static ConfigurationComboBoxItemWidget LanguageConfigurationHandler_ConfigurationComboBoxItemWidget_languageItem(LanguageConfigurationHandler instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler::languageItem;
  }-*/;

  native static void LanguageConfigurationHandler_ConfigurationComboBoxItemWidget_languageItem(LanguageConfigurationHandler instance, ConfigurationComboBoxItemWidget value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler::languageItem = value;
  }-*/;

  public native static UserWorkbenchPreferences LanguageConfigurationHandler_getSelectedUserWorkbenchPreferences(LanguageConfigurationHandler instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler::getSelectedUserWorkbenchPreferences()();
  }-*/;

  public native static void LanguageConfigurationHandler_initHandler(LanguageConfigurationHandler instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler::initHandler()();
  }-*/;

  public native static void LanguageConfigurationHandler_setDefaultConfigurationValues_UserWorkbenchPreferences(LanguageConfigurationHandler instance, UserWorkbenchPreferences a0) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler::setDefaultConfigurationValues(Lorg/uberfire/ext/services/shared/preferences/UserWorkbenchPreferences;)(a0);
  }-*/;
}