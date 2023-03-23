package me.json.pedestrians.ui.tasks;

import java.lang.reflect.InvocationTargetException;

public enum TaskType {

    ADD_TASK(AddTask.class), REMOVE_TASK(RemoveTask.class), CONNECT_TASK(ConnectTask.class), EXPORT_TASK(ExportTask.class), CLOSE_TASK(CloseTask.class);

    private final Class<? extends ITask> iTaskClass;

    TaskType(Class<? extends ITask> iTaskClass) {
        this.iTaskClass = iTaskClass;
    }

    public Class<? extends ITask> iTaskClass() {
        return iTaskClass;
    }

    public ITask newInstance() {
        try {
            return (ITask) iTaskClass.getConstructors()[0].newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
