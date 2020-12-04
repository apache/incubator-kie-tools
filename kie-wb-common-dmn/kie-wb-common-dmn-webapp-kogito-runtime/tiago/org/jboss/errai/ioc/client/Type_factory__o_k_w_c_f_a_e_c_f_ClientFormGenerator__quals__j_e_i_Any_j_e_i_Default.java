package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.FormElementProcessor;
import org.kie.workbench.common.forms.adf.service.building.FormGenerationResourcesProvider;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;
import org.kie.workbench.common.forms.model.FormDefinition;

public class Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFormGenerator> { private class Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFormGenerator implements Proxy<ClientFormGenerator> {
    private final ProxyHelper<ClientFormGenerator> proxyHelper = new ProxyHelperImpl<ClientFormGenerator>("Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ClientFormGenerator instance) {

    }

    public ClientFormGenerator asBeanType() {
      return this;
    }

    public void setInstance(final ClientFormGenerator instance) {
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

    @Override public void initialize() {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initialize();
      } else {
        super.initialize();
      }
    }

    @Override protected I18nHelper getI18nHelper(I18nSettings settings) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final I18nHelper retVal = ClientFormGenerator_getI18nHelper_I18nSettings(proxiedInstance, settings);
        return retVal;
      } else {
        return super.getI18nHelper(settings);
      }
    }

    @Override protected void registerProcessor(FormElementProcessor processor) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        AbstractFormGenerator_registerProcessor_FormElementProcessor(proxiedInstance, processor);
      } else {
        super.registerProcessor(processor);
      }
    }

    @Override protected void registerResources(FormGenerationResourcesProvider provider) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        AbstractFormGenerator_registerResources_FormGenerationResourcesProvider(proxiedInstance, provider);
      } else {
        super.registerResources(provider);
      }
    }

    @Override public FormDefinition generateFormForModel(Object model, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForModel(model, filters);
        return retVal;
      } else {
        return super.generateFormForModel(model, filters);
      }
    }

    @Override public FormDefinition generateFormForClass(Class clazz, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForClass(clazz, filters);
        return retVal;
      } else {
        return super.generateFormForClass(clazz, filters);
      }
    }

    @Override public FormDefinition generateFormForClassName(String className, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = proxiedInstance.generateFormForClassName(className, filters);
        return retVal;
      } else {
        return super.generateFormForClassName(className, filters);
      }
    }

    @Override protected FormDefinition generateFormDefinition(FormDefinitionSettings settings, Object model, FormElementFilter[] filters) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final FormDefinition retVal = AbstractFormGenerator_generateFormDefinition_FormDefinitionSettings_Object_FormElementFilter_array(proxiedInstance, settings, model, filters);
        return retVal;
      } else {
        return super.generateFormDefinition(settings, model, filters);
      }
    }

    @Override protected void processElements(FormGenerationContext context) {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        AbstractFormGenerator_processElements_FormGenerationContext(proxiedInstance, context);
      } else {
        super.processElements(context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFormGenerator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFormGenerator.class, "Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFormGenerator.class, AbstractFormGenerator.class, Object.class, FormGenerator.class });
  }

  public ClientFormGenerator createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final LayoutGenerator _layoutGenerator_0 = (LayoutGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default");
    final ClientFormGenerator instance = new ClientFormGenerator(_layoutGenerator_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _layoutGenerator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientFormGenerator instance) {
    instance.initialize();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_f_a_e_c_f_ClientFormGenerator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator ([org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator, org.jboss.errai.ui.client.local.spi.TranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFormGenerator> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static FormDefinition AbstractFormGenerator_generateFormDefinition_FormDefinitionSettings_Object_FormElementFilter_array(AbstractFormGenerator instance, FormDefinitionSettings a0, Object a1, FormElementFilter[] a2) /*-{
    return instance.@org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator::generateFormDefinition(Lorg/kie/workbench/common/forms/adf/service/definitions/FormDefinitionSettings;Ljava/lang/Object;[Lorg/kie/workbench/common/forms/adf/engine/shared/FormElementFilter;)(a0, a1, a2);
  }-*/;

  public native static void AbstractFormGenerator_registerProcessor_FormElementProcessor(AbstractFormGenerator instance, FormElementProcessor a0) /*-{
    instance.@org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator::registerProcessor(Lorg/kie/workbench/common/forms/adf/engine/shared/formGeneration/processing/FormElementProcessor;)(a0);
  }-*/;

  public native static void AbstractFormGenerator_registerResources_FormGenerationResourcesProvider(AbstractFormGenerator instance, FormGenerationResourcesProvider a0) /*-{
    instance.@org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator::registerResources(Lorg/kie/workbench/common/forms/adf/service/building/FormGenerationResourcesProvider;)(a0);
  }-*/;

  public native static I18nHelper ClientFormGenerator_getI18nHelper_I18nSettings(ClientFormGenerator instance, I18nSettings a0) /*-{
    return instance.@org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientFormGenerator::getI18nHelper(Lorg/kie/workbench/common/forms/adf/service/definitions/I18nSettings;)(a0);
  }-*/;

  public native static void AbstractFormGenerator_processElements_FormGenerationContext(AbstractFormGenerator instance, FormGenerationContext a0) /*-{
    instance.@org.kie.workbench.common.forms.adf.engine.shared.formGeneration.AbstractFormGenerator::processElements(Lorg/kie/workbench/common/forms/adf/engine/shared/formGeneration/FormGenerationContext;)(a0);
  }-*/;
}