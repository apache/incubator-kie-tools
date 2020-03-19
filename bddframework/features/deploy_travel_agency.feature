@quarkus
@cr
Feature: Deploy Travel agency service and verify its functionality

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators
    And "CR" install Kogito Data Index with 1 replicas
    And "CR" deploy service from example file "travelapp-kogito-travel-agency.yaml"
    And Kogito application "kogito-travel-agency" has 1 pods running within 10 minutes
    And HTTP GET request on service "kogito-travel-agency" with path "travels" is successful within 1 minutes

  Scenario: Travel application without required Visa
    When Start "travels" process on service "kogito-travel-agency" with body:
      """json
	{
		"traveller" : {
			"firstName" : "John6",
			"lastName" : "Doe",
			"email" : "john.doe@example.com",
			"nationality" : "American",
			"address" : {
				"street" : "main street",
				"city" : "Boston",
				"zipCode" : "10005",
				"country" : "US"
			}
		},
		"trip" : {
			"city" : "New York",
			"country" : "US",
			"begin" : "2019-12-10T00:00:00.000+02:00",
			"end" : "2019-12-15T00:00:00.000+02:00"
		}
	}
      """
    And Service "kogito-travel-agency" contains 1 instance of process with name "travels"
    And Service "kogito-travel-agency" contains 1 task of process with name "travels" and task name "ConfirmTravel"
    And Complete "ConfirmTravel" task on service "kogito-travel-agency" and process with name "travels" with body:
	  """json
	  {}
	  """

    Then Service "kogito-travel-agency" contains 0 instances of process with name "travels"
