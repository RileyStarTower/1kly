package com.startowerstudio.kly;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Riley on 3/5/2018.
 */
public class MainActivityTest {

    @Test
    public void getNextCount() throws Exception {
        ArrayList<KlyTask> taskList = new ArrayList<>();
        // 0-4, continuous
        taskList.add(new KlyTask("desc0", KlyTask.CURRENT_TASKS_BASE + "0", "expired", "resolution"));
        taskList.add(new KlyTask("desc1", KlyTask.CURRENT_TASKS_BASE + "1", "expired", "resolution"));
        taskList.add(new KlyTask("desc2", KlyTask.CURRENT_TASKS_BASE + "2", "expired", "resolution"));
        taskList.add(new KlyTask("desc3", KlyTask.CURRENT_TASKS_BASE + "3", "expired", "resolution"));
        taskList.add(new KlyTask("desc4", KlyTask.CURRENT_TASKS_BASE + "4", "expired", "resolution"));
        int count = KlyTaskUtils.getInstance().getNextCount(taskList);
        assertEquals(count, 5);

        // 0-4, gap at 1
        taskList.clear();
        taskList.add(new KlyTask("desc0", KlyTask.CURRENT_TASKS_BASE + "0", "expired", "resolution"));
        taskList.add(new KlyTask("desc2", KlyTask.CURRENT_TASKS_BASE + "2", "expired", "resolution"));
        taskList.add(new KlyTask("desc3", KlyTask.CURRENT_TASKS_BASE + "3", "expired", "resolution"));
        taskList.add(new KlyTask("desc4", KlyTask.CURRENT_TASKS_BASE + "4", "expired", "resolution"));
        count = KlyTaskUtils.getInstance().getNextCount(taskList);
        assertEquals(count, 1);

        // 0-4, gap at 0
        taskList.clear();
        taskList.add(new KlyTask("desc1", KlyTask.CURRENT_TASKS_BASE + "1", "expired", "resolution"));
        taskList.add(new KlyTask("desc2", KlyTask.CURRENT_TASKS_BASE + "2", "expired", "resolution"));
        taskList.add(new KlyTask("desc3", KlyTask.CURRENT_TASKS_BASE + "3", "expired", "resolution"));
        taskList.add(new KlyTask("desc4", KlyTask.CURRENT_TASKS_BASE + "4", "expired", "resolution"));
        count = KlyTaskUtils.getInstance().getNextCount(taskList);
        assertEquals(count, 0);

        // 0-7, multiple gaps
        taskList.clear();
        taskList.add(new KlyTask("desc0", KlyTask.CURRENT_TASKS_BASE + "0", "expired", "resolution"));
        taskList.add(new KlyTask("desc2", KlyTask.CURRENT_TASKS_BASE + "2", "expired", "resolution"));
        taskList.add(new KlyTask("desc3", KlyTask.CURRENT_TASKS_BASE + "3", "expired", "resolution"));
        taskList.add(new KlyTask("desc5", KlyTask.CURRENT_TASKS_BASE + "5", "expired", "resolution"));
        taskList.add(new KlyTask("desc6", KlyTask.CURRENT_TASKS_BASE + "6", "expired", "resolution"));
        taskList.add(new KlyTask("desc7", KlyTask.CURRENT_TASKS_BASE + "7", "expired", "resolution"));
        count = KlyTaskUtils.getInstance().getNextCount(taskList);
        assertEquals(count, 1);
    }

}