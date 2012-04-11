package cz.poptavka.sample.shared.domain.adminModule;

import cz.poptavka.sample.domain.invoice.OurPaymentDetails;
import java.io.Serializable;

/**
 * Represents full detail of domain object <b>OurPaymentDetails</b> used in <i>Administration Module</i>.
 * Contains 2 static methods:  1. creating detail object
 *                             2. updating domain object
 *
 * @author Martin Slavkovsky
 *
 */
public class PaymentDetail implements Serializable {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -530982467233195456L;
    private Long id;
    private String bankAccount;
    private String bankCode;
    private String city;
    private int countryVat;
    private String email;
    private String iban;
    private String identificationNumber;
    private String phone;
    private String street;
    private String swiftCode;
    private String taxId;
    private String title;
    private String zipCode;

    /** for serialization. **/
    public PaymentDetail() {
    }

    public PaymentDetail(PaymentDetail demand) {
        this.updateWholeOurPaymentDetail(demand);
    }

    /**
     * Method created <b>PaymentDetail</b> from provided Demand domain object.
     * @param domain - given domain object
     * @return PaymentDetail - created detail object
     */
    public static PaymentDetail createOurPaymentDetailDetail(OurPaymentDetails domain) {
        PaymentDetail detail = new PaymentDetail();

        detail.setId(domain.getId());
        detail.setBankAccount(domain.getBankAccount());
        detail.setBankCode(domain.getBankCode());
        detail.setCity(domain.getCity());
        detail.setCountryVat(domain.getCountryVat());
        detail.setEmail(domain.getEmail());
        detail.setIban(domain.getIban());
        detail.setIdentificationNumber(domain.getIdentificationNumber());
        detail.setPhone(domain.getPhone());
        detail.setStreet(domain.getStreet());
        detail.setSwiftCode(domain.getSwiftCode());
        detail.setTaxId(domain.getTaxId());
        detail.setTitle(domain.getTitle());
        detail.setZipCode(domain.getZipCode());

        return detail;
    }

    /**
     * Method created domain object <b>OurPaymentDetails</b> from provided <b>PaymentDetail</b> object.
     * @param domain - domain object to be updated
     * @param detail - detail object which provides updated data
     * @return OurPaymentDetails - updated given domain object
     */
    public static OurPaymentDetails updateOurPaymentDetails(OurPaymentDetails domain, PaymentDetail detail) {
        if (!domain.getBankAccount().equals(detail.getBankAccount())) {
            domain.setBankAccount(detail.getBankAccount());
        }
        if (!domain.getBankCode().equals(detail.getBankCode())) {
            domain.setBankCode(detail.getBankCode());
        }
        if (!domain.getCity().equals(detail.getCity())) {
            domain.setCity(detail.getCity());
        }
        if (!domain.getCountryVat().equals(detail.getCountryVat())) {
            domain.setCountryVat(detail.getCountryVat());
        }
        if (!domain.getEmail().equals(detail.getEmail())) {
            domain.setEmail(detail.getEmail());
        }
        if (!domain.getIban().equals(detail.getIban())) {
            domain.setIban(detail.getIban());
        }
        if (!domain.getIdentificationNumber().equals(detail.getIdentificationNumber())) {
            domain.setIdentificationNumber(detail.getIdentificationNumber());
        }
        if (!domain.getPhone().equals(detail.getPhone())) {
            domain.setPhone(detail.getPhone());
        }
        if (!domain.getStreet().equals(detail.getStreet())) {
            domain.setStreet(detail.getStreet());
        }
        if (!domain.getSwiftCode().equals(detail.getSwiftCode())) {
            domain.setSwiftCode(detail.getSwiftCode());
        }
        if (!domain.getTaxId().equals(detail.getTaxId())) {
            domain.setTaxId(detail.getTaxId());
        }
        if (!domain.getTitle().equals(detail.getTitle())) {
            domain.setTitle(detail.getTitle());
        }
        if (!domain.getZipCode().equals(detail.getZipCode())) {
            domain.setZipCode(detail.getZipCode());
        }
        return domain;
    }

    //---------------------------- GETTERS AND SETTERS --------------------
    public void updateWholeOurPaymentDetail(PaymentDetail detail) {
        id = detail.getId();
        bankAccount = detail.getBankAccount();
        bankCode = detail.getBankCode();
        city = detail.getCity();
        countryVat = detail.getCountryVat();
        email = detail.getEmail();
        iban = detail.getIban();
        identificationNumber = detail.getIdentificationNumber();
        phone = detail.getPhone();
        street = detail.getStreet();
        swiftCode = detail.getSwiftCode();
        taxId = detail.getTaxId();
        title = detail.getTitle();
        zipCode = detail.getZipCode();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryVat() {
        return countryVat;
    }

    public void setCountryVat(int countryVat) {
        this.countryVat = countryVat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "\nGlobal OurPaymentDetail Detail Info:"
                + "\n     id:" + Long.toString(id)
                + "\n     bankAccount:" + bankAccount
                + "\n     bankCode:" + bankCode
                + "\n     city:" + city
                + "\n     countryVat:" + Integer.toString(countryVat)
                + "\n     email:" + email
                + "\n     iban:" + iban
                + "\n     identificationNumber:" + identificationNumber
                + "\n     phone:" + phone
                + "\n     street:" + street
                + "\n     swiftCode:" + swiftCode
                + "\n     taxId:" + taxId
                + "\n     title:" + title
                + "\n     zipCode:" + zipCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PaymentDetail other = (PaymentDetail) obj;
        if (this.id != other.getId()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}
