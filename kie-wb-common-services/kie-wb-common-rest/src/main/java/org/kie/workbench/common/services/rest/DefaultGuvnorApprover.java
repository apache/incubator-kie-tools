package org.kie.workbench.common.services.rest;


import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.workbench.common.services.shared.rest.JobRequest;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultGuvnorApprover {
    public boolean requestApproval(JobRequest jobRequest) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        KieModule kModule = kr.addKieModule(ks.getResources().newFileSystemResource(getKJar("kjar")));

        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        KieSession kSession = kContainer.newKieSession();
        //kSession.setGlobal("out", out);

        FactHandle jobRequestFactHandle  = kSession.insert(jobRequest);
        kSession.fireAllRules();
        
        //TODO:
        return true;
    }

    public static File getKJar(String kjarName) {
        if (!kjarName.endsWith(".jar")) {
            throw new RuntimeException("The kjar name is invalid");
        }
        
        File kjarFile = new File(kjarName);
        if (!kjarFile.exists()) {
            throw new RuntimeException("The kjar [" + kjarName + "] does not exist");
        }
        
        return kjarFile;
    }
}
