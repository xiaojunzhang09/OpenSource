package cod.mapstruct.example.named;

import cod.mapstruct.example.bean.EnglishRelease;
import cod.mapstruct.example.bean.GermanRelease;
import cod.mapstruct.example.bean.OriginalRelease;
import cod.mapstruct.example.qualifier.mapper.MovieMapper;
import org.junit.Test;

/**
 * @author zhangxiaojun10
 * @title: MovieMapperTest
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 12:46 下午
 */
public class MovieMapperTest {

    @Test
    public void testToTarget() {
        OriginalRelease movies = new OriginalRelease();
        movies.setTitle("中国");
        GermanRelease germanRelease = MovieMapper2.MAPPER.toGerman(movies);
        System.out.println("germanRelease = " + germanRelease.toString());



        EnglishRelease englisRelease = MovieMapper2.MAPPER.toEnglish(movies);
        System.out.println("englisRelease = " + englisRelease.toString());
    }
}
