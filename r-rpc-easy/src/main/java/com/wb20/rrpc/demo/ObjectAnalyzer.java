package com.wb20.rrpc.demo;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Class<?> getComponentType() 返回数组类里组件类型的 Class，如果不是数组类则返回null
 * boolean isArray() 返回这个类是否为数组，同类型的API还有 isAnnotation、isAsciiDigit、isEnum、isInstance、isInterface、isLocalClass、isPrimitive 等
 * int Array.getLength(obj) 返回数组对象obj的长度
 * Object Array.get(obj, i) 获取数组对象下标为i的元素
 * boolean isPrimitive() 返回这个类是否为8种基本类型之一，即是否为boolean, byte, char, short, int, long, float, 和double 等原始类型
 * Field getField(String name) 获取指定名称的域对象
 * AccessibleObject.setAccessible(fields, true) 当访问 Field、Method 和 Constructor 的时候Java会执行访问检查，如果访问者没有权限将抛出SecurityException，譬如访问者是无法访问private修饰的域的。通过设置 setAccessible(true) 可以取消Java的执行访问检查，这样访问者就获得了指定 Field、Method 或 Constructor 访问权限
 * Class<?> Field.getType() 返回一个Class 对象，它标识了此 Field 对象所表示字段的声明类型
 * Object Field.get(Object obj) 获取obj对象上当前域对象表示的属性的实际值，获取到的是一个Object对象，实际使用中还需要转换成实际的类型，或者可以通过 getByte()、getChar、getInt() 等直接获取具体类型的值
 * void Field.set(Object obj, Object value) 设置obj对象上当前域表示的属性的实际值
 */
public class ObjectAnalyzer {
    private ArrayList<Object> visited = new ArrayList<>();

    public String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (visited.contains(obj)) {    // 如果该对象已经处理过，则不再处理
            return "...";
        }
        visited.add(obj);

        Class cl = obj.getClass(); // 获取Class对象
        if (cl == String.class) {   // 如果是String类型则直接转为String
            return (String) obj;
        }
        if (cl.isArray()) {        // 如果是数组
            String r = cl.getComponentType() + "[]{\n";     // 数组的元素的类型
            for (int i = 0; i < Array.getLength(obj); i++) {
                if (i > 0) {   // 不是数组的第一个元素加逗号和换行，显示更加美观
                    r += ",\n";
                }
                r += "\t";
                Object val = Array.get(obj, i);
                if (cl.getComponentType().isPrimitive()) { // Class为8种基本类型的时候为 true，直接输出
                    r += val;
                } else {
                    r += toString(val); // 不是8中基本类型时，说明是类，递归调用toString
                }
            }
            return r + "\n}";
        }
        // 既不是String，也不是数组时，输出该对象的类型和属性值
        String r = cl.getName();
        do {
            r += "[";
            Field[] fields = cl.getDeclaredFields();    // 获取该类自己定义的所有域，包括私有的，不包括父类的
            AccessibleObject.setAccessible(fields, true); // 访问私有的属性，需要打开这个设置，否则会报非法访问异常
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) { // 通过 Modifier 可获取该域的修饰符，这里判断是否为 static
                    //判断r是否以[结束
                    if (!r.endsWith("[")) {
                        r += ",";
                    }
                    r += f.getName() + "=";     // 域名称
                    try {
                        Class t = f.getType();  // 域（属性）的类型
                        Object val = f.get(obj);   // 获取obj对象上该域的实际值
                        if (t.isPrimitive()) {     // 如果类型为8种基本类型，则直接输出
                            r += val;
                        } else {
                            r += toString(val);     // 不是8种基本类型，递归调用toString
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            r += "]";
            cl = cl.getSuperclass(); // 继续打印超类的类信息
        } while (cl != null);
        return r;
    }
}