package jbpm.client.wrapper;

import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;


import java.util.*;

public class ClientSession {
    private String userName;
    private String password;

    private int taskFetchSize;

    private UserTaskServicesClient userTaskServicesClient;
    private ProcessServicesClient processServicesClient;

    private QueryServicesClient queryServicesClient;

    public ClientSession(String userName, String password) {
        this(userName, password, 10);
    }

    public ClientSession(String userName, String password, int taskFetchSize) {
        this.userName = userName;
        this.password = password;
        this.taskFetchSize = taskFetchSize;
    }

    public List<TaskSummary> getUserTasks() {
        return getUserTasks(0);
    }

    public List<TaskSummary> getUserTasks(int pageNumber) {
        return getUserTaskServicesClient().findTasksOwned(userName, Arrays.asList(TaskStatus.Ready.toString(), TaskStatus.Reserved.toString(), TaskStatus.InProgress.toString()), pageNumber, taskFetchSize);
    }

    public Long initiateProcess(String processDefId) {
        return initiateProcess(processDefId, new HashMap<>());
    }

    public Long initiateProcess(String processDefId, Map<String, Object> variables) {
        ProcessDefinition processDef = getLatestProcessDefinitionById(processDefId);
        return initiateProcess(processDef.getContainerId(), processDef.getId(), variables);
    }

    public Long initiateProcess(String containerId, String processDefId) {
        return initiateProcess(containerId, processDefId, null);
    }

    public Long initiateProcess(String containerId, String processDefId, Map<String, Object> variables) {
        return getProcessServicesClient().startProcess(containerId, processDefId, variables);
    }


    public ProcessDefinition getLatestProcessDefinitionById(String processDefId) {
        Comparator<ProcessDefinition> maxProcessDefVersion = Comparator.comparing(ProcessDefinition::getVersion);
        return getQueryServicesClient().findProcessesById(processDefId).stream().max(maxProcessDefVersion).get();
    }

    public TaskInstance findTask(Long id) {
        return getUserTaskServicesClient().findTaskById(id);
    }

    public void startTask(Long taskId) {
        TaskInstance task = findTask(taskId);
        startTask(task);
    }

    public void startTask(TaskInstance task) {
        getUserTaskServicesClient().startTask(task.getContainerId(), task.getId(), userName);
    }

    public void completeTask(TaskInstance task, Map<String, Object> params) {
        getUserTaskServicesClient().completeTask(task.getContainerId(), task.getId(), userName, params);
    }

    public void completeTask(Long taskId, Map<String, Object> params) {
        TaskInstance task = findTask(taskId);
        completeTask(task, params);
    }


    public UserTaskServicesClient getUserTaskServicesClient() {
        if (userTaskServicesClient == null) {
            userTaskServicesClient = ClientFactory.getServicesClient(UserTaskServicesClient.class, userName, password);
        }
        return userTaskServicesClient;
    }

    public ProcessServicesClient getProcessServicesClient() {
        if (processServicesClient == null) {
            processServicesClient = ClientFactory.getServicesClient(ProcessServicesClient.class, userName, password);
        }
        return processServicesClient;
    }

    public QueryServicesClient getQueryServicesClient() {
        if (queryServicesClient == null) {
            queryServicesClient = ClientFactory.getServicesClient(QueryServicesClient.class, userName, password);
        }
        return queryServicesClient;
    }
}
