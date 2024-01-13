package com.dlut.www.ticket.func.schedule.task;

import java.util.List;

public interface Task {
    void book();
    void query();
    boolean authority();
    List<String> getAllCourtInfos();
    List<String> getAllCourtPrices();

}
