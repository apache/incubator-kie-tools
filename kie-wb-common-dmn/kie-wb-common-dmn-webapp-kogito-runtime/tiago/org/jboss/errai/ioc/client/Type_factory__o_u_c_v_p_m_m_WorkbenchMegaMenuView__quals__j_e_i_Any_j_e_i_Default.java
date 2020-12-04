package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Image;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuView;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter.View;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;

public class Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMegaMenuView> { public interface o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchMegaMenuView.class, "Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchMegaMenuView.class, Object.class, View.class, WorkbenchBaseMenuView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("#mega-menu a {\n  cursor: pointer;\n}\n@media (min-width: 768px) {\n  #mega-menu .navbar-pf {\n    display: flex;\n    align-items: center;\n    flex: 1 0 100%;\n  }\n}\n#mega-menu .navbar-pf .navbar-header {\n  flex-shrink: 0;\n  height: 58px;\n  border-bottom: none;\n  flex-grow: 1;\n  min-height: 58px;\n  display: flex;\n  align-items: center;\n}\n@media (min-width: 768px) {\n  #mega-menu .navbar-pf .navbar-header {\n    flex-grow: 0;\n  }\n}\n#mega-menu .navbar-pf .navbar-brand {\n  padding: 0 1.25rem 0 0;\n}\n#mega-menu .navbar-pf .navbar-collapse {\n  flex: 0 1 100%;\n}\n#mega-menu .navbar-right > li > a,\n#mega-menu .nav-item-iconic {\n  font-size: 14px;\n  display: flex;\n  align-items: center;\n  border-left: none;\n  padding-left: 12px;\n}\n@media (min-width: 768px) {\n  #mega-menu .navbar-right > li > a,\n  #mega-menu .nav-item-iconic {\n    min-height: 58px;\n  }\n}\n#mega-menu .nav li a.nav-item-iconic {\n  display: flex;\n}\n#mega-menu .navbar-pf .navbar-nav > li > a {\n  padding-left: 12px;\n  padding-right: 12px;\n}\n#mega-menu .uf-brand-icon {\n  width: auto;\n  max-width: 200px;\n}\n#mega-menu .navbar-context-menu {\n  line-height: 0;\n}\n#mega-menu .navbar-context-menu > ul {\n  margin: 0;\n}\n#mega-menu .navbar-context-menu .navbar-primary.persistent-secondary {\n  padding: 0;\n}\n#mega-menu .navbar-context-menu .navbar-primary.persistent-secondary li.right {\n  float: right;\n}\n#mega-menu .uf-yamm .nav,\n#mega-menu .uf-yamm .collapse,\n#mega-menu .uf-yamm .dropup,\n#mega-menu .uf-yamm .dropdown {\n  position: static;\n}\n#mega-menu .uf-yamm .dropdown.uf-yamm--fw .dropdown-menu {\n  left: 0;\n  right: 0;\n}\n#mega-menu .uf-yamm__dropdown-menu {\n  left: auto;\n  border: none;\n  padding-top: 0;\n}\n@media (min-width: 768px) {\n  #mega-menu .uf-yamm__dropdown-menu {\n    background-color: #171717;\n    color: #d1d1d1;\n  }\n}\n#mega-menu .uf-yamm__section {\n  background-color: #171717;\n  color: #f5f5f5;\n  border-top: 1px solid #373737;\n  display: inline-block;\n  vertical-align: top;\n  float: none;\n}\n@media (min-width: 768px) {\n  #mega-menu .uf-yamm__section {\n    color: #d1d1d1;\n    border-top: none;\n  }\n}\n@media (min-width: 768px) {\n  #mega-menu .uf-yamm__section--top {\n    background-color: #232323;\n    padding-bottom: 0.75rem;\n    padding-top: 0.75rem;\n  }\n}\n#mega-menu .uf-yamm__section--top.empty {\n  padding: 0;\n  min-height: 0;\n}\n@media (min-width: 768px) {\n  #mega-menu .uf-yamm__title {\n    border-bottom: 1px solid #373737;\n    padding-bottom: 1rem;\n  }\n}\n#mega-menu .uf-yamm__subsection {\n  list-style: none;\n  padding-left: 0;\n}\n#mega-menu .uf-yamm__subsection a {\n  color: #d1d1d1;\n}\n#mega-menu .uf-yamm__subsection li {\n  padding-top: 0.75rem;\n  padding-bottom: 0.75rem;\n}\n#mega-menu .uf-yamm__subsection li + li {\n  padding-top: 0;\n}\n#mega-menu .uf-yamm__subsection li:last-child {\n  padding-bottom: 1rem;\n}\n#mega-menu .uf-yamm__subsection a:hover,\n#mega-menu .uf-yamm__subsection a:focus,\n#mega-menu .uf-yamm__subsection a:active,\n#mega-menu .uf-yamm__subsection a.active {\n  color: #f5f5f5;\n  text-decoration: none;\n}\n#mega-menu .navbar-pf .navbar-iconic li.dropdown > .dropdown-toggle .pficon-user {\n  padding-right: 0.5rem;\n}\n#mega-menu .navbar-pf .navbar-utility li.dropdown > .dropdown-toggle {\n  padding-left: 20px;\n}\n@media (min-width: 768px) {\n  #mega-menu .navbar-pf .navbar-utility li.dropdown > .dropdown-toggle {\n    padding-left: 10px;\n  }\n}\n#mega-menu .navbar {\n  border-top-width: 3px;\n}\n#mega-menu .navbar-pf {\n  background-color: #030303;\n}\n\n");
  }

  public WorkbenchMegaMenuView createInstance(final ContextManager contextManager) {
    final UnorderedList _rightMenuItems_7 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ManagedInstance<GroupContextMenuItemPresenter> _groupContextMenuItemPresenters_14 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { GroupContextMenuItemPresenter.class }, new Annotation[] { });
    final UnorderedList _singleMenuItems_5 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ManagedInstance<ChildMenuItemPresenter> _childMenuItemPresenters_11 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ChildMenuItemPresenter.class }, new Annotation[] { });
    final ManagedInstance<ChildContextMenuItemPresenter> _childContextMenuItemPresenters_13 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ChildContextMenuItemPresenter.class }, new Annotation[] { });
    final Anchor _homeLink_2 = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final UnorderedList _leftMenuItems_6 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Div _brand_0 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Image _brandImage_1 = (Image) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Image__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final UnorderedList _contextMenuItems_9 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_10 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Span _menuAccessorText_3 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ListItem _contextMenuItemsContainer_8 = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ListItem _singleMenuItemsContainer_4 = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ManagedInstance<GroupMenuItemPresenter> _groupMenuItemPresenters_12 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { GroupMenuItemPresenter.class }, new Annotation[] { });
    final WorkbenchMegaMenuView instance = new WorkbenchMegaMenuView(_brand_0, _brandImage_1, _homeLink_2, _menuAccessorText_3, _singleMenuItemsContainer_4, _singleMenuItems_5, _leftMenuItems_6, _rightMenuItems_7, _contextMenuItemsContainer_8, _contextMenuItems_9, _translationService_10, _childMenuItemPresenters_11, _groupMenuItemPresenters_12, _childContextMenuItemPresenters_13, _groupContextMenuItemPresenters_14);
    registerDependentScopedReference(instance, _rightMenuItems_7);
    registerDependentScopedReference(instance, _groupContextMenuItemPresenters_14);
    registerDependentScopedReference(instance, _singleMenuItems_5);
    registerDependentScopedReference(instance, _childMenuItemPresenters_11);
    registerDependentScopedReference(instance, _childContextMenuItemPresenters_13);
    registerDependentScopedReference(instance, _homeLink_2);
    registerDependentScopedReference(instance, _leftMenuItems_6);
    registerDependentScopedReference(instance, _brand_0);
    registerDependentScopedReference(instance, _brandImage_1);
    registerDependentScopedReference(instance, _contextMenuItems_9);
    registerDependentScopedReference(instance, _translationService_10);
    registerDependentScopedReference(instance, _menuAccessorText_3);
    registerDependentScopedReference(instance, _contextMenuItemsContainer_8);
    registerDependentScopedReference(instance, _singleMenuItemsContainer_4);
    registerDependentScopedReference(instance, _groupMenuItemPresenters_12);
    setIncompleteInstance(instance);
    o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource templateForWorkbenchMegaMenuView = GWT.create(o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource.class);
    Element parentElementForTemplateOfWorkbenchMegaMenuView = TemplateUtil.getRootTemplateParentElement(templateForWorkbenchMegaMenuView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWorkbenchMegaMenuView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWorkbenchMegaMenuView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(10);
    dataFieldMetas.put("brand", new DataFieldMeta());
    dataFieldMetas.put("brand-image", new DataFieldMeta());
    dataFieldMetas.put("home-link", new DataFieldMeta());
    dataFieldMetas.put("menu-accessor-text", new DataFieldMeta());
    dataFieldMetas.put("single-menu-items-container", new DataFieldMeta());
    dataFieldMetas.put("single-menu-items", new DataFieldMeta());
    dataFieldMetas.put("left-menu-items", new DataFieldMeta());
    dataFieldMetas.put("right-menu-items", new DataFieldMeta());
    dataFieldMetas.put("context-menu-items-container", new DataFieldMeta());
    dataFieldMetas.put("context-menu-items", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Div_brand(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "brand");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Image_brandImage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "brand-image");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Anchor_homeLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "home-link");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Span_menuAccessorText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "menu-accessor-text");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_ListItem_singleMenuItemsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "single-menu-items-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_singleMenuItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "single-menu-items");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_leftMenuItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "left-menu-items");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_rightMenuItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "right-menu-items");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_ListItem_contextMenuItemsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "context-menu-items-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView", "org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_contextMenuItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "context-menu-items");
    templateFieldsMap.put("brand", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Div_brand(instance))));
    templateFieldsMap.put("brand-image", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Image_brandImage(instance))));
    templateFieldsMap.put("home-link", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Anchor_homeLink(instance))));
    templateFieldsMap.put("menu-accessor-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_Span_menuAccessorText(instance))));
    templateFieldsMap.put("single-menu-items-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_ListItem_singleMenuItemsContainer(instance))));
    templateFieldsMap.put("single-menu-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_singleMenuItems(instance))));
    templateFieldsMap.put("left-menu-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_leftMenuItems(instance))));
    templateFieldsMap.put("right-menu-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_rightMenuItems(instance))));
    templateFieldsMap.put("context-menu-items-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_ListItem_contextMenuItemsContainer(instance))));
    templateFieldsMap.put("context-menu-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(WorkbenchMegaMenuView_UnorderedList_contextMenuItems(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfWorkbenchMegaMenuView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((WorkbenchMegaMenuView) instance, contextManager);
  }

  public void destroyInstanceHelper(final WorkbenchMegaMenuView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static UnorderedList WorkbenchMegaMenuView_UnorderedList_contextMenuItems(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::contextMenuItems;
  }-*/;

  native static void WorkbenchMegaMenuView_UnorderedList_contextMenuItems(WorkbenchMegaMenuView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::contextMenuItems = value;
  }-*/;

  native static Anchor WorkbenchMegaMenuView_Anchor_homeLink(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::homeLink;
  }-*/;

  native static void WorkbenchMegaMenuView_Anchor_homeLink(WorkbenchMegaMenuView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::homeLink = value;
  }-*/;

  native static Div WorkbenchMegaMenuView_Div_brand(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::brand;
  }-*/;

  native static void WorkbenchMegaMenuView_Div_brand(WorkbenchMegaMenuView instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::brand = value;
  }-*/;

  native static Span WorkbenchMegaMenuView_Span_menuAccessorText(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::menuAccessorText;
  }-*/;

  native static void WorkbenchMegaMenuView_Span_menuAccessorText(WorkbenchMegaMenuView instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::menuAccessorText = value;
  }-*/;

  native static ListItem WorkbenchMegaMenuView_ListItem_singleMenuItemsContainer(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::singleMenuItemsContainer;
  }-*/;

  native static void WorkbenchMegaMenuView_ListItem_singleMenuItemsContainer(WorkbenchMegaMenuView instance, ListItem value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::singleMenuItemsContainer = value;
  }-*/;

  native static UnorderedList WorkbenchMegaMenuView_UnorderedList_leftMenuItems(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::leftMenuItems;
  }-*/;

  native static void WorkbenchMegaMenuView_UnorderedList_leftMenuItems(WorkbenchMegaMenuView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::leftMenuItems = value;
  }-*/;

  native static UnorderedList WorkbenchMegaMenuView_UnorderedList_rightMenuItems(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::rightMenuItems;
  }-*/;

  native static void WorkbenchMegaMenuView_UnorderedList_rightMenuItems(WorkbenchMegaMenuView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::rightMenuItems = value;
  }-*/;

  native static UnorderedList WorkbenchMegaMenuView_UnorderedList_singleMenuItems(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::singleMenuItems;
  }-*/;

  native static void WorkbenchMegaMenuView_UnorderedList_singleMenuItems(WorkbenchMegaMenuView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::singleMenuItems = value;
  }-*/;

  native static Image WorkbenchMegaMenuView_Image_brandImage(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::brandImage;
  }-*/;

  native static void WorkbenchMegaMenuView_Image_brandImage(WorkbenchMegaMenuView instance, Image value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::brandImage = value;
  }-*/;

  native static ListItem WorkbenchMegaMenuView_ListItem_contextMenuItemsContainer(WorkbenchMegaMenuView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::contextMenuItemsContainer;
  }-*/;

  native static void WorkbenchMegaMenuView_ListItem_contextMenuItemsContainer(WorkbenchMegaMenuView instance, ListItem value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.WorkbenchMegaMenuView::contextMenuItemsContainer = value;
  }-*/;
}