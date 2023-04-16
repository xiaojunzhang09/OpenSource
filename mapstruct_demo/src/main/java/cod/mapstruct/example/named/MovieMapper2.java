package cod.mapstruct.example.named;

import cod.mapstruct.example.bean.EnglishRelease;
import cod.mapstruct.example.bean.GermanRelease;
import cod.mapstruct.example.bean.OriginalRelease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangxiaojun10
 * @title: MovieMapper
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 1:09 下午
 */
@Mapper(uses = Titles2.class)
public interface MovieMapper2 {
    MovieMapper2 MAPPER = Mappers.getMapper(MovieMapper2.class);

    @Mapping(target = "title", qualifiedByName = {"TitleTranslator", "EnglishToGerman"})
    GermanRelease toGerman(OriginalRelease movies);


    @Mapping(target = "title", qualifiedByName = {"TitleTranslator", "GermanToEnglish"})
    EnglishRelease toEnglish(OriginalRelease movies);
}
