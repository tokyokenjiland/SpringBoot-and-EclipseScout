package org.eclipse.scout.tasks.scout.ui.task;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.tasks.data.Task;
import org.eclipse.scout.tasks.scout.ui.task.AbstractTaskTablePage.Table.AcceptMenu;
import org.eclipse.scout.tasks.spring.service.TaskService;

public class TodaysTaskTablePage extends AbstractTaskTablePage {

  @Inject
  private TaskService taskService;

  public TodaysTaskTablePage() {
    getTable().getResponsibleColumn().setDisplayable(false);
    getTable().getAcceptedColumn().setDisplayable(false);
    getTable().getDoneColumn().setDisplayable(false);
    getTable().getMenuByClass(AcceptMenu.class).setVisible(false);
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("TodaysTasks");
  }

  @Override
  protected Collection<Task> getTasks() {
    return taskService.getTodaysTasks(getUserId());
  }
}
