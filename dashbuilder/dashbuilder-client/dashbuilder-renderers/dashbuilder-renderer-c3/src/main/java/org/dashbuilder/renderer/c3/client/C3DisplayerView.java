package org.dashbuilder.renderer.c3.client;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.client.AbstractGwtDisplayerView;
import org.dashbuilder.renderer.c3.client.jsbinding.C3;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Chart;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Padding;
import org.dashbuilder.renderer.c3.client.resources.i18n.C3DisplayerConstants;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

public abstract class C3DisplayerView<P extends C3Displayer> 
        extends AbstractGwtDisplayerView<P> 
        implements C3Displayer.View<P> {
    
    private Panel container = new FlowPanel();
    private Panel filterPanel = new FlowPanel();
    private Panel displayerPanel = new FlowPanel();
    
    private HTML titleHtml = new HTML();
    private C3Chart chart;
    
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
    
}