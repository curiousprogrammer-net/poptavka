package com.eprovement.poptavka.service.address;

import com.eprovement.poptavka.domain.common.ResultCriteria;
import com.eprovement.poptavka.dao.address.LocalityDao;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.enums.LocalityType;
import com.eprovement.poptavka.service.GenericService;

import java.util.List;

/**
 * Basic Service for localities.
 * Should provide only read methods because localities storing is complex problem that must be handled
 * by stored procedure (filling of attributes level, leftBound and rightBound).
 * <p>
 *     If any method "non-read" method is called then TreeItemModificationException should be thrown.
 *
 * @see com.eprovement.poptavka.domain.common.TreeItem
 *
 * @author Juraj Martinka
 *         Date: 5.2.11
 */
public interface LocalityService extends GenericService<Locality, LocalityDao> {

    /**
     * Get all localities of given type.
     *
     * @param localityType type of locality
     * @return all localities of given type
     */
    List<Locality> getLocalities(LocalityType localityType);

    /**
     * Same as {@link #getLocalities(com.eprovement.poptavka.domain.enums.LocalityType)}
     * but additional criteria can be applied.
     * @param localityType type of locality
     * @param resultCriteria optional additional criteria which can be applied to the localities
     * @return all localities of given type filtered by {@code resultCriteria}
     */
    List<Locality> getLocalities(LocalityType localityType, ResultCriteria resultCriteria);

    /**
     * Get locality by given code. Code must be a unique identifier!
     *
     * @param id unique id for identifying locality
     * @return locality with given id or null if no such locality exists
     */
    Locality getLocality(Long id);

    /**
     * Finds region by its full name or abbreviation.
     * In USA region represents the region. There is as well a unique two-letter abbreviation for each region.
     *
     * Abbreviation is typically case insensitive, name is case sensitive.
     *
     * @param region name of region (state) or its abbreviation
     * @return locality representing given region or null if no such region has been found
     */
    Locality findRegion(String region);

    /**
     * Finds city by its name and zipcode.
     * This information should be unique across all cities in given country.
     * Note, that zip code alone is not sufficient, because there can be multiple cities sharing the same zip code.
     *
     * @param cityName name of city,
     * @param zipCode city's zip code
     * @return city identified by given name and zip code or null if no such city exists
     */
    Locality findCityByZipCode(String cityName, String zipCode);

    /**
     * Finds city by its name, district and region (state) to which its belongs.
     * Region and district are required because there
     * can be multiple cities with same names in one region (e.g. region 'Oregon' and city 'Portland').
     *
     * @param region name of region in which the city is located
     * @param district name of district in which the city is located
     * @param city name of city
     * @return found city or null if no such city exists
     */
    Locality findCityByName(String region, String district, String city);

    /**
     * Finds district by its name and region (state) to which its belongs.
     * Region is required because there are multiple districts (counties) with same names in one region (e.g. in USA).
     *
     * @param region name of region in which the district is located
     * @param district name of district (county in USA)
     * @return found city or null if no such district exists
     */
    Locality findDistrictByName(String region, String district);

    /**
     * Gets a list of localities whose name is shorter than <code>maxlengthExcl</code> and
     * contains <code>nameSubstring</code>
     * @param maxLengthExcl all <code>Locality</code>-ies' names returned must be shorter than the given length
     * @param nameSubstring a <code>String</code> that all the localities' names must contain
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMaxLengthExcl(int maxLengthExcl, String nameSubstring);

    /**
     * Gets a list of localities whose name is the same length or longer than <code>minLength</code> and
     * contains <code>nameSubstring</code>
     * @param minLength all <code>Locality</code>-ies' names returned must be at least of the given length
     * @param nameSubstring a <code>String</code> that all the localities' names must contain
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMinLength(int minLength, String nameSubstring);

    /**
     * Gets a list of localities whose name is shorter than <code>maxlengthExcl</code> and
     * contains <code>nameSubstring</code> and whose type is <code>type</code>
     * @param maxLengthExcl all <code>Locality</code>-ies' names returned must be shorter than the given length
     * @param nameSubstring a <code>String</code> that all the localities' names must contain
     * @param type just <code>Locality</code>-ies of the given type will be returned
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMaxLengthExcl(int maxLengthExcl, String nameSubstring,
        LocalityType type);

    /**
     * Gets a list of localities whose name is the same length or longer than <code>minLength</code> and
     * contains <code>nameSubstring</code> and whose type is <code>type</code>
     * @param minLength all <code>Locality</code>-ies' names returned must be at least of the given length
     * @param nameSubstring a <code>String</code> that all the localities' names must contain
     * @param type just <code>Locality</code>-ies of the given type will be returned
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMinLength(int minLength, String nameSubstring,
        LocalityType type);

    /**
     * Loads children of locality identified by LOCALITY_ID.
     * @return all direct children of given locality
     */
    List<Locality> getSubLocalities(long localityId);
}
