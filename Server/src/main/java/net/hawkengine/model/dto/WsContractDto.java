package net.hawkengine.model.dto;

import net.hawkengine.model.enums.NotificationType;

public class WsContractDto {
    private String className;
    private String packageName;
    private String methodName;
    private Object result;
	private NotificationType notificationType;
    private String errorMessage;
    private ConversionObject[] args;

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ConversionObject[] getArgs() {
        return this.args;
    }

    public void setArgs(ConversionObject[] args) {
        this.args = args;
	}

	public NotificationType getNotificationType() {
		return this.notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
    }
}

