package org.uberfire.client.views.pfly.monaco.jsinterop;

import org.gwtproject.core.client.ScriptInjector;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.client.TextResource;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Singleton;

@Singleton
@Startup
public class MonacoLoader {

    @Resource
    public interface Monaco extends ClientBundle {

        Monaco INSTANCE = new MonacoLoader_MonacoImpl();

        @ClientBundle.Source("monaco.min.js.txt")
        TextResource monaco();
    }


    @PostConstruct
    public void init() {
        ScriptInjector.fromString(Monaco.INSTANCE.monaco().getText())
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }
}
