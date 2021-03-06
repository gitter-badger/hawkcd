package net.hawkengine.services;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.DatabaseType;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends DbEntry> extends Service<T> implements ICrudService<T> {
    static final DatabaseType DATABASE_TYPE = ServerConfiguration.getConfiguration().getDatabaseType();

    private IDbRepository<T> repository;

    public IDbRepository<T> getRepository() {
        return this.repository;
    }

    public void setRepository(IDbRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public ServiceResult getById(String id) {
        T dbObject = this.getRepository().getById(id);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "retrieved successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbObjects = this.getRepository().getAll();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(T object) {
        T dbObject = this.getRepository().add(object);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "created successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "already exists");
        }

        return result;
    }

    @Override
    public ServiceResult update(T object) {
        if (object == null) {
            return super.createServiceResult(object, NotificationType.ERROR, "not found");
        }
        T dbObject = this.getRepository().update(object);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "updated successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        T dbObject = this.getRepository().delete(id);

        ServiceResult result = new ServiceResult();
        if (dbObject == null) {
            result = super.createServiceResult((T) result.getObject(), NotificationType.ERROR, "not found");
        } else {
            result = super.createServiceResult((T) result.getObject(), NotificationType.SUCCESS, "deleted successfully");
        }

        result.setObject(dbObject);

        return result;
    }
}
