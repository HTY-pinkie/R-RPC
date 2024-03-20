package com.wb20.rrpc.demo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;

/**
 * String getName() 获取这个Class的类名
 * Constructor[] getDeclaredConstructors() 返回这个类的所有构造器的对象数组，包含保护和私有的构造器；相近的方法 getConstructors() 则返回这个类的所有公有构造器的对象数组，不包含保护和私有的构造器
 * Method[] getDeclaredMethods() 返回这个类或接口的所有方法，包括保护和私有的方法，不包括超类的方法；相近的方法 getMethods() 则返回这个类及其超类的公有方法的对象数组，不含保护和私有的方法
 * Field[] getDeclaredFields() 返回这个类的所有域的对象数组，包括保护域和私有域，不包括超类的域；还有一个相近的API getFields()，返回这个类及其超类的公有域的对象数组，不含保护域和私有域
 * int getModifiers() 返回一个用于描述Field、Method和Constructor的修饰符的整形数值，该数值代表的含义可通过Modifier这个类分析
 * Modifier 类 它提供了有关Field、Method和Constructor等的访问修饰符的信息，主要的方法有：toString(int modifiers)返回整形数值modifiers代表的修饰符的字符串；isAbstract是否被abstract修饰；
 * isVolatile是否被volatile修饰；isPrivate是否为private；isProtected是否为protected；isPublic是否为public；isStatic是否为static修饰；等等，见名知义
 */
public class ReflectionTest {
    public static void main(String[] args) throws ClassNotFoundException {
        String name;
        if (args.length > 0) {
            name = args[0];
        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("输入一个类名（e.g. java.util.Date）："); // reflect.Employee
            name = in.next();
        }
        try {
            Class cl = Class.forName(name);
            //获取当前类的父类
            Class superCl = cl.getSuperclass();
            //类就获取类的修饰符，方法就获取方法的修饰符
            String modifiers = Modifier.toString(cl.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }
            System.out.print("class " + name);
            if (superCl != null && superCl != Object.class) {
                System.out.print(" extends " + superCl.getName());
            }
            System.out.println("\n{");

            printConstructors(cl); // 打印构造方法
            System.out.println();
            printMethods(cl);   // 打印方法
            System.out.println();
            printFields(cl);    // 打印属性
            System.out.println("}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * 打印Class对象的所有构造方法
     */
    public static void printConstructors(Class cl) {
        Constructor[] constructors = cl.getDeclaredConstructors();

        for (Constructor c : constructors) {
            String name = c.getName();
            System.out.print("  ");
            String modifiers = Modifier.toString(c.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }
            System.out.print(name + "(");
            // 打印构造参数
            Class[] paramTypes = c.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(paramTypes[i].getName());
            }
            System.out.println(");");
        }
    }

    /**
     * 打印Class的所有方法
     */
    public static void printMethods(Class cl) {
        Method[] methods = cl.getDeclaredMethods();
        //Method[] methods = cl.getMethods();
        for (Method m : methods) {
            Class retType = m.getReturnType();  // 返回类型
            System.out.print("  ");
            String modifiers = Modifier.toString(m.getModifiers());
            if (modifiers.length() > 0) {
                System.out.print(modifiers + " ");
            }
            System.out.print(retType.getName() + " " + m.getName() + "(");
            Class[] paramTypes = m.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(paramTypes[i].getName());
            }
            System.out.println(");");
        }
    }

    /**
     * 打印Class的所有属性
     */
    public static void printFields(Class cl) {
        Field[] fields = cl.getDeclaredFields();
        for (Field f: fields) {
            Class type = f.getType();
            System.out.print("  ");
            String modifiers = Modifier.toString(f.getModifiers());
            if (modifiers.length()> 0) {
                System.out.print(modifiers + " ");
            }
            System.out.println(type.getName() + " " + f.getName() + ";");
        }
    }
}
