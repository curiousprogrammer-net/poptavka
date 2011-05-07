package cz.poptavka.sample.domain.user;

import cz.poptavka.sample.domain.address.Address;
import cz.poptavka.sample.domain.invoice.Invoice;
import cz.poptavka.sample.domain.product.UserService;
import cz.poptavka.sample.util.orm.Constants;
import cz.poptavka.sample.util.strings.ToStringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Represents essential group users in application that have business relationship to us.
 * <p>
 * Three main categories of this users exist:
 * <ul>
 *  <li>Client - is searching for some services at most situations.</li>
 *  <li>Supplier - provides some services to clients.</li>
 *  <li>Partner - a subject that has partnership beneficial to both parts </li>
 * </ul>
 *
 * @author Juraj Martinka
 *         Date: 28.1.11
 */
@Entity
@Audited
public class BusinessUser extends User {

    @Enumerated(value = EnumType.STRING)
    @Column(length = Constants.ENUM_FIELD_LENGTH)
    private BusinessType businessType;

    /** Company is filled if this object represents the company. For private persons it is null at most situations. */
    @OneToOne
    @Cascade(value = CascadeType.ALL)
    private Company company;

    /** Represents person. It is either the business user itself or contact person for company. */
    @OneToOne
    @Cascade(value = CascadeType.ALL)
    private Person person;


    /** All user's addresses. */
    @NotAudited
    @OneToMany
    @Cascade(value = CascadeType.ALL)
    private List<Address> addresses;

    /**
     *  Verfification state of client. No default value!
     * @see {@link cz.poptavka.sample.domain.user.BusinessUser.Verification} enum
     */
    @Enumerated(value = EnumType.STRING)
    @Column(length = Constants.ENUM_FIELD_LENGTH)
    private Verification verification;

    @NotAudited
    @OneToMany(mappedBy = "user")
    @Cascade(value = CascadeType.ALL)
    private List<UserService> userServices;


    @OneToMany
    @NotAudited
    private List<Invoice> invoices;


    //-------------------------- GETTERS AND SETTERS -------------------------------------------------------------------
    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Verification getVerification() {
        return verification;
    }

    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    public List<UserService> getUserServices() {
        return userServices;
    }

    public void setUserServices(List<UserService> userServices) {
        this.userServices = userServices;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    //-------------------------- End of GETTERS AND SETTERS ------------------------------------------------------------


    public static enum Verification {
        /** User has already been verified by email link activation. */
        VERIFIED,

        /**
         * User has filled out our registration form and received email link activation.
         * If user was in state EXTERNAL before registration we will change it to UNVERIFIED
         * and we will be waiting for email link activation.
         */
        UNVERIFIED,

        /** User came from external system and will be verified after email link activation is performed. */
        EXTERNAL
    }


    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BusinessUser");
        sb.append("{id=").append(getId());
        sb.append(", company=").append(ToStringUtils.printId(company));
        sb.append(", person=").append(ToStringUtils.printId(person));
        sb.append(", verified=").append(verification);
        sb.append('}');
        return sb.toString();
    }
}
