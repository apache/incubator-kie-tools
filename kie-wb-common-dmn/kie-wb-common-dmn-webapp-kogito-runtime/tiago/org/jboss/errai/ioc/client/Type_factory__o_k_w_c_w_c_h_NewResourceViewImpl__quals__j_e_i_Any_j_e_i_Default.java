package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceView;
import org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourceViewImpl> { public interface o_k_w_c_w_c_h_NewResourceViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NewResourceViewImpl implements Proxy<NewResourceViewImpl> {
    private final ProxyHelper<NewResourceViewImpl> proxyHelper = new ProxyHelperImpl<NewResourceViewImpl>("Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final NewResourceViewImpl instance) {

    }

    public NewResourceViewImpl asBeanType() {
      return this;
    }

    public void setInstance(final NewResourceViewImpl instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void init(NewResourcePresenter presenter) {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public void setActiveHandler(NewResourceHandler handler) {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setActiveHandler(handler);
      } else {
        super.setActiveHandler(handler);
      }
    }

    @Override public Package getSelectedPackage() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Package retVal = proxiedInstance.getSelectedPackage();
        return retVal;
      } else {
        return super.getSelectedPackage();
      }
    }

    @Override public void setTitle(String title) {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setTitle(title);
      } else {
        super.setTitle(title);
      }
    }

    @Override public void setResourceName(String resourceName) {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setResourceName(resourceName);
      } else {
        super.setResourceName(resourceName);
      }
    }

    @Override protected void clearErrors() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        NewResourceViewImpl_clearErrors(proxiedInstance);
      } else {
        super.clearErrors();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NewResourceViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NewResourceViewImpl.class, "Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NewResourceViewImpl.class, Object.class, NewResourceView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public NewResourceViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final NewResourceViewImpl instance = new NewResourceViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final TextBox NewResourceViewImpl_fileNameTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NewResourceViewImpl_fileNameTextBox);
    NewResourceViewImpl_TextBox_fileNameTextBox(instance, NewResourceViewImpl_fileNameTextBox);
    final FlowPanel NewResourceViewImpl_handlerExtensions = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NewResourceViewImpl_handlerExtensions);
    NewResourceViewImpl_FlowPanel_handlerExtensions(instance, NewResourceViewImpl_handlerExtensions);
    final HelpBlock NewResourceViewImpl_fileNameHelpInline = (HelpBlock) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NewResourceViewImpl_fileNameHelpInline);
    NewResourceViewImpl_HelpBlock_fileNameHelpInline(instance, NewResourceViewImpl_fileNameHelpInline);
    final HelpBlock NewResourceViewImpl_packageHelpInline = (HelpBlock) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NewResourceViewImpl_packageHelpInline);
    NewResourceViewImpl_HelpBlock_packageHelpInline(instance, NewResourceViewImpl_packageHelpInline);
    final PackageListBox NewResourceViewImpl_packageListBox = (PackageListBox) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_PackageListBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NewResourceViewImpl_packageListBox);
    NewResourceViewImpl_PackageListBox_packageListBox(instance, NewResourceViewImpl_packageListBox);
    o_k_w_c_w_c_h_NewResourceViewImplTemplateResource templateForNewResourceViewImpl = GWT.create(o_k_w_c_w_c_h_NewResourceViewImplTemplateResource.class);
    Element parentElementForTemplateOfNewResourceViewImpl = TemplateUtil.getRootTemplateParentElement(templateForNewResourceViewImpl.getContents().getText(), "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNewResourceViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNewResourceViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("fileNameGroup", new DataFieldMeta());
    dataFieldMetas.put("fileTypeLabel", new DataFieldMeta());
    dataFieldMetas.put("fileNameTextBox", new DataFieldMeta());
    dataFieldMetas.put("fileNameHelpInline", new DataFieldMeta());
    dataFieldMetas.put("packageListBox", new DataFieldMeta());
    dataFieldMetas.put("packageGroup", new DataFieldMeta());
    dataFieldMetas.put("packageHelpInline", new DataFieldMeta());
    dataFieldMetas.put("handlerExtensionsGroup", new DataFieldMeta());
    dataFieldMetas.put("handlerExtensions", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_fileNameGroup(instance));
      }
    }, dataFieldElements, dataFieldMetas, "fileNameGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return NewResourceViewImpl_FormLabel_fileTypeLabel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "fileTypeLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return NewResourceViewImpl_TextBox_fileNameTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "fileNameTextBox");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return NewResourceViewImpl_HelpBlock_fileNameHelpInline(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "fileNameHelpInline");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(NewResourceViewImpl_PackageListBox_packageListBox(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "packageListBox");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_packageGroup(instance));
      }
    }, dataFieldElements, dataFieldMetas, "packageGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return NewResourceViewImpl_HelpBlock_packageHelpInline(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "packageHelpInline");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_handlerExtensionsGroup(instance));
      }
    }, dataFieldElements, dataFieldMetas, "handlerExtensionsGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl", "org/kie/workbench/common/widgets/client/handlers/NewResourceViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return NewResourceViewImpl_FlowPanel_handlerExtensions(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "handlerExtensions");
    templateFieldsMap.put("fileNameGroup", ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_fileNameGroup(instance)));
    templateFieldsMap.put("fileTypeLabel", NewResourceViewImpl_FormLabel_fileTypeLabel(instance).asWidget());
    templateFieldsMap.put("fileNameTextBox", NewResourceViewImpl_TextBox_fileNameTextBox(instance).asWidget());
    templateFieldsMap.put("fileNameHelpInline", NewResourceViewImpl_HelpBlock_fileNameHelpInline(instance).asWidget());
    templateFieldsMap.put("packageListBox", ElementWrapperWidget.getWidget(NewResourceViewImpl_PackageListBox_packageListBox(instance).getElement()));
    templateFieldsMap.put("packageGroup", ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_packageGroup(instance)));
    templateFieldsMap.put("packageHelpInline", NewResourceViewImpl_HelpBlock_packageHelpInline(instance).asWidget());
    templateFieldsMap.put("handlerExtensionsGroup", ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_handlerExtensionsGroup(instance)));
    templateFieldsMap.put("handlerExtensions", NewResourceViewImpl_FlowPanel_handlerExtensions(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfNewResourceViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((NewResourceViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final NewResourceViewImpl instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_fileNameGroup(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_packageGroup(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(NewResourceViewImpl_DivElement_handlerExtensionsGroup(instance)));
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final NewResourceViewImpl instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl ([org.jboss.errai.ui.client.local.spi.TranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NewResourceViewImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static HelpBlock NewResourceViewImpl_HelpBlock_packageHelpInline(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageHelpInline;
  }-*/;

  native static void NewResourceViewImpl_HelpBlock_packageHelpInline(NewResourceViewImpl instance, HelpBlock value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageHelpInline = value;
  }-*/;

  native static FormLabel NewResourceViewImpl_FormLabel_fileTypeLabel(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileTypeLabel;
  }-*/;

  native static void NewResourceViewImpl_FormLabel_fileTypeLabel(NewResourceViewImpl instance, FormLabel value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileTypeLabel = value;
  }-*/;

  native static HelpBlock NewResourceViewImpl_HelpBlock_fileNameHelpInline(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameHelpInline;
  }-*/;

  native static void NewResourceViewImpl_HelpBlock_fileNameHelpInline(NewResourceViewImpl instance, HelpBlock value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameHelpInline = value;
  }-*/;

  native static DivElement NewResourceViewImpl_DivElement_fileNameGroup(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameGroup;
  }-*/;

  native static void NewResourceViewImpl_DivElement_fileNameGroup(NewResourceViewImpl instance, DivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameGroup = value;
  }-*/;

  native static TextBox NewResourceViewImpl_TextBox_fileNameTextBox(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameTextBox;
  }-*/;

  native static void NewResourceViewImpl_TextBox_fileNameTextBox(NewResourceViewImpl instance, TextBox value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::fileNameTextBox = value;
  }-*/;

  native static PackageListBox NewResourceViewImpl_PackageListBox_packageListBox(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageListBox;
  }-*/;

  native static void NewResourceViewImpl_PackageListBox_packageListBox(NewResourceViewImpl instance, PackageListBox value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageListBox = value;
  }-*/;

  native static DivElement NewResourceViewImpl_DivElement_handlerExtensionsGroup(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::handlerExtensionsGroup;
  }-*/;

  native static void NewResourceViewImpl_DivElement_handlerExtensionsGroup(NewResourceViewImpl instance, DivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::handlerExtensionsGroup = value;
  }-*/;

  native static FlowPanel NewResourceViewImpl_FlowPanel_handlerExtensions(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::handlerExtensions;
  }-*/;

  native static void NewResourceViewImpl_FlowPanel_handlerExtensions(NewResourceViewImpl instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::handlerExtensions = value;
  }-*/;

  native static DivElement NewResourceViewImpl_DivElement_packageGroup(NewResourceViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageGroup;
  }-*/;

  native static void NewResourceViewImpl_DivElement_packageGroup(NewResourceViewImpl instance, DivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::packageGroup = value;
  }-*/;

  public native static void NewResourceViewImpl_clearErrors(NewResourceViewImpl instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl::clearErrors()();
  }-*/;
}