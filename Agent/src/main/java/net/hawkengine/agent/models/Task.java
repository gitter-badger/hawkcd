package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.*;
import net.hawkengine.model.MaterialDefinition;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private TaskDefinition taskDefinition;
    private String jobId;
    private String stageId;
    private String pipelineId;
    private RunIf runIfCondition;
    private TaskType type;
    private TaskStatus status;
    private String output;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public MaterialDefinition getMaterialDefinition() {
        return this.materialDefinition;
    }

    public void setMaterialDefinition(MaterialDefinition materialDefinition) {
        this.materialDefinition = materialDefinition;
    }

    private MaterialDefinition materialDefinition;

    public Task() {
        this.startTime = LocalDateTime.now();
    }

    public TaskDefinition getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStageId() {
        return this.stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getPipelineId() {
        return this.pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf runIfCondition) {
        this.runIfCondition = runIfCondition;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
