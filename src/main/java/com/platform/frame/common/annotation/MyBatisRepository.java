package com.platform.frame.common.annotation;

import org.springframework.stereotype.Repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * 标识数据源的MyBatis的DAO,为 {@link org.mybatis.spring.mapper.MapperScannerConfigurer}扫描指定注解类。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repository
public @interface MyBatisRepository {
    String value() default "";
}