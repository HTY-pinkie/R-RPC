package com.wb20.rrpc.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Method getMethod(String name, Class<?>... parameterTypes) 获取指定的 Method，参数 name 为要获取的方法名，parameterTypes 为指定方法的参数的 Class，
 * 由于可能存在多个同名的重载方法，所以只有提供正确的 parameterTypes 才能准确的获取到指定的 Method
 * Object invoke(Object obj, Object... args) 执行方法，第一个参数执行该方法的对象，如果是static修饰的类方法，则传null即可；后面是传给该方法执行的具体的参数值
 */
public class MethodTableTest {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Employee employee = new Employee("小明", "18", "写代码", 1, "Java攻城狮", 100000);
        Method sayHello = employee.getClass().getMethod("sayHello");
        System.out.println(sayHello);   // 打印 sayHello 的方法信息
        sayHello.invoke(employee);      // 让 employee 执行 sayHello 方法

        double x = 3.0;
        Method square = MethodTableTest.class.getMethod("square", double.class);  // 获取 MethodTableTest 的square方法
        //在调用静态方法时，可以将 obj 参数设置为 null，因为静态方法不依赖于特定的对象实例。
        double y1 = (double) square.invoke(null, x);    // 调用类方法 square 求平方，方法参数 x 
        System.out.printf("square    %-10.4f -> %10.4f%n", x, y1);

        Method sqrt = Math.class.getMethod("sqrt", double.class);   // 获取 Math 的 sqrt 方法
        double y2 = (double) sqrt.invoke(null, x);  // 调用类方法 sqrt 求根，方法参数 x 
        System.out.printf("sqrt      %-10.4f -> %10.4f%n", x, y2);
    }

    // static静态方法 计算乘方
    public static double square(double x) {
        return x * x;
    }
}