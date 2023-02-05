# Define a command called tp that can only be done by players, not console
cmd tp(target: player) by player {
    sender.send("You have been teleported to {target.name}.")
    # the @ symbol is used to refer to the location of the target
    sender.teleport(@target)
}
