/*
 * Copyright (C) 2011, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.converter;

import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.demand.Category;
import com.eprovement.poptavka.domain.demand.Demand;
import com.eprovement.poptavka.domain.user.Supplier;
import com.eprovement.poptavka.service.demand.DemandService;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ICatLocDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.supplier.FullSupplierDetail;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Converts Demand to FullDemandDetail.
 * @author Juraj Martinka
 */
public final class FullDemandConverter extends AbstractConverter<Demand, FullDemandDetail> {

    /**************************************************************************/
    /* RPC Services                                                           */
    /**************************************************************************/
    private DemandService demandService;

    @Autowired
    public void setDemandService(DemandService demandService) {
        this.demandService = demandService;
    }

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private final Converter<Supplier, FullSupplierDetail> supplierConverter;
    private final Converter<Locality, ICatLocDetail> localityConverter;
    private final Converter<Category, ICatLocDetail> categoryConverter;

    /**************************************************************************/
    /* Constructor                                                            */
    /**************************************************************************/
    /**
     * Creates FullDemandConverter.
     */
    private FullDemandConverter(
            Converter<Supplier, FullSupplierDetail> supplierConverter,
            Converter<Locality, ICatLocDetail> localityConverter,
            Converter<Category, ICatLocDetail> categoryConverter) {
        // Spring instantiates converters - see converters.xml
        Validate.notNull(supplierConverter);
        this.supplierConverter = supplierConverter;
        this.localityConverter = localityConverter;
        this.categoryConverter = categoryConverter;
    }

    /**************************************************************************/
    /* Convert methods                                                        */
    /**************************************************************************/
    /**
     * @{inheritDoc}
     */
    @Override
    public FullDemandDetail convertToTarget(Demand source) {
        FullDemandDetail detail = new FullDemandDetail();
        detail.setDemandId(source.getId());
        detail.setDemandTitle(source.getTitle());
        detail.setDescription(source.getDescription());
        detail.setPrice(source.getPrice());
        detail.setCreated(convertDate(source.getCreatedDate()));
        detail.setEndDate(convertDate(source.getEndDate()));
        detail.setValidTo(convertDate(source.getValidTo()));
        detail.setMaxSuppliers(source.getMaxSuppliers() == null ? 0 : source.getMaxSuppliers());
        detail.setMinRating(source.getMinRating() == null ? 0 : source.getMinRating());
        if (source.getClient() != null && source.getClient().getOveralRating() != null) {
            detail.setClientRating(source.getClient().getOveralRating());
        } else {
            detail.setClientRating(0);
        }
        //categories
        detail.setCategories(categoryConverter.convertToTargetList(source.getCategories()));
        //localities
        detail.setLocalities(localityConverter.convertToTargetList(source.getLocalities()));

        detail.setDemandStatus(source.getStatus());

        if (source.getType() != null) {
            detail.setDemandType(source.getType().getDescription());
        }

        detail.setClientId(source.getClient().getId());

        setExcludedSuppliers(source, detail);

        return detail;

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public Demand convertToSource(FullDemandDetail source) {
        return demandService.getById(source.getDemandId());
    }

    //--------------------------------------------------- PRIVATE METHODS ----------------------------------------------
    /**
     * Sets excluded suppliers
     * @param demand domain object
     * @param detail object
     */
    private void setExcludedSuppliers(Demand demand, FullDemandDetail detail) {
        final List<FullSupplierDetail> excludedSuppliers = new ArrayList<FullSupplierDetail>();
        if (demand.getExcludedSuppliers() != null) {
            excludedSuppliers.addAll(supplierConverter.convertToTargetList(demand.getExcludedSuppliers()));
        }
        detail.setExcludedSuppliers(excludedSuppliers);
    }
}
