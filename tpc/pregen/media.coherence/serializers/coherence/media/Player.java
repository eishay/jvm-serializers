package serializers.coherence.media;

import com.seovic.pof.annotations.PortableType;

/**
 * Created by a593223 on 11/5/2015.
 */
@PortableType(id = 20001, version = 1)
public enum Player {
    JAVA, FLASH;

    public static Player find(String str) {
        if (str == "JAVA") return JAVA;
        if (str == "FLASH") return FLASH;
        if ("JAVA".equals(str)) return JAVA;
        if ("FLASH".equals(str)) return FLASH;
        String desc = (str == null) ? "NULL" : String.format("'%s'", str);
        throw new IllegalArgumentException("No Player value of "+desc);
    }
}
