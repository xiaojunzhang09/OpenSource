package cod.mapstruct.example.named;

import cod.mapstruct.example.qualifier.annotation.EnglishToGerman;
import cod.mapstruct.example.qualifier.annotation.GermanToEnglish;
import org.mapstruct.Named;

/**
 * @author zhangxiaojun10
 * @title: Titles
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 1:08 下午
 */
@Named("TitleTranslator")
public class Titles2 {
    @Named("EnglishToGerman")
    public String translateTitleEG(String title) {
        // some mapping logic
        return "English logic " + title;
    }

    @Named("GermanToEnglish")
    public String translateTitleGE(String title) {
        // some mapping logic
        return "German logic " + title;
    }
}