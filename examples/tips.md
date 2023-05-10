# Tips

## Getting Started

-   Learn python
-   If you're unsure on how to code something, assume it works the same as it does in python but replace a `:` with `{ }`
-   If it's something specific to Minecraft plugins, check this example folder
    -   If you're still stuck, ask in the Discord
-   Unlike python, adding tab spacing is not required, but is recommended
-   The error handling is intended to be as clear and helpful as possible, so if you have any improvement requests please leave them in the Discord server
-   If you're trying to do something niche, it's probably supported. Search in the [Spigot JavaDocs](https://hub.spigotmc.org/javadocs/spigot/).
    -   _Be careful! If you find a method starting with 'set', 'get', or 'is', use the rest of the method name in snake_case_
    -   _Player#getDisplayName is now player.display_name_

## Plugin.yml

-   The plugin.yml is a file that contains information about your plugin
-   The required fields are `name` and `version`, such as 1.0.0
-   You can find an overview of all of your options [here](https://www.spigotmc.org/wiki/plugin-yml/)
-   Ignore the `main` field, because it's generated for you
    -   _Including it will do nothing_
-   For your plugin's commands to work, be sure to add them to the `commands` field
-   The yml file format is very simple, but any typos will cause your plugin to not work

```yaml
name: ExamplePlugin
version: 1.0.0
commands:
    say:
        description: Say something
        usage: /say <message>
```

## For Python Users
-   Use `true` and `false` instead of `True` and `False`
-   Use `num` instead of `int` or `float`
-   Use `@` for locations, like `@target`
-   No need for f-strings
    -   _Use `"{code}"` instead of `f"{code}"`_
-   `import` doesn't exist
    -   _You are able to reference functions and global variables defined in other files of your plugin any time_
-   Tuples don't exist
    -   _Use a list instead_
-   Classes are not supported
-   Strong typing is required on functions
    -   `def function(arg: num): bool { }`
    -   In this example num is the type of the argument, and bool is the return type
    -   You don't need a return type on a function if it doesn't return anything
    -   Lists or dictionaries types are `list[num]` or `dict[str, num]`
