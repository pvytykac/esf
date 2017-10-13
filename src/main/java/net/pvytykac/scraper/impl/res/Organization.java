package net.pvytykac.scraper.impl.res;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Paly
 * @since 2017-10-12
 */
public final class Organization {

    private final Integer id;
    private final String ico;
    private final String name;
    private final Enumerated form;
    private final Date created;
    private final Date ceased;
    private final String address;
    private final Enumerated district;
    private final Enumerated territory;
    private final Map<String, List<Enumerated>> attributes;

    private Organization(Integer id, String ico, String name, Enumerated form, Date created, Date ceased, String address,
                         Enumerated district, Enumerated territory, Map<String, List<Enumerated>> attributes) {
        this.id = id;
        this.ico = ico;
        this.name = name;
        this.form = form;
        this.created = created;
        this.ceased = ceased;
        this.address = address;
        this.district = district;
        this.territory = territory;
        this.attributes = attributes;
    }

    public String getIco() {
        return ico;
    }

    public String getName() {
        return name;
    }

    public Enumerated getForm() {
        return form;
    }

    public Date getCreated() {
        return created;
    }

    public Date getCeased() {
        return ceased;
    }

    public String getAddress() {
        return address;
    }

    public Enumerated getDistrict() {
        return district;
    }

    public Enumerated getTerritory() {
        return territory;
    }

    public Map<String, List<Enumerated>> getAttributes() {
        return attributes;
    }

    public static class Enumerated {
        private final String id;
        private final String value;

        private Enumerated(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

    }

    public static class OrganizationBuilder {
        private Integer id = null;
        private String ico = null;
        private String name = null;
        private Enumerated form = null;
        private Date created = null;
        private Date ceased = null;
        private String address = null;
        private Enumerated district = null;
        private Enumerated territory = null;
        private Map<String, List<Enumerated>> attributes = new HashMap<>();

        public OrganizationBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public OrganizationBuilder setIco(String ico) {
            this.ico = ico;
            return this;
        }

        public OrganizationBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public OrganizationBuilder setForm(String id, String form) {
            if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(form)) {
                this.form = new Enumerated(id, form);
            }
            return this;
        }

        public OrganizationBuilder setCreated(Date created) {
            this.created = created;
            return this;
        }

        public OrganizationBuilder setCeased(Date ceased) {
            this.ceased = ceased;
            return this;
        }

        public OrganizationBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public OrganizationBuilder setDistrict(String id, String district) {
            if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(district)) {
                this.district = new Enumerated(id, district);
            }
            return this;
        }

        public OrganizationBuilder setTerritory(String id, String territory) {
            if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(territory)) {
                this.territory = new Enumerated(id, territory);
            }
            return this;
        }

        public OrganizationBuilder addAttributes(String key, String id, String value) {
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(id) && StringUtils.isNotBlank(value)) {
                attributes.putIfAbsent(key, new ArrayList<>());
                attributes.get(key).add(new Enumerated(id, value));
            }
            return this;
        }

        public Organization build() {
            return new Organization(id, ico, name, form, created, ceased, address, district, territory, attributes);
        }
    }

}
