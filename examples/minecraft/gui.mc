# Define a gui
# One could pass parameters through here that should show up on the gui, like player or location
gui test_gui(p: player) {
    # Title is optional
    title = "&6Test GUI"
    # A space is always air
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
        case "1" { # these must be the same as the legend
            p.send("You clicked item 1.")
        }
        case "2" {
            p.send("You clicked item 2.")
        }
        case "3" {
            p.send("You clicked item 3.")
        }
        case _ {
            # Nothing happens when clicking anything else
        }
    }
}
