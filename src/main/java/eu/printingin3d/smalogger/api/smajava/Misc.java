package eu.printingin3d.smalogger.api.smajava;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Misc {
    private Misc() {}

    public static String printDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(date);
    }

    public static void hexDump(byte[] buf, int count, int radix) {
        int i, j;
        System.out.printf("--------:");
        for (i = 0; i < radix; i++) {
            System.out.printf(" %02X", i);
        }
        for (i = 0, j = 0; i < count; i++) {
            if (j % radix == 0) {
                /*
                 * if (i > 0) { for (int ii = radix; ii>0; ii--) System.out.print(((buf[i-ii] >=
                 * ' ') && (buf[i-ii] <= '~')) ? buf[i-ii] : '_'); }
                 */

                if (radix == 16) {
                    System.out.printf("\n%08X: ", j);
                } else {
                    System.out.printf("\n%08d: ", j);
                }
            }
            System.out.printf("%02X ", buf[i]);
            j++;
        }
        System.out.printf("\n");
    }
}
