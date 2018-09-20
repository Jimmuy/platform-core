package com.jimmy.dataservice.response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.jimmy.datamodel.GsonConverter;
import com.jimmy.datamodel.ResultModel;
import com.jimmy.dataservice.crypt.CryptorFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicApiResponse extends BasicHttpResponse implements ApiResponse {

    private byte[] decryptResult;
    private boolean isFromCache = false;

    public BasicApiResponse(int statusCode, Map<String, String> headers, byte[] result,
                            byte[] error) {
        this(statusCode, headers, result, error, false);
    }

    public BasicApiResponse(int statusCode, Map<String, String> headers, byte[] result,
                            byte[] error, boolean isFromCache) {
        super(statusCode, headers, result, error);
        this.isFromCache = isFromCache;
        decryptResult = decryptResultData();
    }

    private byte[] decryptResultData() {
        if (getResult() == null || getResult().length == 0) {
            return null;
        }
        Map<String, String> headers = getHeaders();
        if (headers != null && headers.get("Encryption") != null) {
            return CryptorFactory.decryptData(headers.get("Encryption"), getResult());
        } else {
            return CryptorFactory.decryptData(CryptorFactory.TYPE_ENCRYPT_NONE, getResult());
        }

    }

    @Override
    public boolean isFromCache() {
        return isFromCache;
    }

    @Override
    public JsonObject getJsonResult() {
        if (decryptResult == null || decryptResult.length == 0) {
            return null;
        }

        try {
            return (JsonObject) new JsonParser().parse(new String(decryptResult));
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> getResultMap() {
        if (decryptResult == null || decryptResult.length == 0) {
            return null;
        }

        try {
            return GsonConverter.decode(new String(decryptResult), new TypeToken<Map<String,Object>>(){}.getType());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public ResultModel getResultModel() {
        try {
            //remove '[]' to avoid decode error caused by php type ambiguity
            String jsonString = new String(decryptResult);
            Pattern pattern = Pattern.compile("\\{\"[a-zA-Z_]+?\":\\[\\],?");
            Matcher matcher = pattern.matcher(jsonString);
            jsonString = matcher.replaceAll("\\{");

            pattern = Pattern.compile(",\"[a-zA-Z_]+?\":\\[\\]");
            matcher = pattern.matcher(jsonString);
            jsonString = matcher.replaceAll("");

            ResultModel decode = GsonConverter.decode(jsonString, ResultModel.class);
            if (decode == null) {
                throw new Exception("model decode error");
            }

            return decode;
        } catch (Exception ex) {
            ex.printStackTrace();
            return createEmptyResultModel();
        }
    }

    private ResultModel createEmptyResultModel() {
        ResultModel result = new ResultModel();
        result.code = -1024;
        result.status = -1024;
        result.message = "model decode error";
        return result;
    }

}
