package cz.poptavka.sample.client.main.common.category;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.poptavka.sample.client.main.common.creation.ProvidesValidate;
import cz.poptavka.sample.client.resources.StyleResource;


//@Singleton
public class CategorySelectorView extends Composite
    implements CategorySelectorPresenter.CategorySelectorInterface, ProvidesValidate {

    private static CategorySelectorUiBinder uiBinder = GWT.create(CategorySelectorUiBinder.class);
    interface CategorySelectorUiBinder extends UiBinder<Widget, CategorySelectorView> {    }

    //default list visibleItemCount
    private static final int TEN = 10;

    @UiField ScrollPanel masterPanel;
    @UiField Grid categoryListHolder;
    private ArrayList<ListBox> subcategories = new ArrayList<ListBox>();

    @UiField ListBox selectedList;
    private HashSet<String> selectedListStrings = new HashSet<String>();

    @Override
    public void createView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Widget getWidgetView() {
        return this;
    }

    @Override
    public ListBox getSelectedList() {
        return selectedList;
    }

    @Override
    public void addToSelectedList(String text, String value) {
        if (!selectedListStrings.contains(text)) {
            selectedList.addItem(text, value);
            selectedListStrings.add(text);
        }
    }

    @Override
    public void removeFromSelectedList() {
        int index = selectedList.getSelectedIndex();
        String item = selectedList.getItemText(index);
        selectedListStrings.remove(item);
        selectedList.removeItem(index);
    }

    /** Returns actual free depth level. **/
    @Override
    public int getListIndex() {
        return ((subcategories.size() - 1) >= 0 ? (subcategories.size() - 1) : 0);
    }

    @Override
    public Grid getListHolder() {
        return categoryListHolder;
    }

    @Override
    public ListBox createListAtIndex(int index) {
        ListBox list = new ListBox();
        list.setVisibleItemCount(TEN);
        list.setWidth("200");
        if (subcategories.isEmpty()) {
            subcategories.add(list);
        } else {
            subcategories.add(index, list);
        }
        return list;
    }

    @Override
    public void clearChildrenLists(int index) {
        for (int i = getListIndex(); i > index; i--) {
            categoryListHolder.clearCell(0, i);
            subcategories.remove(i);
        }
    }

    public ScrollPanel getScrollPanel() {
        return masterPanel;
    }

    @Override
    public boolean isValid() {
        return selectedList.getItemCount() > 0;
    }

    /** Demand cration getValues method. **/
    public ArrayList<String> getSelectedCategoryCodes() {
        ArrayList<String> codes = new ArrayList<String>();
        for (int i = 0; i < selectedList.getItemCount(); i++) {
            codes.add(selectedList.getValue(i));
        }
        return codes;
    }

    @Override
    public void showLoader(int index) {
        try {
            int columCount = categoryListHolder.getColumnCount();
            int positionToInsert = getListIndex() + 1;
            if (columCount == positionToInsert) {
                categoryListHolder.resizeColumns(columCount + 1);
            }
            HTML html = new HTML("&nbsp;");
            categoryListHolder.setWidget(0, index, html);
            html.setStyleName(StyleResource.INSTANCE.common().smallLoader());
        } catch (Exception ex) {
            Window.alert(ex.getMessage() + "\nColumn count: " + categoryListHolder.getColumnCount()
                    + " posToInsert: " + getListIndex());
        }
    }



}