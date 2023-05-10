# Define a gui
# You can pass parameters through here that can be used to create the gui
# Guis never have a return value
gui test_gui(p: player) { # this player parameter is optional, it's just like a function definition
    # You must specify 2 variables for a gui:
        # "pattern" of type list[str]
        # "legend" of type dict[str, item]
    # You can optionally specify:
        # "match action" which is a match statement
        # "title" which is a variable of type str

    # Title is optional
    title = "&6Test GUI"

    # A space is always means air
    # Size of gui is determined by the pattern
    pattern = [
        "#########",
        "# 1 2 3 #",
        "#########"
    ]
    # Define what each item is
    # Items can have only a material, or can include a name and lore
    legend = {
        "#": item(GRAY_STAINED_GLASS_PANE, " "),
        "1": item(GRASS_BLOCK, "Item 1"),
        "2": item(DIAMOND, "Item 2"),
        "3": item(IRON_INGOT, "Item 3")
    }

    # Define what happens when each item is clicked
    # Action match statement is optional
    # Must match the 'action' keyword specifically
    match action {
        case "1" {  # these case statements must be the same as the characters in the legend,
                    # but you don't have to specify an action for every item in the gui
            p.send("You clicked item 1.")
        }
        case "2" {
            p.send("You clicked item 2.")
        }
        case "3" {
            p.send("You clicked item 3.")
        }
        case _ { # this is the default case, it's optional too
            # Nothing happens when clicking anything else
        }
    }
}
