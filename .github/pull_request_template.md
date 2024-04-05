Many thanks for submitting your Pull Request :heart:! 

Please make sure your PR meets the following requirements:

- [ ] You have read the [contributors guide](README.md#contributing-to-kogito-images-repository)
- [ ] Pull Request title is properly formatted: `[Issue XXXX] Subject`
- [ ] Pull Request contains link to the JIRA issue
- [ ] Pull Request contains description of the issue
- [ ] Pull Request does not include fixes for issues other than the main ticket
- [ ] Your feature/bug fix has a testcase that verifies it
- [ ] You've tested the new feature/bug fix in an actual OpenShift cluster
- [ ] You've added a [RELEASE_NOTES.md](RELEASE_NOTES.md) entry regarding this change

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

- <b>(Re)run Jenkins tests</b>  
  Please add comment: <b>Jenkins [test|retest] this</b>
</details>

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