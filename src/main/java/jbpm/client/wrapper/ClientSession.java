package jbpm.client.wrapper;

import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

import java.util.Comparator;
import java.util.List;

public class ClientSession {
    private String userName;
    private String password;

    private int taskFetchSize;

    private UserTaskServicesClient userTaskServicesClient;
    private ProcessServicesClient processServicesClient;

    private QueryServicesClient queryServicesClient;
    public ClientSession(String userName, String password) {
        this(userName,password,10);
    }
    public ClientSession(String userName, String password,int taskFetchSize) {
        this.userName = userName;
        this.password = password;
        this.taskFetchSize = taskFetchSize;
    }

    public List<TaskSummary> getUserTasks(){
        return getUserTasks(0);
    }
    public List<TaskSummary> getUserTasks(int pageNumber){
        return getUserTaskServicesClient().findTasks(userName,pageNumber,taskFetchSize);
    }

    public Long initiateProcess(String processDefId){
        ProcessDefinition processDef = getLatestProcessDefinitionById(processDefId);
        return initiateProcess(processDef.getContainerId(),processDef.getId());
    }

    public Long initiateProcess(String containerId,String processDefId){
        return getProcessServicesClient().startProcess(containerId,processDefId);
    }

    public ProcessDefinition getLatestProcessDefinitionById(String processDefId){
        Comparator<ProcessDefinition> maxProcessDefVersion = Comparator.comparing( ProcessDefinition::getVersion);
        return getQueryServicesClient().findProcessesById(processDefId).stream().max(maxProcessDefVersion).get();
    }



    public UserTaskServicesClient getUserTaskServicesClient(){
        if (userTaskServicesClient == null){
            userTaskServicesClient = ClientFactory.getServicesClient(UserTaskServicesClient.class,userName,password);
        }
        return userTaskServicesClient;
    }

    public ProcessServicesClient getProcessServicesClient(){
        if(processServicesClient == null){
            processServicesClient = ClientFactory.getServicesClient(ProcessServicesClient.class,userName,password);
        }
        return processServicesClient;
    }

    public QueryServicesClient getQueryServicesClient(){
        if(queryServicesClient == null){
            queryServicesClient = ClientFactory.getServicesClient(QueryServicesClient.class,userName,password);
        }
        return queryServicesClient;
    }
}
