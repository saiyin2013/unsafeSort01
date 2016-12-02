package sort;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by saiyin on 16/12/1.
 *
 */
public class Tools {
    private static Unsafe unsafe;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            System.out.println("Get Unsafe instance occur error" + e);
        }
    }
    public static Unsafe getUnsafe() {
        return unsafe;
    }
    public static long sizeOf(Object o) {
        HashSet<Field> fields = new HashSet<Field>();
        Class c = o.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }

        // get offset
        long maxSize = 0;
        for (Field f : fields) {
            long offset = unsafe.objectFieldOffset(f);
            if (offset > maxSize) {
                maxSize = offset;
            }
        }

        return ((maxSize/8) + 1) * 8;   // padding
    }

    public static Object fromAddress(long address) {
        Object[] array = new Object[] {null};
        long baseOffset = getUnsafe().arrayBaseOffset(Object[].class);
        getUnsafe().putLong(array, baseOffset, address);
        return array[0];
    }
    public static long firstFieldOffset(Class clazz) {
        long minSize = roundUpTo8(headerSize(clazz));

        // Find the min offset for all the classes, up the class hierarchy.
        while (clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    long offset = unsafe.objectFieldOffset(f);
                    if (offset < minSize) {
                        minSize = offset;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return minSize;
    }

    private static long roundUpTo8(final long number) {
        return ((number + 7) / 8) * 8;
    }

    /**
     * Returns the size of the header for an instance of this class (in bytes).
     *
     * <p>More information <a href="http://www.codeinstructions.com/2008/12/java-objects-memory-structure.html">http://www.codeinstructions.com/2008/12/java-objects-memory-structure.html</a>
     * and <a href="http://stackoverflow.com/a/17348396/88646">http://stackoverflow.com/a/17348396/88646</a>
     *
     * <p><pre>
     * ,------------------+------------------+------------------ +---------------.
     * |    mark word(8)  | klass pointer(4) |  array size (opt) |    padding    |
     * `------------------+------------------+-------------------+---------------'
     * </pre>
     *
     * @param clazz
     * @return
     */
    public static long headerSize(Class clazz) {
        checkNotNull(clazz);
        // TODO Should be calculated based on the platform
        // TODO maybe unsafe.addressSize() would help?
        long len = 12; // JVM_64 has a 12 byte header 8 + 4 (with compressed pointers on)
        if (clazz.isArray()) {
            len += 4;
        }
        return len;
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static int[] testA(int sz){
        long startTime= System.currentTimeMillis(); //开始测试时间
        Random random = new Random();
        int a[] = new int[sz];
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(sz);
        }
        long endTime= System.currentTimeMillis(); //获取结束时间
        System.out.println("网上思路代码运行时间： "+(endTime-startTime)+"ms");
        return a;
    }

    public static void main(String[] args) {
        testA(1000000);
    }

}
