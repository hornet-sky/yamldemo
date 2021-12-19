package my.utils;

import org.junit.Test;

public class YamlParserTest {

    @Test
    public void testGetValue() {
        String filePath = this.getClass().getClassLoader().getResource("my.yaml").getPath();
        System.out.println("filePath=" + filePath);
        YamlParser parser = new YamlParser(filePath);
        System.out.println(parser.getValue("username", "none")); // wang
        System.out.println(parser.getValue("age", 0)); // 22
        System.out.println(parser.getValue("salary", 0.0)); // 0.0
        System.out.println(parser.getValue("contacts.e-mail", "none")); // wanghui_java@foxmail.com
        System.out.println(parser.getValue("hobbies[1]", "none")); // football
        System.out.println(parser.getValue("hobbies[3][1]", "none")); // running
        // System.out.println(parser.getValue("abc.a#2", "none")); //包含非法的key！
        // System.out.println(parser.getValue("hobbies[3][4]", "none"));  // IndexOutOfBoundsException
    }
}
