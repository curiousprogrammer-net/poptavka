package cz.poptavka.sample.client.home.suppliers;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;
import cz.poptavka.sample.client.home.HomeEventBus;
import cz.poptavka.sample.client.service.demand.CategoryRPCServiceAsync;
import cz.poptavka.sample.client.service.demand.DemandRPCServiceAsync;
import cz.poptavka.sample.client.service.demand.LocalityRPCServiceAsync;
import cz.poptavka.sample.client.service.demand.SupplierRPCServiceAsync;
import cz.poptavka.sample.domain.address.LocalityType;
import cz.poptavka.sample.shared.domain.CategoryDetail;
import cz.poptavka.sample.shared.domain.LocalityDetail;
import cz.poptavka.sample.shared.domain.supplier.FullSupplierDetail;

//@SuppressWarnings("deprecation")
@EventHandler
public class SuppliersHandler extends BaseEventHandler<HomeEventBus> {

    private LocalityRPCServiceAsync localityService = null;
    private CategoryRPCServiceAsync categoryService = null;
    private DemandRPCServiceAsync demandService = null;
    private SupplierRPCServiceAsync supplierService = null;
    private static final Logger LOGGER = Logger.getLogger("MainHandler");

    @Inject
    public void setLocalityService(LocalityRPCServiceAsync service) {
        localityService = service;
    }

    @Inject
    public void setCategoryService(CategoryRPCServiceAsync service) {
        categoryService = service;
    }

    @Inject
    void setDemandService(DemandRPCServiceAsync service) {
        demandService = service;
    }

    @Inject
    public void setSupplierService(SupplierRPCServiceAsync service) {
        supplierService = service;
    }

    // *** GET LOCALITY
    // ***************************************************************************
    /**
     * Get all localities. Used for display in listBox localities.
     */
    public void onGetLocalities() {
        localityService.getLocalities(LocalityType.REGION,
                new AsyncCallback<ArrayList<LocalityDetail>>() {

                    @Override
                    public void onSuccess(ArrayList<LocalityDetail> list) {
                        eventBus.setLocalityData(list);
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        LOGGER.info("onFailureGetLocalities");
                    }
                });
    }
    // *** GET CATEGORIES
    // ***************************************************************************

    /**
     * Get all categories. Used for display in listBox categories.
     */
    public void onGetCategories() {
        categoryService.getCategories(
                new AsyncCallback<ArrayList<CategoryDetail>>() {

                    @Override
                    public void onSuccess(ArrayList<CategoryDetail> list) {
                        eventBus.displayRootcategories(list);
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        LOGGER.info("onFailureCategory");
                    }
                });
    }

    public void onGetSubCategories(final Long category) {
        categoryService.getCategoryChildren(category, new AsyncCallback<ArrayList<CategoryDetail>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Get children categories failed.");
            }

            @Override
            public void onSuccess(ArrayList<CategoryDetail> result) {
                LOGGER.info("Found subcategories: " + result.size());
                eventBus.displaySubCategories(result, category);
            }
        });
    }

    // *** GET SUPPLIERS COUNTS
    // ***************************************************************************
    public void onGetSuppliersCountByCategoryLocality(Long category, String locality) {
        supplierService.getSuppliersCount(category, locality, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(Long result) {
//                eventBus.createAsyncDataProviderSupplier(result);
                eventBus.resetDisplaySuppliersPager(result.intValue());
            }
        });
    }
    public void onGetSuppliersCountByCategory(Long category) {
        supplierService.getSuppliersCount(category, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(Long result) {
//                eventBus.createAsyncDataProviderSupplier(result);
                eventBus.resetDisplaySuppliersPager(result.intValue());
            }
        });
    }

    public void onGetSuppliersCount(Long category, String locality) {
        supplierService.getSuppliersCount(category, locality, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(Long result) {
//                eventBus.setResultCount(result);
//                eventBus.createAsyncDataProviderSupplier(result);
                eventBus.resetDisplaySuppliersPager(result.intValue());
            }
        });
    }

    public void onGetSuppliersByCategoryLocality(int start, int count, Long category, String locality) {
        supplierService.getSuppliers(start, count, category, locality,
                new AsyncCallback<ArrayList<FullSupplierDetail>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void onSuccess(ArrayList<FullSupplierDetail> result) {
                        eventBus.displaySuppliers(result);
                    }
                });
    }

    public void onGetSuppliersByCategory(int start, int count, Long category) {
        supplierService.getSuppliers(start, count, category, new AsyncCallback<ArrayList<FullSupplierDetail>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(ArrayList<FullSupplierDetail> result) {
                eventBus.displaySuppliers(result);
            }
        });
    }
//    // *** GET DEMANDS
//    // ***************************************************************************
//    public void onGetDemand(FullDemandDetail fullDemandDetail) {
//        demandService.getWholeDemand(fullDemandDetail.getDemandId(), new AsyncCallback<Demand>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                Window.alert("Failed to retrieve whole demand. We are sorry :(. Try later.");
//            }
//
//            @Override
//            public void onSuccess(Demand result) {
////                eventBus.setDemand(result);
//            }
//        });
//    }
//    //*************** GET DEMANDS COUNT *********************
//
//    public void onGetAllDemandsCount() {
//        demandService.getAllDemandsCount(new AsyncCallback<Long>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public void onSuccess(Long result) {
//                eventBus.setResultSource("all");
//                eventBus.setResultCount(result);
//                eventBus.createAsyncDataProvider();
//            }
//        });
//    }
//
//    public void onGetDemandsCountCategory(long id) {
//        demandService.getDemandsCountByCategory(id, new AsyncCallback<Long>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                throw new UnsupportedOperationException("onGetDemandsCountCategory failed.");
//            }
//
//            @Override
//            public void onSuccess(Long result) {
//                eventBus.setResultSource("category");
//                eventBus.setResultCount(result);
//                eventBus.createAsyncDataProvider();
//            }
//        });
//    }
//
//    public void onGetDemandsCountLocality(String code) {
//        demandService.getDemandsCountByLocality(code, new AsyncCallback<Long>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                throw new UnsupportedOperationException("onGetDemandsCountLocality failed.");
//            }
//
//            @Override
//            public void onSuccess(Long result) {
//                eventBus.setResultSource("locality");
//                eventBus.setResultCount(result);
//                eventBus.createAsyncDataProvider();
//            }
//        });
//    }
//
//    //*************** GET DEMANDS DATA *********************
//    /**
//     * Get all demand from database.
//     */
//    public void onGetDemands(int fromResult, int toResult) {
//        demandService.getDemands(fromResult, toResult, new AsyncCallback<List<DemandDetail>>() {
//
//            @Override
//            public void onSuccess(List<DemandDetail> result) {
//                eventBus.displayDemands(result);
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                LOGGER.info("onFailureGetDemands");
//            }
//        });
//    }
//
//    public void onGetDemandsByCategories(int fromResult, int toResult, long id) {
//        demandService.getDemandsByCategory(fromResult, toResult, id,
//                new AsyncCallback<List<DemandDetail>>() {
//
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        throw new UnsupportedOperationException("Not supported yet.");
//                    }
//
//                    @Override
//                    public void onSuccess(List<DemandDetail> result) {
//                        eventBus.displayDemands(result);
//                    }
//                });
//    }
//
//    public void onGetDemandsByLocalities(int fromResult, int toResult, String id) {
//        demandService.getDemandsByLocality(fromResult, toResult, id,
//                new AsyncCallback<List<DemandDetail>>() {
//
//                    @Override
//                    public void onSuccess(List<DemandDetail> result) {
//                        LOGGER.info("onSuccessGetDemandsByLocality");
//                        eventBus.displayDemands(result);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        LOGGER.info("onFailureGetDemandsByLocality");
//                    }
//                });
//    }
}