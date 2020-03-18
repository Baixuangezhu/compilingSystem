package util;

import java.io.*;
import java.util.Vector;

public class FileUtil {
    public  static final int bufferSize = 10;
    private File readFile;
    private InputStreamReader in;
    private Vector<char[]> buffers;
    private int begin = 0;
    private int forward = 0;
    private int bufferId = 0;
    private char cur_char = 0;

    public FileUtil(String readPath, String writePath) throws IOException {
        readFile = new File(readPath);
        in = new InputStreamReader(new FileInputStream(readFile));
        buffers = new Vector<char[]>();
        char[] buffer1 = new char[FileUtil.bufferSize];
        readNextBuffer(buffer1);
        char[] buffer2 = new char[FileUtil.bufferSize];
        readNextBuffer(buffer2);
        buffers.add(buffer1);
        buffers.add(buffer2);
    }

    public boolean getNextBuffer() throws IOException {
        if (!readNextBuffer(this.buffers.get(this.bufferId)))
            return false;
        this.bufferId = (this.bufferId + 1) % 2;
        this.begin = 0;
        return true;
    }

    public void getNextToken() {
        this.begin = this.forward;
    }

    public String getToken() {
        String token = "";
        for (int i = this.begin; i < this.forward; ++i) {
            token += this.buffers.get(this.bufferId)[i];
        }
        return token;
    }

    public void resetForward() {
        this.forward = this.begin;
    }

    public void retract() {
        this.forward --;
    }

    public char getNextChar() throws IOException {
        if (this.forward == FileUtil.bufferSize) {
            if (!getNextBuffer())
                return 0;
        } else if (this.buffers.get(this.bufferId)[this.forward] == 0) { // 0 means eof
            return 0;
        }
        cur_char = this.buffers.get(this.bufferId)[this.forward];
        this.forward++;
        return cur_char;
    }

    public boolean readNextBuffer(char[] buffer) throws IOException {
        int readNum = in.read(buffer);
        if (readNum <= 0)
            return false;
        try {
            if (readNum < buffer.length) {
                buffer[readNum] = 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return true;
    }
}
