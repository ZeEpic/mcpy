# Define an event
when (blocks.break) {
    # This code is run when any block is broken by a player
    player.send("You broke a block of type {block.type}.")
    if block.type == GRASS_BLOCK {
        cancel() # Cancel the event, so the block is not broken
    }
}

# Define a player trait
# This line gives all players a property called "game_level"
# This could also be used to store amount of money, etc.
trait game_level(level: int) by player

# Define a command
cmd level(target: player, level: int = -1) {
    aliases = ["set-level", "lvl", "setlvl"]
    if level < 0 {
        sender.send("{target.name} is level {target.game_level}.")
        return
    }
    sender.send("{target.name} is now level {level}.")
    target.game_level = level
}

cmd gui() by player {
    # Open the gui defined above
    sender.open_gui(test_gui(sender))
}

# Define a gui
# One could pass parameters through here that should show up on the gui, like player or location
gui test_gui(p: player) {
    title = "&6Test GUI"
    # A space is always air
    # Size of gui is determined by the pattern
    pattern = [
        "#########",
        "# 1 2 3 #",
        "#########"
    ]
    # Define what each item is
    # items can have only a material, or can include a name and lore
    legend = {
        "#": item(GRAY_STAINED_GLASS_PANE, " "),
        "1": item(GRASS_BLOCK, "Item 1"),
        "2": item(DIAMOND, "Item 2"),
        "3": item(IRON_INGOT, "Item 3")
    }
    # Define what happens when each item is clicked
    match action {
        case "1" {
            p.send("You clicked item 1.")
        }
        case "2" {
            p.send("You clicked item 2.")
        }
        case "3" {
            p.send("You clicked item 3.")
        }
    }
}

# Define a command called tp that can only be done by players, not console
cmd tp(target: player) by player {
    sender.send("You have been teleported to {target.name}.")
    # the @ symbol is used to refer to the location of the target
    sender.teleport(@target)
}

# Define a timer called "clock"
timer clock(60) {
    broadcast("1 minute has passed.")
}
