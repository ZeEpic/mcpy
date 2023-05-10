trait ice_sword(active: bool, last_used: time, duration: time) by item

# All recipe names must be unique from all other recipes in your plugin
# Recipes can't have parameters
recipe ice_sword() {
    # You must specify 3 variables for a recipe (order doesn't matter):
        # "shaped" of type bool
        # "pattern" of type list[str]
        # "legend" of type dict[str, material]
    # Shaped can be true or false, where true means the pattern must be followed exactly,
     # and false means the items can go in any shape
    shaped = true

    # Pattern must be 3 lines long and each line must be exactly 3 characters long
    # A space is always means air
    pattern = [
        " I ",
        " I ",
        " S "
    ]

    # The key is the string in the pattern and the value is the material
    # The string can't be a space and it can't be more than one character long
    legend = {
        "I": BLUE_ICE,
        "S": STICK
    }

    item = item(IRON_SWORD, "&eIce Sword", ["Cold!", "Deals frost damage."])
    item.add_enchantment(DAMAGE_ALL, 2)
    item.add_enchantment(DURABILITY, 3)
    item.ice_sword.active = true
    item.ice_sword.last_used = time(0)
    item.ice_sword.duration = time()

    # You must return the item that will be crafted using the recipe
    return item
}

# This is just for fun
when entities.damage by entity {
    if event.entity is not player { return }
    player = event.damager
    if not player.sneaking { return }
    item = player.inventory.item_in_main_hand
    if item is None { return }
    if not item.ice_sword.active { return }
    cool_down = time(10)
    if item.ice_sword.last_used + cool_down > now() { return }

    entity = event.entity
    duration = item.ice_sword.duration.ticks
    entity.add_potion_effect(potion(SLOW, duration, 2))
    entity.freeze_ticks = duration
    item.ice_sword.last_used = now()
}
