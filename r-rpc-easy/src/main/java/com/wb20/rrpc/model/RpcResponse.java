package com.wb20.rrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC 响应
 */
//注解会自动生成类的 toString()、equals()、hashCode() 方法，以及所有字段的 getter 和 setter 方法。它可以减少编写样板代码的工作量，使类更加简洁。
@Data
//注解是用于构建器模式(建造者模式)的，它会为类生成一个内部静态类，该类包含所有字段的 builder 方法。使用构建器模式可以方便地创建对象，并且可以避免过多的构造方法重载
@Builder
//注解会为类生成一个包含所有参数的构造方法。换句话说，它会为类中的每个字段生成一个参数，并在构造方法中初始化这些字段
@AllArgsConstructor
//注解会为类生成一个无参构造方法。这在某些情况下很有用，例如当使用反射创建对象时，或者需要在对象创建后设置字段的值。
@NoArgsConstructor
//Serializable 是 Java 中的一个接口，用于标记类的实例可以被序列化
public class RpcResponse {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型（预留）
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 异常信息
     */
    private Exception exception;

}
