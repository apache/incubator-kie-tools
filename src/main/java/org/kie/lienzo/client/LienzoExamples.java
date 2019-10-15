package org.kie.lienzo.client;

import com.ait.lienzo.client.core.config.LienzoCoreEntryPoint;
import org.gwtproject.core.client.EntryPoint;

public class LienzoExamples extends BaseLienzoExamples implements EntryPoint {

    @Override
    public void onModuleLoad() {
        new LienzoCoreEntryPoint().onModuleLoad();
        doLoad();
    }

}