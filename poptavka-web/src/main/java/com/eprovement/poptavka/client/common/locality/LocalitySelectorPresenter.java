package com.eprovement.poptavka.client.common.locality;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

import com.eprovement.poptavka.client.root.RootEventBus;
import com.eprovement.poptavka.domain.enums.LocalityType;
import com.eprovement.poptavka.shared.domain.LocalityDetail;
import java.util.List;

@Presenter(view = LocalitySelectorView.class, multiple = true)
public class LocalitySelectorPresenter
    extends LazyPresenter<LocalitySelectorPresenter.LocalitySelectorInterface, RootEventBus> {

    /** View interface methods. **/
    public interface LocalitySelectorInterface extends LazyView {

        ListBox getRegionList();

        ListBox getDistrictList();

        ListBox getCityList();

        ListBox getSelectedList();

        String getSelectedItem(LocalityType localityType);

        void toggleLoader();

        boolean isValid();

        void addToSelectedList(LocalityType type);

        void removeFromSelectedList();

        Widget getWidgetView();

        ArrayList<String> getSelectedLocalityCodes();
    }

    // for preventing users from double clicking list item, what would result in multiple instances of
    // same list
    private boolean preventMultipleCalls = false;

    public void bindView() {
        view.getRegionList().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isControlKeyDown()) {
                    view.addToSelectedList(LocalityType.REGION);
                } else {
                    if (preventMultipleCalls) {
                        return;
                    }
                    // TODO cursor change would be nice;
                    preventMultipleCalls = true;
                    view.toggleLoader();
                    view.getDistrictList().setVisible(false);
                    view.getCityList().setVisible(false);
                    eventBus.getChildLocalities(LocalityType.DISTRICT, view.getSelectedItem(LocalityType.REGION));
                }
            }
        });
        view.getDistrictList().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isControlKeyDown()) {
                    view.addToSelectedList(LocalityType.DISTRICT);
                } else {
                    if (preventMultipleCalls) {
                        return;
                    }
                    // TODO cursor change would be nice;
                    preventMultipleCalls = true;
                    view.toggleLoader();
                    view.getCityList().setVisible(false);
                    eventBus.getChildLocalities(LocalityType.CITY, view.getSelectedItem(LocalityType.DISTRICT));
                }
            }
        });
        view.getCityList().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                view.addToSelectedList(LocalityType.CITY);
            }
        });
        view.getSelectedList().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent arg0) {
                view.removeFromSelectedList();
            }
        });
    }

    public void initLocalityWidget(SimplePanel embedWidget) {
        eventBus.getLocalities(LocalityType.REGION);
        embedWidget.setWidget(view.getWidgetView());
    }

    public void onSetLocalityData(LocalityType localityType, List<LocalityDetail> list) {
        switch (localityType) {
            case DISTRICT:
                view.toggleLoader();
                setData(view.getDistrictList(), list);
                break;
            case REGION:
                setData(view.getRegionList(), list);
                break;
            case CITY:
                view.toggleLoader();
                setData(view.getCityList(), list);
                break;
            default:
                break;
        }
    }

    private void setData(final ListBox box, final List<LocalityDetail> list) {
        box.clear();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                for (int i = 0; i < list.size(); i++) {
                    box.addItem(list.get(i).getName(), list.get(i).getCode());
                }
            }
        });
        box.setVisible(true);
        // now he can select list again
        preventMultipleCalls = false;
    }

}