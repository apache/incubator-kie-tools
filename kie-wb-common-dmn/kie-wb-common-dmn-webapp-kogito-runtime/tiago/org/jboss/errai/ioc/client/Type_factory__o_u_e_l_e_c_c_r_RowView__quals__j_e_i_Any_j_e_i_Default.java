package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.rows.Row.View;
import org.uberfire.ext.layout.editor.client.components.rows.RowView;

public class Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default extends Factory<RowView> { public interface o_u_e_l_e_c_c_r_RowViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/components/rows/RowView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/components/rows/RowView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RowView.class, "Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RowView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_c_r_RowViewTemplateResource) GWT.create(o_u_e_l_e_c_c_r_RowViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public RowView createInstance(final ContextManager contextManager) {
    final RowView instance = new RowView();
    setIncompleteInstance(instance);
    final Div RowView_upperCenter = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_upperCenter);
    RowView_Div_upperCenter(instance, RowView_upperCenter);
    final Div RowView_upper = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_upper);
    RowView_Div_upper(instance, RowView_upper);
    final Div RowView_bottom = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_bottom);
    RowView_Div_bottom(instance, RowView_bottom);
    final Div RowView_mainRow = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_mainRow);
    RowView_Div_mainRow(instance, RowView_mainRow);
    final Div RowView_row = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_row);
    RowView_Div_row(instance, RowView_row);
    final Div RowView_bottomCenter = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, RowView_bottomCenter);
    RowView_Div_bottomCenter(instance, RowView_bottomCenter);
    o_u_e_l_e_c_c_r_RowViewTemplateResource templateForRowView = GWT.create(o_u_e_l_e_c_c_r_RowViewTemplateResource.class);
    Element parentElementForTemplateOfRowView = TemplateUtil.getRootTemplateParentElement(templateForRowView.getContents().getText(), "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/components/rows/RowView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRowView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRowView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("upper", new DataFieldMeta());
    dataFieldMetas.put("bottom", new DataFieldMeta());
    dataFieldMetas.put("row", new DataFieldMeta());
    dataFieldMetas.put("mainrow", new DataFieldMeta());
    dataFieldMetas.put("upper-center", new DataFieldMeta());
    dataFieldMetas.put("bottom-center", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_upper(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "upper");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_bottom(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "bottom");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_row(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "row");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_mainRow(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "mainrow");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_upperCenter(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "upper-center");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.RowView", "org/uberfire/ext/layout/editor/client/components/rows/RowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_bottomCenter(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "bottom-center");
    templateFieldsMap.put("upper", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_upper(instance))));
    templateFieldsMap.put("bottom", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_bottom(instance))));
    templateFieldsMap.put("row", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_row(instance))));
    templateFieldsMap.put("mainrow", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_mainRow(instance))));
    templateFieldsMap.put("upper-center", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_upperCenter(instance))));
    templateFieldsMap.put("bottom-center", ElementWrapperWidget.getWidget(TemplateUtil.asElement(RowView_Div_bottomCenter(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfRowView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((RowView) instance, contextManager);
  }

  public void destroyInstanceHelper(final RowView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div RowView_Div_bottom(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::bottom;
  }-*/;

  native static void RowView_Div_bottom(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::bottom = value;
  }-*/;

  native static Div RowView_Div_upper(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::upper;
  }-*/;

  native static void RowView_Div_upper(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::upper = value;
  }-*/;

  native static Div RowView_Div_row(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::row;
  }-*/;

  native static void RowView_Div_row(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::row = value;
  }-*/;

  native static Div RowView_Div_upperCenter(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::upperCenter;
  }-*/;

  native static void RowView_Div_upperCenter(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::upperCenter = value;
  }-*/;

  native static Div RowView_Div_bottomCenter(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::bottomCenter;
  }-*/;

  native static void RowView_Div_bottomCenter(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::bottomCenter = value;
  }-*/;

  native static Div RowView_Div_mainRow(RowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::mainRow;
  }-*/;

  native static void RowView_Div_mainRow(RowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.RowView::mainRow = value;
  }-*/;
}