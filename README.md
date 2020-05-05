# path-under-gates
 Minecraft mod that allows grass paths to exist under fence gates.

## White/Blacklist formatting

Format is of the form:

`modid:name:properties`

Name may be the wildcard operator `*`. If no name is specified, it is the same as having name being a wildcard.

Not all properties of a block must be defined, the white/blacklists will match against any properties defined but will ignore those that aren't. Properties are separated `,` with each property being a key-value pair of the format `key=value`. The properties may also be the wildcard operator `*`. If no properties are defined it is the same as them being a wildcard. Properties may also be a meta value, as an integer, that defines a blockstate.

If modid is defined as `ore`, the ore dictionary matched against, where name will be a label in the ore dictionary. Properties do not apply in this case.  
**Note:** Entries in the ore dictionary are parsed differently than those used by this mod. This is due to how ore dictionary entries are accessed in Forge. There is no way to differentiate between, for example, `<minecraft:log>`, `<minecraft:log:0>`, or `<minecraft:log:variant=oak,axis=y>`. This means they often have overly specific meta values that map to a single blockstate (unless they've got a wildcard for a meta value).

Example entries include:
- `minecraft:*` - all blocks in the `minecraft` domain
- `minecraft` - all blocks in the `minecraft` domain
- `minecraft:glass` - all `minecraft:glass` varieties
- `minecraft:stone` - all `minecraft:stone` varieties
- `minecraft:stone:*` - all `minecraft:stone` varieties
- `minecraft:stone:variant=granite` - all `minecraft:stone` varieties with the property `variant=granite`, other properties ignored
- `minecraft:oak_stairs:facing=east` - all `minecraft:oak_stairs` varieties with the property `facing=east`, other properties ignored
- `minecraft:oak_stairs:half=top` - all `minecraft:oak_stairs` varieties with the property `half=top`, other properties ignored
- `minecraft:oak_stairs:facing=east,half=top` - all `minecraft:oak_stairs` varieties with the properties `facing=east` and `half=top`, other properties ignored
- `minecraft:log:0` - `minecraft:log` with meta value `0`, resolves to `minecraft:log:variant=oak,axis=y`
- `ore:woodLog` - all blocks under the `woodLog` ore dictionary entry

**Final Note:** If you still need more help, see the [CraftTweaker](https://www.curseforge.com/minecraft/mc-mods/crafttweaker) wiki for version 1.12. The formatting is almost a one-to-one mapping of Bracket Handlers from that mod.
