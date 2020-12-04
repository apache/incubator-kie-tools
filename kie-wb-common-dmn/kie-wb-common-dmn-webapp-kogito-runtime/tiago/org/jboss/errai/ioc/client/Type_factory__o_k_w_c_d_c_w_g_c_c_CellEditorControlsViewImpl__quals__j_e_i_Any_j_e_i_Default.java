package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControlsViewImpl> { public interface o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/widgets/grid/controls/container/CellEditorControlsViewImpl.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CellEditorControlsViewImpl implements Proxy<CellEditorControlsViewImpl> {
    private final ProxyHelper<CellEditorControlsViewImpl> proxyHelper = new ProxyHelperImpl<CellEditorControlsViewImpl>("Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CellEditorControlsViewImpl instance) {

    }

    public CellEditorControlsViewImpl asBeanType() {
      return this;
    }

    public void setInstance(final CellEditorControlsViewImpl instance) {
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

    @Override public Optional getActiveEditor() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getActiveEditor();
        return retVal;
      } else {
        return super.getActiveEditor();
      }
    }

    @Override public void setActiveEditor(Optional activeEditor) {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setActiveEditor(activeEditor);
      } else {
        super.setActiveEditor(activeEditor);
      }
    }

    @Override public void setup() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public void show(PopupEditorControls editor, int x, int y) {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(editor, x, y);
      } else {
        super.show(editor, x, y);
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CellEditorControlsViewImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CellEditorControlsViewImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CellEditorControlsViewImpl.class, Object.class, CellEditorControlsView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.kie-dmn-cell-editor-controls {\n  position: absolute;\n  -moz-user-select: none;\n  -khtml-user-select: none;\n  -webkit-user-select: none;\n  -ms-user-select: none;\n  user-select: none;\n}\n\n");
  }

  public CellEditorControlsViewImpl createInstance(final ContextManager contextManager) {
    final Div _cellEditorControls_1 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Document _document_0 = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    final Div _cellEditorControlsContainer_2 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final CellEditorControlsViewImpl instance = new CellEditorControlsViewImpl(_document_0, _cellEditorControls_1, _cellEditorControlsContainer_2);
    registerDependentScopedReference(instance, _cellEditorControls_1);
    registerDependentScopedReference(instance, _document_0);
    registerDependentScopedReference(instance, _cellEditorControlsContainer_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImplTemplateResource templateForCellEditorControlsViewImpl = GWT.create(o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImplTemplateResource.class);
    Element parentElementForTemplateOfCellEditorControlsViewImpl = TemplateUtil.getRootTemplateParentElement(templateForCellEditorControlsViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/widgets/grid/controls/container/CellEditorControlsViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/widgets/grid/controls/container/CellEditorControlsViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCellEditorControlsViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCellEditorControlsViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("cellEditorControls", new DataFieldMeta());
    dataFieldMetas.put("cellEditorControlsContainer", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/container/CellEditorControlsViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CellEditorControlsViewImpl_Div_cellEditorControls(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cellEditorControls");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/container/CellEditorControlsViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CellEditorControlsViewImpl_Div_cellEditorControlsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cellEditorControlsContainer");
    templateFieldsMap.put("cellEditorControls", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CellEditorControlsViewImpl_Div_cellEditorControls(instance))));
    templateFieldsMap.put("cellEditorControlsContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CellEditorControlsViewImpl_Div_cellEditorControlsContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCellEditorControlsViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CellEditorControlsViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final CellEditorControlsViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final CellEditorControlsViewImpl instance) {
    instance.setup();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CellEditorControlsViewImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Div CellEditorControlsViewImpl_Div_cellEditorControlsContainer(CellEditorControlsViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl::cellEditorControlsContainer;
  }-*/;

  native static void CellEditorControlsViewImpl_Div_cellEditorControlsContainer(CellEditorControlsViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl::cellEditorControlsContainer = value;
  }-*/;

  native static Div CellEditorControlsViewImpl_Div_cellEditorControls(CellEditorControlsViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl::cellEditorControls;
  }-*/;

  native static void CellEditorControlsViewImpl_Div_cellEditorControls(CellEditorControlsViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl::cellEditorControls = value;
  }-*/;
}