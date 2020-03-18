package lexical;
/*
program → block
block→{ decls  stmts}
decls → decls  decl  | ε
decl → type  id;
type → type[num]  |  basic
stmts → stmts  stmt | ε

stmt → loc=bool;
      | if(bool)stmt
      | if(bool)stmt else stmt
      | while(bool)stmt
      | do stmt while(bool);
      | break;
      | block
Loc → loc[bool]  | id
bool →bool  ||  join   |  join
join → join  ＆＆  equality  | equality
equality → equality==rel  | equality ！= rel  | rel
rel → expr<expr |expr<=expr|expr>=expr|expr>expr|expr
expr → expr+term |expr-term |term
term → term*unary|term/unary|unary
unary→！unary | -unary | factor
factor→ (bool) | loc | num | real | true |false
 */

import util.FileUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LexicalAnalysis {
    private static String[] keys = {"if", "else", "while", "do", "break","int", "bool", "string"};
    private int state = 0;
    private static Set<String> keySet;

    static {
        keySet = new HashSet<String>();
        for (String str : keys)
            keySet.add(str);
    }

    private FileUtil fileUtil;

    public LexicalAnalysis(String readPath, String writePath) {
        try {
            fileUtil = new FileUtil(readPath, writePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String returnToken() {
        String token = "";
        if (state <= 8) {
            token = "<" + fileUtil.getToken() + ", relop>";
        } else if (state == 11) {
            if (keySet.contains(fileUtil.getToken())) {
                token = "<" + fileUtil.getToken() + ", key>";
            } else {
                token = "<" + fileUtil.getToken() + ", id>";
            }
        } else if (state == 19) {
            token = "<" + fileUtil.getToken() + ", num>";
        } else if (state == 30) {
            token = "<" + fileUtil.getToken() + ", delim>";
        }
        return token;
    }

    public String getNextToken() throws IOException {
        fileUtil.getNextToken();
        state = 0;
        while (true) {
            char c = fileUtil.getNextChar();
            switch (state) {
                case 0:
                    if (isBlank(c)) {
                        state = 0;
                        c = fileUtil.getNextChar();
                        break;
                    } else if (isRelop(c)) {
                        // case 1 ~ 8
                        c = fileUtil.getNextChar();
                        anaRelop(c);
                        return returnToken();
                    } else {
                        fail();
                    }
                    break;
                case 9:
                    if (isLetter(c)) {
                        // case 9 ~ 11
                        while (true) {
                            c = fileUtil.getNextChar();
                            if (!isLetter(c) && !Character.isDigit(c)) {
                                fileUtil.retract();
                                break;
                            }
                        }
                        state = 11;
                        return returnToken();
                    } else {
                        fail();
                    }
                    break;
                case 12:
                    if (Character.isDigit(c)) {
                        // case 12 ~27
                        anaDigit();
                        state = 19;
                        return returnToken();
                    } else {
                        fail();
                    }
                    break;
                case 25:
                    if (isDelim(c)) {
                        // case 28 ~30
                        while (true) {
                            c = fileUtil.getNextChar();
                            if (!isDelim(c)) {
                                fileUtil.retract();
                                break;
                            }
                        }
                        state = 30;
                        return returnToken();
                    } else {
                        fail();
                    }
                    break;
                default: System.out.println("error"); break;
            }
        }

    }

    public void fail() {
        int start = state;
        fileUtil.resetForward();
        switch (start) {
            case 0: state = 9; break;
            case 9: state = 12; break;
            case 12: state = 25; break;
            default: System.out.println("error: fail"); break;
        }
    }

    // isXXX function
    boolean isBlank(char c) {
        if (c == 13 || c == 10 || c == 32) {
            state = 0;
            return true;
        }
        return false;
    }

    boolean isRelop(char c) {
        switch (c) {
            case '<': state = 1; return true;
            case '=': state = 5; return true;
            case '>': state = 6; return true;
            default: return false;
        }
    }

    boolean isLetter(char c) {
        if (Character.isLetter(c)) {
            state = 9;
            return true;
        }
        return false;
    }

    boolean isDelim(char c) {
        if (c == '{' || c == '}' || c == '(' || c == ')') {
            state = 28;
            return true;
        }
        return false;
    }

    // anaXXX function
    public void anaRelop(char c) {
        switch (state) {
            case 1:
                if (c == '=') {
                    state = 2;
                } else if (c == '>') {
                    state = 3;
                } else {
                    state = 4;
                    fileUtil.retract();
                }
            case 6:
                if (c == '=') {
                    state = 7;
                } else {
                    state = 8;
                    fileUtil.retract();
                }
            default: System.out.println("not in relop"); break;
        }
    }

    void anaDigit() throws IOException {
        addDigit();
        while (true) {
            char c = fileUtil.getNextChar();
            switch (c) {
                case '.':
                    addDigit(); break;
                case 'E':
                    c = fileUtil.getNextChar();
                    if (c == '+' || c == '-') {
                        fileUtil.getNextChar();
                        addDigit();
                    } else if (Character.isDigit(c)) {
                       addDigit();
                    }
                default: fileUtil.retract(); return; // 19 24 27
            }
        }
    }

    void addDigit() throws IOException {
        char c;
        while (true) {
            c = fileUtil.getNextChar();
            if (!Character.isDigit(c))
                break;
        }
        fileUtil.retract();
    }

}

