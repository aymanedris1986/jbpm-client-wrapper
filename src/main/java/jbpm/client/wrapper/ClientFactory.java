package jbpm.client.wrapper;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.KieServicesConfiguration;
public class ClientFactory {
    static String kieRestUrl;
    static String adminUserName;
    static String adminUserPassword;

    private ClientFactory(){

    }

    static <T> T geAdminServicesClient(Class<T> serviceClient){
        return getServicesClient(serviceClient,adminUserName, adminUserPassword);
    }
    static <T> T getServicesClient(Class<T> serviceClient,String userName,String password){
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(kieRestUrl, userName, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
        return client.getServicesClient(serviceClient);
    }

    public static void createClientFactory(String kieRestUrl,String adminUserName,String adminUserPassword){
        ClientFactory.kieRestUrl = kieRestUrl;
        ClientFactory.adminUserName = adminUserName;
        ClientFactory.adminUserPassword = adminUserPassword;
    }



    public static ClientSession getClientSession(String userName,String password){
        return new ClientSession(userName,password);
    }
}
