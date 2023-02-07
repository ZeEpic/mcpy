# Define a command called "/tp" that can only be done by players, not console
# Commands that can be done by console or players don't need "by player" or "by console"
# Commands never return anything, unlike some functions
cmd tp(target: player) by player {
    sender.send("You have been teleported to {target.name}.")
    # the @ symbol is used to refer to the location of the target, a shortcut for target.location
    sender.teleport(@target)
}
