package cyberslas.pathundergates.asm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GrassPathTransformer implements IClassTransformer {
    private final static String classNameItemSpade = "net.minecraft.item.ItemSpade";
    private final static String classNameBlockGrassPath = "net.minecraft.block.BlockGrassPath";

    private final static String[] stringsItemSpade = {"ajn", "net/minecraft/item/ItemSpade"};
    private final static String[] stringsItemSpade_onItemUseName = {"a", "onItemUse"};
    private final static String[] stringsItemSpade_onItemUseDesc = {"(Laed;Lamu;Let;Lub;Lfa;FFF)Lud;", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Lnet/minecraft/util/EnumActionResult;"};

    private final static String[] stringsBlockGrassPath = {"arc", "net/minecraft/block/BlockGrassPath"};
    private final static String[] stringsBlockGrassPath_updateBlockStateName = {"c", "updateBlockState"};
    private final static String[] stringsBlockGrassPath_updateBlockStateDesc = {"(Lamu;Let;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"};

    private final static String[] stringsWorld = {"amu", "net/minecraft/world/World"};
    private final static String[] stringsWorld_getBlockStateName = {"o", "getBlockState"};
    private final static String[] stringsWorld_getBlockStateDesc = {"(Let;)Lawt;", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"};

    private final static String[] stringsBlockPos = {"et", "net/minecraft/util/math/BlockPos"};
    private final static String[] stringsBlockPos_upName = {"a", "up"};
    private final static String[] stringsBlockPos_upDesc = {"()Let;", "()Lnet/minecraft/util/math/BlockPos;"};

    private final static String[] stringsIBlockState = {"awt", "net/minecraft/block/state/IBlockState"};
    private final static String[] stringsIBlockState_getBlockName = {"u", "getBlock"};
    private final static String[] stringsIBlockState_getBlockDesc = {"()Laow;", "()Lnet/minecraft/block/Block;"};

    private final static String[] stringsBlock = {"aow", "net/minecraft/block/Block"};
    private final static String[] stringsBlockFenceGate = {"aqp", "net/minecraft/block/BlockFenceGate"};

    private static final int OBFUSCATED = 0;
    private static final int UNOBFUSCATED = 1;

    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform) {
        byte[] transformedClass;

        switch(transformedName) {
            case classNameItemSpade:
                transformedClass = transformItemSpade(classToTransform, !transformedName.equals(classNameItemSpade) ? OBFUSCATED : UNOBFUSCATED);
                break;
            case classNameBlockGrassPath:
                transformedClass = transformBlockGrassPath(classToTransform, !transformedName.equals(classNameBlockGrassPath) ? OBFUSCATED : UNOBFUSCATED);
                break;
            default:
                return classToTransform;
        }

        return transformedClass;
    }

    private byte[] transformItemSpade(byte[] classToTransform, int obfuscation) {
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(classToTransform);
        cr.accept(cn, 0);

        for(MethodNode method : cn.methods) {
            if (method.name.equals(stringsItemSpade_onItemUseName[obfuscation]) && method.desc.equals(stringsItemSpade_onItemUseDesc[obfuscation])) {
                AbstractInsnNode targetNode = null;

                for(AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == ALOAD && ((VarInsnNode) instruction).var == 11) {
                        targetNode = instruction;
                        break;
                    }
                }

                /**
                 * Equivalent to changing:
                 * if (facing != EnumFacing.DOWN && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && block == Blocks.GRASS)
                 * to:
                 * if (facing != EnumFacing.DOWN && (worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR || worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFenceGate) && block == Blocks.GRASS)
                 * in
                 * {@link net.minecraft.item.ItemSpade#onItemUse(EntityPlayer, World, BlockPos, EnumHand, EnumFacing, float, float, float)}
                 **/

                InsnList toInject = new InsnList();
                LabelNode ln = new LabelNode();
                toInject.add(new JumpInsnNode(IF_ACMPEQ, ln));
                toInject.add(new VarInsnNode(ALOAD, 2));
                toInject.add(new VarInsnNode(ALOAD, 3));
                toInject.add(new MethodInsnNode(INVOKEVIRTUAL, stringsBlockPos[obfuscation], stringsBlockPos_upName[obfuscation], stringsBlockPos_upDesc[obfuscation], false));
                toInject.add(new MethodInsnNode(INVOKEVIRTUAL, stringsWorld[obfuscation], stringsWorld_getBlockStateName[obfuscation], stringsWorld_getBlockStateDesc[obfuscation], false));
                toInject.add(new MethodInsnNode(INVOKEINTERFACE, stringsIBlockState[obfuscation], stringsIBlockState_getBlockName[obfuscation], stringsIBlockState_getBlockDesc[obfuscation], true));
                toInject.add(new TypeInsnNode(INSTANCEOF, stringsBlockFenceGate[obfuscation]));
                LabelNode oldJumpLabel = ((JumpInsnNode) targetNode.getPrevious()).label;
                toInject.add(new JumpInsnNode(IFEQ, oldJumpLabel));
                toInject.add(ln);
//                toInject.add(new LineNumberNode(0, ln)); // what line number??

                method.instructions.insertBefore(targetNode.getPrevious(), toInject);

                method.instructions.remove(targetNode.getPrevious());

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformBlockGrassPath(byte[] classToTransform, int obfuscation) {
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(classToTransform);
        cr.accept(cn, 0);

        for(MethodNode method : cn.methods) {
            if (method.name.equals(stringsBlockGrassPath_updateBlockStateName[obfuscation]) && method.desc.equals(stringsBlockGrassPath_updateBlockStateDesc[obfuscation])) {
                AbstractInsnNode targetNode = null;

                for(AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == IFEQ) {
                        targetNode = instruction;
                        break;
                    }
                }

                /**
                 * Equivalent to changing:
                 * if (worldIn.getBlockState(pos.up()).getMaterial().isSolid())
                 * to:
                 * if (worldIn.getBlockState(pos.up()).getMaterial().isSolid() && worldIn.getBlockState(pos.up()).getBlock() instanceof BlockFenceGate != true)
                 * in
                 * {@link net.minecraft.block.BlockGrassPath#updateBlockState(World, BlockPos)}
                 **/

                LabelNode exitLabel = ((JumpInsnNode) targetNode).label;

                InsnList toInject = new InsnList();
                toInject.add(new VarInsnNode(ALOAD, 1));
                toInject.add(new VarInsnNode(ALOAD, 2));
                toInject.add(new MethodInsnNode(INVOKEVIRTUAL, stringsBlockPos[obfuscation], stringsBlockPos_upName[obfuscation], stringsBlockPos_upDesc[obfuscation], false));
                toInject.add(new MethodInsnNode(INVOKEVIRTUAL, stringsWorld[obfuscation], stringsWorld_getBlockStateName[obfuscation], stringsWorld_getBlockStateDesc[obfuscation], false));
                toInject.add(new MethodInsnNode(INVOKEINTERFACE, stringsIBlockState[obfuscation], stringsIBlockState_getBlockName[obfuscation], stringsIBlockState_getBlockDesc[obfuscation], true));
                toInject.add(new TypeInsnNode(INSTANCEOF, stringsBlockFenceGate[obfuscation]));
                toInject.add(new JumpInsnNode(IFNE, exitLabel));

                method.instructions.insert(targetNode, toInject);

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
