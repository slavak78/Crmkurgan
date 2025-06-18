package httpclient.common;

import java.io.Serializable;

public class Header implements Serializable {

    private static final String TAG = "Header";

    private final String key;
    private final String value;

    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Header(String name, long value) {
        this(name, String.valueOf(value));
    }

    public Header(String name, double value) {
        this(name, String.valueOf(value));
    }

    public Header(String name, boolean value) {
        this(name, String.valueOf(value));
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Header) {
            Header h = (Header) obj;
            return h.key.equalsIgnoreCase(this.key);
        }

        return false;
    }
}
