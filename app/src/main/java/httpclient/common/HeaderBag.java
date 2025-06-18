package httpclient.common;

import java.util.ArrayList;

public class HeaderBag extends ArrayList<Header> {

    private static final String TAG = "HeaderBag";

    public boolean add(String key, String value) {
        return super.add(new Header(key, value));
    }

    public boolean add(String key, long value) {
        return add(new Header(key, value));
    }

    public boolean add(String key, double value) {
        return add(new Header(key, value));
    }

    public boolean add(String key, boolean value) {
        return add(new Header(key, value));
    }

    @Override
    public boolean add(Header header) {
        if (contains(header)) {
            int index = indexOf(header);
            remove(index);
        }

        return super.add(header);
    }
}
