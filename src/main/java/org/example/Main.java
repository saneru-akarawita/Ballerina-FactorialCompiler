package org.example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import java.io.FileOutputStream;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

class Expr {
}

class Var extends Expr {
    String name;

    public Var(String name) {
        this.name = name;
    }
}

class Func {
    public Func(String name, Var[] args, Stmt[] stmts) {
        this.name = name;
        this.args = args;
        this.stmts = stmts;
    }

    public String name;
    Var[] args;
    Stmt[] stmts;

}

class Stmt {
}

class Return extends Stmt {
    Expr expr;

    public Return(Expr expr) {
        this.expr = expr;
    }
}

public class Main {
    public static void main(String[] args) {
        Stmt[] stmt = new Stmt[]{
                new Return(new Var("a"))
        };
        Func func = new Func("Factorial", new Var[]{new Var("a")}, stmt);

        byte[] bytes = codeGen(func);

        try {
            FileOutputStream fos = new FileOutputStream(func.name + ".class");
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static byte[] codeGen(Func func) {
        ClassWriter classWriter = new ClassWriter(COMPUTE_MAXS);
        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, func.name, null, "java/lang/Object", null);
        genInit(classWriter);
        genFunc(func, classWriter);
        genFixedMain(func, classWriter);
        classWriter.visitEnd();
        return classWriter.toByteArray();

    }

    private static void genInit(ClassWriter classWriter) {

        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }

    private static void genFixedMain(Func func, ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        for (int i = 0; i < func.args.length ; i++) {
            parseArg(methodVisitor, i, i + 1);
        }
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        for (int i = 0; i < func.args.length ; i++) {
            methodVisitor.visitVarInsn(ILOAD, i + 1);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, func.name, func.name, genSig(func.args.length), false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private static void parseArg(MethodVisitor methodVisitor, int argIndex, int varIndex) {
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitIntInsn(BIPUSH, argIndex);
        methodVisitor.visitInsn(AALOAD);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
        methodVisitor.visitVarInsn(ISTORE, varIndex);
    }

    private static void genFunc(Func func, ClassWriter classWriter) {
        MethodVisitor methodVisitor;
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, func.name, genSig(func.args.length), null, null);
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(6, label0);
        methodVisitor.visitVarInsn(ILOAD, 0);
        Label label1 = new Label();
        methodVisitor.visitJumpInsn(IFNE, label1);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLineNumber(7, label2);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitInsn(IRETURN);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLineNumber(9, label1);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitVarInsn(ILOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 0);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitInsn(ISUB);
        methodVisitor.visitMethodInsn(INVOKESTATIC, func.name, func.name, "(I)I", false);
        methodVisitor.visitInsn(IMUL);
        methodVisitor.visitInsn(IRETURN);
        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitLocalVariable("n", "I", null, label0, label3, 0);
        methodVisitor.visitMaxs(3, 1);
        methodVisitor.visitEnd();
    }

    private static String genSig(int length) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < length; i++) {
            sb.append("I");
        }
        sb.append(")I");
        return sb.toString();
    }

}