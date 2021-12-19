package my.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlParser {
    private Map<String, Object> properties;
    public YamlParser() {}
    public YamlParser(String filePath) {
        initWithFile(filePath);
    }
    public void initWithFile(String filePath) {
        properties = null;
        try (InputStream in = new FileInputStream(filePath)) {
            properties = new Yaml().loadAs(in, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initWithStr(String content) {
        properties = new Yaml().loadAs(content, Map.class);
    }

    public <T> T getValue(String key, T defVal) {
        if (properties == null) {
            throw new RuntimeException("请先初始化解析器！");
        }
        if (isBlank(key)) {
            throw new RuntimeException("key不能为空！");
        }
        if (!key.contains(".") && !key.contains("[")) {
            Object val = properties.get(key);
            return val == null ? defVal : (T) val;
        }
        String[] keys = key.split("\\.");
        if (containsIllegalKey(keys)) {
            throw new RuntimeException("包含非法的key！");
        }
        Object target = properties;
        Integer[] indices;
        String elementPath;
        for (String innerKey : keys) {
            indices = null;
            if (innerKey.contains("[")) {
                indices = getIndicesFromKey(innerKey);
                innerKey = innerKey.substring(0, innerKey.indexOf("["));
            }
            target = ((Map<String, Object>) target).get(innerKey);
            if (target == null) {
                return defVal;
            }
            if (indices != null) {
                elementPath = innerKey;
                for (Integer index : indices) {
                    if (!(target instanceof ArrayList)) {
                        throw new RuntimeException(elementPath + "不是数组！");
                    }
                    target = ((ArrayList) target).get(index);
                    if(target == null) {
                        return defVal;
                    }
                    elementPath += "[" + index + "]";
                }
            }
        }
        return (T) target;
    }

    private Integer[] getIndicesFromKey(String key) {
        List<Integer> indices = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(key);
        while(matcher.find()) {
            indices.add(Integer.valueOf(matcher.group(1)));
        }
        return indices.toArray(new Integer[indices.size()]);
    }

    private boolean containsIllegalKey(String[] keys) {
        for (String innerKey: keys) {
            if (!isLegalKey(innerKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLegalKey(String key) {
        return key.matches("[a-zA-Z][\\w\\-\\[\\]]*");
    }
    /*
    public static void main(String[] args) {
        String str = "abc[12][33]";
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
    */
    private boolean isBlank(String str) {
        return str == null || "".equals(str);
    }

    public <T> T getValue(String key) {
        return getValue(key, null);
    }
}
