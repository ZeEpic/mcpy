package com.rimlang.rim.translation.function

import com.rimlang.rim.translation.identifier.Function
import com.rimlang.rim.abstractions.Name
import com.rimlang.rim.abstractions.Type

class TimerFunction : Function(
    mutableListOf(),
    Type("void"),
    Name("beginTimer", Name.NameType.FUNCTION),
    "double seconds, Runnable runnable",
    "Bukkit.getScheduler().runTaskTimer(this, runnable, 0, (long) (20 * seconds));"
)