trait border(is_controlling: bool) by entity

border_mobs = {
    world(Config.overworld_name): {
        "name": "&3World &bBorder",
        "color": ChatColor.AQUA,
        "mob": None
    },
    world(Config.nether_name): {
        "name": "&4World &cBorder",
        "color": ChatColor.RED,
        "mob": None
    }
}

timer move_border(1) {
    for world, mob_info in border_mobs {
        mob = mob_info["mob"]
        if mob is not None or not mob.is_valid {
            border_mobs[world] = find_matching_entity(world)
            continue
        }
        world.world_border.center = @mob
        mob.custom_name = mob_info["name"]
        if mob.vehicle is not None and mob.vehicle.type != PLAYER {
            mob.leave_vehicle()
        }
        mob.portal_cooldown = 2000
    }
}

timer respawn_border(30 * 60) {
    world = world(CONFIG.overworld_name)
    border_mobs[world]["mob"] = create_border_mob(world)
    broadcast("\n&aThe world border was been respawned in the overworld.\n")
}