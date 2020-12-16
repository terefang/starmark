package com.github.terefang.starmark.util;

import lombok.SneakyThrows;
import org.hjson.JsonValue;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ContextUtil {

    @SneakyThrows
    public static Map<String, Object> loadContextFromHjson(Reader _source)
    {
        HashMap<String, Object> _obj = new HashMap<>();
        JsonValue _hson = JsonValue.readHjson(_source);
        for(Map.Entry<String, Object> _entry : hjsonToMap(_hson).entrySet())
        {
            _obj.put(_entry.getKey(), _entry.getValue());
        }
        return _obj;
    }

    static Map<String, Object> hjsonToMap(JsonValue _v)
    {
        Map<String, Object> _ret = new HashMap<>();
        if(_v.isObject())
        {
            _v.asObject().forEach(m -> _ret.put(m.getName(), hjsonToValue(m.getValue())));
        }
        else
        {
            _ret.put("data", hjsonToValue(_v));
        }
        return _ret;
    }

    static Object hjsonToValue(JsonValue value)
    {
        if(value.isObject())
        {
            return hjsonToMap(value);
        }
        else
        if(value.isArray())
        {
            return hjsonToArray(value);
        }
        else
        if(value.isString())
        {
            return value.asString();
        }
        else
        if(value.isNumber())
        {
            return Double.valueOf(value.asDouble());
        }
        else
        if(value.isBoolean())
        {
            return Boolean.valueOf(value.asBoolean());
        }
        else
        if(value.isNull())
        {
            return null;
        }
        else
        {
            return value.toString();
        }
    }

    static List hjsonToArray(JsonValue value)
    {
        List _ret = new Vector();
        value.asArray().forEach(m -> _ret.add(hjsonToValue(m)));
        return _ret;
    }
}
