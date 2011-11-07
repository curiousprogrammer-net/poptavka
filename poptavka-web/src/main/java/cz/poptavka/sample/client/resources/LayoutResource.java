package cz.poptavka.sample.client.resources;

import com.google.gwt.resources.client.CssResource;

/**
 *
 * GWT Wrapper for base app layout.
 *
 * @author Beho
 *
 */
public interface LayoutResource extends CssResource {

    /** MainView.class **/
    @ClassName("layout-public-main")
    String layoutPublic();
    @ClassName("layout-user-main")
    String layoutUser();
    @ClassName("header-container")
    String headerContainer();
    @ClassName("footer-container")
    String footerContainer();
    @ClassName("body-container")
    String bodyContainer();
    @ClassName("login-area")
    String loginArea();
    @ClassName("footer-toggle")
    String footerToggle();

    /** UserView.class **/
    @ClassName("full-size-panel")
    String fullSize();

    /** DemandCreation.class **/
    @ClassName("stackLayout")
    String stackLayout();
    @ClassName("stackLayoutPanelHeader")
    String stackLayoutPanelHeader();

    @ClassName("home-menu")
    String homeMenu();
    @ClassName("search-view")
    String searchView();

    /** DELETE AFTER THIS LINE **/



}
