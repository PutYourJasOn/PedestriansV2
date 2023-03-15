package me.json.pedestrians.ui.tasks;

import java.lang.reflect.InvocationTargetException;

public enum Task {

    ADD_TASK(AddTask.class);

    private final Class<? extends ITask> iTaskClass;

    Task(Class<? extends ITask> iTaskClass) {
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
