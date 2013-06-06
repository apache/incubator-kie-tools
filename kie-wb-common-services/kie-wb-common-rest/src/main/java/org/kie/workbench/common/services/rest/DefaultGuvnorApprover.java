package org.kie.workbench.common.services.rest;


import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.workbench.common.services.shared.rest.JobRequest;

import java.io.File;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultGuvnorApprover {
    public boolean requestApproval(JobRequest jobRequest) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        KieModule kModule = kr.addKieModule(ks.getResources().newFileSystemResource(getKJar("/DefaultGuvnorApprover-0.9.jar")));

        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        KieSession kSession = kContainer.newKieSession();

        FactHandle jobRequestFactHandle  = kSession.insert(jobRequest);
        kSession.fireAllRules();
        
        //TODO:
        System.out.println("approval request result: " + true);
        return true;
    }
    
    public File getKJar(String kjarName) {
        if (!kjarName.endsWith(".jar")) {
            throw new RuntimeException("The kjar name is invalid");
        }
        
        URL u = this.getClass().getResource(kjarName);
        File kjarFile = new File(u.getFile());
        if (!kjarFile.exists()) {
            throw new RuntimeException("The kjar [" + kjarName + "] does not exist");
        }
        
        return kjarFile;
    }
    
/*    public static File getKJar(String kjarName) {
        if (!kjarName.endsWith(".jar")) {
            throw new RuntimeException("The kjar name is invalid");
        }
        
        File kjarFile = new File(kjarName);
        if (!kjarFile.exists()) {
            throw new RuntimeException("The kjar [" + kjarName + "] does not exist");
        }
        
        return kjarFile;
    }*/
    
    public static void main(String[] args) {
    	DefaultGuvnorApprover a = new DefaultGuvnorApprover();
    	JobRequest request = new JobRequest();
    	boolean result = a.requestApproval(request);
    	System.out.println("request result: " + result);
    }
}
