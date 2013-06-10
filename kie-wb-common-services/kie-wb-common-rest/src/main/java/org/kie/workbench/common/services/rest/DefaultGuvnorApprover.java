package org.kie.workbench.common.services.rest;


import org.apache.commons.io.IOUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.workbench.common.services.shared.rest.JobRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultGuvnorApprover {
    public boolean requestApproval(JobRequest jobRequest) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        byte[] kjarBytes = getKarAS7AsByteArray("kie-wb-common-defaultapprover-0.9.jar");        
        KieModule kModule = kr.addKieModule(ks.getResources().newByteArrayResource(kjarBytes));
        
        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        KieSession kSession = kContainer.newKieSession();

        FactHandle jobRequestFactHandle  = kSession.insert(jobRequest);
        kSession.fireAllRules();
        
        //TODO:
        System.out.println("approval request result: " + true);
        return true;
    }
    
    //NOTE, this assumes that the kjar is located within kie-wb-common-rest-6.0.0-SNAPSHOT.jar
    public InputStream getKarAS7(String kjarName) {
        System.out.println("********************************************************************");
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kie-wb-common-defaultapprover-0.9.jar");
        System.out.println("**************kjar is******************* " + is);
        System.out.println("********************************************************************");
        return is;
    }
    
    //NOTE, this assumes that the kjar is located within kie-wb-common-rest-6.0.0-SNAPSHOT.jar
    public byte[] getKarAS7AsByteArray(String kjarName) {
        System.out.println("********************************************************************");
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kie-wb-common-defaultapprover-0.9.jar");
        System.out.println("**************kjar is******************* " + is);
        System.out.println("********************************************************************");
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            //Ignore
        }
        return null;
    }
        
    //NOTE, this does not work. The kjar url returned is sth like "/content/drools.war/WEB-INF/lib/kie-wb-common-defaultapprover-0.9.jar",
    //which does not exist on file system
    //See https://community.jboss.org/thread/175076,  https://community.jboss.org/message/638605
    public File getKarAS72(String kjarName) {
        System.out.println("********************************************************************");
        // mydummy.properties is file located within kie-wb-common-defaultapprover-0.9.jar
        URL url = Thread.currentThread().getContextClassLoader().getResource("mydummy.properties");
        System.out.println("**************mydummy.properties******************* " + url);
        if (url != null) {
            String path = url.getPath();
            String kjarPath = path.substring(0, path.indexOf("/lib/")) + "/lib/kie-wb-common-defaultapprover-0.9.jar";
            System.out.println("**************kie-wb-common-defaultapprover-0.9.jar******************* " + kjarPath);

            File kjarFile = new File(kjarPath);
            System.out.println("**************kie-wb-common-defaultapprover-0.9.jar exist******************* " + kjarFile.exists());

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(kjarPath);
            System.out.println("**************kie-wb-common-defaultapprover-0.9.jar InputStream ******************* " + is);

            return kjarFile;
        }
        return null;
    }

    public static void main(String[] args) {
    	DefaultGuvnorApprover a = new DefaultGuvnorApprover();
    	JobRequest request = new JobRequest();
    	boolean result = a.requestApproval(request);
    	System.out.println("request result: " + result);
    }
}
