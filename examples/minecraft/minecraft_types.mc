def minecraft_types() {

    # You can find a player like:
    p = player("ZeEpic")

    # An offline player like:
    offline = offline_player("ZeEpic")
    # Just be careful because this can be laggy

    # Worlds:
    main_world = world("world")

    # Locations are in this format: world@(x, y, z) or location(world, x, y, z)
    loc = main_world@(0, 0, 0)
    # You can find the block at a location like this:
    block1 = loc.block

    # You can reference the location of a player/entity like:
    player_location = @p

    # You can get a material with it's uppercase name
    # Be careful, some materials will be different from the ones you expect in Minecraft
    mat = IRON

    # You can get an entity in the same way:
    creeper = CREEPER

    # You can spawn an entity like:
    creeper.location = @p

    # You can change the data of an entity by doing this:
    horse = HORSE
    horse.location = @p
    data = horse.data() # note: this won't work for every mob! some don't have any data
    data.color = BLACK


    # Item has many forms:
    item1 = item(IRON_SWORD)
    item2 = item(IRON_BLOCK, 10)
    item3 = item(IRON_INGOT, "Steel Ingot", 2)
    item4 = item(IRON_INGOT, "Steel Ingot", ["This is a steel ingot.", "It's very strong!"], 5)

    # You can manipulate an item:
    item1.name = "Cooler Sword"
    item1.lore = ["This sword is cooler than the other one.", "It's also stronger!"]
    item1.enchant(SHARPNESS, 2)

    # You can manipulate a block:
    block1.type = CAMPFIRE
    block_data = block1.data()
    block_data.signal_fire = false # turns off the campfire

    # You can manipulate a player:
    p.gamemode = CREATIVE
    p.teleport(main_world@(0, 2, 0))
    p.give(item1)
    # Note: you can get a list of all online players with the 'players' variable

    # Permissions:
    perms = permission("cool.stuff")
    if p.has(perms) {
        p.give(item2)
    }

}
