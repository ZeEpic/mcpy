when server.start {
    overworld = world(CONFIG.overworld_name)
    nether = world(CONFIG.nether_name)
    overworld.world_border.size = CONFIG.world_border_size.overworld
    nether.world_border.size = CONFIG.world_border_size.nether
    overworld.game_rule(GameRule.SPAWN_RADIUS, 100)
    overworld.world_border.damage_amount = 0
    nether.world_border.damage_amount = 0
}

when entities.damage {
    world = event.entity.world
    border_mob = border_mobs[world]
    if border_mob is None { return }
    entity = event.entity
    if entity is not LivingEntity { return }
    if not entity.border.is_controlling { return }
    if event.cause == DamageCause.VOID {
        location = entity.location
        location.y = -64
        location.block.type = Material.BEDROCK
        create_border_mob(world)
    }
    if border_mob["mob"] is None {
        border_mobs[world]["mob"] = entity
    }
}

when entities.vehicle.enter {
    if event.entity.border.is_controlling {
        event.cancel()
    }
}

when worlds.load {
    border_mob = border_mobs[event.world]
    if border_mob is None { return }
    if border_mob["mob"] is not None { return }
    border_mobs[event.world]["mob"] = find_matching_entity(event.world)
}

when entities.portal.enter {
    if event.entity.border.is_controlling {
        border = event.entity.world.world_border
        border.center = border.center.add(2, 2, 2)
        create_border_mob(event.entity.world)
    }
}

when entities.zap.pig {
    if event.entity.border.is_controlling {
        event.cancel()
    }
}

when entities.death {
    if event.entity.border.is_controlling {
        event.cancel()
    }
}
