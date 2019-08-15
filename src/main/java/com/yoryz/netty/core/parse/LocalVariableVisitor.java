package com.yoryz.netty.core.parse;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/17 13:56
 */
class LocalVariableVisitor extends MethodVisitor {

    private final Map<String, String[]> memberMap;

    private final String name;

    private final Type[] args;

    private final String[] parameterNames;

    private final boolean isStatic;

    /**
     * The nth entry contains the slot index of the LVT table entry holding the
     * argument name for the nth parameter.
     */
    private final int[] lvtSlotIndex;

    LocalVariableVisitor(Map<String, String[]> memberMap, String name, String desc, boolean isStatic) {
        super(AsmInfo.ASM_VERSION);
        this.memberMap = memberMap;
        this.name = name;
        this.args = Type.getArgumentTypes(desc);
        this.parameterNames = new String[this.args.length];
        this.isStatic = isStatic;
        this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
        this.memberMap.put(name, parameterNames);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        for (int i = 0; i < this.lvtSlotIndex.length; i++) {
            if (this.lvtSlotIndex[i] == index) {
                this.parameterNames[i] = name;
            }
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
        int[] lvtIndex = new int[paramTypes.length];
        int nextIndex = (isStatic ? 0 : 1);
        for (int i = 0; i < paramTypes.length; i++) {
            lvtIndex[i] = nextIndex;
            if (isWideType(paramTypes[i])) {
                nextIndex += 2;
            }
            else {
                nextIndex++;
            }
        }
        return lvtIndex;
    }

    private static boolean isWideType(Type aType) {
        // float is not a wide type
        return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
    }
}
