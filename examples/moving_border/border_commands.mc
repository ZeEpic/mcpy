cmd worldcenter() by player {
    sender.send("&7Your world's border is at &3{sender.world.world_border.center}&7.")
}

cmd spawn() by player {
    sender.send("&7Teleporting to spawn in &65 seconds&7! Don't move.")
    old_location = sender.location
    delay(5)
    if old_location.distance(sender.location) >= 1 {
        sender.send("&cDon't move while teleporting!")
        return
    }
    player.teleport(random_spawn_location())
    sender.send("&aYou are now in spawn.")
}

cmd forcespawn(target: player) {
    target.teleport(random_spawn_location())
    sender.send("{target} was sent to spawn.")
}

cmd borderhat() by player {
    mob = border_mobs[sender.world]
    if mob is None {
        sender.send("Could not find the border mob.")
        return
    }
    if mob["mob"] is None {
        sender.send("Could not find the border mob.")
        return
    }
    sender.add_passenger(mob["mob"])
    sender.send("You now have a border hat!")
}

cmd borderfix(border_type: EntityType = None) by player {
    if border_type in [PLAYER, VILLAGER, ENDER_DRAGON, CREEPER] {
        return
    }
    mob = world_borders[sender.world]
    if mob is None {
        sender.send("Could not create the border mob in this world.")
        return
    }
    if border_type is not None {
        mob["mob"].type = border_type
    }
    create_border_mob(sender.world)
    player.send("Border mob regenerated in your world!")
}

cmd eject() by player {
    for entity in sender.passengers {
        sender.remove_passenger(entity)
    }
    sender.send("Your passengers have been ejected!")
}

cmd freezeborder() by player {
    sender.send(toggle_border_freeze(sender.world))
}