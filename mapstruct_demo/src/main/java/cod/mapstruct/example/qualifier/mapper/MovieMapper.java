package cod.mapstruct.example.qualifier.mapper;

import cod.mapstruct.example.bean.EnglishRelease;
import cod.mapstruct.example.bean.GermanRelease;
import cod.mapstruct.example.bean.OriginalRelease;
import cod.mapstruct.example.qualifier.Titles;
import cod.mapstruct.example.qualifier.annotation.EnglishToGerman;
import cod.mapstruct.example.qualifier.annotation.GermanToEnglish;
import cod.mapstruct.example.qualifier.annotation.TitleTranslator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangxiaojun10
 * @title: MovieMapper
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 12:42 下午
 */
@Mapper( uses = Titles.class )
public interface MovieMapper {

    MovieMapper MAPPER = Mappers.getMapper(MovieMapper.class);

    @Mapping( target = "title", qualifiedBy = { TitleTranslator.class, GermanToEnglish.class } )
    GermanRelease toGerman(OriginalRelease movies );


    @Mapping( target = "title", qualifiedBy = { TitleTranslator.class, EnglishToGerman.class } )
    EnglishRelease toEnglish(OriginalRelease movies );

}