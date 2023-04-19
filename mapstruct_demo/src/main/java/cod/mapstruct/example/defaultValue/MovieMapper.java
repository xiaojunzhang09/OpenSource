package cod.mapstruct.example.defaultValue;

import cod.mapstruct.example.bean.Category;
import cod.mapstruct.example.bean.GermanRelease;
import cod.mapstruct.example.bean.OriginalRelease;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author zhangxiaojun10
 * @title: MovieMapper
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 9:32 下午
 */
public interface MovieMapper {

    @Mapping( target = "category", qualifiedByName = "CategoryToString", defaultValue = "DEFAULT" )
    GermanRelease toGerman(OriginalRelease movies );

    @Named("CategoryToString")
    default String defaultValueForQualifier(Category cat) {
        // some mapping logic
        return cat.name();
    }
}
