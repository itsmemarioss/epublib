package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * metadata meta
 * @author LinQ
 * @version 2013-05-27
 */
public class Meta extends DcmesElement {
    private String property;
    private String refines;
    private String scheme;
    private Map<String, String> customProperties = new HashMap<>();

    public Meta() {}

    public Meta(Property property, String value) {
        this.property = property.value();
        this.setValue(value);
    }

    public enum Property {
        DCTERMS_MODIFIED("dcterms:modified");

        private final String value;

        Property(String v) {
            value = v;
        }

        public String value() {
            return this.value;
        }
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getRefines() {
        return refines;
    }

    public void setRefines(String refines) {
        this.refines = refines;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void addCustomProperties(String property, String value) {
        if (StringUtil.isNotBlank(property)) {
            customProperties.put(property, value);
        }
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "value='" + getValue() + '\'' +
                ", id='" + getId() + '\'' +
                ", property='" + property + '\'' +
                ", refines='" + refines + '\'' +
                ", scheme='" + scheme + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Meta meta = (Meta) o;

        if (property != null ? !property.equals(meta.property) : meta.property != null) return false;
        if (refines != null ? !refines.equals(meta.refines) : meta.refines != null) return false;
        if (scheme != null ? !scheme.equals(meta.scheme) : meta.scheme != null) return false;
        return customProperties != null ? customProperties.equals(meta.customProperties) :
                meta.customProperties == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (refines != null ? refines.hashCode() : 0);
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (customProperties != null ? customProperties.hashCode() : 0);
        return result;
    }
}
