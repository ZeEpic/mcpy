package com.mcpy.lang.translation.function

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Function

class SendMessageFunction : Function(
    mutableListOf(),
    Type.VOID,
    Name("sendMessage", Name.NameType.FUNCTION),
    "CommandSender commandSender, String message",
    "commandSender.sendMessage(message);"
)
