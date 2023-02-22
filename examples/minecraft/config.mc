def config() {
    # Config is defined in a file called config.yml, which will be create when your plugin is first run on a server

    # The config.yml file might look something like this:

    # my_value: 5
    # stuff:
    #   value: The config is working!
    #   another_value: false

    # Access it using 'CONFIG' and whatever properties you have defined
    # The values could be of any type, so be careful
    # If a user of your plugin puts text where they were supposed to put a number, your code might break!

    print(CONFIG.my_value) # Prints 5

    # If you have an indent in your config.yml, you can access those values like this:

    print(CONFIG.stuff.value) # Prints "The config is working!"
    print(CONFIG.stuff.another_value) # Prints "false"

}