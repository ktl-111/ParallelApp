package intlapp.dragonpass.com.mvpmodel.orther;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;

/**
 * Created by steam_l on 2019/3/6.
 * Desprition :不编码参数的body
 */

public class NoEncodedFormBody extends RequestBody {
    private static final MediaType CONTENT_TYPE = MediaType.get("application/x-www-form-urlencoded");

    private final List<String> encodedNames;
    private final List<String> encodedValues;

    NoEncodedFormBody(List<String> encodedNames, List<String> encodedValues) {
        this.encodedNames = Util.immutableList(encodedNames);
        this.encodedValues = Util.immutableList(encodedValues);
    }

    /**
     * The number of key-value pairs in this form-encoded body.
     */
    public int size() {
        return encodedNames.size();
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public long contentLength() {
        return writeOrCountBytes(null, true);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeOrCountBytes(sink, false);
    }

    /**
     * Either writes this request to {@code sink} or measures its content length. We have one method
     * do double-duty to make sure the counting and content are consistent, particularly when it comes
     * to awkward operations like measuring the encoded length of header strings, or the
     * length-in-digits of an encoded integer.
     */
    private long writeOrCountBytes(@Nullable BufferedSink sink, boolean countBytes) {
        long byteCount = 0L;

        Buffer buffer;
        if (countBytes) {
            buffer = new Buffer();
        } else {
            buffer = sink.buffer();
        }

        for (int i = 0, size = encodedNames.size(); i < size; i++) {
            if (i > 0)
                buffer.writeByte('&');
            buffer.writeUtf8(encodedNames.get(i));
            buffer.writeByte('=');
            buffer.writeUtf8(encodedValues.get(i));
        }

        if (countBytes) {
            byteCount = buffer.size();
            buffer.clear();
        }

        return byteCount;
    }

    public static final class Builder {
        private final List<String> names = new ArrayList<>();
        private final List<String> values = new ArrayList<>();
        private final Charset charset;

        public Builder() {
            this(null);
        }

        public Builder(Charset charset) {
            this.charset = charset;
        }

        public NoEncodedFormBody.Builder add(String name, String value) {
            if (name == null)
                throw new NullPointerException("name == null");
            if (value == null)
                throw new NullPointerException("value == null");

            names.add(name);
            values.add(value);
            return this;
        }


        public NoEncodedFormBody build() {
            return new NoEncodedFormBody(names, values);
        }
    }
}
