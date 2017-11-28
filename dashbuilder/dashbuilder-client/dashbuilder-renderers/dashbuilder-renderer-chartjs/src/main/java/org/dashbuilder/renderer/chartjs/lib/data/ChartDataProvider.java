package org.dashbuilder.renderer.chartjs.lib.data;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public abstract class ChartDataProvider<T>{

    protected List<T> data;

    public List<T> getData(){
        return data;
    }

    public void setData(List<T> data){
        this.data = data;
    }

    public abstract void update(AsyncCallback<Void> callback);
}
