package com.mcpy.lang.translation.function

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.translation.identifier.Function
import org.bukkit.inventory.Inventory

class GuiMakerFunction : Function(
    mutableListOf(),
    Type(Inventory::class),
    Name("createGui", Name.NameType.FUNCTION),
    "List<String> patternList, String title, Map<Character, ItemStack> legend",
    "Inventory inventory = Bukkit.createInventory(null, pattern.length() / 9, title);\n" +
    "for (int i = 0; i < pattern.length(); i++) {\n" +
    "    char c = pattern.charAt(i);\n" +
    "    if (c == ' ') continue;\n" +
    "    if (!legend.containsKey(c)) continue;\n" +
    "    inventory.setItem(i, legend.get(c));\n" +
    "}\n" +
    "return inventory;"
)