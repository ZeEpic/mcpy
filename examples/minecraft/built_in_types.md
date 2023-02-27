## Built in Types
- All of these types can be created using a function with their respective name, like world("world name")
- Some of these can be referenced with an uppercase version, specifically the ones from Bukkit, like World and Player
- Every other type not on this list follows the PascalCase rules, like LivingEntity for example
str              A string like `"Hello World"`
num              Any integer or decimal like `25` or `10.2`
bool             A boolean value like `true` or `false`
location         A position in a Minecraft world like `world@(0, 64, 0)` or `location(world, 0, 64, 0)`
vector           A position without a world like `vector(0, 64, 0)` or `location.to_vector()`
block            A Minecraft block like `location.block` (not to be confused with material)
item             A Minecraft item like `item(STONE, 32)`
material         A Minecraft material like `STONE` or `block.type`
color            A chat color like RED or `color("#FFFFFF")`
entity           Any Minecraft entity
player           Any player currently on the server, like `player("ZeEpic")`
world            A Minecraft world such as `world("world name")`
time             Represents a time or length of time like `now()` or `time(1000)` meaning 1 second
list             A list of elements, like `["a", "b", "c"]` or `list("abc")`
dict             A list of key-value pairs, like `{ "a": 20, "b": 2, "c": 34 }`
inventory        An entity's inventory like `player.inventory`
attribute        An entity's attribute like `player.get_attribute(GENERIC_MAX_HEALTH)`
biome            A biome like `FOREST`
enchantment      An enchantment like `SHARPNESS`
potion           A potion including an effect type, duration, and amplifier



## Special Priority Enums
- Some classes are given special priority and don't need the class name like STONE_SWORD instead of Material.STONE_SWORD
     - In specific cases where there is a conflict the class name is required
     - You still have to use the class name if you want to have it as a return type or parameter
```
Material
ChatColor
EntityType
Enchantment
PotionEffectType
Biome
Attribute
Difficulty
```
