
package ireader.proxy;

public class MathRodom {

    public static int toRodom(int Max, int min) {
        int i = (int) (min + Math.random() * (Max - min + 1));
        return i;
    }
}
