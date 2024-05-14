```
Options:
  --help           Show help                                           [boolean]
  --version        Show version number                                 [boolean]
  --env            Name of the environment variables which value will be
                   compared to --eq.                       [array] [default: []]
  --bool           Boolean value to be used as condition   [array] [default: []]
  --eq, --equals   Value to be compared with the condition supplied. Both --bool
                   and --env.                         [string] [default: "true"]
  --operator       Comparison operator                 [string] [default: "and"]
  --then           Command(s) to execute if the condition is true.
                                                              [array] [required]
  --else           Command(s) to execute if the condition is false.
                                                           [array] [default: []]
  --true-if-empty  If the environment variable is not set, makes the condition
                   be true.                          [string] [default: "false"]
  --ignore-errors  Ignore non-zero exit values when running command(s).
                                                     [string] [default: "false"]
  --silent         Hide info logs from output. Logs from commands will still
                   show.                              [boolean] [default: false]
  --force          Makes condition be true. Runs command(s) supplied to --then.
                                                      [boolean] [default: false]
  --catch          Command(s) to execute at the end of execution if one of the
                   commands being executed fails.          [array] [default: []]
  --finally        Command(s) to execute at the end of execution. Provided
                   commands will run even if one of the commands being executed
                   fails.                                  [array] [default: []]


CLI tool to help executing shell scripts conditionally with a friendly syntax on
Linux, macOS, and Windows.


__NOTE FOR WINDOWS USAGE__:
Because 'run-script-if' was created with pnpm/Yarn/NPM scripts, environment
variables and Command Substitution syntax (`$(expr)`) in mind, 'run-script-if'
will force the provided commands to be executed on PowerShell.

This is because pnpm, Yarn, and NPM default to the CMD shell on Windows, making
it not ideal for Command Substitution-dependent commands.

Apart from using it on commands, it's also possible to use the Command
Substitution syntax on boolean conditions, like:

$ run-script-if --bool "$(my-custom-command --isEnabled)" --then "echo 'Hello'"
```

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may be missing the ASF Licensing Header
-

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
