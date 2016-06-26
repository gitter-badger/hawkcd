package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JobAssignerService {
    private IAgentService agentService;
    private IPipelineService pipelineService;
    private static final Logger LOGGER = Logger.getLogger(JobAssignerService.class.getName());

    public JobAssignerService() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
    }

    public JobAssignerService(IAgentService agentService, IPipelineService pipelineService) {
        this.agentService = agentService;
        this.pipelineService = pipelineService;
    }

    public void assignJobs() {
        List<Agent> agents = (List<Agent>) this.agentService.getAllAssignableAgents().getObject();
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelines().getObject();

        for (Pipeline pipeline : pipelines) {
            for (Stage stage : pipeline.getStages()) {
                if (stage.getStatus() == StageStatus.IN_PROGRESS) {
                    for (Job job : stage.getJobs()) {
                        if (job.getStatus() == JobStatus.SCHEDULED) {
                            Agent agent = (Agent) this.agentService.getById(job.getAssignedAgentId()).getObject();
                            boolean isEligible = this.isAgentEligibleForJob(job, agent);
                            if (!isEligible) {
                                agent.setAssigned(false);
                                this.agentService.update(agent);

                                job.setStatus(JobStatus.AWAITING);
                                this.pipelineService.update(pipeline);
                            }
                        }

                        if (job.getStatus() == JobStatus.AWAITING) {
                            List<Agent> eligibleAgents = this.getEligibleAgentsForJob(job, agents);
                            Agent agentForJob = this.pickMostSuitableAgent(eligibleAgents);
                            if (agentForJob != null) {
                                agentForJob.setAssigned(true);
                                this.agentService.update(agentForJob);

                                job.setAssignedAgentId(agentForJob.getId());
                                job.setStatus(JobStatus.SCHEDULED);
                                this.pipelineService.update(pipeline);
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Agent> getEligibleAgentsForJob(Job job, List<Agent> agents) {
        List<Agent> eligibleAgents = new ArrayList<>();
        for (Agent agent : agents) {
            boolean isEligible = true;
            for (String resource : job.getResources()) {
                if (!(agent.getResources().contains(resource))) {
                    isEligible = false;
                    break;
                }
            }

            if (isEligible) {
                eligibleAgents.add(agent);
            }
        }

        return eligibleAgents;
    }

    public Agent pickMostSuitableAgent(List<Agent> agents) {
        Agent agentForJob = null;
        if (agents.size() == 1) {
            agentForJob = agents.get(0);
        } else if (agents.size() > 1) {
            int numberOfResources = Integer.MAX_VALUE;
            for (Agent agent : agents) {
                if (agent.getResources().size() < numberOfResources) {
                    numberOfResources = agent.getResources().size();
                    agentForJob = agent;
                }
            }
        }

        return agentForJob;
    }

    public boolean isAgentEligibleForJob(Job job, Agent agent) {
        boolean isEligible = true;
        if (agent == null || !agent.isConnected() || !agent.isEnabled() || agent.isRunning()) {
            isEligible = false;
        } else {
            for (String resource : job.getResources()) {
                if (!(agent.getResources().contains(resource))) {
                    isEligible = false;
                    break;
                }
            }
        }

        return isEligible;
    }
}
