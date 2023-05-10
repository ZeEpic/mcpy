def find_open_nether_space(loc: location): location {
    loc.y = 32
    for x in range(-32, 32) {
        for z in range(-32, 32) {
            open_space = loc.world@(loc.x + x, loc.y, loc.z + z)
            if open_space.block.type.is_air {
                return open_space
            }
        }
    }
    return loc
}

def create_border_mob(world: world) {
    center = world.world_border.center
    if not center.is_chunk_loaded { return }
    if border_mobs[world]["mob"] is not None and border_mobs[world]["mob"].is_valid {
        return
    }
    find_matching_entity(world).remove()
    where = None
    if world.name == CONFIG.overworld_name {
        where = find_open_nether_space(center)
    } else {
        where = center.to_highest_location().add(0, 2, 0)
    }
    mob = world.spawn_entity(where, type, SpawnReason.CUSTOM)
    mob.border.is_controlling = true
    scoreboard = server.scoreboard_manager.main_scoreboard
    mob_info = border_mobs[world]
    team = scoreboard.getTeam(mob_info["name"])
    if team is None {
        team = scoreboard.register_new_team(mob_info["name"])
        team.color = mob_info["color"]
    }
    team.add_entity(mob)
    mob.is_custom_name_visible = true
    mob.is_persistent = true
    mob.custom_name = mob_info["name"]
    mob.portal_cooldown = 2000
    if mob is Tameable {
        mob.is_tamed = true
    }
    if mob is Steerable {
        mob.saddle = true
    }
    mob.add_potion(PotionEffectType.SLOW, 1)
    mob.is_glowing = true
}

def toggle_border_freeze(world: world): str {
    mob = border_mobs[world]
    if mob is None {
        return "The border mob in this world could not be found."
    }
    if mob.has_ai() {
        mob.ai = false
        return "The border mob has been frozen."
    }
    entity.ai = true
    return "The border mob has been released."
}

def find_matching_entity(world: world): entity {
    for entity in world.entities {
        if entity is LivingEntity and entity.border.is_controlling {
            return entity
        }
    }
}

def random_spawn_location(): location {
    diameter = CONFIG.world_border_size.overworld
    world = world(CONFIG.overworld_name)
    center = world.world_border.center
    x = center.x + random(diameter) - diameter / 2
    z = center.z + random(diameter) - diameter / 2
    y = world.highest_block_at(x, z) + 2
    return world@(x, y, z).to_center_location()
}