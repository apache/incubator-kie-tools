# Permission Resolution

Consider, for instance, the following security policy:

```
role.admin.permission.perspective.read=true
role.manager.permission.perspective.read=false
```
Consider also a user belonging to both admin and manager roles.

What does the following call return?

```
boolean result = authzManager.authorize(perspective1, user);
```

This is a conflictive scenario wich requires to understand how the permission resolution mechanism works.

#### Voting strategy

The _AuthorizationManager_ interface provides different voting strategies. A voting strategy is a very simple algorithm that given a partial list of results chooses a winner. There exists 4 available strategies:

| Strategy    | Description
| ------------|--------------
| AFFIRMATIVE | It is the most lenient strategy. Only a single positive vote is required
| CONSENSUS   | It is based on general agreement. It requires a majority of positive votes
| UNANIMOUS   | It is the least lenient strategy. It requires a 100% of positive votes
| PRIORITY    | It is based on role/group priorities. The highest priority result wins

The voting strategy can be passed as a parameter to any of the methods provided by the _AuthorizationManager_. For example:

```
boolean result = authzManager.authorize(perspective1, user, VotingStrategy.AFFIRMATIVE);
```

Given the example at the beginning of this section, the answer to the question varies depending on the strategy chosen:


| Strategy    | Result
| ------------|----------
| AFFIRMATIVE | true
| CONSENSUS   | false
| UNANIMOUS   | false
| PRIORITY    | (Role priority, see below)

When no voting strategy is passed as a parameter then the system's default voting strategy is used, which can be read or changed as follows:

```
@Inject
PermissionManager permissionManager;

int defaultStrategy = permissionManager.getDefaultVotingStrategy();
permissionManager.setDefaultVotingStrategy(VotingStrategy.AFFIRMATIVE);

```

Notice, the system is configured by default to use the _VotingStrategy.PRIORITY_

#### Role priority

The `PRIORITY` based strategy is a bit special since it requires to
set a priority level for each role within the security policy. If no priority is defined then the value 0 is taken.

Given so, the answer to the question at the initial of this section would be `true` since the two roles have _priority=0_, in such case the first role (_admin_) is taken, which means result=_true_.

Now consider the following changes to the policy:

```
role.admin.priority=1
role.manager.priority=2
role.admin.permission.perspective.read=true
role.manager.permission.perspective.read=false
```

In this case the result would be _false_ since the manager role has higher priority.
