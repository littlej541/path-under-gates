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
- `"minecraft:*"` - all blocks in the `minecraft` domain
- `"minecraft"` - all blocks in the `minecraft` domain
- `"minecraft:stone"` - matches `minecraft:stone`
- `"minecraft:jungle_log"` - all `minecraft:jungle_log` varieties
- `"minecraft:jungle_log:*"` - all `minecraft:jungle_log` varieties
- `"minecraft:jungle_log:axis=x"` — all `minecraft:jungle_log` varieties with the property axis=x other properties ignored
- `"minecraft:oak_stairs:facing=east"` - all `minecraft:oak_stairs` varieties with the property `facing=east`, other properties ignored
- `"minecraft:oak_stairs:half=top"` - all `minecraft:oak_stairs` varieties with the property `half=top`, other properties ignored
- `"minecraft:oak_stairs:facing=east,half=top"` - all `minecraft:oak_stairs` varieties with the properties `facing=east` and `half=top`, other properties ignored
- `"ore:spruce_logs"` — all blocks under the `spruce_logs` vanilla tag
- `"ore:fences/wooden"` — all blocks under the `fences/wooden` Forge tag
- `"minecraft:logs"` — all blocks under the `logs` vanilla tag (this is a tag, though it looks like a block)

## New Feature: Modded Path Compatilibility

When a block is right-clicked with a shovel, a piece of code runs that determines if it can be flattened into a path. This code checks if the block is not underneath something and if the block has a possible conversion to a path. This mod works by preempting that code and doing the check and conversion itself. With mod added paths, there is an issue with the "possible conversion" part. Vanilla blocks have this information stored in a known location, but mod added blocks can have it anywhere. For this mod to perform the conversion, it needs to know what it should be. With that in mind, this mod now has the ability to receive that information. This comes in 2 flavors:

### Config File

Formatting is similar to whitelisting/blacklisting. It is of the form:

`modid:name|modid:name:properties`

The first option is the same format but does not allow for properties to be specified. The second does however, just like the list formatting. Any unspecified properties are assumed default. The major differences between the list format and these is that it is presented as a pair separated by a `|` and that tags/wildcards are **NOT** allowed. This config setting can be abused by the server owner to do unintended things, like turn blocks of coal into blocks of diamond, but surely none are so debauched.

Exaple entries include:

- `minecraft:snow_block|morepaths:snow_path` - Snow block will be flattened into snow path block from `morepath` mod
- `undergarden:deepsoil|ugpaths:deepsoil_path` - Deepsoil block from mod `undergarden` will be flattened into deepsoil ath block from `ugpaths`
- `minecraft:stone|minecraft:stone_slab:type=top` - Stone block will be flattened into top-half stone slab block
- `minecraft:coal_block|minecraft:diamond_block` - Coal block will be "flattened" into diamond block, though none would ever do this

### InterModComms

Mod authors will now be able to tell this mod what a block should turn into when trying to be flattened with a shovel. This involves the use of the `InterModComms` feature provided by Forge.

Messages sent to to this mod will be processed into a `Block`-`BlockState` pair. This is done by sending a a message with `"registerpath"` as its method and an `Object[]` of length 2 containing a `Block` object in the first position and a `BlockState` object in the second. Examples are as follows:

    private void sendComms(InterModEnqueueEvent event) {
        // Register stone blocks to slabs conversion when flattening with shovel
        InterModComms.sendTo("pathundergates", "registerpath", () -> new Object[]{Blocks.STONE, Blocks.STONE_SLAB.defaultBlockState()});
        InterModComms.sendTo("pathundergates", "registerpath", () -> new Object[]{Blocks.ANDESITE, Blocks.ANDESITE_SLAB.defaultBlockState()});
        InterModComms.sendTo("pathundergates", "registerpath", () -> new Object[]{Blocks.DIORITE, Blocks.DIORITE_SLAB.defaultBlockState()});
        InterModComms.sendTo("pathundergates", "registerpath", () -> new Object[]{Blocks.GRANITE, Blocks.GRANITE_SLAB.defaultBlockState()});
    }
