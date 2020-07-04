package pl.marcinchwedczuk.expr.transformers.cglib;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import pl.marcinchwedczuk.expr.Ast;
import pl.marcinchwedczuk.expr.AstPostorderTransformer;
import pl.marcinchwedczuk.expr.AstType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static pl.marcinchwedczuk.expr.AstType.NEGATE;

// Based on: https://dzone.com/articles/fully-dynamic-classes-with-asm
//
public class CompileTransformer implements AstPostorderTransformer<CompileTransformer.MaxStack> {
    public static Class<EvaluableExpression> compile(Ast ast) {
        var compiler = new CompileTransformer();
        compiler.initialize();

        var stats = ast.postOrder(compiler);

        return compiler.finalize0(stats);
    }

    private final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    private String fullClassName = null;
    private MethodVisitor evaluate = null;
    private Label evaluateStart = null;
    private Label evaluateEnd = null;

    // Locals [this, variables, functions, arr, tmp, tmp, func]
    // !!! Double needs two local slots (see tmp) !!!
    private final static int ARG_THIS = 0;
    private final static int ARG_VARIABLES = 1;
    private final static int ARG_FUNCTIONS = 2;
    private final static int VAR_ARR = 3;
    private final static int VAR_TMP = 4;
    private final static int VAR_FUNC = 6;
    private final static int TOTAL_LOCALS_SLOTS = 7;

    private void initialize() {
        String package0 = this.getClass().getPackageName();
        String className = "EvaluableExpression$" + System.currentTimeMillis();
        this.fullClassName = package0 + "." + className;

        cw.visit(V1_8,
                ACC_PUBLIC, // public class
                this.fullClassName.replace('.', '/'),
                null, // signature (null means not generic)
                "java/lang/Object", // superclass
                new String[]{ EvaluableExpression.class.getName().replace('.', '/') }); // interfaces

        declarePublicEmptyConstructor();
        initializeEvaluateMethod();
    }

    @SuppressWarnings("unchecked")
    private Class<EvaluableExpression> finalize0(MaxStack sl) {
        finalizeEvaluateMethod(sl);

        cw.visitEnd(); // Close class definition

        byte[] classBytes = cw.toByteArray();
        try {
            Files.write(Paths.get("generated.class"), classBytes);
        } catch (IOException e) { }

        DynamicClassLoader loader = new DynamicClassLoader();

        var clazz = (Class<EvaluableExpression>)
                loader.defineClass(fullClassName, classBytes);

        return clazz;
    }

    private void declarePublicEmptyConstructor() {
        MethodVisitor ctor = cw.visitMethod(
                ACC_PUBLIC, // public method
                "<init>",
                "()V", // descriptor
                null,  // signature (null means not generic)
                null); // exceptions (array of strings)

        ctor.visitCode();

        // super();
        ctor.visitVarInsn(ALOAD, ARG_THIS);
        ctor.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false); // Is this class an interface?
        ctor.visitInsn(RETURN);

        ctor.visitMaxs(1, 1);
    }

    private void initializeEvaluateMethod() {
        evaluate = cw.visitMethod(
                ACC_PUBLIC, // public method
                "evaluate", // name
                "(Ljava/util/Map;Ljava/util/Map;)D", // descriptor
                null, // signature (null means not generic)
                null); // exceptions (array of strings)
        evaluate.visitCode();

        evaluateStart = new Label();
        evaluate.visitLabel(evaluateStart);
    }

    private void finalizeEvaluateMethod(MaxStack maxStack) {
        evaluate.visitInsn(DRETURN);

        evaluateEnd = new Label();
        evaluate.visitLabel(evaluateEnd);

        evaluate.visitLocalVariable("args", "[D",
                null, evaluateStart, evaluateEnd, VAR_ARR);

        evaluate.visitLocalVariable("tmp", "D",
                null, evaluateStart, evaluateEnd, VAR_TMP);

        evaluate.visitLocalVariable("f", "Lpl/marcinchwedczuk/expr/transformers/cglib/Function;",
                null, evaluateStart, evaluateEnd, VAR_FUNC);

        evaluate.visitMaxs(maxStack.maxStack, TOTAL_LOCALS_SLOTS);
    }

    // ---------------------------------------------------------------

    @Override
    public MaxStack constant(double c) {
        evaluate.visitLdcInsn(c);
        return MaxStack.of(1);
    }

    @Override
    public MaxStack variable(String variableName) {
        evaluate.visitVarInsn(ALOAD, ARG_VARIABLES);
        evaluate.visitLdcInsn(variableName);
        evaluate.visitMethodInsn(INVOKEINTERFACE,
            "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        evaluate.visitTypeInsn(CHECKCAST, "java/lang/Double");
        evaluate.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/Double", "doubleValue", "()D", false);
        return MaxStack.of(2);
    }

    @Override
    public MaxStack binaryOperator(AstType type, MaxStack leftOperand, MaxStack rightOperand) {
        MaxStack maxStack = leftOperand.binaryOp(rightOperand);

        switch (type) {
            case ADD:
                evaluate.visitInsn(DADD);
                break;

            case SUBTRACT:
                evaluate.visitInsn(DSUB);
                break;

            case MULTIPLY:
                evaluate.visitInsn(DMUL);
                break;

            case DIVIDE:
                evaluate.visitInsn(DDIV);
                break;

            case MODULO:
                evaluate.visitInsn(DREM);
                break;

            case EXPONENTIATE:
                evaluate.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D", false);
                break;

            default:
                throw new IllegalArgumentException("Invalid binary operator " + type);
        }

        return maxStack;
    }

    @Override
    public MaxStack unaryOperator(AstType type, MaxStack operand) {
        switch (type) {
            case NEGATE:
                evaluate.visitInsn(DNEG);
                break;

            default:
                throw new IllegalArgumentException("Invalid unary operator " + type);
        }

        return operand;
    }

    @Override
    public MaxStack functionCall(String functionName, List<MaxStack> arguments) {
        evaluate.visitVarInsn(ALOAD, ARG_FUNCTIONS);
        evaluate.visitLdcInsn(functionName);
        evaluate.visitMethodInsn(INVOKEINTERFACE,
                "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        evaluate.visitTypeInsn(CHECKCAST, "pl/marcinchwedczuk/expr/transformers/cglib/Function");
        evaluate.visitVarInsn(ASTORE, VAR_FUNC);

        // Create an array
        evaluate.visitLdcInsn(arguments.size());
        evaluate.visitIntInsn(NEWARRAY, T_DOUBLE);
        evaluate.visitVarInsn(ASTORE, VAR_ARR);

        // Load arguments from stack into the array
        // Stack contains only arguments here:
        for (int i = 0; i < arguments.size(); i++) {
            evaluate.visitVarInsn(DSTORE, VAR_TMP);

            evaluate.visitVarInsn(ALOAD, VAR_ARR);
            evaluate.visitLdcInsn(arguments.size()-1 - i);
            evaluate.visitVarInsn(DLOAD, VAR_TMP);
            evaluate.visitInsn(DASTORE);
        }

        evaluate.visitVarInsn(ALOAD, VAR_FUNC);
        evaluate.visitVarInsn(ALOAD, VAR_ARR);
        evaluate.visitMethodInsn(INVOKEINTERFACE,
                "pl/marcinchwedczuk/expr/transformers/cglib/Function",
                "call",
                "([D)D", true);

        return MaxStack.of(3 + arguments.size());
    }

    public static class MaxStack {
        public static MaxStack of(int maxStack) {
            return new MaxStack(maxStack);
        }

        public final int maxStack;

        public MaxStack(int maxStack) {
            this.maxStack = maxStack;
        }

        public MaxStack binaryOp(MaxStack rightArg) {
            return MaxStack.of(Math.max(maxStack, rightArg.maxStack + 1));
        }
    }
}
