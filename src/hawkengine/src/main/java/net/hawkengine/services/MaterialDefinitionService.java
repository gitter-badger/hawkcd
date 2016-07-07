package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class MaterialDefinitionService extends CrudService<MaterialDefinition> implements IMaterialDefinitionService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";

    public MaterialDefinitionService() {
        super.setRepository(new RedisRepository(MaterialDefinition.class));
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType("MaterialDefinition");
    }

    public MaterialDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        super.setObjectType("MaterialDefinition");
    }

    @Override
    public ServiceResult getById(String materialDefinitionId) {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        MaterialDefinition result = null;

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            for (MaterialDefinition materialDefinition : pipelineDefinition.getMaterials()) {
                if (materialDefinition.getId().equals(materialDefinitionId)) {
                    result = materialDefinition;
                    return super.createServiceResult(result, false, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            materialDefinitions.addAll(pipelineDefinition.getMaterials());
        }

        return super.createServiceResultArray(materialDefinitions, false, this.successMessage);
    }

    @Override
    public ServiceResult add(GitMaterial materialDefinition) {
        return this.addMaterialDefinition(materialDefinition);
    }

    @Override
    public ServiceResult add(NugetMaterial materialDefinition) {
        return this.addMaterialDefinition(materialDefinition);
    }

    public ServiceResult addMaterialDefinition(MaterialDefinition materialDefinition) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(materialDefinition.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterials();
        List<MaterialDefinition> allMaterialDefinitions = (List<MaterialDefinition>) this.getAll().getObject();
        for(MaterialDefinition materialDefinitionToCheck : allMaterialDefinitions){
            if(materialDefinitionToCheck.getId().equals(materialDefinition.getId())){
                return super.createServiceResult(null, true, "already exists");
            }
        }
        boolean hasNameCollision = this.checkForNameCollision(materialDefinitions, materialDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(materialDefinition, true, "with the same name exists");
        }

        materialDefinitions.add(materialDefinition);
        pipelineDefinition.setMaterials(materialDefinitions);
        PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();

        MaterialDefinition result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, true, "not added successfully");
        }

        return super.createServiceResult(result, false, "added successfully");
    }

    @Override
    public ServiceResult update(GitMaterial materialDefinition) {
        return this.updateMaterialDefinition(materialDefinition);
    }

    @Override
    public ServiceResult update(NugetMaterial materialDefinition) {
        return this.updateMaterialDefinition(materialDefinition);
    }

    public ServiceResult updateMaterialDefinition(MaterialDefinition materialDefinition) {
        MaterialDefinition result = null;

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(materialDefinition.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterials();
        boolean hasNameCollision = this.checkForNameCollision(materialDefinitions, materialDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(materialDefinition, true, "with the same name exists");
        }

        int lengthOfMaterialDefinitions = materialDefinitions.size();
        for (int i = 0; i < lengthOfMaterialDefinitions; i++) {
            MaterialDefinition definition = materialDefinitions.get(i);
            if (definition.getId().equals(materialDefinition.getId())) {
                Class resultMaterialClass = this.getMaterialDefinitionType(materialDefinition);
                try {
                    result = (MaterialDefinition) resultMaterialClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                materialDefinitions.set(i, materialDefinition);
                pipelineDefinition.setMaterials(materialDefinitions);
                PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();
                result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinition.getId());
                break;
            }
        }

        if (result == null) {
            return super.createServiceResult(result, true, "not found");
        }

        return super.createServiceResult(result, false, "updated successfully");
    }

    public ServiceResult delete(String materialDefinitionId) {
        boolean isRemoved = false;
        MaterialDefinition materialDefinitionToDelete = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
        if (materialDefinitionToDelete == null) {
            return super.createServiceResult(materialDefinitionToDelete, true, "does not exist");
        }

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService
                .getById(materialDefinitionToDelete.getPipelineDefinitionId())
                .getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterials();

        int lengthOfMaterialDefinitions = materialDefinitions.size();
        if (lengthOfMaterialDefinitions > 1) {
            for (int i = 0; i < lengthOfMaterialDefinitions; i++) {
                MaterialDefinition definition = materialDefinitions.get(i);
                if (definition.getId().equals(materialDefinitionToDelete.getId())) {
                    materialDefinitions.remove(definition);
                    isRemoved = true;
                    break;
                }
            }
        } else {
            return super.createServiceResult(materialDefinitionToDelete, true, "cannot delete the last material definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(materialDefinitionToDelete, true, "not deleted");
        }

        pipelineDefinition.setMaterials(materialDefinitions);
        PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();
        MaterialDefinition result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinitionId);
        if (result != null) {
            return super.createServiceResult(result, true, "not deleted successfully");
        }

        return super.createServiceResult(result, false, "deleted successfully");
    }

    private boolean checkForNameCollision(List<MaterialDefinition> materialDefinitions, MaterialDefinition materialDefinitionToAdd) {
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            if (materialDefinition.getId().equals(materialDefinitionToAdd.getId())) {
                continue;
            }

            if (materialDefinition.getName().equals(materialDefinitionToAdd.getName())) {
                return true;
            }
        }

        return false;
    }

    private MaterialDefinition extractMaterialDefinitionFromPipelineDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId) {
        MaterialDefinition result = pipelineDefinition
                .getMaterials()
                .stream()
                .filter(md -> md.getId().equals(materialDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }

    private Class<?> getMaterialDefinitionType(MaterialDefinition materialDefinition) {
        MaterialType materialDefinitionType = materialDefinition.getType();
        Class result = null;
        switch (materialDefinitionType) {
            case GIT:
                result = GitMaterial.class;
                break;
            case NUGET:
                result = NugetMaterial.class;
                break;
        }

        return result;
    }
}
