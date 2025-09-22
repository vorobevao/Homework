package com.example.dungeon.model;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> neighbors = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private Monster monster;
    private boolean locked;
    private String requiredKey; // Название ключа, необходимого для открытия комнаты

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.locked = false;
        this.requiredKey = null;
    }

    public Room(String name, String description, boolean locked, String requiredKey) {
        this.name = name;
        this.description = description;
        this.locked = locked;
        this.requiredKey = requiredKey;
    }

    public String getName() {
        return name;
    }

    public Map<String, Room> getNeighbors() {
        return neighbors;
    }

    public List<Item> getItems() {
        return items;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster m) {
        this.monster = m;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getRequiredKey() {
        return requiredKey;
    }

    public void setRequiredKey(String requiredKey) {
        this.requiredKey = requiredKey;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + ": " + description);

        if (locked) {
            sb.append("\nДверь закрыта. Нужен ключ: ").append(requiredKey);
        }

        if (!items.isEmpty()) {
            sb.append("\nПредметы: ").append(items.stream().map(Item::getName).collect(Collectors.joining(", ")));
        }
        if (monster != null) {
            sb.append("\nВ комнате монстр: ").append(monster.getName()).append(" (ур. ").append(monster.getLevel()).append(")");
        }
        if (!neighbors.isEmpty()) {
            sb.append("\nВыходы: ").append(String.join(", ", neighbors.keySet()));
        }
        return sb.toString();
    }
}