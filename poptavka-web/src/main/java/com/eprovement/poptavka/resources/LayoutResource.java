package com.eprovement.poptavka.resources;

import com.google.gwt.resources.client.CssResource;

/**
 * GWT Wrapper for base app layout.
 *
 * @author Beho, Jaro
 */
public interface LayoutResource extends CssResource {

    /** MainView.class **/
    @ClassName("body-background")
    String bodyBackground();

    @ClassName("body-container")
    String bodyContainer();

    @ClassName("body-content")
    String bodyContent();

    @ClassName("body-full-container")
    String bodyFullContainer();

    @ClassName("layout-user-main")
    String layoutUser();

    @ClassName("full-size-panel")
    String fullSize();

    @ClassName("fluid-main-grid")
    String fluidMainGrid();

    @ClassName("header-container")
    String headerContainer();

    @ClassName("header-content")
    String headerContent();

    /** Tool Bar Styles **/
    @ClassName("toolbar")
    String bodyNavbar();

    @ClassName("toolbar-header")
    String toolbarHeader();

    @ClassName("toolbar-items-block")
    String toolbarItemsBlock();

    @ClassName("toolbar-item")
    String toolbarItem();

    @ClassName("toolbar-button-blue")
    String toolbarButtonBlue();

    /** Home Menu Styles **/
    @ClassName("home-menu")
    String homeMenu();

    @ClassName("home-logo")
    String homeLogo();

    /** User Menu Styles **/
    @ClassName("user-logo")
    String userLogo();

    /** Search Bar Styles **/
    @ClassName("search-bar")
    String searchBar();

    @ClassName("search-view")
    String searchView();

    /** Login Styles **/
    @ClassName("login")
    String login();

    @ClassName("login-bar")
    String loginBar();

    @ClassName("login-button")
    String loginButton();

    @ClassName("logout-button")
    String logoutButton();

    @ClassName("logout-menu-bar")
    String logoutMenuBar();

    @ClassName("help-button")
    String helpButton();

    /** Toolbar Module styles **/
    @ClassName("submenu-header")
    String submenuHeader();

    @ClassName("submenu-list")
    String submenuList();

    @ClassName("pager")
    String pager();

    /** CellTree Styles**/
    @ClassName("cellTree-container")
    String cellTreeContainer();

    /** Footer Module Styles **/
    @ClassName("footer-container")
    String footerContainer();

    @ClassName("footer-toggle")
    String footerToggle();

    /** Other Styles **/
    @ClassName("data-grid-container")
    String dataGridContainer();

    @ClassName("detail-info")
    String detailInfo();

    @ClassName("popup-dropdown-container")
    String popupDropdownContainer();

    @ClassName("tab-layout-container")
    String tabLayoutContainer();

    @ClassName("tab-layout-panel")
    String tabLayoutPanel();

    @ClassName("stack-layout")
    String stackLayout();

    @ClassName("stack-layout-panel-header")
    String stackLayoutPanelHeader();

    @ClassName("selected")
    String selected();

    /** DELETE AFTER THIS LINE **/

}