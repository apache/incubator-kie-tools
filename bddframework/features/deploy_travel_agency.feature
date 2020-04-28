@quarkus
@travelagency
Feature: Deploy Travel agency service and verify its functionality

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Data Index with 1 replicas
    And Deploy quarkus example service "kogito-travel-agency/travels" with configuration:
      | config | native      | disabled |
      | config | persistence | enabled  |
      | config | events      | enabled  |
    And Deploy quarkus example service "kogito-travel-agency/visas" with configuration:
      | config | native      | disabled |
      | config | persistence | enabled  |
      | config | events      | enabled  |
    And Kogito application "travels" has 1 pods running within 10 minutes
    And HTTP GET request on service "travels" with path "travels" is successful within 1 minutes
    And Kogito application "visas" has 1 pods running within 10 minutes
    And HTTP GET request on service "visas" with path "visaApplications" is successful within 1 minutes

  Scenario: Travel application without required Visa
    When Start "travels" process on service "travels" with body:
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
    And Service "travels" contains 1 instance of process with name "travels"
    And Service "travels" contains 1 task of process with name "travels" and task name "ConfirmTravel"
    And Complete "ConfirmTravel" task on service "travels" and process with name "travels" with body:
	  """json
	  {}
	  """

    Then Service "travels" with process name "travels" is available

  Scenario: Travel application with required Visa
    When Start "travels" process on service "travels" with body:
      """json
	{
		"traveller" : {
			"firstName" : "Jan",
			"lastName" : "Kowalski",
			"email" : "jan.kowalski@example.com",
			"nationality" : "Polish",
			"address" : {
				"street" : "polna",
				"city" : "Krakow",
				"zipCode" : "32000",
				"country" : "Poland"
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
    And Service "travels" contains 1 instance of process with name "travels"
    And Service "travels" contains 1 task of process with name "travels" and task name "VisaApplication"
    And Complete "VisaApplication" task on service "travels" and process with name "travels" with body:
	  """json
	{
		"visaApplication" : {
			"firstName" : "Jan",
			"lastName" : "Kowalski",
			"nationality" : "Polish",
			"city" : "New York",
			"country" : "US",
			"passportNumber" : "ABC09876",
			"duration" : 25
		}
	}
	  """
	And Service "visas" contains 1 instance of process with name "visaApplications" within 1 minutes
	And Service "visas" contains 1 task of process with name "visaApplications" and task name "ApplicationApproval"
	And Complete "ApplicationApproval" task on service "visas" and process with name "visaApplications" with body:
	  """json
	{
		"application" : {
			"firstName" : "Jan",
			"lastName" : "Kowalski",
			"nationality" : "Polish",
			"city" : "New York",
			"country" : "US",
			"passportNumber" : "ABC09876",
			"duration" : 25,
			"approved" : true
		}
	}
	  """
    And Service "travels" contains 1 task of process with name "travels" and task name "ConfirmTravel"
    And Complete "ConfirmTravel" task on service "travels" and process with name "travels" with body:
	  """json
	  {}
	  """

    Then Service "travels" with process name "travels" is available
