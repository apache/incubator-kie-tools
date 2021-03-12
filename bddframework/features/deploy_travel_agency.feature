@quarkus
@travelagency
@infinispan
@kafka
Feature: Deploy Travel agency service and verify its functionality

  Background:
    Given Namespace is created
    And Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |

  Scenario Outline: Travel application without required Visa and build profile <profile>
    Given Clone Kogito examples into local directory
    And Local example service "kogito-travel-agency/extended/travels" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Local example service "kogito-travel-agency/extended/visas" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy quarkus example service "travels" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Deploy quarkus example service "visas" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Runtime "travels" has 1 pods running within 10 minutes
    And Service "travels" with process name "travels" is available within 1 minutes
    And Kogito Runtime "visas" has 1 pods running within 10 minutes
    And Service "visas" with process name "visaApplications" is available within 1 minutes
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

    Examples:
      | profile |
      | default |

    @native
    Examples:
      | profile |
      | native  |

#####

  Scenario Outline: Travel application with required Visa and build profile <profile>
    Given Clone Kogito examples into local directory
    And Local example service "kogito-travel-agency/extended/travels" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Local example service "kogito-travel-agency/extended/visas" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy quarkus example service "travels" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Deploy quarkus example service "visas" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Runtime "travels" has 1 pods running within 10 minutes
    And Service "travels" with process name "travels" is available within 1 minutes
    And Kogito Runtime "visas" has 1 pods running within 10 minutes
    And Service "visas" with process name "visaApplications" is available within 1 minutes
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

    Examples:
      | profile |
      | default |

    @native
    Examples:
      | profile |
      | native  |
