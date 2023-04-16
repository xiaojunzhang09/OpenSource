package cod.mapstruct.example.bean;

import java.util.List;
import java.util.Map;

/**
 * @author xiaojun
 * @title: AbstractEntry
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/16 12:20 下午
 */
public abstract class AbstractEntry {

    private String title;

    private List<String> keyWords;

    private Map<String, List<String>> facts;

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords( List<String> keyWords ) {
        this.keyWords = keyWords;
    }

    public Map<String, List<String> > getFacts() {
        return facts;
    }

    public void setFacts( Map<String, List<String> > facts ) {
        this.facts = facts;
    }

    @Override
    public String toString() {
        return "AbstractEntry{" +
                "title='" + title + '\'' +
                ", keyWords=" + keyWords +
                ", facts=" + facts +
                '}';
    }
}
