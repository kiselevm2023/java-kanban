package test;

import main.manager.Managers;
import main.manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager createManager() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        return manager;
    }

}