package cz.poptavka.sample.server.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Search;

import cz.poptavka.sample.client.service.demand.UserRPCService;
import cz.poptavka.sample.domain.user.BusinessUser;
import cz.poptavka.sample.domain.user.BusinessUserRole;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.domain.user.User;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.user.SupplierService;
import cz.poptavka.sample.shared.domain.UserDetail;
import cz.poptavka.sample.shared.domain.UserDetail.Role;

public class UserRPCServiceImpl extends AutoinjectingRemoteService implements UserRPCService {

    private static final long serialVersionUID = 1132667081084321575L;
    private GeneralService generalService;
    private SupplierService supplierService;

    @Autowired
    public void setSupplierService(SupplierService roleService) {
        this.supplierService = roleService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Override
    public String loginUser(UserDetail userDetail) {

        // TODO BACKEND - pripravit novu servisu, ktora nam overi aj usernam a password. Predany Password sa musi na
        // backende enkryptovat jednosmernou encryptovacou funkcionu a potom sa porovna s passwordom z DB. Zatial je
        // overovanie hesla vypnute a overuje sa len username
//        final User user = (User) generalService.searchUnique(
//                new Search(User.class).addFilterEqual(
//                    "email", userDetail.getEmail()).addFilterEqual(
//                        "password", userDetail.getPassword()));
        final User user = (User) generalService.searchUnique(
                new Search(User.class).addFilterEqual("email", userDetail.getEmail()));
        if (user == null) {
            System.out.println("NULL branch");
            return null;
        }
        userDetail.setUserId(user.getId());
        System.out.println("USER branch");
        return "id=" + userDetail.getUserId();
    }

    @Override
    public UserDetail getSignedUser(String sessionId) {
        // TODO make real implementation of getting user according to sessionID
        // now it's just fake string, that needs to be parsed
        String[] parsedSession = sessionId.split("=");
        Long userId = Long.parseLong(parsedSession[(parsedSession.length - 1)]);

        final BusinessUser user = (BusinessUser) generalService.searchUnique(
                new Search(User.class).addFilterEqual("id", userId));

        UserDetail userDetail = new UserDetail();

        List<BusinessUserRole> roles = user.getBusinessUserRoles();
        for (BusinessUserRole role : roles) {
            if (role instanceof Client) {
                userDetail.setClientId(role.getId());
                userDetail.addRole(Role.CLIENT);
            }
            if (role instanceof Supplier) {
                userDetail.setSupplierId(role.getId());
                userDetail.addRole(Role.SUPPLIER);
            }
            //add other roles
        }
        // TODO add other useful attributes like, count of new demands, offers,
        // messages and so on
        userDetail.setUserId(roles.get(0).getBusinessUser().getId());

        return userDetail;
    }

    @Override
    public UserDetail getUserById(Long userId) {
        return UserDetail.createUserDetail(generalService.find(BusinessUser.class, userId));
    }
}
