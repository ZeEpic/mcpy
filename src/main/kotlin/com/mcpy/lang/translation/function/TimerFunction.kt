package com.mcpy.lang.translation.function

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Function

class TimerFunction : Function(
    mutableListOf(),
    Type("void"),
    Name("beginTimer", Name.NameType.FUNCTION),
    "double seconds, Runnable runnable",
    "Bukkit.getScheduler().runTaskTimer(this, runnable, 0, (long) (20 * seconds));"
)