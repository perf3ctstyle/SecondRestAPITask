package com.epam.esm.entity;

import com.epam.esm.audit.AuditListener;
import com.epam.esm.constant.GiftCertificateConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

@Entity
@Table(name = "gift_certificate")
@EntityListeners(AuditListener.class)
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String name;
    private String description;

    @Column(columnDefinition = "BIGINT")
    private Integer price;
    private Long duration;

    @Column(name = "create_date", columnDefinition = "VARCHAR(30)")
    private LocalDateTime createDate;

    @Column(name = "last_update_date", columnDefinition = "VARCHAR(30)")
    private LocalDateTime lastUpdateDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "gift_and_tag", joinColumns = @JoinColumn(name = "certificate_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public GiftCertificate() {
    }

    public GiftCertificate(Long id,
                           String name,
                           String description,
                           Integer price,
                           Long duration,
                           LocalDateTime createDate,
                           LocalDateTime lastUpdateDate) {
        Validate.notBlank(name);
        Validate.notBlank(description);
        Validate.isTrue(price > 0 && duration > 0);

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public GiftCertificate(Long id,
                           String name,
                           String description,
                           Integer price,
                           Long duration,
                           LocalDateTime createDate,
                           LocalDateTime lastUpdateDate,
                           List<Tag> tags) {
        this(id, name, description, price, duration, createDate, lastUpdateDate);
        this.tags = tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty
    public List<Tag> getTags() {
        return tags;
    }

    public Map<String, String> toMapNotNullFields() {
        Map<String, String> presentFields = new HashMap<>();

        if (name != null) {
            presentFields.put(GiftCertificateConstants.NAME, name);
        }
        if (description != null) {
            presentFields.put(GiftCertificateConstants.DESCRIPTION, description);
        }
        if (price != null) {
            presentFields.put(GiftCertificateConstants.PRICE, price.toString());
        }
        if (duration != null) {
            presentFields.put(GiftCertificateConstants.DURATION, duration.toString());
        }
        if (createDate != null) {
            presentFields.put(GiftCertificateConstants.CREATE_DATE, createDate.toString());
        }
        if (lastUpdateDate != null) {
            presentFields.put(GiftCertificateConstants.LAST_UPDATE_DATE, lastUpdateDate.toString());
        }

        return presentFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GiftCertificate that = (GiftCertificate) o;
        return Objects.equals(id, that.id)
                && name.equals(that.name)
                && description.equals(that.description)
                && price.equals(that.price)
                && duration.equals(that.duration)
                && createDate.equals(that.createDate)
                && lastUpdateDate.equals(that.lastUpdateDate)
                && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, createDate, lastUpdateDate, tags);
    }
}
