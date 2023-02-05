def while_loop(p: player) {
    while (p.health > 5) {
        p.send_action_bar("&cYou are low on health!")
    }
}
