NOTE: Tests in this package should be placed in stunner-bpmn-client module, but it does not allow at this point 
to use the Lienzo junit runner due to the module is using PowerMock - both lienzo-test and powermock are not 
compatible in same classpath.       
Until not removed the use of PowerMock from stunner-bpmn-client module, client side tests that rely on Lienzo  
can be placed here.        