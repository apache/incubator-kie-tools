<!--

Welcome to the SonataFlow Operator! Before contributing, make sure to:

- Rebase your branch on the latest upstream main
- Link any relevant issues, PR's, or documentation
- Check that the commit message is concise and helpful:
    - When fixing an issue, add "Closes #<ISSUE_NUMBER>"
- Follow the below checklist

-->

**Description of the change:**

**Motivation for the change:**

**Checklist**

- [ ] Add or Modify a unit test for your change
- [ ] Have you verified that tall the tests are passing?

<details>
<summary>
How to backport a pull request to a different branch?
</summary>

In order to automatically create a **backporting pull request** please add one or more labels having the following format `backport-<branch-name>`, where `<branch-name>` is the name of the branch where the pull request must be backported to (e.g., `backport-7.67.x` to backport the original PR to the `7.67.x` branch).

> **NOTE**: **backporting** is an action aiming to move a change (usually a commit) from a branch (usually the main one) to another one, which is generally referring to a still maintained release branch. Keeping it simple: it is about to move a specific change or a set of them from one branch to another.

Once the original pull request is successfully merged, the automated action will create one backporting pull request per each label (with the previous format) that has been added.

If something goes wrong, the author will be notified and at this point a manual backporting is needed.

> **NOTE**: this automated backporting is triggered whenever a pull request on `main` branch is labeled or closed, but both conditions must be satisfied to get the new PR created.

</details>
