package org.dashbuilder.renderer.c3.client;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.c3.client.jsbinding.C3;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Chart;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.resources.i18n.C3DisplayerConstants;
import org.dashbuilder.renderer.c3.mutationobserver.MutationObserverFactory;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

import elemental2.dom.DomGlobal;
import elemental2.dom.MutationObserver;
import elemental2.dom.MutationObserverInit;
import elemental2.dom.Node;
import jsinterop.base.Js;

public abstract class C3DisplayerView<P extends C3Displayer> 
        extends AbstractGwtDisplayerView<P> 
        implements C3Displayer.View<P> {
    
    private Panel container = GWT.create(FlowPanel.class);
    private Panel filterPanel = GWT.create(FlowPanel.class);
    protected Panel displayerPanel = GWT.create(FlowPanel.class);
    
    private HTML titleHtml = GWT.create(HTML.class);
    private int width;
    private int height;
    protected C3Chart chart;
    
    @Override
    public void init(P presenter) {
        super.setPresenter(presenter);
        super.setVisualization(container);
        container.add(titleHtml);
        container.add(filterPanel);
        container.add(displayerPanel);
        filterPanel.getElement().setAttribute("cellpadding", "2");
    }

    @Override
    public void updateChart(C3ChartConf conf) {
        displayerPanel.clear();
        conf.setBindto(displayerPanel.getElement());
        chart = C3.generate(conf);
    }
    
    @Override
    public String getGroupsTitle() {
        return C3DisplayerConstants.INSTANCE.common_Categories();
    }
    
    @Override
    public String getColumnsTitle() {
        return C3DisplayerConstants.INSTANCE.common_Series();
    }
    
    @Override
    public void showTitle(String title) {
        titleHtml.setText(title);
    }
    
    @Override
    public void setFilterLabelSet(FilterLabelSet widget) {
        HTMLElement element = widget.getElement();
        element.getStyle().setProperty("position", "absolute");
        element.getStyle().setProperty("z-index", "10");
        filterPanel.clear();
        filterPanel.add(ElementWrapperWidget.getWidget(element));
    }
    
    @Override
    public void setBackgroundColor(String color) {
        chart.getElement().getElementsByTagName("svg")
                          .getItem(0).getStyle()
                          .setBackgroundColor(color);
    }
    
    @Override
    public void noData() {
        FlowPanel noDataPanel = GWT.create(FlowPanel.class);
        noDataPanel.setWidth(width + "px");
        noDataPanel.setHeight(height + "px");
        Label lblNoData = GWT.create(Label.class);
        lblNoData.setText(C3DisplayerConstants.INSTANCE.common_noData());
        noDataPanel.add(lblNoData);
        displayerPanel.clear();
        displayerPanel.add(noDataPanel);
    }
    
    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setResizable(int maxWidth, int maxHeight) {
        displayerPanel.setWidth("100%");
        displayerPanel.getElement().getStyle().setProperty("maxWidth", maxWidth + "px");
        displayerPanel.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
        registerMutationObserver();
    }
    
    private void registerMutationObserver() {
        MutationObserver observer = new MutationObserver((records, obs) ->  {
            Node elementalNode = Js.cast(displayerPanel.getElement());
            if(DomGlobal.document.body.contains((elementalNode))) {
                if (chart != null) {
                    chart.flush();
                }
                obs.disconnect();
            }
            return null;
        });
        MutationObserverInit options = new MutationObserverFactory().mutationObserverInit();
        options.childList = true;
        observer.observe(DomGlobal.document.body, options);
    }
    
}