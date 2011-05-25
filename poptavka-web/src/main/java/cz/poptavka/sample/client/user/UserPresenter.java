package cz.poptavka.sample.client.user;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

import cz.poptavka.sample.client.user.demands.DemandsLayoutPresenter;
import cz.poptavka.sample.shared.domain.UserDetail;

/**
 * Main presenter for User account.
 *
 * @author Beho
 *
 */
@Presenter(view = UserView.class)
public class UserPresenter extends LazyPresenter<UserPresenter.UserViewInterface, UserEventBus> {

    private static final Logger LOGGER = Logger.getLogger("UserPresenter");

    public interface UserViewInterface extends LazyView {
        void setBody(Widget body);

        Widget getWidgetView();
    }

    private DemandsLayoutPresenter demandsLayoutPresenter = null;

    public void onAtAccount(UserDetail user) {
        eventBus.setUserLayout();
        if (demandsLayoutPresenter != null) {
            eventBus.removeHandler(demandsLayoutPresenter);
        }
        demandsLayoutPresenter = eventBus.addHandler(DemandsLayoutPresenter.class);
        demandsLayoutPresenter.onAtAccount(new UserDetail());
        eventBus.setBodyHolderWidget(view.getWidgetView());
//        eventBus.invokeMyDemands();
    }

    public void onSetTabWidget(Widget tabBody) {
        LOGGER.info("widget body here !!");
        view.setBody(tabBody);
    }
}
