package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TokenAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.model.payload.TokenInfo;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IUserService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WsEndpoint extends WebSocketAdapter {
    static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private Gson jsonConverter;
    private UUID id;
    private SecurityServiceInvoker securityServiceInvoker;
    private User loggedUser;
    private IUserGroupService userGroupService;
    private IUserService userService;

    public WsEndpoint() {
        this.id = UUID.randomUUID();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.securityServiceInvoker = new SecurityServiceInvoker();
        this.userGroupService = new UserGroupService();
        this.userService = new UserService();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        System.out.println("Socket Connected: " + session);
        EndpointConnector.setWsEndpoint(this);

        String tokenQuery = session.getUpgradeRequest().getQueryString();

        if (!tokenQuery.equals("token=null")) {
            String token = tokenQuery.substring(6);

            TokenInfo tokenInfo = TokenAdapter.verifyToken(token);
            this.loggedUser = tokenInfo.getUser();

            UserDto userDto = new UserDto();
            userDto.setUsername(tokenInfo.getUser().getEmail());
            userDto.setPermissions(tokenInfo.getUser().getPermissions());

            ServiceResult serviceResult = new ServiceResult();
            serviceResult.setError(false);
            serviceResult.setMessage("User details retrieved successfully");
            serviceResult.setObject(userDto);

            EndpointConnector.passResultToEndpoint("UserInfo", "getUser", serviceResult);
        }

    }

    @Override
    public void onWebSocketText(String message) {
        WsContractDto contract = null;
        RemoteEndpoint remoteEndpoint = null;

        try {
            remoteEndpoint = this.getSession().getRemote();
            contract = this.resolve(message);
            if (contract == null) {
                contract = new WsContractDto();
                contract.setError(true);
                contract.setErrorMessage("Invalid Json was provided");
                remoteEndpoint.sendString(this.jsonConverter.toJson(contract));
                return;
            }

//            SchemaValidator schemaValidator = new SchemaValidator();
//            for (ConversionObject conversionObject : contract.getArgs()) {
//                Class objectClass = Class.forName(conversionObject.getPackageName());
//                Object object = this.jsonConverter.fromJson(conversionObject.getObject(), objectClass);
//                String result = schemaValidator.validate(object);
//
//                if (!result.equals("OK")) {
//                    contract.setError(true);
//                    contract.setErrorMessage(result);
//                    remoteEndpoint.sendString(serializer.toJson(contract));
//                    return;
//                }
//            }
            User currentUser = (User)this.userService.getById(this.loggedUser.getId()).getObject();
            
            this.loggedUser = currentUser;
            this.loggedUser.getPermissions().addAll(this.getUniqueUserGroupPermissions(this.loggedUser));

            List<Permission> orderedPermissions = this.sortPermissions(this.loggedUser.getPermissions());

            ServiceResult result = this.securityServiceInvoker.process(contract, orderedPermissions);
            contract.setResult(result.getObject());
            contract.setError(result.hasError());
            contract.setErrorMessage(result.getMessage());

            String jsonResult = this.jsonConverter.toJson(contract);
            remoteEndpoint.sendStringByFuture(jsonResult);
        } catch (RuntimeException e) {
            LOGGER.error(String.format(LoggerMessages.WSENDPOINT_ERROR, e));
            e.printStackTrace();
            this.errorDetails(contract, this.jsonConverter, e, remoteEndpoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public WsContractDto resolve(String message) {
        WsContractDto contract = null;
        try {
            contract = this.jsonConverter.fromJson(message, WsContractDto.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return contract;
    }

    public void send(WsContractDto contract) {
        if ((this.getSession() == null) || !this.getSession().isOpen()) {
            return;
        }

        RemoteEndpoint remoteEndpoint = this.getSession().getRemote();
        String jsonResult = this.jsonConverter.toJson(contract);
        remoteEndpoint.sendStringByFuture(jsonResult);
    }


    private void errorDetails(WsContractDto contract, Gson serializer, Exception e, RemoteEndpoint endPoint) {
        contract.setError(true);
        contract.setErrorMessage(e.getMessage());
        try {
            String errDetails = serializer.toJson(contract);
            endPoint.sendString(errDetails);
        } catch (IOException | RuntimeException e1) {
            e1.printStackTrace();
        }
    }

    private List<Permission> getUniqueUserGroupPermissions(User user) {
        List<Permission> userGroupPermissions = new ArrayList<>();
        String userId = user.getId();
        List<String> userGroupIds = user.getUserGroupIds();

        for (String userGroupId : userGroupIds) {
            UserGroup userGroup = (UserGroup) this.userGroupService.getById(userGroupId).getObject();
            List<String> userIds = userGroup.getUserIds();
            boolean isPresent = false;

            for (String userWithinGroupId : userIds) {
                if (userWithinGroupId.equals(userId)) {
                    isPresent = true;
                    break;
                }
            }
            if (isPresent) {
                List<Permission> userGroupPermissionsFromDb = userGroup.getPermissions();

                for (Permission userGroupPermissionFromDb: userGroupPermissionsFromDb) {
                    boolean isPermissionPresent = false;
                    for (Permission userPersmission:user.getPermissions()){
                        if (userGroupPermissionFromDb.getPermissionScope() == userPersmission.getPermissionScope() &&
                                userGroupPermissionFromDb.getPermittedEntityId().equals(userPersmission.getPermittedEntityId()) &&
                                userGroupPermissionFromDb.getPermissionType() == userPersmission.getPermissionType()){
                            isPermissionPresent = true;
                            break;
                        }
                    }
                    if (!isPermissionPresent){
                        userGroupPermissions.add(userGroupPermissionFromDb);
                    }
                }
            }
        }

        return userGroupPermissions;
    }

    private List<Permission> sortPermissions(List<Permission> permissions){
        Comparator<Permission> comparator = Comparator.comparing(permission -> permission.getPermissionScope());
        comparator = comparator.thenComparing(Comparator.comparing(permission -> permission.getPermittedEntityId()));

        List<Permission> sortedPermissions = permissions.stream().sorted(comparator).collect(Collectors.toList());

        return sortedPermissions;
    }
}