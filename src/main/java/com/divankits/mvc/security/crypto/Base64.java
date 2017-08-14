package com.divankits.mvc.security.crypto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Base64 {

    int value() default Flag.Default;

    Type type() default Type.ByteArray;

    enum Type {

        ByteArray , String

    }

    class Flag  {

        public static final int Default = android.util.Base64.DEFAULT;
        public static final int NoWrap = android.util.Base64.NO_WRAP;
        public static final int NoPadding = android.util.Base64.NO_PADDING;
        public static final int NoClose = android.util.Base64.NO_CLOSE;
        public static final int UrlSafe = android.util.Base64.URL_SAFE;
        public static final int CRLF = android.util.Base64.URL_SAFE;

    }

}
