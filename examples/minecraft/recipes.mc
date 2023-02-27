trait ice_sword(active: bool, last_used: time, duration: time) by item

# All recipe names must be unique from all other recipes in your plugin
recipe ice_sword() {
    # You must specify 4 variables for a recipe: pattern (list[str]), legend (dict[str, material]), shaped (bool), and result (item)
    # Shaped can be true or false, where true means the pattern must be followed exactly,
     # and false means the items can go in any shape
    shaped = true

    # Pattern must be 3 lines long and each line must be 3 characters long
    pattern = [
        " I ",
        " I ",
        " S ",
    ]

    # The key is the character in the pattern and the value is the material
    legend = {
        "I": BLUE_ICE,
        "S": STICK,
    }

    item = item(IRON_SWORD, "&eIce Sword", ["Cold!", "Deals frost damage."])
    item.add_enchantment(DAMAGE_ALL, 2)
    item.add_enchantment(DURABILITY, 3)
    item.ice_sword.active = true
    item.ice_sword.last_used = time(0)
    item.ice_sword.duration = time()

    # The result is the item that will be crafted
    result = item
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
