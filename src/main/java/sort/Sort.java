package sort;

import sun.misc.Unsafe;

/**
 * Created by saiyin on 16/12/1.
 *
 */
public class Sort {
    private final long base;
    private final long firstFieldOffset;
    private final long elementSize;
    private final long elementSpacing;
    private final Unsafe unsafe;

    public Sort() {
        unsafe = Tools.getUnsafe();
        firstFieldOffset = Tools.firstFieldOffset(Integer.class);
        elementSize = Tools.sizeOf(Integer.class) - firstFieldOffset;
        elementSpacing = Math.max(8, elementSize);
        base = unsafe.allocateMemory(elementSize * 100);
    }

    public void sortNumber() {
        int[] isrc = Tools.testA(100);
        for (int i = 1; i <= isrc.length; i++) {
            long to = offset(i);
            unsafe.copyMemory(isrc[i - 1], firstFieldOffset, null, to, elementSize);
        }
        long startTime = System.currentTimeMillis();
        sort(isrc.length);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);
        System.out.println("输出排序好的结果");
        for (int i = 1; i <= isrc.length; i++) {
            int s = unsafe.getInt(base + elementSize * i);
            System.out.println(s);
        }
    }

    public void sort(int size) {
        for(int i=1;i<=size;i++) {
            // System.out.println("start:" + start);
            for (int j=i;j<=size;j++) {
                int start = unsafe.getInt(base + elementSize * i);
                int end   = unsafe.getInt(base + elementSize * j);
                if(start > end) {
                    Integer temp =0;
                    unsafe.putInt(temp,firstFieldOffset,start);
                    unsafe.copyMemory(null, offset(j), null, offset(i), elementSpacing);
                    unsafe.copyMemory(temp, firstFieldOffset, null, offset(j), elementSize);
                }
            }
        }
    }

    private long offset(int index) {
        return base + index * elementSpacing;
    }

    private static long normalize(int value) {
        if (value >= 0) return value;
        return (~0L >>> 32) & value;
    }

    public static void main(String[] args) {
        Sort sort = new Sort();
        sort.sortNumber();
    }
}
