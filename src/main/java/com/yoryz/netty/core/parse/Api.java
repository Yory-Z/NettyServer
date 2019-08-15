package com.yoryz.netty.core.parse;

import io.netty.handler.codec.http.HttpMethod;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/17 21:10
 */
public class Api {

    private String apiName;

    private String methodName;

    private String[] parameterTypesName;

    private Class<?>[] parameterTypes;

    private String[] parameterNames;

    private int parameterCount;

    private HttpMethod httpMethod;

    private Api(String apiName, String methodName, Class<?>[] parameterTypes,
                String[] parameterNames, boolean isPost) {
        this.apiName = apiName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameterNames = parameterNames;
        this.parameterCount = parameterTypes.length;
        httpMethod = isPost ? HttpMethod.POST : HttpMethod.GET;

        parameterTypesName = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypesName[i] = parameterTypes[i].getTypeName();
        }
    }

    static Api apiGet(String apiName, String methodName, Class<?>[] types, String[] parameterName) {
        return new Api(apiName, methodName, types, parameterName, false);
    }

    static Api apiPost(String apiName, String methodName, Class<?>[] types, String[] parameterName) {
        return new Api(apiName, methodName, types, parameterName, true);
    }

    public String getApiName() {
        return apiName;
    }

    public String getParamTypeName(int index) {
        return parameterTypesName[index];
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public String getParamName(int index) {
        return parameterNames[index];
    }

    public String[] getAllParamName() {
        return parameterNames;
    }

    public int getParamCount() {
        return parameterCount;
    }

    public boolean isHttpMethodEqual(HttpMethod method) {
        return httpMethod.equals(method);
    }
}
