package br.com.restapi.vo.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;
import jakarta.xml.bind.annotation.XmlElement;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

@JsonPropertyOrder({"id", "firstName", "lastName", "address", "gender"})
public class PersonVO extends RepresentationModel<PersonVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Mapping("id")
    @JsonProperty("id")
    private Long key;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String address;
    private String gender;
    private Boolean enabled;

    public PersonVO() {
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }
    @XmlElement(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @XmlElement(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonVO personVO = (PersonVO) o;
        return Objects.equals(key, personVO.key)
                && Objects.equals(firstName, personVO.firstName)
                && Objects.equals(lastName, personVO.lastName)
                && Objects.equals(address, personVO.address)
                && Objects.equals(gender, personVO.gender)
                && Objects.equals(enabled, personVO.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), key
                , firstName
                , lastName
                , address
                , gender
                , enabled);
    }
}
