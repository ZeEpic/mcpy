def minecraft_types() {

    # You can find a player like:
    p = player("ZeEpic")

    # An offline player like:
    offline = offline_player("ZeEpic")
    # Just be careful because any code you run afterwards will be async, which can cause issues!

    # You can reference the location of a player/entity like:
    player_location = @p

    # You can get a material with it's uppercase name:
    m = IRON

    # You can get an entity in the same way:
    creeper = CREEPER

    # You can spawn an entity like:
    creeper.location = @p

    # Item has many forms:
    item1 = item(IRON_SWORD)
    item2 = item(IRON_BLOCK, 10)
    item3 = item(IRON_INGOT, "Steel Ingot", 2)
    item4 = item(IRON_INGOT, "Steel Ingot", ["This is a steel ingot.", "It's very strong!"], 5)

    # You can manipulate an item:
    item1.name = "Cooler Sword"
    item1.lore = ["This sword is cooler than the other one.", "It's also stronger!"]
    item1.enchant(SHARPNESS, 2)

    # Worlds:
    world = world("world")

    # You can manipulate a block:
    block1 = world@(0, 0, 0)
    block1.type = IRON_BLOCK

    # You can manipulate a player:
    p.gamemode = CREATIVE
    p.teleport(world@(0, 2, 0))
    p.give(item1)

    # Permissions:
    perms = permission("cool.stuff")
    if p.has(perms) {
        p.give(item2)
    }

}
