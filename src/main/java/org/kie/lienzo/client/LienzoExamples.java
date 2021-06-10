package org.kie.lienzo.client;

import com.ait.lienzo.client.core.config.LienzoCoreEntryPoint;
import com.google.gwt.core.client.EntryPoint;

public class LienzoExamples extends BaseLienzoExamples implements EntryPoint {

    @Override
    public void onModuleLoad() {
        new LienzoCoreEntryPoint().onModuleLoad();
        doLoad();
    }
}