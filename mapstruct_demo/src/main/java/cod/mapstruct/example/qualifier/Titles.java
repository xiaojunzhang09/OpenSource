package cod.mapstruct.example.qualifier;

import cod.mapstruct.example.qualifier.annotation.EnglishToGerman;
import cod.mapstruct.example.qualifier.annotation.GermanToEnglish;
import cod.mapstruct.example.qualifier.annotation.TitleTranslator;

/**
 * @author xiaojun
 * @title: Titles
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 12:02 下午
 */
@TitleTranslator
public class Titles {
    @EnglishToGerman
    public String translateTitleEG(String title) {
        // some mapping logic
        return "English logic " + title;
    }

    @GermanToEnglish
    public String translateTitleGE(String title) {
        // some mapping logic
        return "German logic " + title;
    }
}
