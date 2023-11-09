package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private Integer counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public Task addTask(Task task) {
        counter++;
        task.setId(counter);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(Task task) {
        Task taskEdit = null;
        if (task != null) {
            taskEdit = tasks.get(task.getId());
            historyManager.add(taskEdit);
        }
        return taskEdit;
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            Task taskEdit = tasks.get(task.getId());
            taskEdit.setName(task.getName());
            taskEdit.setDescription(task.getDescription());
            taskEdit.setStatus(task.getStatus());
        }
    }

    @Override
    public void deleteTaskById(Task task) {
        tasks.remove(task.getId());
        historyManager.remove(task.getId());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public Epic addEpic(Epic epic) {
        counter++;
        epic.setId(counter);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(Epic epic) {
        Epic epicEdit = null;
        if (epic != null) {
            epicEdit = epics.get(epic.getId());
            historyManager.add(epicEdit);
        }
        return epicEdit;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            Epic epicEdit = epics.get(epic.getId());
            epicEdit.setName(epic.getName());
            epicEdit.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteEpicById(Epic epic) {
        for (Integer idsubtask : epic.getCatalogSubtaskId()) {
                subTasks.remove(idsubtask);
                historyManager.remove(idsubtask);
        }
        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Subtask addSubTask(Subtask subtask) {
        counter++;
        subtask.setId(counter);
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskIdInEpic(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubTask(){
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public  ArrayList getSubtasksForEpic(Epic epic) {
        ArrayList <Integer> catalogIdSubtasks = epic.getCatalogSubtaskId();
        ArrayList <Subtask> subTasksEdit = new ArrayList();
        for (Integer catalogIdSubtask : catalogIdSubtasks) {
            subTasksEdit.add(subTasks.get(catalogIdSubtask));
        }
        return subTasksEdit;
    }

    @Override
    public Subtask getSubtaskById(Subtask subtask){
        Subtask subTaskEdit = null;
        if (subtask != null) {
            subTaskEdit = subTasks.get(subtask.getId());
            historyManager.add(subTaskEdit);
        }
        return subTaskEdit;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask subTaskEdit = subTasks.get(subtask.getId());
            subTaskEdit.setName(subtask.getName());
            subTaskEdit.setDescription(subtask.getDescription());
            subTaskEdit.setStatus(subtask.getStatus());
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void deleteSubtaskById(Subtask subtask) {

        // Проверяем, существует ли Сабтаск:
        if (subtask == null) {
            return;
        }

        // Записываем в переменную номера Сабтаска, Эпика id и сам эпик из Сабтаска:
        Integer epicId = subtask.getEpicId();
        Integer numberOfSubtask = subtask.getId();
        Epic epic = epics.get(epicId);
        if ( epic != null) {
            // Удаляем сам Сабтаск, а потом его ID из эпика:
            subTasks.remove(numberOfSubtask);
            historyManager.remove(numberOfSubtask);

            epic.getCatalogSubtaskId().remove(numberOfSubtask);

            // Проверяем статус Эпика:
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void deleteSubtasks() {
        subTasks.clear();
        for (Epic value : epics.values()) {
            value.getCatalogSubtaskId().clear();
            updateEpicStatus(value.getId());
        }
    }

    // Проверка статуса Эпика:
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getCatalogSubtaskId();

        // Проверяем на пустоту:
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = null;
        for (int subtaskId: subtaskIds) {
            Subtask subtask = subTasks.get(subtaskId);

            // Если первый проход, то сохраняем статус первой подзадачи:
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            // Проверяем на равенство и IN_PROGRESS:
            if (status.equals(subtask.getStatus()) && !status.equals("IN_PROGRESS")) {
                continue;
            }

            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        epic.setStatus(status);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
    @Override
    public void clear() {
        historyManager.clear();
    }
}
