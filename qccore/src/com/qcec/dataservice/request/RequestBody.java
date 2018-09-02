package com.qcec.dataservice.request;

import android.graphics.Bitmap;

import com.qcec.datamodel.GsonConverter;
import com.qcec.image.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sunyun on 16/4/22.
 */
public abstract class RequestBody {

    private static final String CONTENT_TYPE_URL_ENCODED_DESCRIPTION = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String CONTENT_TYPE_JSON_DESCRIPTION = "application/json; charset=UTF-8";
    private static final String CONTENT_TYPE_FILE_DESCRIPTION = "multipart/form-data; boundary=";

    public abstract String contentType();

    public abstract void writeTo(OutputStream out) throws IOException;

    public static RequestBody create(final String contentType, final String content) {
        return new RequestBody() {
            @Override public String contentType() {
                return contentType;
            }

            @Override public void writeTo(OutputStream out) throws IOException{
                out.write(content.getBytes("UTF-8"));
            }
        };
    }

    public static final class FormBody extends RequestBody {

        private Map<String, String> params = new HashMap<>();

        public FormBody(Map<String, String> params) {
            if(params != null) {
                this.params = params;
            }
        }

        public Map<String, String> getParams() {
            return params;
        }

        @Override
        public String contentType() {
            return CONTENT_TYPE_URL_ENCODED_DESCRIPTION;
        }

        public String encodeParams() throws IOException {
            StringBuilder encodedParams = new StringBuilder();
            Iterator uee = params.entrySet().iterator();
            while (uee.hasNext()) {
                Map.Entry entry = (Map.Entry) uee.next();
                encodedParams.append(URLEncoder.encode((String) entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }

            return encodedParams.toString();
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(encodeParams().getBytes("UTF-8"));
        }
    }

    public static final class JsonBody extends RequestBody {

        private Object data;

        public JsonBody(Object data) {
            this.data = data;
        }

        public Object getData() {
            return data;
        }

        public String getJson() {
            return GsonConverter.toJson(data);
        }

        @Override
        public String contentType() {
            return CONTENT_TYPE_JSON_DESCRIPTION;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            if(data != null) {
                out.write(getJson().getBytes("UTF-8"));
            }
        }
    }


    public static final class ImageBody extends RequestBody {

        private static final int IMAGE_SIZE = 800;

        private String path;
        private int orientation;

        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String LINE_END = "\r\n";

        public ImageBody(String path, int orientation) {
            this.path = path;
            this.orientation = orientation;
        }

        @Override
        public String contentType() {
            return CONTENT_TYPE_FILE_DESCRIPTION + BOUNDARY;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(path, IMAGE_SIZE, IMAGE_SIZE, orientation);
            ByteArrayOutputStream bitmapOs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bitmapOs);

            StringBuffer contentFormStart = new StringBuffer();
            contentFormStart.append("--" + BOUNDARY + LINE_END);
            contentFormStart.append("Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + "image.jpg" + "\"" + LINE_END);
            contentFormStart.append("Content-Type: application/octet-stream" + LINE_END);
            contentFormStart.append(LINE_END);

            out.write(contentFormStart.toString().getBytes());
            //文件内容
            out.write(bitmapOs.toByteArray());
            out.write(LINE_END.getBytes());

            String endMark = "--" + BOUNDARY + "--" + LINE_END + LINE_END;
            out.write(endMark.getBytes());
        }
    }

}
