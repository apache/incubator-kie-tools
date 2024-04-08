const { gql } = require("apollo-server-express");
module.exports = typeDefs = gql`
  scalar DateTime

  schema {
    query: Query
    mutation: Mutation
  }

  type Mutation {
    ProcessInstanceSkip(id: String): String
    ProcessInstanceAbort(id: String): String
    ProcessInstanceRetry(id: String): String
    ProcessInstanceUpdateVariables(id: String, variables: String): String
    NodeInstanceTrigger(id: String, nodeId: String): String
    NodeInstanceCancel(id: String, nodeInstanceId: String): String
    NodeInstanceRetrigger(id: String, nodeInstanceId: String): String
    JobCancel(id: String): String
    JobReschedule(id: String, data: String): String
  }

  type Query {
    ProcessInstances(
      where: ProcessInstanceArgument
      orderBy: ProcessInstanceOrderBy
      pagination: Pagination
    ): [ProcessInstance]
    Travels(where: TravelsArgument, orderBy: TravelsOrderBy, pagination: Pagination): [Travels]
    VisaApplications(
      where: VisaApplicationsArgument
      orderBy: VisaApplicationsOrderBy
      pagination: Pagination
    ): [VisaApplications]
    Jobs(where: JobArgument, orderBy: JobOrderBy, pagination: Pagination): [Job]
  }

  type ProcessInstance {
    id: String!
    processId: String!
    processName: String
    parentProcessInstanceId: String
    parentProcessInstance: ProcessInstance
    rootProcessInstanceId: String
    rootProcessId: String
    roles: [String!]
    state: ProcessInstanceState!
    serviceUrl: String
    endpoint: String!
    nodes: [NodeInstance!]!
    nodeDefinitions: [Node!]
    milestones: [Milestones!]
    variables: String
    start: DateTime!
    end: DateTime
    businessKey: String
    childProcessInstances: [ProcessInstance!]
    error: ProcessInstanceError
    addons: [String!]
    source: String
    lastUpdate: DateTime!
    diagram: String
  }

  type KogitoMetadata {
    lastUpdate: DateTime!
    processInstances: [ProcessInstanceMeta]
  }

  input KogitoMetadataOrderBy {
    lastUpdate: OrderBy
  }

  input KogitoMetadataArgument {
    lastUpdate: DateArgument
    processInstances: ProcessInstanceMetaArgument
  }

  type ProcessInstanceMeta {
    id: String!
    processId: String!
    processName: String
    parentProcessInstanceId: String
    rootProcessInstanceId: String
    rootProcessId: String
    roles: [String!]
    state: ProcessInstanceState!
    endpoint: String!
    start: DateTime!
    end: DateTime
    lastUpdate: DateTime!
    businessKey: String
    serviceUrl: String
  }

  type ProcessInstanceError {
    nodeDefinitionId: String!
    message: String
  }

  enum ProcessInstanceState {
    PENDING
    ACTIVE
    COMPLETED
    ABORTED
    SUSPENDED
    ERROR
  }

  type NodeInstance {
    id: String!
    name: String!
    type: String!
    enter: DateTime!
    exit: DateTime
    definitionId: String!
    nodeId: String!
  }

  type Node {
    id: String!
    nodeDefinitionId: String!
    name: String!
    type: String!
    uniqueId: String!
  }

  type Milestones {
    id: String!
    name: String!
    status: MilestoneStatus!
  }
  enum MilestoneStatus {
    ACTIVE
    AVAILABLE
    COMPLETED
  }

  input ProcessInstanceOrderBy {
    processId: OrderBy
    processName: OrderBy
    rootProcessId: OrderBy
    state: OrderBy
    start: OrderBy
    end: OrderBy
    error: ProcessInstanceErrorOrderBy
    lastUpdate: OrderBy
  }

  input ProcessInstanceErrorOrderBy {
    nodeDefinitionId: OrderBy
    message: OrderBy
  }

  input ProcessInstanceArgument {
    and: [ProcessInstanceArgument!]
    or: [ProcessInstanceArgument!]
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    error: ProcessInstanceErrorArgument
    nodes: NodeInstanceArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
    addons: StringArrayArgument
    lastUpdate: DateArgument
    businessKey: StringArgument
  }

  input ProcessInstanceErrorArgument {
    nodeDefinitionId: StringArgument
    message: StringArgument
  }

  input ProcessInstanceMetaArgument {
    id: IdArgument
    processId: StringArgument
    processName: StringArgument
    parentProcessInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    state: ProcessInstanceStateArgument
    endpoint: StringArgument
    roles: StringArrayArgument
    start: DateArgument
    end: DateArgument
  }

  input NodeInstanceArgument {
    id: IdArgument
    name: StringArgument
    definitionId: StringArgument
    nodeId: StringArgument
    type: StringArgument
    enter: DateArgument
    exit: DateArgument
  }

  input StringArrayArgument {
    contains: String
    containsAll: [String!]
    containsAny: [String!]
    isNull: Boolean
  }

  input IdArgument {
    in: [String!]
    equal: String
    isNull: Boolean
  }

  input StringArgument {
    in: [String!]
    like: String
    isNull: Boolean
    equal: String
  }

  input BooleanArgument {
    isNull: Boolean
    equal: Boolean
  }

  input NumericArgument {
    in: [Int!]
    isNull: Boolean
    equal: Int
    greaterThan: Int
    greaterThanEqual: Int
    lessThan: Int
    lessThanEqual: Int
    between: NumericRange
  }

  input NumericRange {
    from: Int!
    to: Int!
  }

  input DateArgument {
    isNull: Boolean
    equal: DateTime
    greaterThan: DateTime
    greaterThanEqual: DateTime
    lessThan: DateTime
    lessThanEqual: DateTime
    between: DateRange
  }

  input DateRange {
    from: DateTime!
    to: DateTime!
  }

  input ProcessInstanceStateArgument {
    equal: ProcessInstanceState
    in: [ProcessInstanceState]
  }

  type Subscription {
    ProcessInstanceAdded: ProcessInstance!
    ProcessInstanceUpdated: ProcessInstance!
  }

  enum OrderBy {
    ASC
    DESC
  }

  input Pagination {
    limit: Int
    offset: Int
  }

  type Travels {
    flight: Flight
    hotel: Hotel
    id: String
    traveller: Traveller
    trip: Trip
    visaApplication: VisaApplication
    metadata: KogitoMetadata
  }

  type Flight {
    arrival: String
    departure: String
    flightNumber: String
    gate: String
    seat: String
  }

  type Hotel {
    address: Address
    bookingNumber: String
    name: String
    phone: String
    room: String
  }

  type Address {
    city: String
    country: String
    street: String
    zipCode: String
  }

  type Traveller {
    address: Address
    email: String
    firstName: String
    lastName: String
    nationality: String
  }

  type Trip {
    begin: String
    city: String
    country: String
    end: String
    visaRequired: Boolean
  }

  type VisaApplication {
    approved: Boolean
    city: String
    country: String
    duration: Int
    firstName: String
    lastName: String
    nationality: String
    passportNumber: String
  }

  input TravelsArgument {
    and: [TravelsArgument!]
    or: [TravelsArgument!]
    flight: FlightArgument
    hotel: HotelArgument
    id: IdArgument
    traveller: TravellerArgument
    trip: TripArgument
    visaApplication: VisaApplicationArgument
    metadata: KogitoMetadataArgument
  }

  input FlightArgument {
    arrival: StringArgument
    departure: StringArgument
    flightNumber: StringArgument
    gate: StringArgument
    seat: StringArgument
  }

  input HotelArgument {
    address: AddressArgument
    bookingNumber: StringArgument
    name: StringArgument
    phone: StringArgument
    room: StringArgument
  }

  input AddressArgument {
    city: StringArgument
    country: StringArgument
    street: StringArgument
    zipCode: StringArgument
  }

  input TravellerArgument {
    address: AddressArgument
    email: StringArgument
    firstName: StringArgument
    lastName: StringArgument
    nationality: StringArgument
  }

  input TripArgument {
    begin: StringArgument
    city: StringArgument
    country: StringArgument
    end: StringArgument
    visaRequired: BooleanArgument
  }

  input VisaApplicationArgument {
    approved: BooleanArgument
    city: StringArgument
    country: StringArgument
    duration: NumericArgument
    firstName: StringArgument
    lastName: StringArgument
    nationality: StringArgument
    passportNumber: StringArgument
  }

  input TravelsOrderBy {
    flight: FlightOrderBy
    hotel: HotelOrderBy
    traveller: TravellerOrderBy
    trip: TripOrderBy
    visaApplication: VisaApplicationOrderBy
    metadata: KogitoMetadataOrderBy
  }

  input FlightOrderBy {
    arrival: OrderBy
    departure: OrderBy
    flightNumber: OrderBy
    gate: OrderBy
    seat: OrderBy
  }

  input HotelOrderBy {
    address: AddressOrderBy
    bookingNumber: OrderBy
    name: OrderBy
    phone: OrderBy
    room: OrderBy
  }

  input AddressOrderBy {
    city: OrderBy
    country: OrderBy
    street: OrderBy
    zipCode: OrderBy
  }

  input TravellerOrderBy {
    address: AddressOrderBy
    email: OrderBy
    firstName: OrderBy
    lastName: OrderBy
    nationality: OrderBy
  }

  input TripOrderBy {
    begin: OrderBy
    city: OrderBy
    country: OrderBy
    end: OrderBy
    visaRequired: OrderBy
  }

  input VisaApplicationOrderBy {
    approved: OrderBy
    city: OrderBy
    country: OrderBy
    duration: OrderBy
    firstName: OrderBy
    lastName: OrderBy
    nationality: OrderBy
    passportNumber: OrderBy
  }

  type VisaApplications {
    id: String
    visaApplication: VisaApplication
    metadata: KogitoMetadata
  }

  input VisaApplicationsArgument {
    and: [VisaApplicationsArgument!]
    or: [VisaApplicationsArgument!]
    id: IdArgument
    visaApplication: VisaApplicationArgument
    metadata: KogitoMetadataArgument
  }

  input VisaApplicationsOrderBy {
    visaApplication: VisaApplicationOrderBy
    metadata: KogitoMetadataOrderBy
  }

  input JobArgument {
    and: [JobArgument!]
    or: [JobArgument!]
    id: IdArgument
    processId: StringArgument
    processInstanceId: IdArgument
    rootProcessInstanceId: IdArgument
    rootProcessId: StringArgument
    status: JobStatusArgument
    expirationTime: DateArgument
    priority: NumericArgument
    scheduledId: IdArgument
    lastUpdate: DateArgument
    endpoint: StringArgument
    nodeInstanceId: StringArgument
  }

  input JobOrderBy {
    processId: OrderBy
    rootProcessId: OrderBy
    status: OrderBy
    expirationTime: OrderBy
    priority: OrderBy
    retries: OrderBy
    lastUpdate: OrderBy
    executionCounter: OrderBy
  }

  input JobStatusArgument {
    equal: JobStatus
    in: [JobStatus]
  }

  type Job {
    id: String!
    processId: String
    processInstanceId: String
    rootProcessInstanceId: String
    rootProcessId: String
    status: JobStatus!
    expirationTime: DateTime
    priority: Int
    callbackEndpoint: String
    repeatInterval: Int
    repeatLimit: Int
    scheduledId: String
    retries: Int
    lastUpdate: DateTime
    executionCounter: Int
    endpoint: String
    nodeInstanceId: String
  }

  enum JobStatus {
    ERROR
    EXECUTED
    SCHEDULED
    RETRY
    CANCELED
  }
`;
