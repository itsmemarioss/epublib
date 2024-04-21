package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DCMES element
 * @author LinQ
 * @version 2013-05-27
 */
public class DcmesElement implements Serializable {
    private String id;
    private String lang;
    private String direction;
    private String value;
    // refines metas
    private List<Meta> metas = new ArrayList<>();

    public DcmesElement() {}

    public DcmesElement(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addMeta(Meta meta) {
        metas.add(meta);
    }

    public List<Meta> getMetas() {
        return metas;
    }

    @Override
    public String toString() {
        return "DcmesElement{" +
                "id='" + id + '\'' +
                ", lang='" + lang + '\'' +
                ", direction='" + direction + '\'' +
                ", value='" + value + '\'' +
                ", metas=" + metas +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DcmesElement that = (DcmesElement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lang != null ? !lang.equals(that.lang) : that.lang != null) return false;
        if (direction != null ? !direction.equals(that.direction) : that.direction != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return metas != null ? metas.equals(that.metas) : that.metas == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (metas != null ? metas.hashCode() : 0);
        return result;
    }
}
