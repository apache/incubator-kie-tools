@quarkus
@cr
Feature: Deploy Travel agency service and verify its functionality

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators
    And "CR" install Kogito Data Index with 1 replicas
    And "CR" deploy service from example file "travelapp-kogito-travel-agency.yaml"
    And "CR" deploy service from example file "travelapp-kogito-visas.yaml"
    And Kogito application "kogito-travel-agency" has 1 pods running within 10 minutes
    And HTTP GET request on service "kogito-travel-agency" with path "travels" is successful within 1 minutes
    And Kogito application "kogito-visas" has 1 pods running within 10 minutes
    And HTTP GET request on service "kogito-visas" with path "visaApplications" is successful within 1 minutes

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

  Scenario: Travel application with required Visa
    When Start "travels" process on service "kogito-travel-agency" with body:
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
    And Service "kogito-travel-agency" contains 1 instance of process with name "travels"
    And Service "kogito-travel-agency" contains 1 task of process with name "travels" and task name "VisaApplication"
    And Complete "VisaApplication" task on service "kogito-travel-agency" and process with name "travels" with body:
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
	And Service "kogito-visas" contains 1 instance of process with name "visaApplications"
	And Service "kogito-visas" contains 1 task of process with name "visaApplications" and task name "ApplicationApproval"
	And Complete "ApplicationApproval" task on service "kogito-visas" and process with name "visaApplications" with body:
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
    And Service "kogito-travel-agency" contains 1 task of process with name "travels" and task name "ConfirmTravel"
    And Complete "ConfirmTravel" task on service "kogito-travel-agency" and process with name "travels" with body:
	  """json
	  {}
	  """

    Then Service "kogito-travel-agency" contains 0 instances of process with name "travels"