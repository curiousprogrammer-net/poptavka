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
     * @param localityType
     * @return
     */
    List<Locality> getLocalities(LocalityType localityType);

    /**
     * Same as {@link #getLocalities(com.eprovement.poptavka.domain.enums.LocalityType)}
     * but additional criteria can be applied.
     * @param localityType
     * @param resultCriteria optional additional criteria which can be applied to the localities
     * @return
     */
    List<Locality> getLocalities(LocalityType localityType, ResultCriteria resultCriteria);

    /**
     * Get locality by given code. Code must be a unique identifier!
     *
     * @param id unique id for identifying locality
     * @return
     */
    Locality getLocality(Long id);

    /**
     * Gets a list of localities whose name is shorter than <code>maxlengthExcl</code> and
     * is prefixed with <code>namePrefix</code> and whose type is <code>type</code>
     * @param maxLengthExcl all <code>Locality</code>-ies' names returned must be shorter than the given length
     * @param namePrefix a <code>String</code> that all the localities' names must be prefixed with
     * @param type just <code>Locality</code>-ies of the given type will be returned
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMaxLengthExcl(int maxLengthExcl, String namePrefix,
            LocalityType type);

    /**
     * Gets a list of localities whose name is the same length or longer than <code>minLength</code> and
     * is prefixed with <code>namePrefix</code> and whose type is <code>type</code>
     * @param minLength all <code>Locality</code>-ies' names returned must be at least of the given length
     * @param namePrefix a <code>String</code> that all the localities' names must be prefixed with
     * @param type just <code>Locality</code>-ies of the given type will be returned
     * @return a <code>List<code> of localities satisfying criteria
     */
    List<Locality> getLocalitiesByMinLength(int minLength, String namePrefix,
            LocalityType type);

    /**
     * Loads children of locality identified by LOCALITY_ID.
     * @return all direct children of given locality
     */
    List<Locality> getSubLocalities(long localityId);
}
