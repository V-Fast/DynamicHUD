package com.tanishisherewith.dynamichud.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WidgetGroup {
    private final UUID id;
    private String name;
    private final List<Widget> members = new ArrayList<>();

    public WidgetGroup(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public WidgetGroup(String name) {
        this(UUID.randomUUID(), name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Widget> getMembers() {
        return members;
    }

    public void addMember(Widget widget) {
        if (!members.contains(widget)) {
            members.add(widget);
            widget.setGroup(this);
        }
    }

    public void removeMember(Widget widget) {
        if (members.remove(widget)) {
            widget.setGroup(null);
        }
    }
}
