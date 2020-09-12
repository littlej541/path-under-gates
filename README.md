# path-under-gates
 Minecraft mod that allows grass paths to exist under fence gates.

## White/Blacklist formatting

Format is of the form:

`modid:name:properties`

Name may be the wildcard operator `*`. If no name is specified, it is the same as having name being a wildcard.

Not all properties of a block must be defined, the white/blacklists will match against any properties defined but will ignore those that aren't. Properties are separated `,` with each property being a key-value pair of the format `key=value`. The properties may also be the wildcard operator `*`. If no properties are defined it is the same as them being a wildcard.

If modid is defined as `ore`, [vanilla](https://minecraft.gamepedia.com/Tag#Blocks) and [Forge](https://github.com/MinecraftForge/MinecraftForge/tree/1.15.x/src/generated/resources/data/forge/tags/blocks) tags will be matched against, where name will be a tag path. If a mod uses it's own tag namespace, that must be specified as the modid to match properly. The path portion of the tag is specified as the name. Properties do not apply in this case, and using them will cause a failure to match a tag.

When only modid and name are defined, tags will attempt to be matched against first. Failing that, the entry will try to be matched to a block instead.

Example entries include:
- `minecraft:*` - all blocks in the `minecraft` domain
- `minecraft` - all blocks in the `minecraft` domain
- `minecraft:stone` - matches `minecraft:stone`
- `minecraft:jungle_log` - all `minecraft:jungle_log` varieties
- `minecraft:jungle_log:*` - all `minecraft:jungle_log` varieties
- `minecraft:jungle_log:axis=y` - all `minecraft:jungle_log` varieties with the property `axis=y`, other properties ignored
- `minecraft:oak_stairs:facing=east` - all `minecraft:oak_stairs` varieties with the property `facing=east`, other properties ignored
- `minecraft:oak_stairs:half=top` - all `minecraft:oak_stairs` varieties with the property `half=top`, other properties ignored
- `minecraft:oak_stairs:facing=east,half=top` - all `minecraft:oak_stairs` varieties with the properties `facing=east` and `half=top`, other properties ignored
- `ore:spruce_logs` - all blocks under the `spruce_logs` vanilla tag
- `ore:fences/wooden` - all blocks under the `fences/wooden` Forge tag
- `minecraft:logs` - all blocks under the `logs` vanilla tag (this is a tag, though it looks like a block)

**Final Note:** If you still need more help with blocks, see the [CraftTweaker](https://www.curseforge.com/minecraft/mc-mods/crafttweaker) wiki for version 1.12. The formatting is almost a one-to-one mapping of Bracket Handlers from that mod.
