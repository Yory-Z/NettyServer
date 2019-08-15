package com.yoryz.netty.core.parse;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/17 12:27
 */
public class ParameterNameVisitor extends ClassVisitor {

    private static final String STATIC_CLASS_INIT = "<clinit>";
    private static final String CLASS_INIT = "<init>";

    private final Map<String, String[]> memberMap;

    ParameterNameVisitor(Map<String, String[]> memberMap) {
        super(AsmInfo.ASM_VERSION);
        this.memberMap = memberMap;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // exclude synthetic + bridged && static class initialization
        if (!isSyntheticOrBridged(access) &&
                !STATIC_CLASS_INIT.equals(name) &&
                !CLASS_INIT.equals(name)) {
            return new LocalVariableVisitor(memberMap, name, descriptor, isStatic(access));
        }
        return null;
    }

    private static boolean isSyntheticOrBridged(int access) {
        return (((access & Opcodes.ACC_SYNTHETIC) | (access & Opcodes.ACC_BRIDGE)) > 0);
    }

    private static boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) > 0);
    }


}
