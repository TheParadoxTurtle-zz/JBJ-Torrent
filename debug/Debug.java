package debug;

public class Debug {
    public static final boolean _DEBUG = true;
    public static void print(String s) {
        if(_DEBUG)
            System.out.println(s);
    }

    public static void print(int s) {
        if(_DEBUG)
            System.out.println(s);
    }
}
