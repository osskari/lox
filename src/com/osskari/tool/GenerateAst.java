package com.osskari.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Logical  : Expr left, Token operator, Expr right",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = STR."\{outputDir}/\{baseName}.java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.osskari.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println(STR."abstract class \{baseName} {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String[] strings = type.split(":");
            String className = strings[0].trim();
            String fields = strings[1].trim();
            defineType(writer, baseName, className, fields);
            writer.println();
        }

        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");

        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(STR."    static class \{className} extends \{baseName} {");

        String[] fields = fieldList.split(", ");

        for (String field : fields) {
            writer.println(STR."        final \{field};");
        }

        writer.println();

        writer.println(STR."        \{className}(\{fieldList}) {");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(STR."            this.\{name} = \{name};");
        }

        writer.println("        }");

        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println(STR."            return visitor.visit\{className}\{baseName}(this);");
        writer.println("        }");

        writer.println("    }");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(STR."        R visit\{typeName}\{baseName}(\{typeName} \{baseName.toLowerCase()});");
            writer.println();
        }

        writer.println("    }");
    }
}
