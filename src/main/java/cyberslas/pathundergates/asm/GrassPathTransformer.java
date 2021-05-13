package cyberslas.pathundergates.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class GrassPathTransformer implements IClassTransformer {
    private final static String classNameBlockGrassPath = "net.minecraft.block.BlockGrassPath";

    private final static String[] stringsBlockGrassPath_updateBlockStateName = {"b", "updateBlockState"};
    private final static String[] stringsBlockGrassPath_updateBlockStateDesc = {"(Lamu;Let;)V", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"};

    private final static String[] stringsBlockPos = {"et", "net/minecraft/util/math/BlockPos"};
    private final static String[] stringsBlockPos_upName = {"a", "up"};
    private final static String[] stringsBlockPos_upDesc = {"()Let;", "()Lnet/minecraft/util/math/BlockPos;"};

    private final static String stringUtil = "cyberslas/pathundergates/util/Util";
    private final static String stringUtil_blockAllowsPathBelowName = "blockAllowsPathBelow";
    private final static String[] stringUtil_blockAllowsPathBelowDesc = {"(Lamu;Let;)Z", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform) {
        byte[] transformedClass;

        switch(transformedName) {
            case classNameBlockGrassPath:
                transformedClass = transformBlockGrassPath(classToTransform, !transformedName.equals(name));
                break;
            default:
                return classToTransform;
        }

        return transformedClass;
    }

    private byte[] transformBlockGrassPath(byte[] classToTransform, boolean obfuscated) {
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(classToTransform);
        cr.accept(cn, 0);
        
        int index = obfuscated ? 0 : 1;

        for(MethodNode method : cn.methods) {
            if (method.name.equals(stringsBlockGrassPath_updateBlockStateName[index]) && method.desc.equals(stringsBlockGrassPath_updateBlockStateDesc[index])) {
                AbstractInsnNode targetNode = null;
                AbstractInsnNode[] instructions = method.instructions.toArray();

                for(int i = 0; i < instructions.length; ++i) {
                    if (instructions[i].getOpcode() == IFEQ) {
                        method.instructions.remove(instructions[i - 1]);
                        method.instructions.remove(instructions[i - 2]);
                        method.instructions.remove(instructions[i - 3]);
                        method.instructions.remove(instructions[i - 4]);
                        targetNode = instructions[i];
                        break;
                    }
                }

                instructions = null;

                /**
                 * Equivalent to changing:
                 * if (worldIn.getBlockState(pos.up()).getMaterial().isSolid())
                 * to:
                 * if (!Util.blockAllowsPathBelow(worldIn, pos.up()))
                 * in
                 * {@link net.minecraft.block.BlockGrassPath#updateBlockState(World, BlockPos)}
                 **/

                LabelNode exitLabel = ((JumpInsnNode) targetNode).label;

                InsnList toInject = new InsnList();
                toInject.add(new MethodInsnNode(INVOKEVIRTUAL, stringsBlockPos[index], stringsBlockPos_upName[index], stringsBlockPos_upDesc[index], false));
                toInject.add(new MethodInsnNode(INVOKESTATIC, stringUtil, stringUtil_blockAllowsPathBelowName, stringUtil_blockAllowsPathBelowDesc[index], false));
                toInject.add(new JumpInsnNode(IFNE, exitLabel));

                method.instructions.insertBefore(targetNode, toInject);
                method.instructions.remove(targetNode);

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
