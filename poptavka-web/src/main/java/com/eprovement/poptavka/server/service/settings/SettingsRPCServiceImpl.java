/*
 * Copyright (C) 2012, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.service.settings;

import com.eprovement.poptavka.application.security.Encryptor;
import com.eprovement.poptavka.domain.settings.NotificationItem;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.googlecode.genericdao.search.Search;

import com.eprovement.poptavka.client.service.demand.SettingsRPCService;
import com.eprovement.poptavka.domain.address.Address;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.demand.Category;
import com.eprovement.poptavka.domain.enums.CommonAccessRoles;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.domain.product.Service;
import com.eprovement.poptavka.domain.product.UserService;
import com.eprovement.poptavka.domain.user.BusinessUser;
import com.eprovement.poptavka.domain.user.BusinessUserRole;
import com.eprovement.poptavka.domain.user.Client;
import com.eprovement.poptavka.domain.user.Supplier;
import com.eprovement.poptavka.domain.user.User;
import com.eprovement.poptavka.server.converter.Converter;
import com.eprovement.poptavka.server.security.PoptavkaUserAuthentication;
import com.eprovement.poptavka.server.service.AutoinjectingRemoteService;
import com.eprovement.poptavka.service.GeneralService;
import com.eprovement.poptavka.shared.domain.AddressDetail;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.selectors.catLocSelector.ICatLocDetail;
import com.eprovement.poptavka.shared.domain.ServiceDetail;
import com.eprovement.poptavka.shared.domain.SupplierDetail;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;
import com.eprovement.poptavka.shared.domain.settings.NotificationDetail;
import com.eprovement.poptavka.shared.domain.settings.SettingDetail;
import com.eprovement.poptavka.shared.exceptions.ApplicationSecurityException;
import com.eprovement.poptavka.shared.exceptions.RPCException;
import com.googlecode.genericdao.search.Field;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This RPC handles all requests from Settings module.
 * @author Martin Slavkovsky
 */
@Configurable
public class SettingsRPCServiceImpl extends AutoinjectingRemoteService
        implements SettingsRPCService {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private GeneralService generalService;
    private Encryptor encryptor;
    private Converter<Locality, ICatLocDetail> localityConverter;
    private Converter<Category, ICatLocDetail> categoryConverter;
    private Converter<Address, AddressDetail> addressConverter;
    private Converter<BusinessUser, BusinessUserDetail> businessUserConverter;
    private Converter<Service, ServiceDetail> serviceConverter;

    /**************************************************************************/
    /* Autowire services and converters                                       */
    /**************************************************************************/
    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Autowired
    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Autowired
    public void setLocalityConverter(
            @Qualifier("localityConverter") Converter<Locality, ICatLocDetail> localityConverter) {
        this.localityConverter = localityConverter;
    }

    @Autowired
    public void setCategoryConverter(
            @Qualifier("categoryConverter") Converter<Category, ICatLocDetail> categoryConverter) {
        this.categoryConverter = categoryConverter;
    }

    @Autowired
    public void setAddressConverter(
            @Qualifier("addressConverter") Converter<Address, AddressDetail> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Autowired
    public void setBusinessUserConverter(
            @Qualifier("businessUserConverter") Converter<BusinessUser, BusinessUserDetail> businessUserConverter) {
        this.businessUserConverter = businessUserConverter;
    }

    @Autowired
    public void setServiceConverter(
            @Qualifier("serviceConverter") Converter<Service, ServiceDetail> serviceConverter) {
        this.serviceConverter = serviceConverter;
    }

    /**************************************************************************/
    /* Business events                                                        */
    /**************************************************************************/
    /**
     * Get user settings.
     * @param userId
     * @return settings detail
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public SettingDetail getUserSettings(long userId) throws RPCException, ApplicationSecurityException {
        final BusinessUser user = (BusinessUser) generalService.find(User.class, userId);

        SettingDetail settingsDetail = new SettingDetail();
        settingsDetail.setUserId(userId);
        //user settings
        settingsDetail.setUser(businessUserConverter.convertToTarget(user));

        List<BusinessUserRole> roles = user.getBusinessUserRoles();
        for (BusinessUserRole role : roles) {
            //client settings
            if (role instanceof Client) {
                Client client = (Client) role;
                settingsDetail.setClientRating(client.getOveralRating());
            }
            //supplier settings
            if (role instanceof Supplier) {
                Supplier supplier = (Supplier) role;
                SupplierDetail supplierDetail = new SupplierDetail();
                supplierDetail.setOveralRating(supplier.getOveralRating());
                supplierDetail.setLocalities(localityConverter.convertToTargetList(supplier.getLocalities()));
                supplierDetail.setCategories(categoryConverter.convertToTargetList(supplier.getCategories()));
                supplierDetail.setServices(convertServices(supplier.getBusinessUser().getUserServices()));
                settingsDetail.setSupplier(supplierDetail);
            }

        }
        //system settings
        ArrayList<NotificationDetail> notifications = new ArrayList<NotificationDetail>();
        for (NotificationItem item : user.getSettings().getNotificationItems()) {
            NotificationDetail targetItem = new NotificationDetail();
            /**/ targetItem.setNotificationIdemId(item.getId());
            /**/ targetItem.setEnabled(item.isEnabled());
            /**/ targetItem.setName(item.getNotification().getName());
            /**/ targetItem.setPeriod(item.getPeriod());
            notifications.add(targetItem);
        }
        settingsDetail.setNotifications(notifications);

        return settingsDetail;
    }

    /**
     * This method will update number of unread messages of logged user.
     * Since this RPC class requires access of authenticated user (see security-web.xml) this method will be called
     * only when PoptavkaUserAuthentication object exist in SecurityContextHolder and we can retrieve userId.
     *
     * @return UnreadMessagesDetail with number of unread messages and other info to be displayed after users logs in
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public UnreadMessagesDetail updateUnreadMessagesCount() throws RPCException, ApplicationSecurityException {
        Long userId = ((PoptavkaUserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        Search unreadMessagesSearch = new Search(UserMessage.class);
        unreadMessagesSearch.addFilterNotNull("message.demand");
        unreadMessagesSearch.addFilterEqual("isRead", false);
        unreadMessagesSearch.addFilterEqual("user.id", userId.longValue());
        unreadMessagesSearch.addField("id", Field.OP_COUNT);
        unreadMessagesSearch.setResultMode(Search.RESULT_SINGLE);
        UnreadMessagesDetail unreadMessagesDetail = new UnreadMessagesDetail();
        unreadMessagesDetail.setUnreadMessagesCount((
                (Long) generalService.searchUnique(unreadMessagesSearch)).intValue());
        Search unreadSystemMessagesSearch = new Search(UserMessage.class);
        unreadSystemMessagesSearch.addFilterNull("message.demand");
        unreadSystemMessagesSearch.addFilterEqual("isRead", false);
        unreadSystemMessagesSearch.addFilterEqual("user.id", userId.longValue());
        unreadSystemMessagesSearch.addField("id", Field.OP_COUNT);
        unreadSystemMessagesSearch.setResultMode(Search.RESULT_SINGLE);
        unreadMessagesDetail.setUnreadSystemMessageCount((
                (Long) generalService.searchUnique(unreadSystemMessagesSearch)).intValue());
        return unreadMessagesDetail;
    }

    /**
     * Updates settings data.
     * @param settingsDetail
     * @return true if updated, false otherwise
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public Boolean updateSettings(SettingDetail settingsDetail) throws RPCException, ApplicationSecurityException {
        final BusinessUser user = (BusinessUser) generalService.searchUnique(
                new Search(User.class).addFilterEqual("id", settingsDetail.getUserId()));

        List<BusinessUserRole> roles = user.getBusinessUserRoles();
        for (BusinessUserRole role : roles) {
            /** CLIENT DATA. **/
            if (role instanceof Client) {
                Client client = (Client) role;
                client.setOveralRating(settingsDetail.getClientRating());

            }
            /** SUPPLIER DATA. **/
            if (role instanceof Supplier) {
                Supplier supplier = (Supplier) role;
                if (settingsDetail.getSupplier().getOveralRating() != null) {
                    supplier.setOveralRating(settingsDetail.getSupplier().getOveralRating());
                }
                supplier.setLocalities(localityConverter.convertToSourceList(
                        settingsDetail.getSupplier().getLocalities()));
                supplier.setCategories(categoryConverter.convertToSourceList(
                        settingsDetail.getSupplier().getCategories()));
            }

        }
        /** CONTACT DATA. **/
        user.getBusinessUserData().setPersonFirstName(settingsDetail.getUser().getPersonFirstName());
        user.getBusinessUserData().setPersonLastName(settingsDetail.getUser().getPersonLastName());
        user.getBusinessUserData().setPhone(settingsDetail.getUser().getPhone());
        /** COMPANY DATA. **/
        user.getBusinessUserData().setCompanyName(settingsDetail.getUser().getCompanyName());
        user.getBusinessUserData().setTaxId(settingsDetail.getUser().getTaxId());
        user.getBusinessUserData().setIdentificationNumber(settingsDetail.getUser().getIdentificationNumber());
        /** ADDITIONAL DATA. **/
        user.getBusinessUserData().setWebsite(settingsDetail.getUser().getWebsite());
        user.getBusinessUserData().setDescription(settingsDetail.getUser().getDescription());
        /** ADDRESS DATA. **/
        List<Address> addresses = new ArrayList<Address>();
        for (AddressDetail addressDetail : settingsDetail.getUser().getAddresses()) {
            addresses.add(addressConverter.convertToSource(addressDetail));
        }
        user.setAddresses(addresses);

        /** NOTIFICATIONS. **/
        for (int i = 0; i < user.getSettings().getNotificationItems().size(); i++) {
            NotificationItem domainItem = user.getSettings().getNotificationItems().get(i);
            NotificationDetail item = settingsDetail.getNotifications().get(i);

            domainItem.setPeriod(item.getPeriod());
            domainItem.setEnabled(item.isEnabled());
        }

        generalService.save(user);
        return true;
    }

    /**
     * Check if new and current password matches.
     * @param userId
     * @param password
     * @return true if matches, false otherwise
     * @throws RPCException
     * @throws ApplicationSecurityException
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public Boolean checkCurrentPassword(long userId, String password) throws
            RPCException, ApplicationSecurityException {
        User user = generalService.find(User.class, userId);
        return encryptor.matches(password, user.getPassword());
    }

    /**
     * Set new password for user.
     * @param userId whose password will be set
     * @param newPassword new password to be set
     * @return new random password
     */
    @Override
    @Secured(CommonAccessRoles.CLIENT_ACCESS_ROLE_CODE)
    public Boolean resetPassword(long userId, String newPassword) throws RPCException, ApplicationSecurityException {
        final User user = this.generalService.find(User.class, userId);
        user.setPassword(newPassword);
        generalService.save(user);
        return true;
    }

    /**
     * Convert services
     * @param services
     * @return list of service details
     */
    private List<ServiceDetail> convertServices(List<UserService> services) {
        List<ServiceDetail> servicesDetails = new ArrayList<ServiceDetail>();
        for (UserService userService : services) {
            servicesDetails.add(serviceConverter.convertToTarget(userService.getService()));
        }
        return servicesDetails;
    }
}
