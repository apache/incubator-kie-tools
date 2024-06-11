Feature: Deploy SonataFlow Operator

  @devMode
  Scenario: order-processing DevMode E2E test
    Given Namespace is created
    When SonataFlow Operator is deployed
    When SonataFlowPlatform is deployed
    When SonataFlow orderprocessing example is deployed
    Then SonataFlow "orderprocessing" has the condition "Running" set to "True" within 5 minutes
    Then SonataFlow "orderprocessing" is addressable within 1 minute
    Then HTTP POST request as Cloud Event on SonataFlow "orderprocessing" is successful within 1 minute with path "", headers "content-type= application/json,ce-specversion= 1.0,ce-source= /from/localhost,ce-type= orderEvent,ce-id= f0643c68-609c-48aa-a820-5df423fa4fe0" and body:
    """json
    {"id":"f0643c68-609c-48aa-a820-5df423fa4fe0",
    "country":"Czech Republic",
    "total":10000,
    "description":"iPhone 12"
    }
    """

    Then Deployment "event-listener" pods log contains text 'source: /process/shippinghandling' within 1 minutes
    Then Deployment "event-listener" pods log contains text 'source: /process/fraudhandling' within 1 minutes
    Then Deployment "event-listener" pods log contains text '"id":"f0643c68-609c-48aa-a820-5df423fa4fe0","country":"Czech Republic","total":10000,"description":"iPhone 12"' within 1 minutes
    Then Deployment "event-listener" pods log contains text '"fraudEvaluation":true' within 1 minutes
    Then Deployment "event-listener" pods log contains text '"shipping":"international"' within 1 minutes
