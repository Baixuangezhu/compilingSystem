import lexical.LexicalAnalysis;

import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        String readPath = "/home/yyx/IdeaProjects/compilingSystem/file/testDecls.txt";
        String writePath = "/home/yyx/IdeaProjects/compilingSystem/log/test.log";
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(readPath, writePath);
        try {
            String token = lexicalAnalysis.getNextToken();
            System.out.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
