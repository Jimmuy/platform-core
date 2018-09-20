package com.qcec.dataservice.request;

import com.qcec.datamodel.GsonConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class JsonBody extends RequestBody {
    private static final String CONTENT_TYPE_JSON_DESCRIPTION = "application/json; charset=UTF-8";

    private Map<String, Object> params = new HashMap<>();

    public JsonBody(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public MediaType contentType() {
        return MediaType.get(CONTENT_TYPE_JSON_DESCRIPTION);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.writeUtf8(GsonConverter.toJson(params));
    }

    public Map<String, Object> getParams() {
        return params;
    }

}
