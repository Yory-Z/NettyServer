package com.yoryz.netty.core.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yoryz.netty.core.scan.AbstractPackageScanner;
import com.yoryz.netty.core.scan.ComponentFactory;
import com.yoryz.netty.exception.*;
import com.yoryz.netty.util.MyResponse;
import com.yoryz.netty.core.parse.Api;
import com.yoryz.netty.core.parse.InterfaceParser;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 18:19
 */
class Router {

    /**
     * single instance
     */
    private static final Router INSTANCE = new Router();
    private Router() {}
    static Router getInstance() {
        return INSTANCE;
    }

    private static final String BASE_PACKAGE = "java.lang.";

    private final Logger logger = LogManager.getLogger(Router.class);

    private final InterfaceParser interfaceParser = InterfaceParser.getInstance();

    private final AbstractPackageScanner componentFactory = ComponentFactory.getInstance();

    /**
     * the path must has more than one dimensions,
     * such as /user/, /user/userLogin, /admin/get/info
     * can not be /user, /admin
     * the root request / won't come to this method
     *
     * @param request the request
     * @return correct MyResponse instance
     */
    MyResponse analyse(FullHttpRequest request) {

        MyResponse myResponse;
        try {
            String[] twoDimensionUri = extractUri(request.uri());

            // extract second uri
            // if the uri have parameter, we need to slice the uri, get the first part
            // or we just get the twoDimensionUri[1] which is the second uri
            myResponse = handleRequest(
                    extractParameters(request), request.method(), twoDimensionUri[0], twoDimensionUri[1]);

        } catch (RequestUriNotMatchException e) {
            logger.warn(e.getMessage());
            myResponse = MyResponse.errorNotFound(request.method() + " " + request.uri());

        } catch (ParameterNameNotMatchException | RequestMethodNotSupportExceptoin |
                ParameterValueEmptyException | RequestJsonFormatWrongException e) {
            logger.warn(e.getMessage());
            myResponse = MyResponse.errorMsg(e.getMessage());

        } catch (Exception e) {
            logger.warn(e.getMessage());
            myResponse = MyResponse.serverException(e.getMessage());
        }
        return myResponse;
    }

    /**
     * extract second uri
     * if the uri have parameter, we ignore it
     * and then we slice the request uri into two part,
     *
     * the first part is between the first "/" and the second "/"(not including),
     * the second part is from the second "/" to the end of the uri without parameter
     *
     * @param uri request uri
     * @return uri array have two element
     *          the first element is the base uri
     *          the second element is the second uri
     * @throws RequestUriNotMatchException the request uri doesn't have second uri.
     *                                   ("/" only this could be second uri)
     */
    private String[] extractUri(String uri) throws RequestUriNotMatchException {
        int secondUriInd = -1;
        // find the index of the second "/"
        for (int i = 1; i < uri.length(); ++i) {
            if (uri.charAt(i) == '/') {
                secondUriInd = i;
                break;
            }
        }
        // doesn't found second uri
        if (secondUriInd == -1) {
            throw new RequestUriNotMatchException("uri not found");
        }

        // abandon the parameter string
        String methodMatch = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;

        String[] res = new String[2];
        res[0] = methodMatch.substring(0, secondUriInd);
        res[1] = methodMatch.substring(secondUriInd);
        return res;
    }

    /**
     * extract request parameters from request uri or contentBody
     * if the request have content body, it's format must be json
     *
     * @param request the httpRequest
     * @return parameter map, this could be null
     * @throws ParameterValueEmptyException when the uri's request parameter's value is empty
     * @throws RequestJsonFormatWrongException parse json format wrong
     */
    private Map<String, String> extractParameters(FullHttpRequest request)
            throws ParameterValueEmptyException, RequestJsonFormatWrongException {
        if (HttpMethod.POST.equals(request.method())) {
            if (request.content().readableBytes() > 0) {
                try {
                    return (Map) JSON.parse(request.content().toString(CharsetUtil.UTF_8));
                } catch (JSONException e) {
                    throw new RequestJsonFormatWrongException("Wrong request json format.");
                }
            }
            return null;
        }

        String uri = request.uri();
        if (!uri.contains("?")) {
            return null;
        }

        // we need to slice the uri firstly, we just need the string after the "?" symbol
        String[] params = uri.substring(uri.indexOf("?") + 1).split("&");
        Map<String, String> paramMap = new HashMap<>(params.length);

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length != 2) {
                throw new ParameterValueEmptyException(keyValue[0] + "'s value is empty.");
            }
            paramMap.put(keyValue[0], keyValue[1]);
        }
        return paramMap;
    }

    /**
     * handle the http request
     *
     * @param requestParam httpRequest parameter name => value
     * @param httpMethod httpRequest method
     * @param secondUri the second uri
     * @return correct MyResponse
     * @throws RequestUriNotMatchException when the specified methodName doesn't exist
     */
    private MyResponse handleRequest(Map<String, String> requestParam, HttpMethod httpMethod,
                                     String baseUri, String secondUri)
            throws RequestUriNotMatchException, ParameterNameNotMatchException, RequestMethodNotSupportExceptoin {

        Object res = null;
        try {
            Api[] apis = interfaceParser.getMethodMap(baseUri);

            if (apis != null) {
                for (Api api : apis) {
                    // found the matching method
                    if (api.getApiName().equals(secondUri) &&
                            matchParameterName(api.getAllParamName(), requestParam)) {

                        // the request method not matched
                        if (!api.isHttpMethodEqual(httpMethod)) {
                            throw new RequestMethodNotSupportExceptoin(
                                    "request method " + httpMethod.name() + " not support");
                        }

                        Object[] args;
                        if (requestParam != null) {
                            args = getMethodArgs(api, requestParam);
                        } else {
                            args = new Object[0];
                        }

                        String ctlerAbsName = interfaceParser.getControllerAbsoluteName(baseUri);

                        res = Class.forName(ctlerAbsName)
                                .getMethod(api.getMethodName(), api.getParameterTypes())
                                .invoke(componentFactory.getInstance(ctlerAbsName), args);
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (res == null) {
            throw new RequestUriNotMatchException(
                    httpMethod.name() + " uri: /" + baseUri + "/" + secondUri + " not found.");
        }
        return (MyResponse) res;
    }

    /**
     * check whether the request parameter's name were
     * matched with the method's parameter's name or not
     *
     * @param apiParameterName method's parameter's name array
     * @param requestParam httpRequest parameter
     * @return true if all parameter name are matched
     * @throws ParameterNameNotMatchException without need parameter's name
     */
    private boolean matchParameterName(String[] apiParameterName, Map<String, String> requestParam)
            throws ParameterNameNotMatchException {
        if (apiParameterName.length > 0 && requestParam == null) {
            throw new ParameterNameNotMatchException(apiParameterName[0] + " parameter not found.");
        }

        for (String paramName : apiParameterName) {
            if (!requestParam.containsKey(paramName)) {
                throw new ParameterNameNotMatchException(paramName + " parameter not found.");
            }
        }
        return true;
    }

    /**
     * get arguments' value which used to invoke the target method.
     * this will automatically filter the mismatching request parameter.
     *
     * so this will return empty array when the target method doesn't have parameter
     * even if the request have parameter
     *
     * @param api the target method
     * @param requestParams http request's key value
     * @return arguments' value
     */
    private Object[] getMethodArgs(Api api, Map<String, String> requestParams) {
        final int paramCount = api.getParamCount();
        Object[] args = new Object[paramCount];

        for (int i = 0; i < paramCount; i++) {
            Object tempValue;
            String strValue = requestParams.get(api.getParamName(i));

            switch (api.getParamTypeName(i)) {
                case BASE_PACKAGE + "String":
                    tempValue = String.valueOf(strValue);
                    break;

                case "int":
                case BASE_PACKAGE + "Integer":
                    tempValue = Integer.valueOf(strValue);
                    break;

                case "long":
                case BASE_PACKAGE + "Long":
                    tempValue = Long.valueOf(strValue);
                    break;

                case "float":
                case BASE_PACKAGE + "Float":
                    tempValue = Float.valueOf(strValue);
                    break;

                case "double":
                case BASE_PACKAGE + "Double":
                    tempValue = Double.valueOf(strValue);
                    break;

                case "char":
                case BASE_PACKAGE + "Character":
                    tempValue = strValue.charAt(0);
                    break;

                default:
                    throw new RuntimeException(
                            "Parameter type " + api.getParamTypeName(i) + " doesn't support.");
            }
            args[i] = tempValue;
        }
        return args;
    }
}
