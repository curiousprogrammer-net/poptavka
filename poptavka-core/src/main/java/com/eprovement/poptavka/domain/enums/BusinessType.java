package com.eprovement.poptavka.domain.enums;

/**
 * Type of {@link com.eprovement.poptavka.domain.user.BusinessUser}.
 * <p/>
 * Forms of business entities
 *
 * @author Juraj Martinka
 *         Date: 28.1.11
 */
public enum BusinessType {

    //----------------------- COMPANIES --------------------------------------------------------------------------------
    /**
     * Spolocnost s rucenim obmedzenim.
     */
    COMPANY_SRO("s.r.o"),
    /**
     * Verejna obchodna spolocnost.
     */
    COMPANY_VOS("v.o.s"),
    /**
     * Akciova spolocnost.
     */
    COMPANY_AS("a.s."),
    /**
     * Komanditna spolocnost.
     */
    COMPANY_KS("k.s."),

    /**
     * Europska spolocnost.
     */
    COMPANY_ES("e.s."),


    //----------------------- PERSON -----------------------------------------------------------------------------------
    /**
     * "Osoba samostatne zarobkovo cinna.
     */
    OSZC_ZIVNOSTNIK("zivnost"),

    PRIVATE_PERSON("private_person");


    private final String value;

    BusinessType(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
