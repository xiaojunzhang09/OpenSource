package cod.mapstruct.example.qualifier.annotation;

import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiaojun
 * @title: TitleTranslator
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 12:24 下午
 */

@Qualifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TitleTranslator {
}
