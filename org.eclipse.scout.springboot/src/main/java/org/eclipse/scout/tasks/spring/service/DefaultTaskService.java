package org.eclipse.scout.tasks.spring.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.util.Assertions;
import org.eclipse.scout.rt.platform.util.date.DateUtility;
import org.eclipse.scout.tasks.data.Task;
import org.eclipse.scout.tasks.model.TaskEntity;
import org.eclipse.scout.tasks.spring.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultTaskService implements TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Override
  @Transactional
  public void addTask(Task task) {
    if (task == null) {
      return;
    }

    // make sure task is not already in list
    if (taskRepository.exists(task.getId())) {
      return;
    }

    taskRepository.save(taskRepository.convert(task));
  }

  @Override
  @Transactional
  public void saveTask(Task task) {
    Assertions.assertNotNull(task.getCreator(), "creator must be set");

    TaskEntity taskEntity = taskRepository.save(taskRepository.convert(task));
    taskEntity.setCreator(task.getCreator());
    taskEntity.setResponsible(task.getResponsible());
  }

  protected List<Task> getUserTasks(String userId) {
    return taskRepository.findByResponsible(userId)
        .stream()
        .map(t -> taskRepository.convert(t))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Task getTask(UUID taskId) {
    return taskRepository.convert(taskRepository.findOne(taskId));
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Task> getInbox(String userId) {
    List<Task> inboxList = new ArrayList<>();

    for (Task task : getUserTasks(userId)) {
      if (!task.isAccepted()) {
        inboxList.add(task);
      }
    }

    return inboxList;
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Task> getOwnTasks(String userId) {
    List<Task> ownList = new ArrayList<>();

    for (Task task : getUserTasks(userId)) {
      if (task.isAccepted()) {
        ownList.add(task);
      }
    }

    return ownList;
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Task> getTodaysTasks(String userId) {
    List<Task> todaysList = new ArrayList<>();

    for (Task task : getUserTasks(userId)) {
      if (!task.isAccepted() || task.isDone()) {
        continue;
      }

      if (isToday(task.getDueDate()) || isToday(task.getReminder())) {
        todaysList.add(task);
      }
    }

    return todaysList;
  }

  protected boolean isToday(Date date) {
    if (date == null) {
      return false;
    }

    return DateUtility.isSameDay(new Date(), date);
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Task> getAllTasks() {
    return taskRepository.findAll().stream()
        .map(t -> taskRepository.convert(t))
        .collect(Collectors.toSet());
  }

}
